
import java.util.Observable;


public class Serva {
	
	public static void main(String[] args) {
		
		new Thread(new Server()).start();	
		
	}

}
