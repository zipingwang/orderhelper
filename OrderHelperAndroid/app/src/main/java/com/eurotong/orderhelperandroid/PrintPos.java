package com.eurotong.orderhelperandroid;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;


public class PrintPos
{
    Table _table;    
    PrintLayout _printLayout;
    Boolean _isAsync;
	BluetoothAdapter _btAdapter;
	BluetoothDevice  _bluetoothDevice;

    public PrintPos(Table table, PrintLayout printLayout, Boolean isAsync)
    {
        _table = table;
        _printLayout = printLayout;
        _isAsync=isAsync;
		if(Setting.Current().getPrintertype().toLowerCase().equals("bluetooth"))
		{
			_btAdapter = BluetoothAdapter.getDefaultAdapter();

			Set<BluetoothDevice> pairedDevices = _btAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				// Loop through paired devices
				for (BluetoothDevice device : pairedDevices) {
					if(device.getName().equals(Setting.Current().getPrinterIP()))
					{
						_bluetoothDevice=device;
						break;
					}
					// Add the name and address to an array adapter to show in a ListView
					// mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					//Log.i("bluetooth devices", device.getName() + "\n" + device.getAddress());
				}
			}
			if(_bluetoothDevice==null)
				Common.GetToastLong("找不到蓝牙" + Setting.Current().getPrinterIP()).show();
		}
    }

    static Socket _socket;

    static Socket GetSocket() throws UnknownHostException, IOException
    {
    	if(_socket==null)
    	{    	
			_socket=new Socket(Setting.Current().getPrinterIP(), Setting.Current().getPrinterPort());		
    	}
    	else
    	{
    		if(_socket.isClosed() || _socket.isOutputShutdown())
    		{
    			_socket=new Socket(Setting.Current().getPrinterIP(), Setting.Current().getPrinterPort());
    		}
    	}
    	return _socket;
    }
    /// <summary>
    /// print document via ethernet using STAR or ESC/POS Command
    /// </summary>
    /// <param name="ip"></param>
    /// <param name="port"></param>
    /// <param name="commandType"></param>
    private String PrintPosLan(String ip, int port, CommandMode commandType, Boolean isAsync)
    {
        String result="error";
    	try {

	        byte[] data = null;
	        
	        if (commandType.equals(CommandMode.STAR))
	        {
	            data = RestsoftBitmap.GetSTARPOSCommand(_table, _printLayout);
	        }
	        else
	        {
	            data = RestsoftBitmap.GetESCPOSCommand(_table, _printLayout);
	        }
	        if(data!=null)
	        {
	        	if(isAsync==true)
	        	{
		        	//asyc way. it works. but difficult to get result
		        	PrintLanAsyncTask printTask=new PrintLanAsyncTask(ip, port, data);
		        	printTask.execute();		        
		        /*
					Thread.sleep(Setting.Current().getPrintwaittime());				
		        	
		        	//if(printTask.getStatus() != AsyncTask.Status.FINISHED)
		        	for(int icount=0; icount<(30000)/200; icount++){
		        		if(!printTask.Finished && printTask.HasError==false)
		            	{	            		
		    					Thread.sleep(200);
		            	}
		        		else
		        		{
		        			break;
		        		}
		        	} 
		        	*/
		        	synchronized (printTask) {
		        		printTask.wait(20000);
		        		}
		        	
		        	if(printTask.Finished && printTask.HasError==false)
		        	{
		        			result="success";
		        	}        
		        	else
		        	{
		        		Log.e(Define.APP_CATALOG, "finished:" + printTask.Finished.toString());
		        		Log.e(Define.APP_CATALOG,  "HasError" + printTask.HasError.toString());
		        		result="error";
		        	}
	        	}
	        	else
	        	{
	        		result=DoPrintLan(ip, port, data);
	        		result="success";
	        	}	        
	        }
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result;
    }

	byte[] GetPrintRawData(CommandMode commandType)
	{
		byte[] data = null;

		if (commandType.equals(CommandMode.STAR))
		{
			data = RestsoftBitmap.GetSTARPOSCommand(_table, _printLayout);
		}
		else
		{
			data = RestsoftBitmap.GetESCPOSCommand(_table, _printLayout);
		}
		return data;
	}

	private String PrintPosBluetooth( CommandMode commandType, Boolean isAsync)
	{
		String result="error";
		try {

			byte[] data = GetPrintRawData(commandType);

			if(data!=null)
			{
				if(isAsync==true)
				{
					//asyc way. it works. but difficult to get result
					PrintBluetoothAsyncTask printTask=new PrintBluetoothAsyncTask(_bluetoothDevice, data);
					printTask.execute();
		        /*
					Thread.sleep(Setting.Current().getPrintwaittime());

		        	//if(printTask.getStatus() != AsyncTask.Status.FINISHED)
		        	for(int icount=0; icount<(30000)/200; icount++){
		        		if(!printTask.Finished && printTask.HasError==false)
		            	{
		    					Thread.sleep(200);
		            	}
		        		else
		        		{
		        			break;
		        		}
		        	}
		        	*/
					synchronized (printTask) {
						printTask.wait(20000);
					}

					if(printTask.Finished && printTask.HasError==false)
					{
						result="success";
					}
					else
					{
						Log.e(Define.APP_CATALOG, "finished:" + printTask.Finished.toString());
						Log.e(Define.APP_CATALOG,  "HasError" + printTask.HasError.toString());
						result="error";
					}
				}
				else
				{
					result=DoPrintBluetooth(_bluetoothDevice, data);
					result="success";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private String DoPrintLan(String ip, int port,  byte[] bytes)
    {
    	//allow call socket in main thread
    	//http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
    	Socket sock;
    	String result="error";
		try {
			sock = new Socket(ip, port);
		
			OutputStream os = sock.getOutputStream();  
			//String txt = "id:60bc4976-1dd2-4e9b-b771-577c9bc97630@@051207637@@@ORDER@@@20120928004@@@wang2@@vital2@@noordlaan@@8800@@roeselare@@051207637@@0477130089@@vital@@Mr@@vital.wang@eurotong.com@@@Option1@@Today@@18:25@@@3@2@@@0 ";
			//byte[] bytes= bytes;
			os.write(bytes);
			os.flush();
			int addTime = _table.TableOrders().size() * 50;
			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);
			os.close();
			sock.close();
			result="success";
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
		
		return result;	
    }

	private String DoPrintBluetooth(BluetoothDevice device,  byte[] bytes)
	{
		//allow call socket in main thread
		//http://stackoverflow.com/questions/6343166/android-os-networkonmainthreadexception
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		BluetoothSocket sock;
		String result="error";
		try {
			//_btAdapter.cancelDiscovery();
			sock = device.createRfcommSocketToServiceRecord(UUID.fromString(Setting.Current().PrinterBluetoothUUID));
			sock.connect();
			OutputStream outStream = sock.getOutputStream();
			outStream.write(bytes);
			outStream.flush();
			int addTime = _table.TableOrders().size() * 50;
			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);
			outStream.close();
			sock.close();
/*
			OutputStream os = sock.getOutputStream();
			//String txt = "id:60bc4976-1dd2-4e9b-b771-577c9bc97630@@051207637@@@ORDER@@@20120928004@@@wang2@@vital2@@noordlaan@@8800@@roeselare@@051207637@@0477130089@@vital@@Mr@@vital.wang@eurotong.com@@@Option1@@Today@@18:25@@@3@2@@@0 ";
			//byte[] bytes= bytes;
			os.write(bytes);
			os.flush();
			int addTime = _table.TableOrders().size() * 50;
			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);
			os.close();
			sock.close();
			*/
			result="success";

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}

		return result;
	}

    public String Print()
    {
    	String result = "error";
        try
        {
			String printerType=Setting.Current().getPrintertype().toLowerCase();
			if(printerType.equals("lan") || printerType.equals("wlan")) {
				if (Setting.Current().getPrinterCommandMode().equals(CommandMode.STAR.toString().toLowerCase())) {
					result = PrintPosLan(Setting.Current().getPrinterIP(), Setting.Current().getPrinterPort(), CommandMode.STAR, _isAsync);
				} else if (Setting.Current().getPrinterCommandMode().equals(CommandMode.ESCPOS.toString().toLowerCase())) {
					result = PrintPosLan(Setting.Current().getPrinterIP(), Setting.Current().getPrinterPort(), CommandMode.ESCPOS, _isAsync);
				} else {
					Common.GetToastLong("print command mode must be escpos or star. check setting file").show();
				}
			}
			else if(printerType.equals("bluetooth"))
			{
				if (Setting.Current().getPrinterCommandMode().equals(CommandMode.STAR.toString().toLowerCase())) {
					result = PrintPosBluetooth(CommandMode.STAR, _isAsync);
				} else if (Setting.Current().getPrinterCommandMode().equals(CommandMode.ESCPOS.toString().toLowerCase())) {
					result = PrintPosBluetooth(CommandMode.ESCPOS, _isAsync);
				} else {
					Common.GetToastLong("print command mode must be escpos or star. check setting file").show();
				}
			}
        }
        catch (Exception ex)
        {
        	result="operation error";
			Log.e(Define.APP_CATALOG, ex.toString());
        }
        return result;
    }

	Boolean _busy;
	private void DoPrintPos_Sync(Table table, PrintLayout printlayout, int numberOfPrints, boolean isAsync) //( printpos)	   
    {
		 
		   /*
		   DateTime now=new DateTime();
		   int waittime=Setting.Current().PrintTwoWaitTime + Setting.Current().getPrintwaittime();
		   waittime=waittime*numberOfPrints;
		   if(now.isBefore(_preTap.plusMillis(waittime)))
		   {
			   return;
		   }
		   _preTap=now;
		   */
        if (_busy == false)
        {             
            _busy = true;
            if (Common.IsLicensed())
            {
         	  
         	   String result="";
         	   
                for (int icount = 0; icount < numberOfPrints; icount++)
                {
                    String tableNr = _table.TableNr;
                    try
                    {                            
                        if (icount > 0)
                        {
                            _table.TableNr = _table.TableNr + "   (Duplicate tofix)";
                        }
                        PrintPos printpos = new PrintPos(_table, printlayout, isAsync);
                        //when phone connect to pc via usb(when debuging windows phone devide) it always success
                        //when disconnect from pc, ok.
                        result = printpos.Print();
                        if (!result.toLowerCase().equals("success"))
                        {
                            break;
                        }
                    }
                    finally
                    {
                        _table.TableNr = tableNr;
                    }                      
                }
                String msg="";
                if (result.toLowerCase().equals("success"))
	               {
             	   msg=MyApplication.getAppContext().getText(R.string.msg_print_success).toString();	                  
	               }
	               else
	               {
	            	   msg=MyApplication.getAppContext().getText(R.string.msg_print_error).toString();            	   
	               }  
                //Common.GetToastShort(R.string.msg_print_success).show();
                new AlertDialog.Builder(MyApplication.getAppContext())
		           .setMessage(msg)
		           .setCancelable(false)
		             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	   _busy = false;
			            	   //_preTap= _preTap.minusDays(1);			            	 
			               }
			           })
		           .show();
         	   //dialog or toast not block time.
                //_preTap= _preTap.minusDays(1);
            }
            else
            {
            	Common.GetToastLong(R.string.msg_print_error_no_license).show();
            	_busy = false;
            }
           
        }            
    }
	 
}


