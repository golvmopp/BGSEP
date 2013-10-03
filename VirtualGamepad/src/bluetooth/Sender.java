package bluetooth;

/**
 * 
 * Includes the methods for sending data to the server
 *
 */
public interface Sender {
	
	/**
	 * Sends a message about which button was pressed or released.
	 * @param id an integer from 0 to 19 that represents a button
	 * @param pressed a boolean indicating if the button was pressed, else release
	 * @return a boolean indicating if the parameters were correct
	 */
	public boolean send(int id, boolean pressed);
	
	/**
	 * Sends a message about which joystick was moved.
	 * @param id an integer from 0 to 19 that represents a joystick
	 * @param value a value between -1 and 1 describing the joystick's new position
	 * @return a boolean indicating if the parameters were correct
	 */
	public boolean send(int id, float value);
	
	/**
	 * Tells the server that the connection will close.
	 * @param message a cause to display in the server
	 * @return
	 */
	public boolean send(String message);
}
