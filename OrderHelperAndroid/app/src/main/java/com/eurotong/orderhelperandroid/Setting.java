package com.eurotong.orderhelperandroid;

import java.io.*;

import android.R.integer;

enum PrinterType
{
    Lan, WLan, Bluetooth, Usb
}

public class Setting
{
    
	private String Printertype = "";
   
	private String PrinterIP = "";
   
	private int PrinterPort = 0;
    
	private String Comma;

    static Setting _setting;
    
    private String PrinterCommandMode; 
    
    //A byte in Java has the range -128 - 127. There is nothing you can do about it. If the function you're passing the byte to accepts a byte then it does not expect the value to exceed 127.
    //http://stackoverflow.com/questions/4266756/can-we-make-unsigned-byte-in-java
    //public byte BlackDepth;
    public int BlackDepth;
    
    private int Printwaittime;

    private boolean _isTest;
   
    private String  _defaultOperatorName;
  
    private String _ownerPassword;
    
    public String OperatorPassword;
  
    
    public String InstallerPassword;
   
   
    private String _version;
   
    private boolean _isTryVersion;
   
    public String LicenseNr;

	public String PrinterBluetoothUUID;

	public  boolean IsAsyncPrinting;
    
    public int PrintTwoWaitTime = 2000;
    public int NumberOfPrints = 1;
    public int NumberOfPrintsBar = 1;
    public int NumberOfPrintsKitchen = 1;
    
	public boolean get_isTryVersion() {
		return _isTryVersion;
	}

	public void set_isTryVersion(boolean _isTryVersion) {
		this._isTryVersion = _isTryVersion;
	}

	public String get_version() {
		return _version;
	}

	public void set_version(String _version) {
		this._version = _version;
	}

	public String get_ownerPassword() {
		return _ownerPassword;
	}

	public void set_ownerPassword(String _ownerPassword) {
		this._ownerPassword = _ownerPassword;
	}

	
	public String get_defaultOperatorName() {
		return _defaultOperatorName;
	}

	public void set_defaultOperatorName(String _defaultOperatorName) {
		this._defaultOperatorName = _defaultOperatorName;
	}

	public boolean get_isTest() {
		return _isTest;
	}

	public void set_isTest(boolean _isTest) {
		this._isTest = _isTest;
	}

	public String getComma() {
		return Comma;
	}

	public void setComma(String comma) {
		Comma = comma;
	}

	public int getPrinterPort() {
		return PrinterPort;
	}

	public void setPrinterPort(int printerPort) {
		PrinterPort = printerPort;
	}

	public String getPrinterIP() {
		return PrinterIP;
	}

	public void setPrinterIP(String printerIP) {
		PrinterIP = printerIP;
	}

	public String getPrintertype() {
		return Printertype;
	}

	public void setPrintertype(String printertype) {
		Printertype = printertype;
	}

	  
      public String getPrinterCommandMode() {
		return PrinterCommandMode;
	}

	public void setPrinterCommandMode(String printerCommandMode) {
		PrinterCommandMode = printerCommandMode;
	}
	  public int getBlackDepth() {
			return BlackDepth;
		}

		public void setBlackDepth(int blackDepth) {
			BlackDepth = blackDepth;
		}

		public int getPrintwaittime() {
			return Printwaittime;
		}

		public void setPrintwaittime(int printwaittime) {
			Printwaittime = printwaittime;
		}
    
	public static Setting Current()
    {
       
            if (_setting == null)
            {
            	_setting = new Setting();
            	
                if (Common.ExistsInStorage(Define.SETTING_FILE_NAME))
                //load from isolated storage
                {                   
                    try {
						_setting = Setting.Parse(Common.GetFileInputStreamFromStorage(Define.SETTING_FILE_NAME));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                else
                {
                    _setting = new Setting();
                }
               
            }
            return _setting;        
    }

    public static Setting Parse(FileInputStream inputStream) throws Exception
    {    	
    	BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode"));       
    	Setting setting = new Setting();       
        String line = sr.readLine();
        while (line != null)
        {
        	
            if (Common.IsDataLine(line)==true)
            {
                String[] items = Common.SplitStringInTwo(line, Define.SEPERATOR);
                
	            	String item=items[0].toLowerCase();
	                if(item.equals("printertype"))
	                {                        
	                       setting.setPrintertype(items[1].trim());
	                }
                   else if(item.equals("printerip"))
                   {
                	   setting.setPrinterIP(items[1].trim());
                   }
                   else if(item.equals("printerport"))
                   {                	   
                       setting.setPrinterPort(Integer.parseInt(items[1].trim()));     
                   }
                   else if(item.equals("comma"))
                   {
                	   setting.setComma(items[1].trim());
                   }
                   else if(item.equals("printercommandmode"))
                   {
                	   setting.setPrinterCommandMode(items[1].trim());
                   }
                   else if(item.equals("blackdepth"))
                   {
                	   setting.setBlackDepth(Integer.parseInt(items[1].trim()));
                   }
                   else if(item.equals("printwaittime"))
                   {
                	   setting.setPrintwaittime(Integer.parseInt(items[1].trim()));
                   }  
                   else if(item.equals("printwaittime"))
                   {
                	   setting.setPrintwaittime(Integer.parseInt(items[1].trim()));
                   } 
                   else if (item.equals("printtwowaittime"))
                   {
                       setting.PrintTwoWaitTime = Integer.parseInt(items[1].trim());
                   }
                   else if (item.equals("numberofprints"))
                    {
                        setting.NumberOfPrints = Integer.parseInt(items[1].trim());
                    }
                   else if (item.equals("numberofprintsbar"))
                   {
                       setting.NumberOfPrintsBar = Integer.parseInt(items[1].trim());                   }
	                
                   else if (item.equals("numberofprintskitchen"))
                   {
                       setting.NumberOfPrintsKitchen = Integer.parseInt(items[1].trim());
                   }
                   else if(item.equals("istest"))
                   {
                	   if(items[1].trim().equals("1"))
                	   {
                		   setting.set_isTest(true);
                	   }
                	   else
                	   {
                		   setting.set_isTest(false);
                	   }                	   
                   }    
                   else if(item.equals("defaultoperatorname"))
                   {
                	   setting.set_defaultOperatorName(items[1].trim());
                   }    
                   else if(item.equals("ownerpassword"))
                   {
                	   setting.set_ownerPassword(items[1].trim());
                   }    
                   else if(item.equals("version"))
                   {
                	   setting.set_version(items[1].trim());
                   }    
                   else if(item.equals("istryversion"))
                   {
                	   if(items[1].trim().equals("0"))
                	   {
                		   setting.set_isTryVersion(false);
                	   }
                	   else
                	   {
                		   setting.set_isTryVersion(true);
                	   }                	   
                   }    
                 
                   else if(item.equals("licensenr"))
                   {
                	   setting.LicenseNr=items[1].trim();
                   }
					else if(item.equals("printerbluetoothuuid"))
					{
						setting.PrinterBluetoothUUID=items[1].trim();
					}
					else if(item.equals("printisasync"))
					{
						if(items[1].trim().equals("1"))
							setting.IsAsyncPrinting=true;
						else
							setting.IsAsyncPrinting=false;
					}
					else
                   {
                	 Common.GetToastLong("item not found in parse setting:" + items[0].trim().toLowerCase() + line).show();
                   }                    
                
            }
            
            line = sr.readLine();
        }
        sr.close();
        return setting;
    }

    public static void Reload()
    {
    	_setting=null;       
    }
}
