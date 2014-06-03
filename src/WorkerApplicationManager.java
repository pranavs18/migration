import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.net.Socket;
import java.net.UnknownHostException;



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
  // System.out.println("PROCESS" + processID);
	

	if(message[0].equals("Launch")){
		
		
		System.out.println("messages "+ message[0]+" "+message[1]+ " " + message[2] + " " +  message[3]);
		PrintStream out = null;
		String []tmp = new String[message.length-3];
		System.out.println(message.length);
		if(message.length > 3){
		for(int i=0;i<message.length -3;i++){
		  	tmp[i] = message[i+3];
		  	//System.out.println(tmp[i] + " " + tmp);
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
			         
					//command = (MigratableProcess)Class.forName(processName).newInstance();
					
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
                
				Thread pThread = new Thread(process);
				threadID = pThread.getId();
				System.out.println("Thread Id should be same as grep"+ threadID);
				Worker.threadIds.put(processID, threadID);
				
				System.out.println("map entries: "+Worker.threadIds);
				
				pThread.start();
				
				try {
					pThread.join();
				} catch (InterruptedException e) {
					
					System.out.println("Thread number "+threadID+" interuupted");
				}
				
				
				
				/* After completion The entry in the threadIds Hashmap is removed*/
				out.println("Terminated "+processName+" "+processID);		
				System.out.println("map entries exit: "+Worker.threadIds);
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		  

	}
	
	
	else if(message[0].equals("Remove")){
		
		Socket tempSocket = null;
		try {
			tempSocket = new Socket(MasterIp,MasterPort);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
			
				
				System.out.println("Id found in thread pool "+id+" actual Thread from pool "+ t.getId());
		
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
		System.out.println("Worker.tread " + Worker.threadIds);
		try {
			tempSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		
		PrintStream out = null;
		
		
		/* IO operation objects */
		try {
			out = new PrintStream(slaveMigrationConnection.getOutputStream());
		} catch (IOException e2) {

			e2.printStackTrace();
		}
		
		System.out.println("Worker.tread " + Worker.threadIds);

		for(Thread t : Thread.getAllStackTraces().keySet()){
			System.out.println("Process ID"+ processID+"Id found in thread pool "+id+" actual Thread from pool "+ t.getId());
			if(t.getId()==id){
			
				
				System.out.println("Id found in thread pool "+id+" actual Thread from pool "+ t.getId());
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
				
				
//				InputStreamReader input = null;
//				BufferedReader in = null;
//				try {
//					input = new InputStreamReader(slaveMigrationConnection.getInputStream());
//					 in = new BufferedReader(input);
//				} catch (IOException e) {
//					 
//					e.printStackTrace();
//				}
//				String response = null;
//				try {
//					response = in.readLine();
//				} catch (IOException e) {
//			
//					e.printStackTrace();
//				}
				
				try {
					slaveMigrationConnection.close();
				} catch (IOException e) {
					e.printStackTrace();
			}
				
				
//				if(response.equals("MigrationComplete")){
//					
//					try {
//						slaveMigrationConnection.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
				
				
				
				
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
			System.out.println(r.toString());
		
			in.close();
			fileInput.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		Thread t = new Thread(r);
		t.start();
		Long threadid = t.getId();
		
		Worker.threadIds.put(processID, threadid);
		System.out.println("Put Put " + Worker.threadIds);
		
		  try {
	            Field field = Thread.class.getDeclaredField("target");
	            field.setAccessible(true);
	            r = (Runnable) field.get(t);

	            if (r == null) r = t;

	            Field removing = r.getClass().getDeclaredField("suspending");
	            removing.setAccessible(true);
	            removing.setBoolean(r, false);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		  
		  
		  
		  
		  Socket migrateReplytSocket = null;
		try {
			migrateReplytSocket = new Socket(MasterIp, MasterPort);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
			/* Keeps sending heartbeat with process map and its own server port number */
			
			PrintStream out = null;
			try {
				out = new PrintStream(migrateReplytSocket.getOutputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			out.println("Migrated "+processName+" "+processID+" "+selfIp+" "+selfPort);
			out.flush();
		 
			try {
				migrateReplytSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
