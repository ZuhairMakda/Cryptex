package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A class used to make logs in the console, used for debugging purposes. 
 * @author Somar Aani
 *
 */
public class Logger {
	private static DateTimeFormatter formatter;
	
	static {
		formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	}
	
	/**
	 * Add info message to the log
	 * @param s message to add 
	 */
	public static void info(String s) {
		LocalDateTime time = LocalDateTime.now();  
		System.out.println("[" + formatter.format(time) + "] " + s);
	}
	/**
	 * Add error message to log
	 * @param s error description
	 */
	public static void error(String s) {
		LocalDateTime time = LocalDateTime.now();  
		System.out.println("[" + formatter.format(time) + "] " + "ERROR: " + s);
	}
	/**
	 * Add warning message to log
	 * @param s warning description 
	 */
	public static void warning(String s) {
		LocalDateTime time = LocalDateTime.now();  
		System.out.println("[" + formatter.format(time) + "] " + "Warning: " + s);
	}

}
