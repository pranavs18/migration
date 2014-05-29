import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

public class UserConsole extends Thread implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int processID = 1;
	
	// map to maintain user processes 
    public static HashMap<Integer,userProcessStructure> userProcessMap = new HashMap<Integer,userProcessStructure>();
	
	public static void main(String args[]){
		ProcessManager pm = new ProcessManager(args[0],Integer.parseInt(args[1]));
		 System.out.println("Process Manager started : Status Running : IpAddress:" + args[0] + "Port: " + args[1] );
		 new Thread(pm).start(); 
		 	
		 while(true){
			int n=0;
			String pname = null;
			while(true){
				System.out.println("Press \n 1 - Launch a process \n 2 - Remove a process \n 3 - Migrate a process \n 4 - List the running user processes");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					 n = Integer.parseInt(br.readLine());
				} catch (NumberFormatException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			   switch(n){
			   case 1:{
				  
				  System.out.println("Please enter the name of the process you want to launch\n");
				  try {
				    pname = br.readLine();    
					System.out.println("Choose Ipaddress:port no of the machine on which you want to launch the process");
					 if(ProcessManager.ProcessTable.entrySet().isEmpty()){
						   System.out.println("\n Please launch a worker process on any machine to launch the example process on it \n");
					   }
					 else{
					   for (Entry<Integer, HashMap<InetAddress,Integer>> obj: ProcessManager.ProcessTable.entrySet()) {
						   System.out.println(" | Process ID -> " + obj.getKey() + " | IP Address:Port -> |" + obj.getValue() + " | ");
					   }
					   
					   System.out.println("Enter the IP address from the list \n");
					   String ipAddress = br.readLine();
					   System.out.println("Enter the corresponding port of the IP address you chose above \n");
					   String port = br.readLine();
					   String commandName = "Launch";
					   
					   
					   String sendData = commandName + " " + pname + " " + processID;
					   userProcessStructure ups = new userProcessStructure(ipAddress,pname);
			           userProcessMap.put(processID,ups);
					   processID++;
						// Extract the port number from the heart beat 	   
					   Socket MasterSocket = new Socket(ipAddress, Integer.parseInt(port));
					   PrintStream out = new PrintStream(MasterSocket.getOutputStream());
					   out.println(sendData);
					   MasterSocket.close();
					   
					 }
					break;
				  } catch (IOException e) {
					e.printStackTrace();
				   }
				 
			    }
			   
			   case 2:{
				   System.out.println("Please enter the name of the process you want to remove");
				   
				   break;
			   }
			   
			   case 3:{
				   System.out.println("Please enter the name of the process you want to migrate");
				   System.out.println(" \n Please choose the destination IP address and Port for the process from the list below to migrate the example process");
				   if(ProcessManager.ProcessTable.entrySet().isEmpty()){
					   System.out.println("\n Please launch a process on any machine to migrate the example process");
				   }
				   for (Entry<Integer, HashMap<InetAddress,Integer>> obj: ProcessManager.ProcessTable.entrySet()) {
					   System.out.println(" | Process ID -> " + obj.getKey() + " | IP Address:Port -> |" + obj.getValue() + " | ");
				   }
				   break;
			   }
			   
			   case 4:{
				   System.out.println("\n The list of user processes which are running are as follows \n");
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   System.out.println(" |  Process ID ->" + obj.getKey() + " | Process Name ->" + obj.getValue().getProcessName() + " | Slave Machine IP -> " + obj.getValue().getIpAddress() + " |  State -> |" + obj.getValue().getState());
				   }
			   }
			   
			   
			   }
			}
		}
		}
	
	}
