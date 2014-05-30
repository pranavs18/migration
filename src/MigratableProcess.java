
import java.io.Serializable;


 public interface MigratableProcess extends Serializable,Runnable {

	static final long serialVersionUID = 1L;

    public void suspend();
    
    public void remove();

    
}