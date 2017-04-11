package com.eurotong.orderhelperandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import android.provider.Settings.Secure;

import com.eurotong.orderhelperandroid.R.string;

import android.R.bool;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import static java.lang.Math.abs;

@SuppressLint("ShowToast")
public class Common extends Activity {
	/*
	 static Common _current;
	 public static Common Current()
	 {
		 if(_current==null)
		 {
			 _current=new Common();
		 }
		 return _current;
	 }
	 */
	 static public String FormatDouble(double inputvalue)
     {
		 String value="UNKNOWN";
		 int sign=1;
		 if(inputvalue<0)
		 {
			 sign=-1;
		 }
		 inputvalue=inputvalue * sign;
		 
         if (inputvalue == 0)
         {
             return "0" + Setting.Current().getComma() + "00";
         }

         if (inputvalue < 0.1)
         {
        	 Common.GetToastLong("format double error: " + inputvalue).show();
         }   
         else
         {        
	         value=  new DecimalFormat("#").format(inputvalue * 100); //Double.toString(inputvalue * 100);
	         String comma=Setting.Current().getComma();
	         value = value.substring(0, value.length() - 2) + comma + value.substring(value.length() - 2, value.length());
	         if (value.length() == 3)
	             value = "0" + value;
	         
	         if(sign<1)
	         {
	        	 value="-"+value;
	         }
         }
         return value;          
     }
	 
	 static public String FormatDoubleToNoDecimal(double inputvalue)
	 {
		return new DecimalFormat("#").format(inputvalue);
	 }
	 
	 static public double GetDoubleResult(String value, double defaultValue)
     {
         double price;
         try
         {
        	 price=Double.parseDouble(value);
        	 return price;
         }
         catch(Exception exe)
         {
        	 return defaultValue;
         }
     }
	 
	 public static  boolean IsDataLine(String line)
	 {
		 return (!line.trim().equals("") && line.length()>2 && !line.substring(0, 2).equals("//"));
		 /* return will not leave the method
		 if (line.trim() != "" && line.length()>2 && line.substring(0, 2) != "//")
			 return true;
		 else
			 return false;
			 */
	 }
	 
	 public static FileInputStream GetFileInputStreamFromStorage(String fileName) throws IOException
	 {
		 FileInputStream fi=null;
		 /*
		  * very strange. return MyApplication.getAppContext().openFileInput(fileName); then still goes to return null;
		 if(ExistsInStorage(fileName))
		 {
			 return MyApplication.getAppContext().openFileInput(fileName);
		 }
		 else
		 {
		     return null;
		 }
		 */
		 if(ExistsInStorage(fileName))
		 {
			 fi= MyApplication.getAppContext().openFileInput(fileName);
		 }
		 else
		 {
		     fi= null;
		 }
		 return fi;
	 }
	 
	 public static boolean ExistsInStorage(String fileName)
	 {		
		 File file =MyApplication.getAppContext().getFileStreamPath(fileName);
		 Log.i("orderhelper", file.getAbsolutePath().toString());
		 return file.exists();			  
	 }	 
	 
	 @SuppressLint("ShowToast")
	public static Toast GetToastLong(String msg)	 {
		 
		Toast tst= Toast.makeText(MyApplication.getAppContext(), msg, Toast.LENGTH_LONG);
     	return tst;
	 }
	 
	 public static Toast GetToastLong(int msgid)
	 {
		return GetToastLong(MyApplication.getAppContext().getText(msgid).toString());
	 }
	 
	 
	 public static Toast GetToastShort(String msg)
	 {
		Toast tst= Toast.makeText(MyApplication.getAppContext(), msg, Toast.LENGTH_SHORT);
     	return tst;
	 }
	 
	 public static Toast GetToastShort(int msgid)
	 {
		 return GetToastShort(MyApplication.getAppContext().getText(msgid).toString());
	 }
	
	 static public int GetWidth(String inputvalue, Double scaleFactor)
     {
        return  (int)(Integer.parseInt(inputvalue) * scaleFactor);
     }
	 public static Table GetTableFromIntent(Activity activity)
	 {
		 Table table=null;
		 Bundle extras = activity.getIntent().getExtras(); 
	     if(extras!=null)
	     {
		        String tablenr= extras.getString(Define.TABLE_NR);        
		        if(tablenr!=null && tablenr!="")
		        {
		        	table=TableHelper.GetTableByNumber(tablenr);
		        }
	     }
	     return table;
	 }
	 
	 public static String[] SplitStringInTwo(String line, String seperator)
	 {
		 String[] items=SplitString(line, seperator); //new String[2];
	     if (items.length != 2)
         {                	
             Common.GetToastLong("items length is not 2 in splitstringintwo: " + line).show();
             
         }    
		 return items;
	 }
	 
