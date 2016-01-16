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

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CalculationBean {

	public static final String KEY_LEFT = "l";

	public static final String KEY_TARGET = "t";

	public static final String KEY_RIGHT = "r";

	/** class name for the logger */
	private static final String LOG_CLASS = CalculationBean.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	private final ShoppingCartBean cart;

	public CalculationBean(final ShoppingCartBean aCart) {
		cart = aCart;
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
				// update
				aData.put(KEY_TARGET, version.getFolder().getAbsolutePath());
				// set the data
				excel.setData(getExcelInputFromParameters(aData));
				// write
				writeJson(version.getResultFile(), getResultFromExcelOutput(excel.getData(getExcelOutput())));
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

	private final Map<String, String> getExcelInputFromParameters(final Map<String, String> aData) {
		// logging support
		final String LOG_METHOD = "getExcelInputFromParameters(aData)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aData);
		}
		// excel map
		final Map<String, String> result = new HashMap<String, String>();
		// add
		result.put("B1", aData.get(KEY_LEFT));
		result.put("B2", aData.get(KEY_RIGHT));

		result.put("K2", aData.get(KEY_TARGET));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	private final Iterable<String> getExcelOutput() {
		final List<String> result = new ArrayList<String>();
		for (int i = 0; i < 20; ++i) {
			result.add("B" + (7 + i));
		}
		// ok
		return result;
	}

	private final Map<String, Double[]> getResultFromExcelOutput(final Map<String, String> aData) {
		// logging support
		final String LOG_METHOD = "getResultFromExcelOutput(aData)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aData);
		}
		// excel map
		final Map<String, Double[]> result = new HashMap<String, Double[]>();
		final Double[] rows = new Double[20];
		for (int row = 0; row < 20; row++) {

			final String data = aData.get("B" + (7 + row));

			rows[row] = Double.parseDouble(data.replace(',', '.'));
		}
		result.put("result", rows);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	private final void writeJson(final File aDstFile, final Map<String, Double[]> aResult) throws IOException {
		// logging support
		final String LOG_METHOD = "writeJson(aDstFile, aObject)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aDstFile, aResult });
		}
		// construct the stream
		final Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aDstFile), JSON_CHARTSET));
		try {
			// write
			writeJson(out, aResult);
		} finally {
			// done
			out.close();
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}

	}

	private final void writeJson(final Writer aOut, final Map<String, Double[]> aResult)
			throws JsonGenerationException, JsonMappingException, IOException {
		// logging support
		final String LOG_METHOD = "writeJson(aResult)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, aResult);
		}
		// map
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(aOut, aResult);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}
}
