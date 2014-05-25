import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ProcessManager implements MigratableProcess, Runnable,Serializable {
    
	private static final long serialVersionUID = 1L;
	private String Ipaddress = null;
    private int port ;
    private int conn=0;
    private Socket connection;
    private int ID;
    
    HashMap<Integer,HashMap<String,Integer>> ProcessTable = new HashMap<Integer,HashMap<String,Integer>>();
	
    public ProcessManager(String ipAddress, int port) {
		// TODO Auto-generated constructor stub
		this.Ipaddress= ipAddress;
		this.port = port;
	}

	public ProcessManager(Socket SOCK, int i) {
		this.connection = SOCK;
		this.ID = i;
	}

	public void createConnection() throws IOException{
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(this.port);
		
		while ( true ) {
		    try {
			Socket clientSocket = ss.accept();
			conn++;
			ProcessConnection newconn = new ProcessConnection(clientSocket, conn, this);
			new Thread(newconn).start();
		    }   
		    catch (IOException e) {
			System.out.println(e);
		    }
	
		}
	}
		catch (IOException e1) {
			e1.printStackTrace();
		}
		finally{
			ss.close();
		}
	}

	@Override
	public void run(){
		
	}
	
	@Override
	public void suspend() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void launch(String pname, String Ipaddress, int portno) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]) throws IOException{
		
		if(args.length != 2){
			System.out.println("Please provide both the IPaddress and Port number for the process manager to run");
		    System.exit(1);
		}
	    	
		String IpAddress = args[0];
		int port = Integer.parseInt(args[1]);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ProcessManager pm = new ProcessManager(IpAddress,port);
		 System.out.println("Process Manager started : Status Running : IpAddress:" + IpAddress + "Port: " + port );
		pm.createConnection();

	}

	@Override
	public void launch(String pname) {
		// TODO Auto-generated method stub
		
	}
	
}

class ProcessConnection implements Runnable{
	  ProcessManager server;
	  Socket SOCK;
	  int id;
	  BufferedReader br = null;
      PrintStream ps = null;
      boolean done = false;
	  
      public ProcessConnection(Socket clientSocket, int id, ProcessManager pm) {
			this.SOCK = clientSocket;
			this.id = id;
			this.server = pm;
			System.out.println( "Connection " + id + " established with: " + SOCK );
			try {
			    br = new BufferedReader(new InputStreamReader(SOCK.getInputStream()));
			    ps = new PrintStream(SOCK.getOutputStream());
			} catch (IOException e) {
			    System.out.println(e);
			}
		}
      
	public void run(){
		while(!done){
			try {
				String message = br.readLine();
				System.out.println( "Received " + message + " from connection " + id + "." );
				ps.println("welcome client " +  id );
				if(message == "close"){
					System.out.println( "Connection " + id + " closed." );
		            br.close();
		            ps.close();
		            SOCK.close();
		            break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		 
		
		}
	}
}