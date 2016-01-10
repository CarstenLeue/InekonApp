package com.inekon.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/excel")
public class ExcelService {

	/** class name for the logger */
	private static final String LOG_CLASS = ExcelService.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	@GET
	@Produces("application/json")
	public Response doCalculation() {
		// logging support
		final String LOG_METHOD = "doCalculation()";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// the resulting JSON
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("Carsten", "Leue");
		// build the response
		final Response resp = Response.status(200).entity(jsonObject.toString()).build();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// the data
		return resp;
	}
}
