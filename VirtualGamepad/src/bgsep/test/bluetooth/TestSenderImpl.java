package bgsep.test.bluetooth;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.app.Activity;
import bluetooth.BluetoothHandler;
import bluetooth.SenderImpl;
import junit.framework.TestCase;
import static lib.Protocol.*;

public class TestSenderImpl extends TestCase {
	
	SenderImpl testSender = new SenderImpl(new BluetoothHandler(new Activity()));
	

	public void testShouldBeEscaped() {
		Method method;
		try {
			method = SenderImpl.class.getDeclaredMethod("shouldBeEscaped", byte.class);
			method.setAccessible(true);
			assertTrue((Boolean) method.invoke(testSender, ESCAPE)); 
			assertTrue((Boolean) method.invoke(testSender, START));
			assertTrue((Boolean) method.invoke(testSender, STOP));
			assertTrue(!(Boolean) method.invoke(testSender, MESSAGE_TYPE_CLOSE));
			assertTrue(!(Boolean) method.invoke(testSender, MESSAGE_TYPE_NAME));
			
		} catch (NoSuchMethodException e) {
			fail("No method: shouldBeEscaped(byte)");
		} catch (IllegalArgumentException e) {
			fail("Illegal argument: testArray incorrect");
		} catch (IllegalAccessException e) {
			fail("Illegal access: method private");
		} catch (InvocationTargetException e) {
			fail("Invocation target object incorrect");
		}
		
	}
	
	public void testInsertEscapeBytes() {	
		Method method;
		byte[] testArrayOne = {START, ESCAPE, MESSAGE_TYPE_BUTTON, ESCAPE, START, STOP, STOP};
		byte[] excpectedOne = {START, ESCAPE, ESCAPE, MESSAGE_TYPE_BUTTON, ESCAPE, ESCAPE, ESCAPE, START, ESCAPE, STOP, STOP};
		byte[] testArrayTwo = {STOP,START};
		byte[] excpectedTwo = {STOP,START};
		byte[] testArrayThree = {ESCAPE, ESCAPE, ESCAPE};
		byte[] excpectedThree = {ESCAPE, ESCAPE, ESCAPE, ESCAPE};
		
		try {
			method = SenderImpl.class.getDeclaredMethod("insertEscapeBytes", byte[].class);
			method.setAccessible(true);
			
			byte[] actualOne = (byte[]) method.invoke(testSender, testArrayOne);
			byte[] actualTwo = (byte[]) method.invoke(testSender, testArrayTwo);	
			byte[] actualThree = (byte[]) method.invoke(testSender, testArrayThree);

			assertTrue(Arrays.equals(actualOne, excpectedOne));
			assertTrue(Arrays.equals(actualTwo, excpectedTwo));
			assertTrue(Arrays.equals(actualThree, excpectedThree));
			
		} catch (NoSuchMethodException e) {
			fail("No method insertEscapeBytes(byte[])");
		} catch (IllegalArgumentException e) {
			fail("Illegal argument: testArray incorrect");
		} catch (IllegalAccessException e) {
			fail("Illegal access: method private");
		} catch (InvocationTargetException e) {
			fail("Invocation target object incorrect");
		}
	}

}
