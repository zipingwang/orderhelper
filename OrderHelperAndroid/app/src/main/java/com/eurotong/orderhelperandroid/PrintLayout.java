package com.eurotong.orderhelperandroid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class PrintLayout {

	private PrintLayout() { IsPrintMode = false; LoadedFromStorage = false; LayoutType = Define.LAYOUT_TYPE_RESTAURANT;}
    private String _layoutFileName;
    public PrintLayout(String layoutfileName) {
        this();
        _layoutFileName = layoutfileName;
    }
    
    public int LayoutType;

    //private int _deviceWidth;
    public int DeviceWidth;

    
    public int Width() {       
            if (IsPrintMode == false)
            {
                return DeviceWidth;
            }
            else
            {
                return PrintWidth;
            }
    }
    
    public int TopMargin;
    public int LeftMargin;
    public int PrintWidth;
    public boolean IsPrintMode;
    
    public double ScaleFactor() {
        if (IsPrintMode == true)
        {
            double d = PrintWidth;
            return d / DeviceWidth;           
        }
        else
        {
            return 1;
        }
    }
    
    private static PrintLayout _current;
    private static PrintLayout _kitchenLayout;
    private static PrintLayout _barLayout;
    
    public static PrintLayout Current()
    {
       
            if (_current == null)
            {
                _current = new PrintLayout(Define.PRINT_LAYOUT_NAME);
                _current.LayoutType = Define.LAYOUT_TYPE_RESTAURANT;
            }
            return _current; 
    }
    
    
    public static PrintLayout KitchenLayout()
    {
       
            if (_kitchenLayout == null)
            {
                _kitchenLayout = new PrintLayout(Define.PRINT_LAYOUT_KITCHEN_NAME);
                _kitchenLayout.LayoutType = Define.LAYOUT_TYPE_KITCHEN;
            }
            return _kitchenLayout;       
    }     
    
    public static PrintLayout BarLayout()
    {
       
            if (_barLayout == null)
            {
                _barLayout = new PrintLayout(Define.PRINT_LAYOUT_BAR_NAME);
                _barLayout.LayoutType = Define.LAYOUT_TYPE_BAR;
            }
            return _barLayout;        
    }
    
    public boolean LoadedFromStorage=false;
    List<String> _lines;
    public List<String> Lines()
    {      
        if (LoadedFromStorage==false)
        {
            LoadPrintlayoutFromStorageFile(); // new List<Menu>();
        }
        return _lines;       
    }
    
    //LinkedHashMap
    //Hashtable get is not same order as put
    //http://stackoverflow.com/questions/6405694/does-hashtable-maintains-the-insertion-order
    List<LinkedHashMap<String, String>> _params;    
    // public List<Hashtable<String, String>> Params()
    public List<LinkedHashMap<String, String>> Params()   
    {
        
    	if (LoadedFromStorage==false)
        {
            LoadPrintlayoutFromStorageFile(); // new List<Menu>();
        }
        return _params;         
    }

    public void setParams(List<LinkedHashMap<String, String>> value)
    {
         _params=value;         
    }
    

    
    public void setLines(List<String> value)
    {
    	_lines=value;
    }

    private void LoadPrintlayoutFromStorageFile()
    {
        if (!Common.ExistsInStorage(_layoutFileName))
        {
            Common.GetToastLong("LoadPrintlayoutFromStorageFile: tofix file not exists").show();
           this.setParams(new ArrayList<LinkedHashMap<String, String>>());
           this.setLines(new ArrayList<String>());
        }
        else
        {
        	PrintLayout pl;
        	try {
				pl = PrintLayout.Parse(Common.GetFileInputStreamFromStorage(_layoutFileName));
				 this.setParams(pl.Params());
			     this.setLines(pl.Lines());
			     this.DeviceWidth = pl.DeviceWidth;
	            this.LeftMargin = pl.LeftMargin;
	            this.TopMargin = pl.TopMargin;
	            this.PrintWidth = pl.PrintWidth;
	            this.LoadedFromStorage=true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        }
    }
    
    public static PrintLayout Parse(FileInputStream inputStream) throws Exception
    {    	
    	PrintLayout pl = new PrintLayout();
    	BufferedReader sr = new BufferedReader(new InputStreamReader(inputStream, "Unicode"));       
    	    
    	pl._lines=new ArrayList<String>();
    	pl._params=new ArrayList<LinkedHashMap<String, String>>();
        String line = sr.readLine();
        while (line != null)
        {
        	
            if (Common.IsDataLine(line)==true)
            {
            	 LinkedHashMap<String, String> dic = new LinkedHashMap<String, String>();
                 line = line.replace("{", "");
                 line = line.replace("}", "");
                 String[] items =Common.SplitString(line, ",");
                 String layoutString="layout:layout";    
                 for (int i = 0; i < items.length; i++) {
                	 String item=items[i];
                	 String[] subitems = Common.SplitStringInTwo(item, Define.SEPERATOR);
                	 if (subitems.length != 2)
                	 {
                		 //MessageBox.Show("length not 2:" + item);
                		 Common.GetToastLong("Printlayout parse.length not 2:" + item).show();
                	 }
                	 else
                	 {
                		 dic.put(subitems[0].trim(), subitems[1].trim());
                		 if (line.length() > layoutString.length() && line.substring(0, layoutString.length()).equals(layoutString))
                         {                           
                			 String keyString = subitems[0].trim().toLowerCase();
                             if (keyString.equals("devicewidth"))
                             {
                                 pl.DeviceWidth = Integer.parseInt(subitems[1]);
                             }
                             else if (keyString.equals("topmargin"))
                             {
                                 pl.TopMargin = Integer.parseInt(subitems[1]);
                             }
                             else if (keyString.equals("leftmargin"))
                             {
                                 pl.LeftMargin = Integer.parseInt(subitems[1]);
                             }
                             else if (keyString.equals("printwidth"))
                             {
                                 pl.PrintWidth = Integer.parseInt(subitems[1]);
                             }
                             else if (keyString.equals("layout"))
                             {                               
                             }
                             else
                             {
                                Common.GetToastLong("item not recognized in layout property:" + subitems[0]).show();
                             }
                         }
                	 }
                 }
                        
                 pl.LoadedFromStorage=true;
                 pl.Params().add(dic);                   
                 pl.Lines().add(line);
            }
            
            line = sr.readLine();
        }
        sr.close();
        return pl;
    }
    public static void Reload()
    {
        PrintLayout.Current().LoadedFromStorage=false;
        PrintLayout.KitchenLayout().LoadedFromStorage = false;
        PrintLayout.BarLayout().LoadedFromStorage = false;
    }
    
}
