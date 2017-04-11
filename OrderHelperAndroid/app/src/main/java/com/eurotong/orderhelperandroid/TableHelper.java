package com.eurotong.orderhelperandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

import android.R.integer;
import android.util.Log;

public class TableHelper {
	//static PriorityQueue<Table> _tableList;
	static ArrayList<Table> _tableList;
    
    /*
    public static PriorityQueue<Table> TableList()
    {       
        if (_tableList == null)
        {
            _tableList = new PriorityQueue<Table>();
        }      
       
        return _tableList;       
    }
    */
	
    public static ArrayList<Table> TableList()
    {       
        if (_tableList == null)
        {
            _tableList = new ArrayList<Table>();
        }      
       
        return _tableList;       
    }

    public static String GetNextTakeAwayTableNumber() {
        int nextTakeAwayTableNumber=0;
        int tempValue=0;
        for(Table table:TableList())
        {
                if(table.IsTakeAway) {
                    tempValue=Integer.parseInt(table.TableNr.substring(1, table.TableNr.length()));
                    nextTakeAwayTableNumber=nextTakeAwayTableNumber>tempValue?nextTakeAwayTableNumber:tempValue;
                }
        }
        nextTakeAwayTableNumber++;
        return  "w" +  nextTakeAwayTableNumber;
    }
    //note: it is copy
    public static Table[] GetSortedTableList()
    {
    	Table[] tables=new Table[TableHelper.TableList().size()];
    	TableHelper.TableList().toArray(tables);
    	Arrays.sort(tables);
    	return tables;
    }
    
    //for testing
    public static void InitTableListForTest(int numberOfTables, int numberOfOrders)
    {
        TableList().clear();
        for(int icount=1; icount<numberOfTables; icount++)
        {
        	Table table=new Table(Integer.toString(icount), false);
            TableList().add(table);
            for (int icountOrder = 1; icountOrder < numberOfOrders; icountOrder++)
            {
                try {
					table.AddOrder(Integer.toString(icountOrder), 1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }            
    }
    
    public static Table GetTableByNumber(String tableNumber)
    {
    	for(Table table:TableList())
    	{
    		if(table.TableNr.equals(tableNumber))
    		{
    			return table;
    		}
    	}
        return null;
    }
    
    public static void DeleteTableByNumber(String tableNumber)
    {
    	Table table=GetTableByNumber(tableNumber);
    	if(table!=null)
    	{
    		table:TableList().remove(table);
    	}    
    }
    
    //use StringBuilder instead of string + -, performance
    //http://stackoverflow.com/questions/4645020/when-to-use-stringbuilder-in-java
    public static void SaveTablesToStorage()
    {   	
        //DateTime beginTime = DateTime.Now;
    	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    	Date date = new Date();
    	String datetime=dateFormat.format(date);
    	
    	Table[] tables=GetSortedTableList();
        StringBuilder builder = new StringBuilder();
        String fileContent="DT"+ Define.MENU_SEPERATOR + datetime + Define.NEW_LINE;
        builder.append(fileContent);
        for (Table table : tables)
        {
        	builder.append(table.ToString());
        	builder.append(Define.NEW_LINE);
            for(Order order : table.TableOrders())
            {
            	builder.append(order.toString());
            	builder.append(Define.NEW_LINE);
            }
        }
        /*
        for (Table table : tables)
        {
            fileContent += table.ToString() + Define.NEW_LINE;
            for(Order order : table.TableOrders())
            {
                fileContent += order.toString() + Define.NEW_LINE;
            }
        }
        */
        try {
			//Common.SaveToIsolatedStorage(fileContent, Define.TABLE_STORAGE_FILE);
			Common.SaveToIsolatedStorage(builder.toString(), Define.TABLE_STORAGE_FILE);
			MyApplication.NeedUpdate=true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        //int duration = DateTime.Now.Subtract(beginTime).Milliseconds;
        //System.Diagnostics.Debug.WriteLine("SaveTablesToStorage duration:" + duration);
    }
    
    public static void LoadTablesFromStorage() throws IOException
    {
        //DateTime beginTime = DateTime.Now;
        if (!Common.ExistsInStorage(Define.TABLE_STORAGE_FILE))
        {
        	Log.e(Define.APP_CATALOG, "Table storage file not exists");            
            return;
        }
       InputStream inputStream= Common.GetFileInputStreamFromStorage(Define.TABLE_STORAGE_FILE);
       BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode")); 
       
       String line = sr.readLine();
       TableList().clear();
       Table table = null;
       Boolean newday = false;
       Table parentTable = null;
       DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
   	   Date date = new Date();
   	   String dateString=dateFormat.format(date);
       
        while (line!=null && newday==false)
       {
           if (Common.StartedWith(line, "DT"))
           {
              //Table header
               String[] items =Common.SplitString(line, Define.MENU_SEPERATOR);
               if (!dateFormat.format(date).equals(items[1]))
               {
                   newday = true;
               }
           }else           
           if (Common.StartedWith(line, "TBL"))
           {
               //table
              table= Table.Parse(line);
              TableList().add(table);
              if (table.IsChildTable)
              {
                  if (parentTable != null)
                  {
                      table.ParentTable = parentTable;
                      parentTable.ChildTalbes.add(table);
                  }
              }
              else
              {
                  parentTable = table;
              }
           }
           else
           {
               Order order = Order.parse(line, table);                   
               table.AddOrder(order);
           }
           line = sr.readLine();
       }//while
       sr.close();
      if(newday==true)
      {
    	  TableList().clear();
    	  TableHelper.SaveTablesToStorage();
      }
    }

    public static void SaveWholeMenu()
    {
    	StringBuilder sb=new StringBuilder();
		
		try {			
				 for(ProductGroup menugroup:Product.MenuGroups())
				 {
					
						 sb.append(menugroup.toString());
						 sb.append(Define.NEW_LINE);	
						 for (Product menu : menugroup.Menus)
			            {          
			            	
			            	sb.append(menu.toString());    
			            	sb.append(Define.NEW_LINE);		
			            }						
				 }
		    			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		try {
			Common.SaveToIsolatedStorage(sb.toString(), Define.MENU_FILE_NAME);
			Product.Reload();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
