package com.eurotong.orderhelperandroid;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import android.util.Log;

public class Device {
	//keep it simple, use public field
	 public String Os;   
	 public String Brand;
     public String Id;
	     //List<Device> _deviceList;
	     public  List<Device> DeviceList;
	     
	     private static Device _current;
        public static Device getCurrent()
        {
          
            if (_current == null)
            {
            	 if (Common.ExistsInStorage(Define.DEVICE_FILE))
                     //load from isolated storage
                     {                   
                         try {
                        	 _current = Device.Parse(Common.GetFileInputStreamFromStorage(Define.DEVICE_FILE));
     					} catch (Exception e) {
     						// TODO Auto-generated catch block
     						e.printStackTrace();
     					}
                     }
                     else
                     {
                    	 _current = new Device();
                    	 _current.DeviceList=new ArrayList<Device>();
                     }
            }
            return _current;    
            
        }
        
	   
	     //http://stackoverflow.com/questions/416266/sorted-collection-in-java
	     //http://www.java2s.com/Code/JavaAPI/java.util/newPriorityQueueintinitialCapacity.htm
	     //The difference with a List sorted using Collections.sort(...) is that this will maintain order at all times, and have good insertion performance by using a heap data structure, where inserting in a sorted ArrayList will be O(n) (i.e., using binary search and move).
	     public static Device Parse(FileInputStream inputStream) throws Exception
	     {
	    	 Device device =new Device();
	    	 List<Device> lstDevice = new ArrayList<Device>();
	         device.DeviceList = lstDevice;
	    	
	         BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode")); // "UTF-8"));  
	         String line = null;
	         line = sr.readLine();
	         int iCount = 1;
	         Device aDevice=new Device();
	         while (line != null)
	         {
	        	 if (Common.IsDataLine(line))
	             {
	        		 aDevice=new Device();
	        		 lstDevice.add(aDevice);
	        		 line=line.replace("{", "");
	        		 line=line.replace("}", "");
	        		 String[] items =Common.SplitString(line, Define.DEVICE_FILE_SEPERATOR);	                 
	                 for (int i = 0; i < items.length; i++) {
	                	 String item=items[i];
	                	 String[] subitems = Common.SplitStringInTwo(item, Define.SEPERATOR);
	                	 if (subitems.length != 2)
	                	 {
	                		 //MessageBox.Show("length not 2:" + item);
	                		 Common.GetToastLong("device parse.length not 2:" + item).show();
	                	 }
	                	 else
	                	 {	                		                        
	                			 String keyString = subitems[0].trim().toLowerCase();
	                			 String value=subitems[1].trim();
	                             if (keyString.equals("os"))
	                             {
	                                 aDevice.Os = value;
	                             }
	                             else if (keyString.equals("brand"))
	                             {
	                                 aDevice.Brand = value;
	                             }
	                             else if (keyString.equals("id"))
	                             {
	                                 aDevice.Id = value;
	                             }	                           
	                             else
	                             {
	                                Common.GetToastLong("item not recognized in layout property:" + keyString).show();
	                             }
	                	 }
	                 }	        		
	        		 
	        		 
	             }
	             line = sr.readLine();
	             iCount++;
	         }
	         sr.close();
	         return device;
	     }

	     public static Device LoadDeviceFromStorageFile() throws Exception
	     {
	         if (!Common.ExistsInStorage(Define.DEVICE_FILE))
	         {
	        	 Common.GetToastLong("请先下载菜单。").show();
	             return new Device(); // List<Product>();              
	         }         
	         else
	         {
	        	 return Parse(Common.GetFileInputStreamFromStorage(Define.DEVICE_FILE));
	         }
	     }

	     public static void Reload()
	     {
	     	_current=null;       
	     }

	     public static boolean IsRegisterDevice(String deviceid)
	        {
	    	 	boolean flag = false;
	    	 	try
	    	 	{
		            for(Device device : getCurrent().DeviceList)
		            {
		                if (device.Id.equals(deviceid))
		                {
		                    flag = true;
		                    break;
		                }
		            }
	    	 	}
	            catch(Exception e)
	        	{
	        		Log.e(Define.APP_CATALOG, e.toString());
	        		e.printStackTrace();
	        	}
	            return flag;
	        }
}
