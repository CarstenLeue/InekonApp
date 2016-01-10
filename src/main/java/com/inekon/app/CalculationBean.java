package com.inekon.app;

import static com.inekon.app.ShoppingCartBean.JSON_CHARTSET;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class CalculationBean {

	private final ShoppingCartBean cart;

	public CalculationBean(final ShoppingCartBean aCart) {
		cart = aCart;
	}

	/** class name for the logger */
	private static final String LOG_CLASS = CalculationBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	private final Map<String, Double> getResultFromExcelOutput(final Map<String, String> aData) {
		// logging support
		final String LOG_METHOD = "getResultFromExcelOutput(aData)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aData);
		}
		// excel map
		final Map<String, Double> result = new HashMap<String, Double>();
		result.put("result", Double.parseDouble(aData.get("B4")));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	private final Map<String, String> getExcelInputFromParameters(final Map<String, String> aData) {
		// logging support
		final String LOG_METHOD = "getExcelInputFromParameters(aData)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aData);
		}
		// excel map
		final Map<String, String> result = new HashMap<String, String>();
		// add
		result.put("B1", aData.get("left"));
		result.put("B2", aData.get("right"));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	private final Iterable<String> getExcelOutput() {
		final List<String> result = new ArrayList<String>();
		result.add("B4");
		// ok
		return result;
	}

	private final JSONObject resultToJson(final Map<String, Double> aResult) {
		// logging support
		final String LOG_METHOD = "resultToJson(aResult)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aResult);
		}
		// construct the object
		final JSONObject obj = new JSONObject();
		for (Map.Entry<String, Double> e : aResult.entrySet()) {
			obj.put(e.getKey(), e.getValue());
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// ok
		return obj;
	}

	private final void writeJson(final File aDstFile, final JSONObject aObject) throws IOException {
		// logging support
		final String LOG_METHOD = "writeJson(aDstFile, aObject)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aDstFile, aObject });
		}
		// construct the stream
		final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aDstFile), JSON_CHARTSET));
		try {
			// write
			aObject.write(out);
		} finally {
			// done
			out.close();
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	public ShoppingCartBean.VersionBean doCalculation(final Map<String, String> aData) throws IOException {
		// logging support
		final String LOG_METHOD = "doCalculation(aData)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aData);
		}
		// construct a new version
		final ShoppingCartBean.VersionBean version = cart.createNewVersion();
		assert version != null;
		// fill in the data
		final ExcelBean excel = new ExcelBean();
		try {
			// check if we have a valid file
			if (excel.openFile(version.getExcelFile())) {
				// set the data
				excel.setData(getExcelInputFromParameters(aData));
				// write
				writeJson(version.getResultFile(),
						resultToJson(getResultFromExcelOutput(excel.getData(getExcelOutput()))));
			}
		} finally {
			// shutdown
			excel.close();
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, version);
		}
		// ok
		return version;
	}
}
