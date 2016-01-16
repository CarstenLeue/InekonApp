package com.inekon.app;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartListResultBean {

	private final ShoppingCartListBean parent;
	
	public ShoppingCartListResultBean(ShoppingCartListBean aParent) {
		parent = aParent;
	}
	
	private List<ShoppingCartRefBean> list;
	
	
	public List<ShoppingCartRefBean> getShoppingCarts() {
		if (list == null) {
			list = new ArrayList<ShoppingCartRefBean>();
			for (ShoppingCartBean bean : parent.getShoppingCarts()) {
				list.add(new ShoppingCartRefBean(bean));
			}
		}
		return list;
	}
}
