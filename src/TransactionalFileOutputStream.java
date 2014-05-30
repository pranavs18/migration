import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream implements Serializable {

	/**
	 * 
	 */
    private transient RandomAccessFile raf = null;
	private static final long serialVersionUID = 1L;
    private boolean fileClosed;
    private File file;
	private int currentFileOffset;
    
	public TransactionalFileOutputStream(String string, boolean b) {
		this.currentFileOffset=0;
		this.file = new File(string);
		this.fileClosed = b;
	}

	public TransactionalFileOutputStream(File file) {
        this.file = file;
        this.currentFileOffset = 0;
    }
	
	@Override
	public void write(byte[] bytes) {
	
		 try {
	            raf = sendBytesOutputStream();
	            try {
					raf.write(bytes);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            currentFileOffset+=bytes.length;
	        } finally {
	            if (raf != null) {
	                try {
						raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }
	}
	
	private RandomAccessFile sendBytesOutputStream() {
	        RandomAccessFile raf = null;
			try {
				raf = new RandomAccessFile(file, "rw");
			    raf.seek(currentFileOffset);
			} catch (IOException e) {
		         e.printStackTrace();
	         }
	        return raf;
	    }
	
	@Override
    public void write(byte[] bytes, int fileOffset, int length) {
        try {
            raf = sendBytesOutputStream();
            raf.write(bytes, fileOffset, length);
            currentFileOffset+=length;
            
          }catch (Exception e) {
	         e.printStackTrace();
        }finally{
        	if (raf != null) {
                try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				  }
               }
           }
	   }

	@Override
	public void write(int b) {
		 try {
	            raf = sendBytesOutputStream();
	            try {
					raf.write(b);
				} catch (IOException e) {
					e.printStackTrace();
				}
	            currentFileOffset++;
	        } finally {
	            if (raf != null) {
	                try {
						raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
	        }
		
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

	public boolean isFileClosed() {
		return fileClosed;
	}

	public void setFileClosed(boolean fileClosed) {
		this.fileClosed = fileClosed;
	}
	
	
}
    


