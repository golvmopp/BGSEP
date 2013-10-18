/*
   Copyright (C) 2013 Isak Eriksson, Linus Lindgren

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

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
     * @author Isak Eriksson (isak.eriksson@mail.com) & Linus Lindgren (linlind@student.chalmers.se)
     **/
public class Protocol {
	
    public static final byte MESSAGE_TYPE_BUTTON = 0X00;
    public static final byte MESSAGE_TYPE_JOYSTICK = 0X01;
    public static final byte MESSAGE_TYPE_CLOSE = 0X02;
    public static final byte MESSAGE_TYPE_NAME = 0X03;
    public static final byte MESSAGE_TYPE_POLL = 0X04;
    public static final byte MESSAGE_TYPE_SERVER_FULL = 0X05;
    public static final byte MESSAGE_TYPE_CONNECTION_ACCEPTED = 0X06;
    
    public static final byte START = 0x42;
    public static final byte STOP = 0x24;
    public static final byte ESCAPE = (byte) 0xAC;
    
    public static final String SERVER_UUID = "27012f0c-68af-4fbf-8dbe-6bbaf7aa432a";

}