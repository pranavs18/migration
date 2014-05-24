import java.io.*;

public class UserConsole {
	public static void main(String args[]){
		while(true){
			int n=0;
			String pname = null;
			while(true){
				System.out.println("Press \n 1 - Launch a process \n 2 - Remove a process \n 3 - Migrate a process");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					 n = Integer.parseInt(br.readLine());
				} catch (NumberFormatException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   switch(n){
			   case 1:{
				  
				  System.out.println("Please enter the name of the process you want to launch\n");
				  try {
				    pname = br.readLine();
					System.out.println("Choose Ipaddress:port no of the machine on which you want to launch the process");
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
				   break;
			   }
			   
			   
			   }
			}
		}
		}
	}
