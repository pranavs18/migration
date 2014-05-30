
// user process structure for the processes launched by a user on any slave machine 
public class userProcessStructure  {
   private String ipAddress;
   private State state;
   private String processName;
   private int slaveProcessID;
   private int slaveProcessPort;
  
   // constructor
   public userProcessStructure(String ipAddress, String processName, int slaveProcessID, int slaveProcessPort){
	   this.ipAddress = ipAddress;
	   this.state = State.RUNNING;
	   this.processName = processName;
	   this.slaveProcessID = slaveProcessID;
	   this.slaveProcessPort = slaveProcessPort;
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

   public int getSlaveProcessID() {
	return slaveProcessID;
}

   public void setSlaveProcessID(int slaveProcessID) {
	this.slaveProcessID = slaveProcessID;
}

   public int getSlaveProcessPort() {
	return slaveProcessPort;
}

   public void setSlaveProcessPort(int slaveProcessPort) {
	this.slaveProcessPort = slaveProcessPort;
}


}
