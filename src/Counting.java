import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Counting implements MigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private volatile boolean suspending;
	int counter =0;
	int max  = 0;
	
	
	public Counting(String args[]) throws Exception {
        if (args.length != 1) {
            System.out.println("usage: Counting <max>");
            throw new Exception("Invalid Arguments");
        } 
        this.max = Integer.parseInt(args[0]);

    }
	
	@Override
	public void run() {

		while (!suspending) {
            System.out.println(counter);
            counter++;
            if(counter > max)
            	break;
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // ignore it
            }
           
        }
	
        suspending = false;
	}

	@Override
	public void suspend() {
		suspending = true;
        while (suspending);
	}

	@Override
	public void remove() {
		
	}
	
	@Override
	public String toString() {
        return "Counting process" + max ;
    }

	
	
}