import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class UserConsole extends Thread implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int processID = 1;
	
	// map to maintain user processes 
    public static Map<Integer,userProcessStructure> userProcessMap = Collections.synchronizedMap(new HashMap<Integer,userProcessStructure>());
	
	public static void main(String args[]) throws IOException{
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
				  
				  System.out.println("LAUNCH A NEW PROCESS - Please enter the name of the process you want to launch\n");
				  try {
				    pname = br.readLine(); 
				    String tmp[] = pname.split(" ");
				    String temp = "";
				    if(tmp.length > 1){
				    for(int i=1;i<tmp.length;i++){
				    	System.out.println(tmp[i]);
				    	temp = temp + tmp[i] + " ";
				    }
				    }
				    System.out.println(temp);
				    pname = tmp[0];
					System.out.println("Choose Ipaddress:port no of the machine on which you want to launch the process");
					 if(ProcessManager.ProcessTable.entrySet().isEmpty()){
						   System.out.println("\n Please launch a worker process on any machine to launch the example process on it \n");
					   }
					 else{
					   for (Entry<Integer, HashMap<InetAddress,Integer>> obj: ProcessManager.ProcessTable.entrySet()) {
						   System.out.println(" | Process ID -> " + obj.getKey() + " | IP Address:Port -> |" + obj.getValue() + " | ");
					   }
					   
					   System.out.println("Enter the slave process ID from the list on which you want to launch the user process");
					   int pid = Integer.parseInt(br.readLine());
					   System.out.println("Enter the IP address from the list \n");
					   String ipAddress = br.readLine();
					   System.out.println("Enter the corresponding port of the IP address you chose above \n");
					   String port = br.readLine();
					   String commandName = "Launch";
					   
				
					   String sendData = commandName + " " + tmp[0] + " " + processID + " " + temp;
					   
					   userProcessStructure ups = new userProcessStructure(ipAddress,pname,pid,Integer.parseInt(port));
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
				   System.out.println("REMOVE A PROCESS - To remove(kill) a process, Please choose the name and ID of the process from the list mentioned below \n");
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   System.out.println(" |  Process ID ->" + obj.getKey() + " | Process Name ->" + obj.getValue().getProcessName() + " | Slave Machine IP -> " + obj.getValue().getIpAddress() + " |  State -> |" + obj.getValue().getState());
				   }
				   System.out.println(" \n Enter the process ID from the map \n");
				   int pid = 0;
				try {
					pid = Integer.parseInt(br.readLine());
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				   String name = null;
				   try {  
					System.out.println("\n Enter the name of the process");   
				    name = br.readLine();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				   String commandName = "Remove";
				   String ipAddress = null;
				   int port = 0 ;
				   String sendData = commandName + " " + name + " " + pid;
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   if(obj.getKey() == pid){
						   ipAddress = obj.getValue().getIpAddress();
						   port = obj.getValue().getSlaveProcessPort();
						   break;
						   //System.out.println(ipAddress +" and " + port);
					   }
				   }
				   Socket MasterSocket = null;
				try {
					MasterSocket = new Socket(ipAddress, port);
				   PrintStream out = null;
			
					out = new PrintStream(MasterSocket.getOutputStream());
				   out.println(sendData);
				   MasterSocket.close();
				 }catch (IOException e) {
						
						e.printStackTrace();
					}
				
				   break;
			   }
			   
			   case 3:{
				   System.out.println("MIGRATE A PROCESS");
				   if(userProcessMap.entrySet().isEmpty()){
					   System.out.println(" No user processes running right now \n");
				   }
				   else{
				   String slaveIPAddress = null;
				   int slavePort = 0 ;
				   System.out.println("\n The list of user processes which are running are as follows \n");
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   System.out.println(" |  Process ID ->" + obj.getKey() + " | Process Name ->" + obj.getValue().getProcessName() + " | Slave Machine IP -> " + obj.getValue().getIpAddress() + " |  State -> |" + obj.getValue().getState());
				     }
				   System.out.println("\n Enter the ID of the process listed in the table you want to migrate \n");
				   int pid = Integer.parseInt(br.readLine());
				   System.out.println("\n Enter the coresponding name of the process you want to migrate \n");
				   String processName = br.readLine();
				   System.out.println(" \n Please choose the destination IP address for the process from the list below to migrate the example process");
				   if(ProcessManager.ProcessTable.entrySet().isEmpty()){
					   System.out.println("\n Please launch a process on any machine to migrate the user process");
				   }
				   for (Entry<Integer, HashMap<InetAddress,Integer>> obj: ProcessManager.ProcessTable.entrySet()) {
					   System.out.println(" | Process ID -> " + obj.getKey() + " | IP Address:Port -> |" + obj.getValue() + " | ");
				   }
				   String destIP = br.readLine();
				   System.out.println(" \n Please choose the destination Port for the process from the list below to migrate the example process");
				   String destPort = br.readLine();
				   String commandName = "Migrate";
				  
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   if(obj.getKey() == pid){
						   slaveIPAddress = obj.getValue().getIpAddress();
						   slavePort = obj.getValue().getSlaveProcessPort();
						   if(destIP.equals(obj.getValue().getSlaveProcessID())){
							   System.out.println("Migration Not possible within the same machine...Please choose a different IP");
							   break;
						   }
						   break;
						   //System.out.println(ipAddress +" and " + port);
					   }
				   }
				   String sendData= commandName + " " + processName + " " + pid + " " + destIP + " " + destPort + " " + slaveIPAddress + " " + slavePort;
				    Socket MasterSocket = null;
					try {
						MasterSocket = new Socket(slaveIPAddress, slavePort);
					    PrintStream out = null;
				        out = new PrintStream(MasterSocket.getOutputStream());
					    out.println(sendData);
					    MasterSocket.close();
					 }catch (IOException e) {		
							e.printStackTrace();
						}
					  
				     }
				   break;
			   }
			   
			   case 4:{
				   if(userProcessMap.entrySet().isEmpty()){
					   System.out.println(" No user processes running right now \n");
					   break;
				   }
				   else{
				   System.out.println("\n The list of user processes which are running are as follows \n");
				   for(Entry<Integer,userProcessStructure> obj: userProcessMap.entrySet()){
					   System.out.println(" |  Process ID ->" + obj.getKey() + " | Process Name ->" + obj.getValue().getProcessName() + " | Slave Machine IP -> " + obj.getValue().getIpAddress() + " |  State -> |" + obj.getValue().getState());
				   }
				   break;
				   }
			   }
			   
			   
			   }
			}
		}
		}
	
	}
