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
	
	public GrepProcess(){
		
	}

	public GrepProcess(String args[]) throws Exception
	{
		if (args.length != 3) {
			System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
			throw new Exception("Invalid Arguments");
		}
		
		query = args[0];
		inFile = new TransactionalFileInputStream(args[1]);
		outFile = new TransactionalFileOutputStream(args[2], false);
	}

	public void run()
	
	{
		System.out.println("grep process started...");
		
		/*PrintStream out = new PrintStream(outFile);
	     BufferedReader in = new BufferedReader(new InputStreamReader(inFile));

		try {
			while (!suspending) {
				String line = in.readLine();

				if (line == null) break;
				
				if (line.contains(query)) {
					out.println(line);
				}
				
				// Make grep take longer so that we don't require extremely large files for interesting results
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// ignore it
				}
			}
		} catch (EOFException e) {
			//End of File
		} catch (IOException e) {
			System.out.println ("GrepProcess: Error: " + e);
		}


		suspending = false; */
	}

	public void suspend()
	{
		suspending = true;
		while (suspending);
	}

	@Override
	public void launch(String pname) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launch(String pname, String Ipaddress, int portno) {
		// TODO Auto-generated method stub
		
	}

	

}