	 //make same split logic like in .net, if seperator at end, java gives only one item, .net give two
	 public static String[] SplitString(String line, String seperator)
	 {
		 return line.split(seperator, 50);
	 }
	 
	 public static void SaveToIsolatedStorage(String fileContent, String fileName) throws Exception
	 {		 
		 //http://stackoverflow.com/questions/4228699/write-and-read-strings-to-from-internal-file
 	    FileOutputStream fos =MyApplication.getAppContext().openFileOutput(fileName, Context.MODE_PRIVATE);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "Unicode");
		osw.append(fileContent);
 	    osw.close();	 
	 }
	 
	 public static boolean StartedWith(String line, String startString)
	 {
		 boolean flag=false;
		 if(line==null || startString==null || line.length()<startString.length())
		 {
			 flag=false;
		 }
		 else
		 {
			 if(line.substring(0, startString.length()).equals(startString))
			 {
				 flag=true;
			 }
		 }
		 return flag;	 
	 }
	 
	 public static Boolean IsLicensed()
     {
		 Boolean hasLicense = false;
       
         String deviceID = Common.GetDeviceUniqueID();
         if ( Device.IsRegisterDevice(Common.ScrampString(deviceID)))
         {
             hasLicense = true;
         }
         return hasLicense;
     }

	public static Boolean IsAllowedToAddTable() throws Exception {
		if (TableHelper.TableList().size() > 1 && !Common.IsLicensed()) {
			throw new Exception();
		}
		return  true;
	}


     public static String GetWifiMacAddress()
     {
         String id = "UNKNOWN";
         Object idObject = null;
         WifiManager wifiMan = (WifiManager) MyApplication.getAppContext().getSystemService(
                 Context.WIFI_SERVICE);
         WifiInfo wifiInf = wifiMan.getConnectionInfo();
         String macAddr = wifiInf.getMacAddress();
         id=macAddr;
         return id;
     }
     
     //http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
     @SuppressWarnings("deprecation")
	public static String GetIpAddress()
     {
         String id = "UNKNOWN";
         Object idObject = null;
         WifiManager wifiMan = (WifiManager) MyApplication.getAppContext().getSystemService(
        		 Context.WIFI_SERVICE);
         //formatIpAddress is deprecated. because it only handles ip4
        id= Formatter.formatIpAddress(wifiMan.getConnectionInfo().getIpAddress());
        return id;
     }
     
     public static String GetWifiSSID()
     {
    	 String id = "UNKNOWN";
    	 WifiManager wifiMan = (WifiManager) MyApplication.getAppContext().getSystemService(
        		 Context.WIFI_SERVICE);
	     WifiInfo wifiInfo = wifiMan.getConnectionInfo();
	     id= wifiInfo.getSSID().toString();
	     return id;
     }
     
     public static String GetDeviceUniqueID_Based_On_MAC_But_Since_Android_6_ALWAYS_RETURN_SAME_MAC()
     {
    	 String mac=GetWifiMacAddress();
     	long id=-1;
     	long idMod=0;
     	String idString="UNKNOWN";
     	if(!mac.equals("UNKNOWN")){
     		id=ConvertMacToLong(mac);     		
     		idString=Long.toString(id);
     		idString=idString.concat("0000000000").substring(0, 10);
     		id= Long.parseLong(idString);
     		idMod=id % Define.MOD;
     		String idModString=Long.toString(idMod);
     		idModString="00".concat(idModString);
     		idModString=idModString.substring(idModString.length()-2, idModString.length());
     		idString=Long.toString(id) + idModString;
     		//id=id / 10000;
     	}
     	return idString;
     }

	public static String GetDeviceUniqueID()
	{
		//http://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id/2853253#2853253
		String android_id = Secure.getString(MyApplication.getAppContext().getContentResolver(),
				Secure.ANDROID_ID);

		long id=-1;
		long idMod=0;
		String idString="UNKNOWN";
		if(android_id!=null){
			id=abs(android_id.hashCode()); //could get negative value
			idString=Long.toString(id);
			idString=idString.concat("0000000000").substring(0, 10);
			id= Long.parseLong(idString);
			idMod=id % Define.MOD;
			String idModString=Long.toString(idMod);
			idModString="00".concat(idModString);
			idModString=idModString.substring(idModString.length()-2, idModString.length());
			idString=Long.toString(id) + idModString;
			//id=id / 10000;
		}
		return idString;
	}

     public static long ConvertMacToLong(String mac)
     {
    	mac=mac.replace(":", "");
    	long id= Long.parseLong(mac, 16);
    	return id;
     }
         
     public static String ScrampString(String input)
     {
		 //from 机身号码(input) to 客户 号码 (return value)
         String scrampedString = "";
         /*
         for (int icount =0; icount<input.length(); icount++)
         {
        	 char c=(char)(input.charAt(icount) - 8);
             scrampedString = scrampedString + c;
         }
         */
         long id;
         id=Long.parseLong(input);
       
         id = (id * 3 + 98234579) * 2;
         long idMod = id % Define.MOD;
         String idModString = Long.toString(idMod);
         idModString = "00" + idModString;
         idModString = idModString.substring(idModString.length() - 2, idModString.length());
         scrampedString = Long.toString(id) + idModString;
         
         return scrampedString;
     }

     public static String GetStringBackFromScrapedString(String input)
     {
         String scrampedString = "";
         for (int icount =0; icount<input.length(); icount++)
         {
        	 char c=(char)(input.charAt(icount) - 12);
             scrampedString = scrampedString + c;
         }
         return scrampedString;
     }
     public static String GetBaseUrl()
     {
         String url = "";
         if (Customer.Current().ServerAddress.equals(""))
         {
             url = "http://" + Define.BASE_URL + "/mobileapp/ol/";
         }
         else
         {
             url = "http://" + Customer.Current().ServerAddress + "/mobileapp/ol/";
         }
         return url;
     }
     
   
     public static Boolean TablesLoadedFromStorage=false;
     
     
     public static void downloadFiles(boolean includeMenuFile)
     {
		 if(includeMenuFile) {
			 Common.downloadFile(Define.MENU_FILE_NAME);
		 }
			Common.downloadFile(Define.BUSINESS_INFO_FILE_NAME);
			Common.downloadFile(Define.PRINT_LAYOUT_NAME);
			Common.downloadFile(Define.PRINT_LAYOUT_KITCHEN_NAME);
			Common.downloadFile(Define.SETTING_FILE_NAME);
			Common.downloadFile(Define.PRINT_LAYOUT_BAR_NAME);
			Common.downloadFile(Define.DEVICE_FILE);
     }


     @TargetApi(9)
 	public static  void downloadFile(String fileName)
     {
     	try {
     		//http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception
     		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

     		StrictMode.setThreadPolicy(policy); 
     		
     		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
         	Date date = new Date();
         	String datetime=dateFormat.format(date);
     	    // Create a URL for the desired page    		
     	    URL url = new URL(Common.GetBaseUrl()+ Customer.Current().CustomerID + "/" + fileName + "?d="+ datetime);

     	    // Read all the text returned by the server  
     	    
     	    BufferedInputStream in = new BufferedInputStream(url.openStream());

     	    //http://stackoverflow.com/questions/4228699/write-and-read-strings-to-from-internal-file
     	    FileOutputStream fos =  MyApplication.getAppContext().openFileOutput(fileName, Context.MODE_PRIVATE);
     	    /*
     	    DataOutputStream out = 
     	            new DataOutputStream(fos);

     	    String str;
     	    while ((str = in.readLine()) != null) {
     	        // str is one line of text; readLine() strips the newline character(s)
     	    	Log.i(Define.APP_CATALOG, str);
     	    	out.writeUTF(str);
     	    }
     	    */
     	    BufferedOutputStream out=new BufferedOutputStream(fos, 4096);
     	    byte[] data = new byte[4096];
             int bytesRead = 0, totalRead = 0;
             while((bytesRead = in.read(data, 0, data.length)) >= 0)
             {
                     out.write(data, 0, bytesRead);
                     
                     // update progress bar
                     totalRead += bytesRead;
                     /*
                     int totalReadInKB = totalRead / 1024;
                     msg = Message.obtain(parentActivity.activityHandler,
                                     AndroidFileDownloader.MESSAGE_UPDATE_PROGRESS_BAR,
                                     totalReadInKB, 0);
                     parentActivity.activityHandler.sendMessage(msg);
                     */
             }
     	    
     	    
     	    in.close();
     	    //fos.close();
     	    out.close();
     	    
     	    Toast.makeText(MyApplication.getAppContext(), fileName + MyApplication.getAppContext().getString(R.string.msg_download_file_finished), Toast.LENGTH_LONG).show();
     	    
     	    try {
     	    	if(fileName.equals(Define.MENU_FILE_NAME))
     	    	{
     	    		Product.ParseMenuList(Common.GetFileInputStreamFromStorage(fileName));
     	    		Log.i(Define.APP_CATALOG, "menus size:" + "");
     	    		Product.Reload();
     	    	}
     	    	else if(fileName.equals(Define.PRINT_LAYOUT_NAME))
     	    	{
     	    		PrintLayout bi=	PrintLayout.Parse(Common.GetFileInputStreamFromStorage(Define.PRINT_LAYOUT_NAME));
         	    	Log.i(Define.APP_CATALOG, Define.PRINT_LAYOUT_NAME);
         	    	PrintLayout.Reload();
     	    	}
     	    	else if(fileName.equals(Define.BUSINESS_INFO_FILE_NAME))
     	    	{
     	    	 	BusinessInfo bi=	BusinessInfo.Parse(Common.GetFileInputStreamFromStorage(Define.BUSINESS_INFO_FILE_NAME));
         	    	Log.i(Define.APP_CATALOG, "setting: businessname:" + bi.getBusinessName());
         	    	BusinessInfo.Reload();
     	    	}
     	    	else if(fileName.equals(Define.SETTING_FILE_NAME))
     	    	{
     	    	   Setting setting=	Setting.Parse(Common.GetFileInputStreamFromStorage(Define.SETTING_FILE_NAME));
     	    	   Log.i(Define.APP_CATALOG, "setting: printer port" + setting.getPrinterPort());
     	    	   setting.Reload();
     	    	}
     	    	else if(fileName.equals(Define.PRINT_LAYOUT_BAR_NAME))
     	    	{
     	    		PrintLayout bi=	PrintLayout.Parse(Common.GetFileInputStreamFromStorage(Define.PRINT_LAYOUT_BAR_NAME));
         	    	Log.i(Define.APP_CATALOG, Define.PRINT_LAYOUT_BAR_NAME);
         	    	PrintLayout.Reload();
     	    	}
     	    
     	    	else if(fileName.equals(Define.PRINT_LAYOUT_KITCHEN_NAME))
     	    	{
     	    		PrintLayout bi=	PrintLayout.Parse(Common.GetFileInputStreamFromStorage(Define.PRINT_LAYOUT_KITCHEN_NAME));
         	    	Log.i(Define.APP_CATALOG, Define.PRINT_LAYOUT_KITCHEN_NAME);
         	    	PrintLayout.Reload();
     	    	}
     	    	else if(fileName.equals(Define.DEVICE_FILE))
     	    	{
     	    		Device bi=	Device.Parse(Common.GetFileInputStreamFromStorage(Define.DEVICE_FILE));
         	    	Log.i(Define.APP_CATALOG, Define.DEVICE_FILE);
         	    	Device.Reload();
     	    	}
     	    	
 			} catch (Exception e) {
 				// TODO Auto-generated catch block
 				e.printStackTrace();
 			}
     	} catch (MalformedURLException e) { Log.e(Define.APP_CATALOG, e.toString());
     	} catch (IOException e) {
     		Toast.makeText(MyApplication.getAppContext(), fileName + MyApplication.getAppContext().getString(R.string.msg_download_error), Toast.LENGTH_LONG).show();
     		Log.e(Define.APP_CATALOG, e.toString());
     		e.printStackTrace();
     	}
     	catch (Exception e) {
     		Toast.makeText(MyApplication.getAppContext(), fileName +  MyApplication.getAppContext().getString(R.string.msg_download_error), Toast.LENGTH_LONG).show();
     		Log.e(Define.APP_CATALOG, e.toString());
     	}
     }

	public  static String downloadContentFromInternet(String urlString) throws IOException
	{
		/*
		URLConnection feedUrl=null;
		try
		{
			feedUrl = new URL(url).openConnection();
		}
		catch (MalformedURLException e)
		{
			Log.v("ERROR","MALFORMED URL EXCEPTION");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try
		{
			InputStream in = feedUrl.getInputStream();
			String json = convertStreamToString(in);
			return  json;
		}
		catch(Exception e){}
		return null;
		*/
		//http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
		Date date = new Date();
		String datetime=dateFormat.format(date);
		// Create a URL for the desired page
		URL url = new URL(urlString + "?d="+ datetime);

		// Read all the text returned by the server

		InputStream in = url.openStream();
		String content=convertStreamToString(in);
		return content;
	}

	private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"Unicode"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
class DownloadFileAsyncTask extends AsyncTask<Void, Void, Void>
{

    ProgressDialog mProgressDialog;
  
    public Boolean HasError;
    public Boolean Finished;
    public DownloadFileAsyncTask(String url)
    {    
    	HasError=false;
    	Finished=false;
    }
    
    @Override
    protected void onPostExecute(Void result) {    	
    	//Finished=true;
        //mProgressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
    	
        //mProgressDialog = ProgressDialog.show(ActivityName.this, "Loading...", "Data is Loading...");
    }

    @Override
    protected Void doInBackground(Void... params) {
    
        return null;
    }
    
}

