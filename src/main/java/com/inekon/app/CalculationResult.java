package com.inekon.app;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inekon.app.ShoppingCartBean.VersionBean;

/**
 * Result of the compuation. We serve the ID of the version and the actual
 * result data. The structure of this bean actually defines the resulting JSON
 * record.
 * 
 * @author cleue
 */
public class CalculationResult {

	private Map<?, ?> data;

	private final VersionBean result;

	public CalculationResult(final VersionBean aResult) {
		result = aResult;
	}

	public String getCartId() {
		return result.getParent().getId();
	}

	public Map<?, ?> getData() throws JsonParseException, JsonMappingException, IOException {
		// lazily construct the data map
		if (data == null) {
			// serve back the result
			final ObjectMapper mapper = new ObjectMapper();
			data = mapper.readValue(result.getResultFile(), Map.class);
		}
		// ok
		return data;
	}

	public String getVersionId() {
		return result.getId();
	}
}
