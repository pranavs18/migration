import java.io.Serializable;
import java.net.InetAddress;


public class ProcessInformation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	InetAddress IP;
	long threadID;
	int processID;
	State state;
	String processName;
	
	public InetAddress getIP() {
		return IP;
	}

	public void setIP(InetAddress iP) {
		IP = iP;
	}

	
	public long getThreadID() {
		return threadID;
	}

	public void setThreadID(long threadID) {
		this.threadID = threadID;
	}
	
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}


	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	
}
