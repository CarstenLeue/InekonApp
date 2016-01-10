package com.inekon.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.sun.jna.platform.win32.COM.util.Factory;

@WebListener
public class Application implements ServletContextListener {
	
	/** class name for the logger */
	private static final String LOG_CLASS = Application.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);


	public void contextInitialized(ServletContextEvent sce) {
		// logging support
		final String LOG_METHOD = "contextInitialized(sce)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { sce });
		}
		// initialize the COM thread
		final Factory factory = new Factory();

		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	public void contextDestroyed(ServletContextEvent sce) {
		// logging support
		final String LOG_METHOD = "contextDestroyed(sce)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { sce });
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

}
