import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class Worker extends Thread implements Runnable{
	
	String MasterIp;
	int MasterPort;
	int workerServerPort;
    
	/* This hashmap maintains a list of all the processed in the worker */
	public static HashMap<Integer,Long> threadIds = new HashMap<Integer, Long>();
	
	public Worker(String MasterIp, int MasterPort, int workerServerPort){
		
		this.MasterIp = MasterIp;
		this.MasterPort = MasterPort;
		this.workerServerPort = workerServerPort;
		
		
	}
	
	public Worker() {
	
		
	}
	
	
	public void startWorkerHost(String MasterIp, int MasterPort, int workerServerPort) throws UnknownHostException, IOException{
		@SuppressWarnings("resource")
		ServerSocket workerServer = new ServerSocket(workerServerPort);

		while(true){
		
		Socket masterClientSocket = workerServer.accept();	
		InputStreamReader input = new InputStreamReader(masterClientSocket.getInputStream());
		BufferedReader in = new BufferedReader(input);
		
		String[] arguments;
		
		String readString = "";
		while(( readString = in.readLine()) != null){
			arguments = readString.split(" ");
			
			/* Start a new thread to perform operations as required by the 
			 * message sent by master
			 */	  
			WorkerApplicationManager wam = new WorkerApplicationManager(arguments,MasterIp,MasterPort, masterClientSocket);
			new Thread(wam).start();

		 }

		}
	  
	}
	
	/*
	 * This thread starts the worker host
	 */
	@Override
	public void run() {
		
		try {
			startWorkerHost(MasterIp, MasterPort,workerServerPort);
		} catch (UnknownHostException e) {	
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	
 public static void main(String[] args){
		
		if(args.length != 3){
			System.out.println("Please enter the Arguments of the form - HostIp port");
			
		}
		
		String MasterIp = args[0]; 
		int MasterPort =  Integer.parseInt(args[1]);
		int workerServerPort = Integer.parseInt(args[2]);
	
		Worker worker = new Worker(MasterIp, MasterPort, workerServerPort);
		HeartBeat heartBeat = new HeartBeat(MasterIp, MasterPort,workerServerPort);
		
		// Starts worker host thread
		new Thread(worker).start();
		
		// Start heartbeat thread
		new Thread(heartBeat).start();
	
		//Worker.slaveServerPort++;
	}
	
	
	
}
