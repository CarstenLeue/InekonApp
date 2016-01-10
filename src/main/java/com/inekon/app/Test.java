package com.inekon.app;

import java.io.File;
import java.io.IOException;

import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorkbook;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;

public class Test {

	public static void main(String[] args) throws InterruptedException, IOException {
				
	
		System.out.println(ApplicationBean.isCellName("B5"));
		
		System.exit(0);
		
		final Factory factory = new Factory();
		try {
		
			ComExcel_Application excelObject = factory.createObject(ComExcel_Application.class);
			ComIApplication msExcel = excelObject.queryInterface(ComIApplication.class);
	
			System.out.println("MSExcel version: " + msExcel.getVersion());
			msExcel.setVisible(false);
			
			final File f = new File("dirk.xls");
			
			ComIWorkbook wb = msExcel.getWorkbooks().Open(f.getAbsolutePath());
			
			ComIWorksheet ws = msExcel.getActiveSheet();
			
			ws.getRange("B1").setValue("5");
			ws.getRange("B2").setValue("6");
			
			System.out.println(ws.getRange("B4").getText());
						
			// close and save the active sheet
		    wb.Close(true);
			
			msExcel.Quit();
			msExcel = null;
		} finally {
			factory.disposeAll();
			factory.getComThread().terminate(1000);
		}
	}

}