class PrintLanAsyncTask extends AsyncTask<Void, Void, Void>
{

    ProgressDialog mProgressDialog;
    byte[] _bytes;
    String _ip;
    int _port;
    public Boolean HasError;
    public Boolean Finished;
    public PrintLanAsyncTask(String ip, int port,  byte[] bytes)
    {
    	_bytes=bytes;
    	_ip=ip; 
    	_port=port;
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
    	Socket sock=null;
		try {		
			sock =  new Socket(_ip, _port); //PrintPos.GetSocket();
			//int sendsize=sock.getSendBufferSize(); //524288
			//int receiversize=sock.getReceiveBufferSize(); //524288
			//byte buff[] = new byte[1024];
			//InputStream input = sock.getInputStream(); 
			 //input.read(buff,0,buff.length);
			 //System.out.println(new String(buff,0));
			 
			OutputStream os = sock.getOutputStream();  
			//String txt = "id:60bc4976-1dd2-4e9b-b771-577c9bc97630@@051207637@@@ORDER@@@20120928004@@@wang2@@vital2@@noordlaan@@8800@@roeselare@@051207637@@0477130089@@vital@@Mr@@vital.wang@eurotong.com@@@Option1@@Today@@18:25@@@3@2@@@0 ";
			byte[] bytes= _bytes;
			os.write(bytes);
			os.flush();
			// input.read(buff,0,buff.length);
			//add extra time based on size of bytes. in mini seconds
			int addTime = bytes.length / 30; //4 lines 24162 bytes, 16 lines 60666 bytes
			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);
			// input.read(buff,0,buff.length);
			sock.shutdownOutput();
			sock.shutdownInput();
			os.close();		
			sock.close();	
			Finished=true;			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
		finally
		{	
			Finished=true;
			try
			{
				
				// String foo="foo";
				 synchronized (this) {
					 this.notify();
				 }
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			if(sock!=null && sock.isConnected())
			{
				try {
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sock!=null)
			{
				sock=null;
			}
			*/
		}
        return null;
    }
}


class PrintBluetoothAsyncTask extends AsyncTask<Void, Void, Void>
{

	ProgressDialog mProgressDialog;
	byte[] _bytes;
	BluetoothDevice _device;
	public Boolean HasError;
	public Boolean Finished;

	public PrintBluetoothAsyncTask(BluetoothDevice device ,  byte[] bytes)
	{
		_bytes=bytes;
		_device=device;
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
		BluetoothSocket sock=null;
		try {

			sock = _device.createRfcommSocketToServiceRecord(UUID.fromString(Setting.Current().PrinterBluetoothUUID));
			sock.connect();
			OutputStream outStream = sock.getOutputStream();
			outStream.write(_bytes);
			outStream.flush();
			int addTime = _bytes.length / 30; //4 lines 24162 bytes, 16 lines 60666 bytes
			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);

			Thread.sleep(Setting.Current().getPrintwaittime() + addTime);
			outStream.close();
			sock.close();

			Finished=true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			HasError=true;
			Finished=true;
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
		finally
		{
			Finished=true;
			try
			{
				// String foo="foo";
				synchronized (this) {
					this.notify();
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			if(sock!=null && sock.isConnected())
			{
				try {
					sock.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(sock!=null)
			{
				sock=null;
			}
			*/
		}
		return null;
	}
}