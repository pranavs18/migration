
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.net.Socket;



public class WorkerApplicationManager implements Runnable {

	String[] message = null;
	String MasterIp = null;
	int MasterPort = 0;
	Socket socket = null;
	
public WorkerApplicationManager(String[] message,String MasterIp, int MasterPort, Socket socket){
	
	this.socket = socket;
	this.message = message;
	this.MasterIp = MasterIp;
	this.MasterPort = MasterPort;
	
}
	
public void performOperation() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{

    Long threadID;
	String processName = message[1];
	Integer processID = Integer.parseInt(message[2]);
	
/* When we encounter the launch command we start a new instance of the process */
	if(message[0].equals("Launch")){
		
		
		
		PrintStream out = null;
		String []tmp = new String[message.length-3];
		
		if(message.length > 3){
		for(int i=0;i<message.length -3;i++){
		  	tmp[i] = message[i+3];
		 } 
		}
		
		
		/* IO operation objects */
		try {
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e2) {
			
			e2.printStackTrace();
		}
		
			
		 try {
		   
		        MigratableProcess process = null;
		        Object[] x = {tmp};
				try {
					// java reflection to launch the process whose class name is detected at run time
					Class<?> className = Class.forName(processName);
					Constructor<?> constructor = className.getConstructor(String [].class);
					process = (MigratableProcess) constructor.newInstance(x);
					
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
                
				Thread pThread = new Thread(process);
				threadID = pThread.getId();
				Worker.threadIds.put(processID, threadID);				
				pThread.start();
				System.out.println(processName + " launched");
				try {
					pThread.join();
					
				} catch (InterruptedException e) {
					
				}
				
				
				
				/* After completion The entry in the threadIds Hashmap is removed*/
				out.println("Terminated "+processName+" "+processID);		
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		  

	}
	
	/* This block stops and removes an executing process from a list */
	else if(message[0].equals("Remove")){
		
		Socket tempSocket = null;
		try {
			tempSocket = new Socket(MasterIp,MasterPort);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Long id = Worker.threadIds.get(processID);
		PrintStream out = null;
		

		/* IO operation objects */
		try {
			out = new PrintStream(tempSocket.getOutputStream());
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		
		for(Thread t : Thread.getAllStackTraces().keySet()){
			if(t.getId()==id){
			
				
		
		/* We extract the runnable by using reflections and call the suspend method to exit the thread. 
		 * We then assign the runnable to 0 to make it garbage collectable */
				        try {
				            Field field = Thread.class.getDeclaredField("target");
				            field.setAccessible(true);
				            Runnable r = (Runnable) field.get(t);

				            if (r == null) r = t;

				            Class<?> noParmeter[] = {};
				            Method suspending = r.getClass().getDeclaredMethod("suspend", noParmeter );
				            suspending.setAccessible(true);
				            suspending.invoke(r, (Object[])null);
				            r=null;
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
				 	
				
				
			}
		}
		out.println("Terminated "+processName+" "+processID);
		Worker.threadIds.remove(processID);

		try {
			tempSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	else if(message[0].equals("Migrate")){
		
		String destIp = message[3];
		int destPort = Integer.parseInt(message[4]);
		String selfIp = message[5];
		int selfPort = Integer.parseInt(message[6]);
		
		
		
		Long id = Worker.threadIds.get(processID);
		
		Socket slaveMigrationConnection = null;
		try {
			slaveMigrationConnection = new Socket(destIp,destPort);
		} catch (IOException e3) {

			e3.printStackTrace();
		}

		
		PrintStream out = null;
		
		
		/* IO operation objects */
		try {
			out = new PrintStream(slaveMigrationConnection.getOutputStream());
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		

		for(Thread t : Thread.getAllStackTraces().keySet()){
			
			if(t.getId()==id){
			
				/* We extract the runnable by using reflections and call the suspend method to exit the thread. 
				 * We then serialize and send this object to the other machine and then deserialize and run it on a new thread */
				Runnable r = null;
				        try {
				            Field field = Thread.class.getDeclaredField("target");
				            field.setAccessible(true);
				            r = (Runnable) field.get(t);

				            if (r == null) r = t;
				            
				            Class<?> noParmeter[] = {};
				            Method suspending = r.getClass().getDeclaredMethod("suspend", noParmeter );
				            suspending.setAccessible(true);
				            suspending.invoke(r, (Object[])null);
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
				  
				        
				  FileOutputStream file = null;
				  ObjectOutputStream  oos = null;
				try {
					file = new FileOutputStream(message[0]+message[1]+message[2]+".txt");
				} catch (FileNotFoundException e1) {
					
					e1.printStackTrace();
				}      
				  try {
					oos = new ObjectOutputStream(file);	
					oos.writeObject(r);
					
					
					
					
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				  

				try {
					oos.close();
					file.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				
				Worker.threadIds.remove(processID);
				out.println("SlaveMigrateRequest "+processName+" "+processID+" "+selfIp+" "+selfPort+" "+destIp+" "+destPort+" "+message[0]+message[1]+message[2]+".txt");

				
				try {
					slaveMigrationConnection.close();
				} catch (IOException e) {
					e.printStackTrace();
			}
		
				
				
			}
		}	
	}
	
	else if(message[0].equals("SlaveMigrateRequest")){
		
		String destIp = message[3];
		int destPort = Integer.parseInt(message[4]);
		String selfIp = message[5];
		int selfPort = Integer.parseInt(message[6]);
		String fileName = message[7];
		
		FileInputStream fileInput = null;
		ObjectInputStream in = null;
		try {
			fileInput = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        try {
			 in = new ObjectInputStream(fileInput);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Runnable r = null;
		try {
			r = (MigratableProcess)in.readObject();
			System.out.println("Migration of object "+r+" complete");
			in.close();
			fileInput.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread t = new Thread(r);
		t.start();
		Long threadid = t.getId();
		
		Worker.threadIds.put(processID, threadid);


		  
		  
		  Socket migrateReplytSocket = null;
		try {
			migrateReplytSocket = new Socket(MasterIp, MasterPort);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
			/* Keeps sending heartbeat with process map and its own server port number */
			
			PrintStream out = null;
			try {
				out = new PrintStream(migrateReplytSocket.getOutputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			out.println("Migrated "+processName+" "+processID+" "+selfIp+" "+selfPort);
			out.flush();
		 
			try {
				migrateReplytSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	
}
	@Override
public void run() {
		try {
			performOperation();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		System.out.println();

	}

}
