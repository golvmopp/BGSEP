package lib;

	/**
     * <i>Messages:</i>
     * <br>
     * <table>
     * <tr><td>0</td><td>start byte (0x42)</td></tr>
     * <tr><td>1</td><td>second byte specifies message type</td></tr>
     * <tr><td>2..n</td><td>the following bytes are message type specific</td></tr>
     * <tr><td>n</td><td>stop byte (0x24)</td></tr>
     *</table>
     *<br>
     *<p>
     *All messages must use start byte 0x42 and stop byte 0x24.
     *If any of these bytes are used inside a message they are escaped with 0xAC.
     *If an escape byte should be interpreted as a normal byte it should be escaped.
     *</p>
     *<i>Message types:</i>
     * <br>
     * <table>
     *     	<tr><td>Button</td><td>0</td></tr>
     *      <tr><td>Joystick</td><td>1</td></tr>
     *      <tr><td>Close</td><td>2</td></tr>
     *      <tr><td>Name</td><td>3</td></tr>
     *      <tr><td>Poll</td><td>4</td></tr>
     *</table>
     *<br>
     * <i>Specifics:</i>       	
     * <table>
     * <tr><td><strong>byte</strong></td><td><strong>1</strong></td><td><strong>2</strong></td><td><strong>3</strong></td><td><strong>4</strong></td><td><strong>5</strong></td><td><strong>6</strong></td></tr>
     * 
     * <tr><td>Button:</td><td>id</td><td>bool</td></tr>
     * <tr><td>Joystick:</td><td>id</td><td>float (32 bit float == 4 bytes)</td></tr>
     * <tr><td>Close:</td><td>String</td></tr>
     * <tr><td>Name:</td><td>String</td></tr><strong>
     * <tr><td>Poll</td></tr>
     * </table>
     * <br>
     **/
public class Protocol {
	

    public static final byte MESSAGE_TYPE_BUTTON = 0X00;
    public static final byte MESSAGE_TYPE_JOYSTICK = 0X01;
    public static final byte MESSAGE_TYPE_CLOSE = 0X02;
    public static final byte MESSAGE_TYPE_NAME = 0X03;
    public static final byte MESSAGE_TYPE_POLL = 0X04;

    public static final byte START = 0x42;
    public static final byte STOP = 0x24;
    public static final byte ESCAPE = (byte) 0xAC;
    
    public static final String SERVER_UUID = "27012f0c-68af-4fbf-8dbe-6bbaf7aa432a";

	
    
}
