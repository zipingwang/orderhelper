package com.eurotong.orderhelperandroid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Customer {

    public String CustomerID = "";   

    public String ServerAddress = "";
  
    private static Customer _current;
    public static Customer Current()
    {       
            if (_current == null)
            {
            	 if (Common.ExistsInStorage(Define.CUSTOMER_ID_FILE))
                     //load from isolated storage
                     {                   
                         try {
                        	 _current = Customer.Parse(Common.GetFileInputStreamFromStorage(Define.CUSTOMER_ID_FILE));
     					} catch (Exception e) {
     						// TODO Auto-generated catch block
     						e.printStackTrace();
     					}
                     }
                     else
                     {
                    	 _current = new Customer();
                     }
            }
            return _current;  
    }

    public String ToString()
    {
        String result = "";
        result = result + "customerid" + Define.SEPERATOR + CustomerID + Define.NEW_LINE;
        result = result + "serveraddress" + Define.SEPERATOR + ServerAddress + Define.NEW_LINE; 
        return result;
    }
    
    public static Customer Parse(FileInputStream inputStream) throws Exception
    {
   	 	Customer cus =new Customer();   	 
   	
        BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode")); // "UTF-8"));  
        String line = null;
        line = sr.readLine();
        int iCount = 1;
      
        while (line != null)
        {
       	 if (Common.IsDataLine(line))
            {
       		 
       		 	String[] items =Common.SplitString(line, Define.SEPERATOR);	    
	       		 if (items.length != 2)
	           	 {
	           		 //MessageBox.Show("length not 2:" + item);
	           		 Common.GetToastLong("Customer id file length not 2:" + line).show();
	           	 }
	       		 else
	       		 { 
           			String keyString = items[0].trim().toLowerCase();
           			String value=items[1].trim();
                    if (keyString.equals("customerid"))
                    {
                        cus.CustomerID = value;
                    }
                    else if (keyString.equals("serveraddress"))
                    {
                        cus.ServerAddress= value;
                    }
                                           
                    else
                    {
                       Common.GetToastLong("item not recognized in customer id property:" + keyString).show();
                    }
                    
                }	  
            }
            line = sr.readLine();
            iCount++;
        }
        sr.close();
        return cus;
    }

}
