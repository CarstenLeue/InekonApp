package com.inekon.app;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShoppingCartListBean {

	/** class name for the logger */
	private static final String LOG_CLASS = ShoppingCartListBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	private final File rootFolder;

	private final File templateFile;

	public ShoppingCartListBean(final File aTemplateFile) {
		this(aTemplateFile, new File(new File(System.getProperty("java.io.tmpdir")), "inekon"));
	}

	public ShoppingCartListBean(final File aTemplateFile, final File aRootFolder) {
		// logging support
		final String LOG_METHOD = "ShoppingCartListBean(aTemplateFile, aRootFolder)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aTemplateFile, aRootFolder });
		}
		// init
		rootFolder = aRootFolder;
		templateFile = aTemplateFile;
		// make sure the root exists
		rootFolder.mkdirs();
		assert templateFile.exists();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	public ShoppingCartBean createNewShoppingCart() throws IOException {
		// logging support
		final String LOG_METHOD = "createNewShoppingCart()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// use the current timestamp
		final long time = System.currentTimeMillis();
		final String folderName = "cart" + time;
		// new directory
		final File newFolder = new File(rootFolder, folderName);
		newFolder.mkdirs();
		// the bean
		final ShoppingCartBean bean = new ShoppingCartBean(newFolder);
		// template file
		final File srcFile = templateFile, dstFile = bean.getTemplateFile();
		// copy our excel template
		Files.copy(srcFile.toPath(), dstFile.toPath(), REPLACE_EXISTING);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bean);
		}
		// returns the new cart
		return bean;
	}

	public ShoppingCartBean getShoppingCartBean(final String aId) {
		// logging support
		final String LOG_METHOD = "getShoppingCartBean(aId)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aId);
		}
		// the folder
		final File cartFolder = new File(rootFolder, aId);
		final ShoppingCartBean result = new ShoppingCartBean(cartFolder);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	public List<ShoppingCartBean> getShoppingCarts() {
		// logging support
		final String LOG_METHOD = "getShoppingCarts()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the versions
		final List<ShoppingCartBean> carts = new ArrayList<ShoppingCartBean>();
		// list the versions
		rootFolder.mkdirs();
		for (File file : rootFolder.listFiles()) {
			// check the name
			if (file.isDirectory()) {
				carts.add(new ShoppingCartBean(file));
			}
		}
		// order
		Collections.sort(carts);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, carts);
		}
		// ok
		return carts;
	}

}
