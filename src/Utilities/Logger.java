package Utilities;

/**
 * "Object... args" arguments are varargs
 * final argument may be passed as an array or as a sequence of arguments.
 */
public class Logger {
	 public static void log(String mesg, Object... args) { 
	        System.out.printf(mesg+"\n", args);
	    }
}
