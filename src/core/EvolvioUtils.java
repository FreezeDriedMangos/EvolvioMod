package core;

import java.io.PrintStream;

public class EvolvioUtils {
	public static void printStackTrace(PrintStream stream) {
		for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
			stream.println("\t" + e);
		}
	}
}
