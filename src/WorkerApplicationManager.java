import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;



public class WorkerApplicationManager implements Runnable {

	String[] message = null;
	String MasterIp = null;
	int MasterPort = 0;
	
public WorkerApplicationManager(String[] message,String MasterIp, int MasterPort){
	
	this.message = message;
	this.MasterIp = MasterIp;
	this.MasterPort = MasterPort;
	
}
	
public void performOperation(){

    Long threadID = Thread.currentThread().getId();
	String processName = message[1];
	Integer processID = Integer.parseInt(message[2]);
	

	if(message[0].equals("Launch")){
		
		Worker.threadIds.put(processID, threadID);
		PrintStream out = null;
		/* Socket created to send the Process Status */
		Socket processStatusSocket = null;
		try {
			processStatusSocket = new Socket(MasterIp, MasterPort);
		} catch (UnknownHostException e3) {
			
			e3.printStackTrace();
		} catch (IOException e3) {
			
			e3.printStackTrace();
		}
		
		/* IO operation objects */
		try {
			out = new PrintStream(processStatusSocket.getOutputStream());
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
                
				command.run();
				
				out.println("Terminated "+processID+" "+processName);
				
				 /* After completion The entry in the threadIds Hashmap is removed*/
				Worker.threadIds.remove(processID);
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			}
		  

	}
	
	
	else if(message[0].equals("Remove")){
		
		
		
	}

	
}
	@Override
public void run() {


		performOperation();
		System.out.println();

	}

}
