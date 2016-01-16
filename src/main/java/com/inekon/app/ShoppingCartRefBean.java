package com.inekon.app;

public class ShoppingCartRefBean {

	private final ShoppingCartBean parent;
	
	public ShoppingCartRefBean(final ShoppingCartBean aParent) {
		parent = aParent;
	}

	public String getId() {
		return parent.getId();
	}

	public final String getTitle() {
		return parent.getTitle();
	}
	
	
}
