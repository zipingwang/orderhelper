package com.eurotong.orderhelperandroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import android.app.Application;
import android.util.Log;

public class Table  implements Comparable<Table>{
	 //public Table() { 
       
      //}
      public Table(String tableNr, boolean isTakeAway)
      {    	  
          TableNr = tableNr;  
          DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
          DateFormat hourFormat = new SimpleDateFormat("hhmmss");
      	  Date date = new Date();
          TableOrderTimeDay = dateFormat.format(date);
          TablePrintTimeDay = dateFormat.format(date);
          TableOrderTimeHour = hourFormat.format(date);
          TablePrintTimeHour = hourFormat.format(date);
          NextFreeChildID = 1;
          IsTakeAway=isTakeAway;
      }
  
    public String TableNr="";

  
    public String TableOrderTimeDay="";

  
    public String TableOrderTimeHour="";

  
    public String TablePrintTimeDay="";

  
    public String TablePrintTimeHour="";

  
    public String TablePersonCount="";

  
    public String TableOperator="";

  
    public String TableSequenceNumber="";

    //PriorityQueue<Order> _tableOrders;
    ArrayList<Order> _tableOrders;
    
    
   
    public Table ParentTable;
    
    public int NextFreeChildID;    

    public List<Table> ChildTalbes=new ArrayList<Table>();
    
    public Boolean IsChildTable=false;

    public boolean IsTakeAway=false;
    /*
    public PriorityQueue<Order> TableOrders()
    {
        
        if (_tableOrders == null)
        {
             _tableOrders = new PriorityQueue<Order>(10, new OrderComparator());  
        }

        return _tableOrders;
    }
	*/
    public ArrayList<Order> TableOrders()
    {
        
        if (_tableOrders == null)
        {
             _tableOrders = new ArrayList<Order>();  
        }

        return _tableOrders;
    }
    
    public boolean HasOrder()
    {
       
        if (TableOrders() == null)
        {
            return false;
        }
        else
        {
            return TableOrders().size() > 0 ? true : false;
        }        
    }

    //end amount customer must to pay
    public double Total()
    {
        /*
    	double value=0;
    	for(Order order:TableOrders())
    	{
    		value+=	order.OrderSubTotal();
    	}
    	return value;
    	*/
        if(IsTakeAway &&  BusinessInfo.Current().getHasTakeProcent())
        {
            double totalTakeAway=TotalBeforeDiscount() * (100- BusinessInfo.Current().getTakeAwayPercent())/100;
            return  totalTakeAway;
        }
        else
            return TotalBeforeDiscount();
    }

    //amount before discount. e.g. if takeaway has 10 procent, this total is the amount before abstruct of 10 procent
    public double TotalBeforeDiscount() {
        double value=0;
        for(Order order:TableOrders())
        {
            value+=	order.OrderSubTotal();
        }
        return value;
    }

    public String TotalString()
    {
        if(IsTakeAway &&  BusinessInfo.Current().getHasTakeProcent())
        {
            return Common.FormatDouble(TotalBeforeDiscount()) + " - " + BusinessInfo.Current().getTakeAwayPercent() + "% = " + Common.FormatDouble(Total());
        }
        else
            return Common.FormatDouble(Total());
    }


    public double Tax1Total()
    {                
            return CalculateSubTaxTotal(BusinessInfo.Current().getTax1());
    }

    public String Tax1TotalString()
    {
            return Common.FormatDouble(Tax1Total());
    }
    
    public double Tax2Total()
    {
    	 return CalculateSubTaxTotal(BusinessInfo.Current().getTax2());
    }
    public String Tax2TotalString()
    {
    	 return Common.FormatDouble(Tax2Total());
    }

    public double Tax3Total()
    {
    	 return CalculateSubTaxTotal(BusinessInfo.Current().getTax3());
    }
    public String Tax3TotalString()
    {
    	 return Common.FormatDouble(Tax3Total());
    }

    public double Tax4Total()
    {
    	 return CalculateSubTaxTotal(BusinessInfo.Current().getTax4());
    }
    
    public String Tax4TotalString()
    {
    	 return Common.FormatDouble(Tax4Total());
    }
    
    public String ToString()
    {
    	 String childTableFlag = "0";
        String isTakeAwayTemp = "0";
         if (IsChildTable || ParentTable!=null)
         {
             childTableFlag = "1";
         }
         if(IsTakeAway)
             isTakeAwayTemp="1";

        return String.format("%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s@%s", "TBL", TableNr, TableOrderTimeDay, TablePrintTimeDay, TableOrderTimeHour, TablePrintTimeHour, TablePersonCount, TableSequenceNumber, TableOperator, childTableFlag, NextFreeChildID, isTakeAwayTemp);
    }

