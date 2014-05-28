import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class HeartBeat implements Runnable {


	String MasterIp;
	int MasterPort;
	int workerServerPort;
	
	HeartBeat(String MasterIp,int MasterPort,int workerServerPort){
		
		this.MasterIp = MasterIp;
		this.MasterPort = MasterPort;
		this.workerServerPort = workerServerPort;
	}
	
	
public void startHeartBeat(String MasterIp, int MasterPort,int workerServerPort) throws UnknownHostException, IOException{
		
		System.out.println("Connection to master established - Sending heart beat");
		
		Socket heartBeatSocket = new Socket(MasterIp, MasterPort);
		/* Keeps sending heartbeat with process map and its own server port number */
		
		PrintStream out = new PrintStream(heartBeatSocket.getOutputStream());
		
		InputStreamReader input = new InputStreamReader(heartBeatSocket.getInputStream());
		BufferedReader in = new BufferedReader(input);
		
		FileOutputStream processInfoFile =
		         new FileOutputStream(""+workerServerPort);
		ObjectOutputStream oos = new ObjectOutputStream(processInfoFile);
		
		String readS = "";
		out.println("Hello "+ workerServerPort);
		System.out.println("Hello "+ workerServerPort);
		out.flush();
		oos.writeObject(Worker.processMap);
		
		while((readS = in.readLine())!=null){
			System.out.println(readS);
			
			/* Worker acting as client */
			out.println("Hello "+ workerServerPort);
			oos.writeObject(Worker.processMap);
			out.flush();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
		}
		
		/* Closing all opened streams */
		oos.close();
		processInfoFile.close();
	    out.close();
	    heartBeatSocket.close();
	}
	
	
	@Override
	public void run() {
		HeartBeat hb = new HeartBeat(MasterIp, MasterPort,workerServerPort);
		try {
			
			hb.startHeartBeat(MasterIp, MasterPort,workerServerPort);
		} catch (UnknownHostException e) {
			System.out.println("Host not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Read error");
			e.printStackTrace();
		}
		
	}

}
