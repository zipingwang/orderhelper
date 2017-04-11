package com.eurotong.orderhelperandroid;

import java.util.List;

public class ProductKeywords {
	public String Key;
	public List<Product> ProductList;
	public Boolean ContainsKey(String inputkey)
	{
		Boolean flag=false;
		if(Key.equals(inputkey))
		{
			flag=true;
		}
		return flag;
	}
}