    static public Table Parse(String tableString)
    {
        String[] items =Common.SplitString(tableString, Define.MENU_SEPERATOR);
        Table table = new Table(items[1], false);
        try
        {
            //TBL  items[0];
            table.TableNr = items[1];
            table.TableOrderTimeDay = items[2];
            table.TablePrintTimeDay = items[3];
            table.TableOrderTimeHour = items[4];
            table.TablePrintTimeHour = items[5];
            table.TablePersonCount = items[6];
            table.TableSequenceNumber = items[7];
            table.TableOperator = items[8];
            if (items[9].equals("0"))
            {
                table.IsChildTable = false;
            }
            else
            {
                table.IsChildTable = true;
            }
            table.NextFreeChildID = Integer.parseInt(items[10]);
            if (items[11].equals("1"))
            {
                table.IsTakeAway = true;
            }
            else
            {
                table.IsTakeAway = false;
            }
        }
        catch (Exception e)
        {
        	String msg="parse err;or in talbe." +  e.toString();
        	Log.e(Define.APP_CATALOG, msg);
        	Common.GetToastLong(msg).show();       
        }

        return table;
    }
    
    public void AddOrder(Order order)
    {  
        TableOrders().add(order);
    }
    
    public Order GetOrderByMenuNr(String menuNr)
    {
    	Order theOrder=null;
    	for(Order order:TableOrders())
    	{
    		if(order.OrderMenuNr.equals(menuNr))
    		{
    			theOrder=order;
    			break;
    		}
    	}
    	return theOrder;
    }
    
    public void AddOrder(String menuNr, double count) throws Exception
    {
        DoAddRemoveOrder(menuNr, count);
    }
    
    private void DoAddRemoveOrder(String menuNr, double count) throws Exception
    {
    	Order theOrder=null;
    	for(Order order:TableOrders())
    	{
    	   if(order.OrderMenuNr.equals(menuNr))
    	   {
    	       theOrder=order;
    	       break;
    	   }
    	}
        
        if (theOrder!=null)
        {
            theOrder.OrderCount = theOrder.OrderCount + count;
            if (theOrder.OrderCount <= 0)
            {
                TableOrders().remove(theOrder);
            }
        }
        else
        {
            //menunr not exists
            if (count > 0)
            {
            	Product menu = Product.GetMenuByNumber(menuNr);
            	if(menu!=null)
            	{
	                Order order = new Order(this);
	                order.OrderCount = count;
	                order.OrderDisplayOrder = menu.MenuDisplayOrder;
	                order.OrderMenuName = menu.MenuName;
	                order.OrderMenuNameZH = menu.MenuNameZH;
	                order.OrderMenuNr = menu.MenuNr;
                    if(this.IsTakeAway && !BusinessInfo.Current().getHasTakeProcent()) {
                        order.OrderPrice=menu.MenuPriceTakeAway;
                    }
                    else {
                        order.OrderPrice = menu.MenuPrice;
                    }
	                order.OrderTax = menu.MenuTax;
                    order.KitchenGroup=menu.MenuKitchenGroup;
	                this.AddOrder(order);
            	}
            }
        }
       // OnPropertyChanged("TableOrders");
       // OnPropertyChanged("Total");
       // OnPropertyChanged("TotalString"); 
    }
    public String GetPropertyStringValue(String propertyName, String format)
    {
        String value = "Not Found";
        try
        {
        //android(java 5, 6 compatibel) not support switch
        String item = propertyName.toLowerCase();
        if (item.equals("tablenr"))
        {
            value = TableNr;
        }
        else if (item.equals("total"))
        {
            value = TotalString();
        }
        else if (item.equals("tableordertimeday"))
        {                
            value = GetDayString(format, TableOrderTimeDay);
        }
        else if (item.equals("tableordertimehour"))
        {
            value = GetHourString(format, TableOrderTimeHour);
        }
        else if (item.equals("tableprinttimeday"))
        {
            value = GetDayString(format, TablePrintTimeDay);
        }
        else if (item.equals("tableprinttimehour"))
        {
            value = GetHourString(format, TablePrintTimeHour);
        }
        else if (item.equals("tablepersoncount"))
        {
            value = TablePersonCount;
        }
        else if (item.equals("tableoperator"))
        {
            value = TableOperator;
        }
        else if (item.equals("tablesequencenumber"))
        {
            value = TableSequenceNumber;
        }
        else if (item.equals("tax1total"))
        {
            value = Tax1TotalString();
        }
        else if (item.equals("tax2total"))
        {
            value = Tax2TotalString();
        }
        else if (item.equals("tax3total"))
        {
            value = Tax3TotalString();
        }
        else if (item.equals("tax4total"))
        {
            value = Tax4TotalString();
        }
        }catch(Exception e)
        {
        	Log.e(Define.APP_CATALOG, "error in GetPropertyStringValue");
        	Log.e(Define.APP_CATALOG, e.toString());       
        }
        
        return value;
    }

