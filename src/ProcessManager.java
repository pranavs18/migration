import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;


public class ProcessManager implements MigratableProcess, Runnable,Serializable {
    
	private static final long serialVersionUID = 1L;
	private String Ipaddress = null;
    private int port ;
    private int conn=0;
    private Socket connection;
    private int ID;
    
    public static Map<Integer, HashMap<InetAddress, Integer>> ProcessTable = Collections.synchronizedMap(new HashMap<Integer,HashMap<InetAddress,Integer>>());
    
    public ProcessManager(String ipAddress, int port) {
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
			slaveProcessConnection newconn = new slaveProcessConnection(clientSocket, conn, this);
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
		
		ProcessManager pm = new ProcessManager(Ipaddress,port);
		try {
			pm.createConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}

class slaveProcessConnection implements Runnable {
	  ProcessManager server;
	  Socket SOCK;
	  int id;
	  BufferedReader br = null;
      PrintStream ps = null;
      boolean done = false;
	  
      HashMap<InetAddress,Integer> SocketTable = new HashMap<InetAddress,Integer>();
     
      public slaveProcessConnection(Socket client, int id, ProcessManager pm) {
			this.SOCK = client;
			this.id = id;
			this.server = pm;
			System.out.println( "Connection " + id + " established with: " + SOCK );
			SocketTable.put(client.getInetAddress(), client.getPort());
			ProcessManager.ProcessTable.put(id,SocketTable);
		
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
				
				//System.out.println("Message"+message);
				if(message != null){
				String words[] = message.split(" ");
				if(words[0].equals("Migrated")){
					int pid = Integer.parseInt(words[2]);
					for(Entry<Integer,userProcessStructure> obj: UserConsole.userProcessMap.entrySet()){
						if(obj.getKey() == pid){
						   obj.getValue().setIpAddress(words[3]);
						   obj.getValue().setSlaveProcessPort(Integer.parseInt(words[4]));
						}
					}
				 }
				
				else if(words[0].equals("Terminated")){
					
					int pid = Integer.parseInt(words[2]);
					System.out.println("Terminated " +pid );
					UserConsole.userProcessMap.remove(pid);
					
				}
				}
				
				ps.println(" \n welcome client " +  id );
				if(message == null){
					System.out.println( "Connection " + id + " closed." );
					ProcessManager.ProcessTable.remove(id);
					for(Entry<Integer,userProcessStructure> obj: UserConsole.userProcessMap.entrySet()){
						if(obj.getValue().getSlaveProcessID() == id){
							UserConsole.userProcessMap.remove(obj.getKey());
						}
						
					}
		             br.close();
		             ps.close();
		             SOCK.close();
		            break;
				}
			} catch (Exception e) {
				//System.exit(1);
				//e.printStackTrace();	
				System.out.println( "Connection " + id + " closed." );
				ProcessManager.ProcessTable.remove(id);
				ArrayList<Integer> keyToRemove = new ArrayList<Integer>();
				for(Entry<Integer,userProcessStructure> obj: UserConsole.userProcessMap.entrySet()){
					if(obj.getValue().getSlaveProcessID() == id){
						keyToRemove.add(obj.getKey());
						}
					}
				for(int key: keyToRemove) {
					UserConsole.userProcessMap.remove(key);
				}
				 try {
					br.close();
					ps.close();
		            SOCK.close();
				} catch (IOException e1) {
					//e1.printStackTrace();
				}
	             
				break;
			}
		 
		
		}
	}
}
