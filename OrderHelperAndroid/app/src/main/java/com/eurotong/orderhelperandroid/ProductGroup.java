package com.eurotong.orderhelperandroid;

import java.util.ArrayList;
import java.util.List;

public class ProductGroup {
	  public String Name;
      public String NameShort;
      public String NameZh;
      public String NameZhShort;
      public List<Product> Menus=new ArrayList<Product>();
      public int KitchenGroup; //1, 2, 3, 4. e.g 1 is soep, 2, voorgerecht, 3 gerechten, if not provided it is 0

      @Override
      public String toString()
      {  
     	 Product menu=new Product();
     	 menu.MenuName=Name;
     	 menu.MenuNameZH=NameZh;
          menu.MenuKitchenGroup=KitchenGroup;

     	 return menu.toString();     	
      }
      
      public static ProductGroup Parse(String menuString)
      {
     	Product menu = Product.Parse(menuString);
        ProductGroup pg=new ProductGroup();
          pg.Name = menu.MenuName;
          pg.NameShort = menu.MenuName;
          pg.NameZh = menu.MenuNameZH;
          pg.NameZhShort = menu.MenuNameZH;
          pg.KitchenGroup=menu.MenuKitchenGroup;

        return pg;
      }
      
      public static ProductGroup GetGroupByName(String groupName)
      {
    	  try {
			for(ProductGroup pg:Product.MenuGroups())
			  {
				  if(pg.Name.equals(groupName))
				  {
					  return pg;
				  }
			  }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  return null;
      }
}
