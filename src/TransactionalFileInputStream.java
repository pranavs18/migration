import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileInputStream extends InputStream implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private int currentFileOffset;
   
    private String filename;
    
	public TransactionalFileInputStream(String filename) throws FileNotFoundException {
		this.filename = filename;
		this.currentFileOffset = 0;
	}
	
	
	@Override
    public void close() {
        currentFileOffset = 0;
        try {
			super.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	
	@Override
	public int read() {
		FileInputStream in = null;
        try {
            int readBytes = 0;
        	 in = BytesInputStream();
            try {
				readBytes = in.read();
			} catch (IOException e) {
				e.printStackTrace();
			}
            currentFileOffset++;
            return readBytes;
        } finally {
          if (in != null) {
              try {
				in.close();
			} catch (IOException e) {
			e.printStackTrace();
				}
            }
        }
    }
	
	
	public FileInputStream BytesInputStream() {
        FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
		    try {
				fis.skip(currentFileOffset);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        return fis;
    }
	
}
