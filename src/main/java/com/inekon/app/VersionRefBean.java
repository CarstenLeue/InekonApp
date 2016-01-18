package com.inekon.app;

import com.inekon.app.ShoppingCartBean.VersionBean;

public class VersionRefBean {

	private final VersionBean parent;

	public VersionRefBean(final VersionBean aParent) {
		parent = aParent;
	}

	public String getCartId() {
		return parent.getParent().getId();
	}

	public String getId() {
		return parent.getId();
	}

	public final String getTitle() {
		return parent.getTitle();
	}

}
