package com.eurotong.orderhelperandroid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import android.util.Log;

public class Product implements Comparable<Product> {
	//keep it simple, use public field
	 public String MenuNr="";
     public String MenuName=""; 
     public String MenuNameZH=""; 
     public double MenuPrice=0;
     public double MenuPriceTakeAway=0;
     public double MenuTax=0;
     public boolean MenusIsPrint=false;
     public Boolean MenuIsBarMenu=false;
     public Boolean MenuIsPrintKitchen=false;
     public int MenuDisplayOrder=0;
     public String MenuKeywords="";
     public int MenuKitchenGroup; //1, 2, 3, 4. e.g 1 is soep, 2, voorgerecht, 3 gerechten, if not provided it is 0

     /*
     static PriorityQueue<Product> _menuList;
     
     public static  PriorityQueue<Product> MenuList() throws Exception
     {
         
             if (_menuList == null || _menuList.size()==0)
             {
                 _menuList = LoadMenuFromStorageFile(Define.MENU_FILE_NAME); // new List<Menu>();
             }
             return _menuList;         
     }
     */

     private static List<ProductGroup> _menuGroups;
     public static List<ProductGroup> MenuGroups() throws Exception
     {       
    	 /*
         if (_menuList == null || _menuList.size() == 0)
         {
        	 _menuList=LoadMenuFromStorageFile(Define.MENU_FILE_NAME); // new List<Menu>();
         }
         */
    	  if (_menuGroups == null || _menuGroups.size() == 0)
          {
         	 LoadMenuFromStorageFile(Define.MENU_FILE_NAME); // new List<Menu>();
          }
         return _menuGroups;         
     }
     
     public static Product GetMenuByNumber(String menuNumber) throws Exception
     {
    	 for(ProductGroup pg:Product.MenuGroups())
    	 {
	    	 for(Product aProduct : pg.Menus){
	    		 if(aProduct.MenuNr.equals(menuNumber))
	    		 {
	    		     return aProduct;
	    		 }
	    	}
    	 }
    	 return null;
     }

     @Override
     public String toString()
     {
    	 String s="";
    	 StringBuilder sb=new StringBuilder();
    	 sb.append(MenuNr);
    	 sb.append(Define.MENU_SEPERATOR);
    	 sb.append(MenuName);
    	 sb.append(Define.MENU_SEPERATOR);
    	 sb.append(MenuNameZH);
    	 sb.append(Define.MENU_SEPERATOR);
    	 sb.append(MenuPrice);
    	 sb.append(Define.MENU_SEPERATOR);
         sb.append(MenuPriceTakeAway);
         sb.append(Define.MENU_SEPERATOR);
    	 sb.append(MenuTax);
    	 sb.append(Define.MENU_SEPERATOR);
         if(MenusIsPrint)
         {
             sb.append("1");
         }
         else
         {
             sb.append("");
         }
         sb.append(Define.MENU_SEPERATOR);
    	 if(MenuIsBarMenu)
    	 {
    		 sb.append("1");
    	 }
    	 else
    	 {
    		 sb.append("");
    	 }
    	 sb.append(Define.MENU_SEPERATOR);
    	 
    	 if(MenuIsPrintKitchen)
    	 {
    		 sb.append("1");
    	 }
    	 else
    	 {
    		 sb.append("");
    	 }
    	 sb.append(Define.MENU_SEPERATOR);
    	 
    	 sb.append(MenuKeywords);
         sb.append(Define.MENU_SEPERATOR);
    	 sb.append(MenuKitchenGroup);
    	
    	 return sb.toString();
     }
     
     public static Product Parse(String menuString)
     {
    	 Product menu = new Product();
         String[] items = Common.SplitString(menuString, Define.MENU_SEPERATOR);
         menu.MenuNr = items[0];
         menu.MenuName = items[1];
         menu.MenuNameZH = items[2];
         menu.MenuPrice = Common.GetDoubleResult(items[3], 0);
         menu.MenuPriceTakeAway = Common.GetDoubleResult(items[4], 0);
         menu.MenuTax = Common.GetDoubleResult(items[5], 0);
         if(items[6].toString().trim().equals("1"))
         {
             menu.MenusIsPrint=true;
         }
         else
         {
             menu.MenuIsBarMenu=false;
         }
         if(items[7].toString().trim().equals("1"))
         {
        	 menu.MenuIsBarMenu=true;
         }
         else
         {
        	 menu.MenuIsBarMenu=false;
         }
         //not working
         //menu.MenuIsBarMenu = items[5].toString().trim().equals("1") ? true : false;
         //menu.MenuIsPrintKitchen = items[6].toString().trim().equals("1") ? true : false;
         if(items[8].toString().trim().equals("1"))
         {
        	 menu.MenuIsPrintKitchen=true;
         }
         else
         {
        	 menu.MenuIsPrintKitchen=false;
         }
         menu.MenuKeywords=items[9];

         if(items.length<11 || items[10].isEmpty())
             menu.MenuKitchenGroup=0;
         else
            menu.MenuKitchenGroup=Integer.parseInt(items[10]);

         return menu;
     }

