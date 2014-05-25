import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ProcessManager implements MigratableProcess, Runnable,Serializable {
    
	private static final long serialVersionUID = 1L;
	private String Ipaddress = null;
    private int port ;

	public ProcessManager(String ipAddress, int port) {
		// TODO Auto-generated constructor stub
		this.Ipaddress= ipAddress;
		this.port = port;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean done = false;
		ServerSocket ss = null;
		while(!done){
			try {
				 ss = new ServerSocket(this.port);
				Socket SOCK = ss.accept();
				InputStreamReader isr = new InputStreamReader(SOCK.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				
				String message = br.readLine();
				if(message != null){
					PrintStream ps = new PrintStream(SOCK.getOutputStream());
					System.out.println(ps.toString());
				}
				
			} 
			catch (IOException e) {
                e.printStackTrace();
            }
			finally{
			  try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			  }
			}
			
			
		   
		   try {
			Thread.sleep(1000);
		  } catch (InterruptedException e) {
			e.printStackTrace();
		}
		  System.out.println("Process Manager started : Status Running : IpAddress:" + this.Ipaddress + "Port: " + this.port );
		}
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
	
	public static void main(String args[]){
		
		if(args.length != 2){
			System.out.println("Please provide both the IPaddress and Port number for the process manager to run");
		    System.exit(1);
		}
	    	
		String IpAddress = args[0];
		int port = Integer.parseInt(args[1]);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ProcessManager pm = new ProcessManager(IpAddress,port);
		pm.run();

	}
	
}