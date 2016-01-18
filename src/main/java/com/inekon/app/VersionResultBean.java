package com.inekon.app;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inekon.app.ShoppingCartBean.VersionBean;

public class VersionResultBean {

	private final VersionBean parent;

	public VersionResultBean(final VersionBean aParent) {
		parent = aParent;
	}

	public Map<?, ?> getData() throws JsonParseException, JsonMappingException, IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(parent.getResultFile(), Map.class);
	}
}