     //http://stackoverflow.com/questions/416266/sorted-collection-in-java
     //http://www.java2s.com/Code/JavaAPI/java.util/newPriorityQueueintinitialCapacity.htm
     //The difference with a List sorted using Collections.sort(...) is that this will maintain order at all times, and have good insertion performance by using a heap data structure, where inserting in a sorted ArrayList will be O(n) (i.e., using binary search and move).
     //public static PriorityQueue<Product> ParseMenuList(FileInputStream inputStream) throws Exception
     public static void ParseMenuList(FileInputStream inputStream) throws Exception
     {
    	 //PriorityQueue<Product> menuList = new PriorityQueue<Product>();
    	 try
    	 {
         BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode")); // "UTF-8"));  
         String line = null;
         line = sr.readLine();
         int iCount = 1;
         ProductGroup menugroup=new ProductGroup();
         _menuGroups = new ArrayList<ProductGroup>();
         while (line != null)
         {
        	 if (Common.IsDataLine(line))
             {
        		 Product menu = Product.Parse(line);
        		 if (menu.MenuNr.equals("") && (menu.MenuPrice== 0 ))
                 {
                     //it is menu group
                     menugroup = ProductGroup.Parse(line);
                     _menuGroups.add(menugroup);
                     /*
                     menugroup = new ProductGroup();
                     menugroup.Menus = new ArrayList<Product>();


                     menugroup.Name = menu.MenuName;
                     menugroup.NameShort = menu.MenuName;
                     menugroup.NameShort = menu.MenuNameZH;
                     menugroup.NameZhShort = menu.MenuNameZH;
                     menugroup.KitchenGroup=menu.MenuKitchenGroup;

                     _menuGroups.add(menugroup);
                     */
                 }
                 else
                 {
                     menu.MenuDisplayOrder = iCount;
                     //menuList.add(menu);
                     menugroup.Menus.add(menu);                       
                 }
             }
             line = sr.readLine();
             iCount++;
         }
         sr.close();
    	 }
    	 catch (Exception e) {
 			Log.e(Define.APP_CATALOG, e.toString());
 			e.printStackTrace();
 		}
        // return menuList;
     }

     //public static PriorityQueue<Product> LoadMenuFromStorageFile(String menuFile) throws Exception
     public static void LoadMenuFromStorageFile(String menuFile) throws Exception
     {
         if (!Common.ExistsInStorage(menuFile))
         {
        	 Common.GetToastLong(R.string.msg_menu_file_not_exists).show();
             //return new PriorityQueue<Product>(); // List<Product>();              
         }     
         else
         {
        	 ParseMenuList(Common.GetFileInputStreamFromStorage(menuFile));
         }
     }

     public static void Reload()
     {
     	//_menuList=null;    
    	 _menuGroups=null;
     	ProductHints.Reload();
     }
     
     public ProductGroup GetGroup()
     {
    	 try {
			for(ProductGroup pg:Product.MenuGroups())
			 {
				 for(Product p:pg.Menus)
				 {
					 if(p.MenuNr.equals(MenuNr))
					 {
						 return pg;					   
					 }
				 }
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 return null;
     }
     
    public String GetDefaultMenuKeywords()
    {
    	StringBuilder sb=new StringBuilder();
    	sb.append(MenuNr);
    	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
    	String[] items=MenuName.split(" ");
    	if(items.length==1)
    	{
    		sb.append(items[0].substring(0, 0));
        	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
        	sb.append(items[0].substring(0, 1));
        	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
    	}
    	if(items.length==2)
    	{
    		sb.append(items[0].substring(0, 0));
        	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
        	sb.append(items[0].substring(0, 1));
        	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
        	sb.append(items[0].substring(0, 0));
        	sb.append(items[1].substring(0, 0));
        	sb.append(Define.MENU_KEYWORDS_SEPEROTOR);
    	}
    	return sb.toString();
    }
    
	@Override
	public int compareTo(Product product2) {		
		return MenuDisplayOrder-product2.MenuDisplayOrder;	
	}
}
