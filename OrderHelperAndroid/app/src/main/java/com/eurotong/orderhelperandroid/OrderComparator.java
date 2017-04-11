package com.eurotong.orderhelperandroid;

import java.util.Comparator;

public class OrderComparator implements Comparator<Order> {

	@Override
	public int compare(Order lhs, Order rhs) {
		return lhs.OrderDisplayOrder-rhs.OrderDisplayOrder;
	}

}
