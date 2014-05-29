
// user process structure for the processes launched by a user on any slave machine 
public class userProcessStructure  {
   private String ipAddress;
   private State state;
   private String processName;
  
   // constructor
   public userProcessStructure(String ipAddress, String processName){
	   this.ipAddress = ipAddress;
	   this.state = State.RUNNING;
	   this.processName = processName;
   }
   
   public userProcessStructure() {
    }

public String getIpAddress() {
	return ipAddress;
   }
   
   public void setIpAddress(String ipAddress) {
	this.ipAddress = ipAddress;
   }
   
   public State getState() {
	return state;
   }
   
   public void setState(State state) {
	this.state = state;
   }
   
   public String getProcessName() {
	return processName;
   }
   public void setProcessName(String processName) {
	this.processName = processName;
   }

}
