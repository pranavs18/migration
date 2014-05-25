
public class ProcessInformation {
	
	int processID;
	String processIP;
	int port;
	State state;

	
	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public String getProcessIP() {
		return processIP;
	}

	public void setProcessIP(String processIP) {
		this.processIP = processIP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public enum State {
		RUNNING,TERMINATED,SUSPENDED
	}

}
