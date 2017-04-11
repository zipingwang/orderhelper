package com.eurotong.orderhelperandroid;
import com.eurotong.orderhelperandroid.Common;



//public class Order  {
	public class Order  implements Comparable<Order> {
	 
     public String OrderMenuNr;    
     
     public double OrderCount;
     
     
     public double OrderPrice;
     
     
     public double OrderTax;
     
     
     public int OrderDisplayOrder;
     
     
     public String OrderMenuName;

     
     public String OrderMenuNameZH;

     public double OrderSubTotal()
     { 
    	 return OrderCount * OrderPrice; 
     }
     
     public double OrderTaxTotal() { return (OrderSubTotal() * OrderTax)/100; } 

     //
     public String OrderPriceString()
     {       
         return Common.FormatDouble(OrderPrice);        
     }

     public String OrderSubTotalString()
     {         
             return Common.FormatDouble(OrderSubTotal());         
     } 
	
     private Table _table;
     //private Order() { }

     public Order(Table table)
     {
         _table = table;
     }

     public  int KitchenGroup;
     
	@Override
	public int compareTo(Order order2) {
		return OrderDisplayOrder-order2.OrderDisplayOrder;
	}
	
	@Override
	public String toString()
	{
		String result="";
		result=String.format("%s@%s@%s@%s@%s@%s@%s", _table.TableNr, OrderMenuNr, OrderCount, OrderPrice, OrderTax, OrderDisplayOrder, KitchenGroup);
		
		return result;
	}
	
	
	public static Order parse(String orderString, Table table)
	{
		  String[] items =Common.SplitString(orderString, Define.MENU_SEPERATOR);
          Order order = new Order(table);
          //order.OrderTableNr = items[0];
          order.OrderMenuNr = items[1];
          order.OrderCount = Common.GetDoubleResult(items[2], 0);
          order.OrderPrice = Common.GetDoubleResult(items[3], 0);
          order.OrderTax = Common.GetDoubleResult(items[4], 0);
          order.OrderDisplayOrder = Integer.parseInt(items[5].toString());
            try {
                order.KitchenGroup = Integer.parseInt(items[6].toString());
            }
                catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

          //order.OrderMenuName = items[6];
          //order.OrderMenuNameZH = items[7];
          Product menu=null;
			try {
				menu = Product.GetMenuByNumber(order.OrderMenuNr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          if (menu != null)
          {
              order.OrderMenuName = menu.MenuName;
              order.OrderMenuNameZH = menu.MenuNameZH;
          }
          return order;
	}
	/// <summary>
    /// get value for printing. not use reflection. easy to reuse to android
    /// </summary>
    /// <param name="propertyName"></param>
    /// <returns></returns>
    public String GetPropertyStringValue(String propertyName)
    {
    	String value = "Not Found";
        String item = propertyName.toLowerCase();
        if(item.equals("ordermenunr"))
        {
            value = OrderMenuNr;
        }
        else if (item.equals("ordercount"))
        {
            value = Integer.toString((int)OrderCount);
        }            
        else if(item.equals("orderprice"))
        {              
             value = Common.FormatDouble(OrderPrice);                    
        }
        else if (item.equals("ordertax"))
        {
            value = Double.toString(OrderTax);
        }
        else if (item.equals("ordermenuname"))
        {
            value = OrderMenuName;
        }
        else if (item.equals("ordermenunamezh"))
        {
            value = OrderMenuNameZH;
        }
        else if (item.equals("ordersubtotal"))
        {
            value = Common.FormatDouble(OrderSubTotal());
        }           
       
        return value;
    }
}
