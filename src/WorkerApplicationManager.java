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
	
public void performOperation() throws InstantiationException, IllegalAccessException, ClassNotFoundException{

    Long threadID;
	String processName = message[1];
	Integer processID = Integer.parseInt(message[2]);
	
	

	if(message[0].equals("Launch")){
		
		
		PrintStream out = null;
		
		
		/* IO operation objects */
		try {
			out = new PrintStream(socket.getOutputStream());
		} catch (IOException e2) {
			
			e2.printStackTrace();
		}
		
			
		 try {
		    	MigratableProcess command = null;		
		    	
				try {
					// java reflection to launch the process whose class name is detected at run time

					command = (MigratableProcess)Class.forName(processName).newInstance();
					
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
                
				Thread pThread = new Thread(command);
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
				
				out.println("Terminated "+processID+" "+processName);
				
				/* After completion The entry in the threadIds Hashmap is removed*/
				Worker.threadIds.remove(processID);
				System.out.println("map entries exit: "+Worker.threadIds);
				
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		  

	}
	
	
	else if(message[0].equals("Remove")){
		
		Long id = Worker.threadIds.get(processID);
		PrintStream out = null;
		
		

		/* IO operation objects */
		try {
			out = new PrintStream(socket.getOutputStream());
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

				            Field removing = r.getClass().getDeclaredField("removing");
				            removing.setAccessible(true);
				            removing.setBoolean(r, true);
				        } catch (Exception e) {
				            e.printStackTrace();
				        }
				 	
				
				
			/*
			Class<?> noparams[]={};	
			Method x = null;
			Object obj = null;
			
			try{
			Class<?> cls = Class.forName(processName);
			obj = cls.newInstance();
			
				 x = cls.getDeclaredMethod("suspend", noparams);
				 System.out.println(x.toString() + " " + x.getName() + " " +x.isAccessible());
				//x = t.getClass().getDeclaredMethod("suspend", (Class<?>[]) null);
			} catch (NoSuchMethodException | SecurityException e) {

				e.printStackTrace();
			}
			try {
				if(x != null)
				{
					x.invoke(obj, (Object[])null);
				}
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				
				e.printStackTrace();
			}
			

			*/}
		}

		out.println("Terminated "+processID+" "+processName);
		
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

		for(Thread t : Thread.getAllStackTraces().keySet()){
			if(t.getId()==id){
			
				
				System.out.println("Id found in thread pool "+id+" actual Thread from pool "+ t.getId());
				Runnable r = null;
				        try {
				            Field field = Thread.class.getDeclaredField("target");
				            field.setAccessible(true);
				            r = (Runnable) field.get(t);

				            if (r == null) r = t;

				            Field removing = r.getClass().getDeclaredField("suspending");
				            removing.setAccessible(true);
				            removing.setBoolean(r, true);
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
				
				out.println("SlaveMigrateRequest "+processName+" "+processID+" "+selfIp+" "+selfPort+" "+message[0]+message[1]+message[2]+".txt");
				
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
		String fileName = message[5];
		
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
		
		  
//		  Socket slaveMigrationConnection = null;
//		  PrintStream out = null;
//		  
//		  try {
//				slaveMigrationConnection = new Socket(destIp,destPort);
//			} catch (IOException e3) {
//				e3.printStackTrace();
//			}
//		 
//			
//			
//			/* IO operation objects */
//			try {
//				out = new PrintStream(slaveMigrationConnection.getOutputStream());
//			} catch (IOException e2) {
//
//				e2.printStackTrace();
//			}
//			
//			out.println("MigrationComplete");
			

		
	}

	
}
	@Override
public void run() {


		try {
			performOperation();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println();

	}

}
