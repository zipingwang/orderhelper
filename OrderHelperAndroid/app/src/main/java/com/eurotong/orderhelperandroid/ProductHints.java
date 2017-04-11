package com.eurotong.orderhelperandroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProductHints {

	List<ProductKeywords> ProductKeywordsList;
	
	static ProductHints _current;
	public static ProductHints Current()
	{
		if(_current==null)
		{
			_current=new ProductHints();
			_current.BuildProductKeywordsList();
		}
		return _current;
	}
	
	public Product[] GetProductsByKeywords(String keywords)
	{
		ArrayList<Product> productList=new ArrayList<Product>();
		for(ProductKeywords pk:ProductKeywordsList)
		{
			if(pk.ContainsKey(keywords))
			{
				for(Product p:pk.ProductList)
				{
					if(!productList.contains(p))
					{
						productList.add(p);
					}
				}				
			}
		}
		Product[] list=new Product[productList.size()];
		productList.toArray(list);
		Arrays.sort(list);
		return list;
	}
	
	private void BuildProductKeywordsList()	
	{
		try {
			ProductKeywordsList=new ArrayList<ProductKeywords>();		
			List<String> keys=new ArrayList<String>();
			//List<ProductKeywords> pkList=new  ArrayList<ProductKeywords>();
			for(ProductGroup pg:Product.MenuGroups())
			{
				for(Product menu:pg.Menus)
				{
					String[] items=Common.SplitString(menu.MenuKeywords, Define.MENU_KEYWORDS_SEPEROTOR);
					for(String itemTemp:items)
					{
						String item=itemTemp.trim().toLowerCase();
						if(!item.equals(""))
						{
							if(!keys.contains(item))
							{
								keys.add(item);
								ProductKeywords pk=new ProductKeywords();
								pk.Key=item;
								pk.ProductList=new ArrayList<Product>();
								pk.ProductList.add(menu);
								ProductKeywordsList.add(pk);
								//pkList.add(pk);
							}
							else
							{
								for(ProductKeywords pk:ProductKeywordsList)
								{
									if(pk.ContainsKey(item))
									{
										if(!pk.ProductList.contains(menu))
										{
											pk.ProductList.add(menu);
											break;
										}
									}
								}
							
							}
						}
					}
				}//for(Product menu:pg.Menus)
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 public static void Reload()
     {
     	_current=null;       
     }
}
