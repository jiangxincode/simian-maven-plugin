package edu.jiangxin.mvn.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import au.com.redhillconsulting.simian.SimianTask;

public class FileSimianTask extends SimianTask {
	/**
	 * Make sure the directory structure exists and
	 * send the output to a specified file.
	 */
	public void setOutput(File output) {
		output.getParentFile().mkdirs();

		try {
			setOutput(new FileOutputStream(output));
		} catch (FileNotFoundException e) {

		}
	}
}