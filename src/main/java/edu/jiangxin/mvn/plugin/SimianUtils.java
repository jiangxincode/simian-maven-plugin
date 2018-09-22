package edu.jiangxin.mvn.plugin;

import java.io.File;

public class SimianUtils {

	public static File file(String s) {
		File f = new File(s);
		return f;
	}
	
	public static Integer toInteger(String s) {
		return Integer.parseInt(s);
	}
}