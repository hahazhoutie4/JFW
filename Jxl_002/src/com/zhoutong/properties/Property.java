package com.zhoutong.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Property {
	/**
	 * @author hahazhoutie4
	 * @website cnblogs.com/hahazhoutie4-blogs/
	 */
	public Property() {
	};

	public static Properties getProperites(String file_path)
			throws FileNotFoundException, IOException {
		File file = new File(file_path);
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		return properties;
	}

	public static Properties invoke(File file) {
		Properties properties = new Properties();
		try {
			System.out.println("hello");
			properties.load(new FileInputStream(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return properties;
	}
}
