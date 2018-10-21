package edu.jiangxin.mess;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Tmp {
	public static void main(String[] args) throws IOException {
		String path = "D:\\temp\\OJ\\HWOJJAVA\\src\\main\\java\\edu\\jiangxin\\jobdu";
		File dir = new File(path);
		for (File file : dir.listFiles(new MyFilenameFilter())) {
			String fileName = file.getName();
			String num = fileName.substring(2, 6);
			System.out.println("fileName:" + fileName);
			String content = FileUtils.readFileToString(file);
			content = "package edu.jiangxin.jobdu;" + "\n\n" + content;
			content = content.replace("public class Main", "public class Q" + num);
			String outFileName = "Q" + num + ".java";
			FileUtils.write(new File(dir, outFileName), content);
		}
	}

}

class MyFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		if (name.startsWith("题目") && name.endsWith(".java")) {
			return true;
		}
		return false;
	}
	
}