    private String GetDayString(String format, String inputday)
    {           
        String s = format;
        try
        {
        if(inputday!=null)
        {
	        String day = inputday;
	        //if user input is 6 long. then make s 8 long. e.g 121126 -> nn121126
	        //if uset input is 4 long. then make s 8 long. e.g 1126->nnnn1126
	        if (inputday.length() == 6)
	            day = "nn" + inputday;
	        else if (inputday.length() == 4)
	            day = "nnnn" + inputday;
	        
	        if (s.contains(Define.DOUBLE_POINT_REPLACER) && inputday.length() >= 2)
	        {
	            s = s.replace(Define.DOUBLE_POINT_REPLACER, ":");
	        }
	
	        if (s.contains("yyyy") && day.length() >= 4)
	        {
	            s = s.replace("yyyy", day.substring(0, 4));
	        }
	        if (s.contains("yy") && day.length() >= 4)
	        {
	            s = s.replace("yy", day.substring(2, 4));
	        }
	        if (s.contains("MM") && day.length() >= 6)
	        {
	            s = s.replace("MM", day.substring(4, 6));
	        }
	        if (s.contains("dd") && day.length() >= 8)
	        {
	            s = s.replace("dd", day.substring(6, 8));
	        }
        }
        }catch(Exception e)
        {
        	Log.e(Define.APP_CATALOG, "error in GetDayString");
        	Log.e(Define.APP_CATALOG, e.toString());       
        }
       
        return s;
    }

    private String GetHourString(String format, String inputhour)
    {
    	String s = format;
    	try
    	{
    	if(inputhour!=null)
    	{
	        String hour = inputhour;
	        if (inputhour.length() == 2)
	        {
	            hour = inputhour + "nnnn";
	        }
	        else if (inputhour.length() == 4)
	        {
	            hour = inputhour + "nn";
	        }
	
	        if (s.contains(Define.DOUBLE_POINT_REPLACER) && inputhour.length() >= 2)
	        {
	            s = s.replace(Define.DOUBLE_POINT_REPLACER, ":");
	        }
	        if (s.contains("hh") && inputhour.length() >= 2)
	        {
	            s = s.replace("hh", inputhour.substring(0, 2));
	        }
	        if (s.contains("mm") && inputhour.length() >= 4)
	        {
	            s = s.replace("mm", inputhour.substring(2, 4));
	        }
	        if (s.contains("ss") && inputhour.length() >= 6)
	        {
	            s = s.replace("ss", inputhour.substring(4, 6));
	        }
    	}
    	}catch(Exception e)
        {
        	Log.e(Define.APP_CATALOG, "error in GetHourString");
        	Log.e(Define.APP_CATALOG, e.toString());       
        }
        return s;
    }
    
    public Table CreateChildTable()
    {
        Table table = new Table(TableNr + "-" + NextFreeChildID, false);
        table.ParentTable = this;

        ChildTalbes.add(table);
        NextFreeChildID++;

        return table;
    }
    
    private double CalculateSubTaxTotal(double taxRate)
    {
        double tax = 0;
        //donnot use this
        //double tax=TableOrders.Where(order => order.OrderTax == BusinessInfo.Current.Tax1).Sum(order => order.OrderTaxTotal);

        if (taxRate > 0)
        {
            for (Order order: _tableOrders)
            {
                if (order.OrderTax == taxRate)
                {
                    tax = tax + order.OrderTaxTotal();
                }
            }
        }
        return tax;
    }
    
	@Override
	public int compareTo(Table anotherTable) {
		Table b=anotherTable;
        String compareA=GetTableNrForCompare(this);
        String compareB = GetTableNrForCompare(b);
        
        return compareA.compareTo(compareB);        
	}
	
	private String GetTableNrForCompare(Table table)
    {
        String tableNrForCompare = table.TableNr;
       
        if (tableNrForCompare.contains("-"))
        {
            String[] items =Common.SplitString(tableNrForCompare, "-");
            String temp = items[1];
            if (temp.length() == 1)
            {
                temp = "0" + temp;
            }
            tableNrForCompare = items[0] + "-" + temp;
        }
        else
        {
            tableNrForCompare = table.TableNr + "-00";
        } 
      
        String prefix = "0000000000";
        int len = prefix.length();
        tableNrForCompare = (prefix.substring(0, len - tableNrForCompare.length()) + tableNrForCompare).substring(0, len);
        return tableNrForCompare;
    }

    public int  GetKitchenGroupCount()
    {
        int firstGroup=0;
        int secondGroup=0;
        int thirdGroup=0;
       for (Order order:TableOrders())
       {
           if(order.KitchenGroup==1)
           {
               firstGroup=1;
           }
           else if(order.KitchenGroup==2)
           {
               secondGroup=1;
           }
           else if(order.KitchenGroup==3)
           {
               thirdGroup=1;
           }
       }
        return  firstGroup + secondGroup + thirdGroup;
    }

    public  boolean HasFirstGroup()
    {
       return  HasGroup(1);
    }


    public boolean HasSecondGroup()
    {
      return  HasGroup(2);
    }

    public  boolean HasThirdGroup()
    {
       return  HasGroup(3);
    }

    public boolean HasGroup(int group)
    {
        for (Order order:TableOrders())
        {
            if(order.KitchenGroup==group)
            {
                return true;
            }
        }
        return false;
    }

    public boolean HasNextGroup(int group)
    {
        if(group==1)
            return HasSecondGroup() || HasThirdGroup();
        else if(group==2)
            return HasThirdGroup();

        return  false;
    }
}
