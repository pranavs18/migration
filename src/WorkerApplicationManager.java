import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

		/* Remove */
		System.out.println("This is the remove string "+message[0]+" "+message[1]+" "+message[2]);
		
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
				            Field fTarget = Thread.class.getDeclaredField("target");
				            fTarget.setAccessible(true);
				            Runnable r = (Runnable) fTarget.get(t);

				            // This handles the case that the service overrides the run() method
				            // in the thread instead of setting the target runnable
				            if (r == null) r = t;

				            Field fI = r.getClass().getDeclaredField("suspending");
				            fI.setAccessible(true);
				            fI.setBoolean(r, true);
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

	
}
	@Override
public void run() {


		try {
			performOperation();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();

	}

}
