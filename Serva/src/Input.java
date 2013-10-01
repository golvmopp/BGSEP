import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;


public class Input extends Observable {

	private String input;
	
	public Input(Observer obs) {
		addObserver(obs);
		start();
		
		input = "";
		
	}
	
	public void start() {
		
		Scanner in = new Scanner(System.in);
		while(true) {
			System.out.print("Enter message: ");
			input = in.nextLine();
			if(input.equals("Stop")) {
				in.close();
				break;
			}
			setChanged();
			notifyObservers(input);
		}
	}
	
}
