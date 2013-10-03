package util;

public class ClientIdGenerator {
	private static int currentId = 0;
	
	public static int getGeneratedId(){
		return currentId++;
	}
}
