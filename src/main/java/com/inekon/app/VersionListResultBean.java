package com.inekon.app;

import java.util.ArrayList;
import java.util.List;

import com.inekon.app.ShoppingCartBean.VersionBean;

public class VersionListResultBean {

	private List<VersionRefBean> list;

	private final ShoppingCartBean parent;

	public VersionListResultBean(ShoppingCartBean aParent) {
		parent = aParent;
	}

	public String getId() {
		return parent.getId();
	}

	public List<VersionRefBean> getVersions() {
		if (list == null) {
			list = new ArrayList<VersionRefBean>();
			for (VersionBean bean : parent.getVersions()) {
				list.add(new VersionRefBean(bean));
			}
		}
		return list;
	}
}
