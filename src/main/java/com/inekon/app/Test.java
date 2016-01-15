package com.inekon.app;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.inekon.app.ShoppingCartBean.VersionBean;
import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorkbook;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;

public class Test {

	public static void _main(String[] args) throws InterruptedException, IOException {

		final Map<String, String> input = new HashMap<String, String>();
		input.put("left", "10");
		input.put("right", "20");

		final File f = new File("dirk.xls");

		final ShoppingCartListBean list = new ShoppingCartListBean(f);

		final ShoppingCartBean cart = list.createNewShoppingCart();
		final CalculationBean calc = new CalculationBean(cart);

		final VersionBean result = calc.doCalculation(input);
		
		System.out.println(result.getResultFile());
	}

}
