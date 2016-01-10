package com.inekon.app;

import java.io.Closeable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.management.timer.Timer;

import com.sun.jna.platform.win32.COM.util.Factory;
import com.sun.jna.platform.win32.COM.util.office.excel.ComExcel_Application;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIApplication;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorkbook;
import com.sun.jna.platform.win32.COM.util.office.excel.ComIWorksheet;

/**
 * Representation of all data operations on one excel sheet
 * 
 * @author cleue
 *
 */
public class ExcelBean implements Closeable {

	/**
	 * Matches an Excel cell pattern
	 */
	private static final Pattern CELL_PATTERN = Pattern.compile("[A-Z]+[1-9][0-9]*");

	/** class name for the logger */
	private static final String LOG_CLASS = ExcelBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * Maximim time to wait for thread shutdown
	 */
	private static final long SHUTDOWN_TIMEOUT = 10 * Timer.ONE_SECOND;

	/**
	 * Checks if a name matches a CELL name
	 * 
	 * @param aName
	 *            the name
	 * @return <code>true</code> if this is a cell name, else <code>false</code>
	 */
	public static final boolean isCellName(final String aName) {
		return CELL_PATTERN.matcher(aName).matches();
	}

	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	private ComExcel_Application excelApplication;

	private Factory factory;

	/**
	 * the currently open file
	 */
	private File file;

	private ComIApplication msExcel;

	private ComIWorkbook openWorkbook;

	public ExcelBean() {
	}

	public void close() {
		// logging support
		final String LOG_METHOD = "close()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// close down
		internalClose();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	public boolean closeFile() {
		// logging support
		final String LOG_METHOD = "closeFile()";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// just dispatch
		final boolean bResult = closeWorkbook();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}

	private final boolean closeWorkbook() {
		// logging support
		final String LOG_METHOD = "closeWorkbook()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// result
		final boolean bResult;
		// close an open workbook
		if (openWorkbook != null) {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Closing open workbook ...");
			}
			// close, and save changes
			openWorkbook.Close(true);
			openWorkbook = null;
			// we closed the workbook
			bResult = true;
		} else {
			// nothing to close
			bResult = false;
		}
		// reset the file reference
		file = null;
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}

	/**
	 * Retrieves the final data set
	 * 
	 * @param aCells
	 *            the cells
	 */
	public Map<String, String> getData(final Iterable<String> aCells) {
		// logging support
		final String LOG_METHOD = "getData(aCells)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aCells);
		}
		// access the current worksheet
		final ComIWorksheet sheet = getExcel().getActiveSheet();
		// the result
		final Map<String, String> result = new HashMap<String, String>();
		for (String cellName : aCells) {
			// validate the input
			if (isCellName(cellName)) {
				// set the value
				result.put(cellName, sheet.getRange(cellName).getText());
			} else {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"The name [{0}] is not a valid cell name in Excel ...", cellName);
				}
			}
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	private final ComIApplication getExcel() {
		// logging support
		final String LOG_METHOD = "getExcel()";
		// lazily access the interface
		if (msExcel == null) {
			msExcel = getExcelApplication().queryInterface(ComIApplication.class);
			msExcel.setVisible(true);
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Accessing the Excel application object, version [{0}] ...", msExcel.getVersion());
			}
		}
		// ok
		return msExcel;
	}

	private final ComExcel_Application getExcelApplication() {
		// logging support
		final String LOG_METHOD = "getExcelApplication()";
		// lazily create the app
		if (excelApplication == null) {
			excelApplication = getFactory().createObject(ComExcel_Application.class);
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Created excel app ...");
			}

		}
		// the app
		return excelApplication;
	}

	private final Factory getFactory() {
		// logging support
		final String LOG_METHOD = "getFactory()";
		// lazily construct the factory
		if (factory == null) {
			factory = new Factory();
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Constructing factory ...");
			}

		}
		// the factory
		return factory;
	}

	private final void internalClose() {
		// logging support
		final String LOG_METHOD = "internalClose()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// close the workbook
		closeWorkbook();
		// quit the application
		if (excelApplication != null) {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Quitting Excel ...");
			}
			// quit
			getExcel().Quit();
		}
		// reset all data
		excelApplication = null;
		msExcel = null;
		// close down the factory if any
		if (factory != null) {
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Disposing objects ...");
			}
			/**
			 * Dispose all objects
			 */
			factory.disposeAll();
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Shutting down thread ...");
			}
			/**
			 * make sure to close down the COM thread
			 */
			factory.getComThread().terminate(SHUTDOWN_TIMEOUT);
			// done
			factory = null;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	public boolean openFile(final File aFile) {
		// logging support
		final String LOG_METHOD = "openFile(aFile)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aFile);
		}
		// the result
		final boolean bResult;
		// close the current workbook
		closeWorkbook();
		// sanity check
		assert openWorkbook == null;
		assert file == null;
		// opens a new workbook
		openWorkbook = getExcel().getWorkbooks().Open(aFile.getAbsolutePath());
		if (openWorkbook != null) {
			bResult = true;
			file = aFile;
		} else {
			bResult = false;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, bResult);
		}
		// ok
		return bResult;
	}

	/**
	 * Initializes the excel cells via a simple mapping between cell name and
	 * cell value
	 * 
	 * @param aCells
	 */
	public void setData(final Iterable<Map.Entry<String, String>> aCells) {
		// logging support
		final String LOG_METHOD = "setData(aCells)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aCells);
		}
		// access the current worksheet
		final ComIWorksheet sheet = getExcel().getActiveSheet();
		// iterate over the tuples
		for (Map.Entry<String, String> tuple : aCells) {
			// check for the element
			final String cellName = tuple.getKey();
			if (isCellName(cellName)) {
				// set the value
				sheet.getRange(cellName).setValue(tuple.getValue());
			} else {
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"The name [{0}] is not a valid cell name in Excel ...", cellName);
				}
			}
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Initializes the excel cells via a simple mapping between cell name and
	 * cell value
	 * 
	 * @param aCells
	 */
	public void setData(final Map<String, String> aCells) {
		// logging support
		final String LOG_METHOD = "setData(aCells)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aCells);
		}
		// iterate over the tuples
		setData(aCells.entrySet());
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}
}
