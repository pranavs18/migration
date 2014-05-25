import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Worker implements Runnable{


	public void startWorkerHost(String MasterIp, int MasterPort) throws UnknownHostException, IOException{
		boolean done = false;
		
		Socket workerSocket = new Socket(MasterIp, MasterPort);
		
		PrintStream out = new PrintStream(workerSocket.getOutputStream());
		InputStreamReader input = new InputStreamReader(workerSocket.getInputStream());
		BufferedReader in = new BufferedReader(input);
	
		String arguments[];
		String readString = "";
		
		System.out.println("Connection established - Sending heartbeat");
		System.out.print("is connected " + workerSocket.isConnected());
		
		out.println("Hello Server");
		int count = 0;
		while(( readString = in.readLine()) != null){
			System.out.println(readString);
			out.println("hello u there " + (count++) );
		
		try {
			
			Thread.sleep(2000);
		
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		}
		
	workerSocket.close();
	}

	public void run(String MasterIp, int MasterPort) {
		
		try {
			startWorkerHost(MasterIp, MasterPort);
		} catch (UnknownHostException e) {	
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		
		if(args.length != 2){
			
			System.out.println("Please enter the Arguments of the form - HostIp port");
			
		}
		
		String MasterIp = args[0]; 
		int MasterPort =  Integer.parseInt(args[1]);
		
		Worker worker = new Worker();
		worker.run(MasterIp, MasterPort);	
		
	}

	@Override
	public void run() {
		
	}
	
}
