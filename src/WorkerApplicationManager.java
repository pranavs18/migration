

public class WorkerApplicationManager implements Runnable {

	String[] message = null;
	
public WorkerApplicationManager(String[] message){
	
	this.message = message;
	
}
	
public void performOperation(){
    long threadID = Thread.currentThread().getId();
	String processName = message[1];
	ProcessInformation newProcess = new ProcessInformation();
	newProcess.setProcessID(-1);
	newProcess.setProcessName(processName);
	newProcess.setState(State.RUNNING);
	newProcess.setThreadID(threadID);
	
	Worker.processMap.put(threadID, newProcess);

	if(message[0].equals("Launch")){
	
		 try {
		    	MigratableProcess command = null;
				try {
					// java reflection to launch the process whose class name is detected at run time
					command = (MigratableProcess)Class.forName(processName).newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
                command.run(); 
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}	
	}
	System.out.println(Worker.processMap);
	System.out.println(Worker.processMap.get(Thread.currentThread().getId()).getProcessID());
	System.out.println(Worker.processMap.get(Thread.currentThread().getId()).getProcessName());
	System.out.println(Worker.processMap.get(Thread.currentThread().getId()).getThreadID());
	System.out.println(Worker.processMap.get(Thread.currentThread().getId()).getState());
	
	newProcess.setState(State.TERMINATED);
	Worker.processMap.put(threadID, newProcess);
	System.out.println(Worker.processMap.get(Thread.currentThread().getId()).getState());
	
	
}
	@Override
public void run() {
     performOperation();
	}

}
