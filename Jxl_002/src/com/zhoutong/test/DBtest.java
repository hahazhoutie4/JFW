package com.zhoutong.test;

import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class DBtest {
	/**
	 *@author hahazhoutie4
	 * @throws IOException 
	 * @throws BiffException 
	 *@website cnblogs.com/hahazhoutie4-blogs/
	 */
	public static void main(String[] args)   {
		Workbook workbook;
		try {
			workbook = Workbook.getWorkbook(new File("I:\\报业主结算书\\2、清单结算\\工程量汇总表格（做完删）\\1#土建工程量.xls"));
			Sheet sheet=workbook.getSheet(0);
			Cell[] cells=sheet.getColumn(2);
			System.out.println(cells[3].getContents().toString());
		} catch (BiffException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}