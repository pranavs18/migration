import java.io.PrintStream;
import java.io.EOFException;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;

public class GrepProcess implements MigratableProcess
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TransactionalFileInputStream  inFile;
	private TransactionalFileOutputStream outFile;
	private String query;

	private volatile boolean suspending;
	private volatile boolean removing;
	private boolean done = true;
	public GrepProcess(){
		
	}

	public GrepProcess(String args[]) throws Exception
	{
	/*	if (args.length != 3) {
			System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], false);*/
	}

	@Override
	public void run()
	
	{
		
		System.out.println("grep process started... with Id "+Thread.currentThread().getId());
		// PrintStream out = new PrintStream(outFile);
	    // BufferedReader in = new BufferedReader(new InputStreamReader(inFile));

		while (done) {
			System.out.println("Suspendin "+ suspending);
			while(suspending){
				
				suspend();

			}
			
			System.out.println("grep");
		//	String line = in.readLine();

/*			if (line == null) break;
			
			if (line.contains(query)) {
				out.println(line);
			}
			
			// Make grep take longer so that we don't require extremely large files for interesting results
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// ignore it
			}*/
		}
		

		suspending = false;
		removing = false;
	}

	@Override
	public void suspend()
	{
		System.out.println("suspendsuspendsuspend");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		suspending = true;
		while (suspending);
	}

	

	public void remove() {		
		System.out.println("removeremoveremove");
		removing = true;	
	}

	

}