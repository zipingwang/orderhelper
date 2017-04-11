package com.eurotong.orderhelperandroid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import android.R.integer;
import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;


public class RestsoftBitmap
{
    static LinkedHashMap<String, integer>  _dicTextHeight;
    static int BrushColor = Color.RED;
    //static int BrushColor = Color.BLACK;


    public static byte[] GetSTARPOSCommandForTestTable()
    {
        PrintLayout.Current().IsPrintMode = true;
        return GetSTARPOSCommand(CreateTable(), PrintLayout.Current());
    }

    
    public static byte[] GetESCPOSCommand(Table table, PrintLayout printLayout)
    {
        printLayout.IsPrintMode = true;
        Bitmap wb = SetupRenderedTextBitmap(table, printLayout);
        BitmapData bitmapData= BitmapHelper.GetBitmapData(wb);
        wb.recycle();
        return ESCPOSCommand.GetESCPOSBitmapByteArray(bitmapData);
    }

    public static byte[] GetSTARPOSCommand(Table table, PrintLayout printLayout)
    {
    	  printLayout.IsPrintMode = true;
          Bitmap wb = SetupRenderedTextBitmap(table, printLayout);
          BitmapData bitmapData= BitmapHelper.GetBitmapData(wb);
          wb.recycle();
          return ESCPOSCommand.GetSTARBitmapByteArray(bitmapData);
    }

    public static Bitmap SetupRenderedTextBitmap(Table table, PrintLayout printLayout)
    {
    	Bitmap bitmap=null;
    	try
    	{

        //List<Order> orders = new ArrayList<Order>();
        //orders.addAll(table.TableOrders());
        _dicTextHeight = new LinkedHashMap<String, integer>();
        List<LineParam> lines = new ArrayList<LineParam>();
        //
        int bitmapHeight =GetRequiredBitmapHeight(table, _dicTextHeight, printLayout);
        //bitmapHeight=200;
        int bitmapWidth = printLayout.Width();

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, conf);
        Canvas canvas = new Canvas(bitmap);
        

        int iCount = 0;
        List<OrderLine> lstOrderlineField = new ArrayList<OrderLine>();
        OrderLineBlock orderlineBlock = null;
      
        //get orderline
        for (String line2 : printLayout.Lines())
        {
            if (StartWith(line2, "orderlinesblock:"))
            {
                orderlineBlock = new OrderLineBlock(printLayout.Params().get(iCount), printLayout.ScaleFactor());
            }
            if (StartWith(line2, "orderline:"))
            {
                OrderLine ol = new OrderLine(printLayout.Params().get(iCount),orderlineBlock, printLayout.ScaleFactor());
                lstOrderlineField.add(ol);
            }
            iCount++;
        }

        iCount = 0;
      
        int currentHeight = 0;
        int preHeight = 0;
        
        for (String line : printLayout.Lines())
        {   
        	int actualHeight=0;
        	
            if (StartWith(line, "fieldname:"))
            {
                TextField txt = new TextField(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                //first try found in table
                String fieldValue = table.GetPropertyStringValue(txt.FieldName, txt.Format);

                //then try found in businessinfo.
                if (fieldValue.equals("Not Found"))
                    fieldValue=BusinessInfo.Current().GetPropertyStringValue(txt.FieldName);

                //tb = CreateTextBlock(fieldValue, txt);                       
                actualHeight=GetRequiredHeight(fieldValue, txt);

                if (txt.SkipIfEmpty == true && (fieldValue.equals("") ||  fieldValue.equals(Common.FormatDouble(0))))
                {
                }
                else
                {
                    if (txt.YOffset == 0)
                        currentHeight = preHeight;

                    currentHeight += txt.YOffset;

                    //TranslateTransform tf = new TranslateTransform();
                    //tf.X = txt.StartX;
                    //tf.Y = currentHeight;
                    //bitmap.Render(tb, tf);
                    DrawText(canvas, fieldValue, txt, currentHeight, lines);
                   
                    //currentHeight += Convert.ToInt32(tb.ActualHeight);

                    if (txt.YOffset == 0)
                        currentHeight = preHeight + actualHeight;
                    else
                    {
                        currentHeight += actualHeight;
                        preHeight = currentHeight - actualHeight;
                    }
                    
                    //return bitmap;
                } 
            }
            if (StartWith(line, "staticfield:"))
            {
                TextField txt = new TextField(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                actualHeight=GetRequiredHeight(txt.FieldName, txt);
                
                if (txt.YOffset == 0)
                    currentHeight = preHeight;

                currentHeight += txt.YOffset;

                DrawText(canvas, txt.FieldName, txt, currentHeight, lines);

                if (txt.YOffset == 0)
                {
                    currentHeight = preHeight + actualHeight;
                }
                else
                {
                    currentHeight += actualHeight;
                    preHeight = currentHeight - actualHeight;
                }
            }
            if(StartWith(line, "orderlinesblock:"))  
            {
            	 orderlineBlock = new OrderLineBlock(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                 currentHeight += orderlineBlock.YOffset;
                
                 Order[] orders=new Order[table.TableOrders().size()];		
                 table.TableOrders().toArray(orders);
         		 Arrays.sort(orders);

                if(printLayout.LayoutType==Define.LAYOUT_TYPE_KITCHEN && table.IsTakeAway==false) //kitchen restaurant
                {
                   currentHeight=PrintOrderSubGroup(table, orders, 1, printLayout, currentHeight, orderlineBlock, canvas, lstOrderlineField, lines);
                    currentHeight=PrintOrderSubGroup(table, orders, 2, printLayout, currentHeight, orderlineBlock, canvas, lstOrderlineField, lines);
                    currentHeight=PrintOrderSubGroup(table, orders, 3, printLayout, currentHeight, orderlineBlock, canvas, lstOrderlineField, lines);
                }
                else {
                    for (Order order : orders) {
                        if (IsOrderLineNeedToPrint(order, printLayout.LayoutType)) {
                            int maxHeight = 0;
                            for (OrderLine ol : lstOrderlineField) {
                                actualHeight = GetRequiredHeight(order.GetPropertyStringValue(ol.OrderFieldName), ol);
                                DrawText(canvas, order.GetPropertyStringValue(ol.OrderFieldName), ol, currentHeight, lines);

                                maxHeight = (maxHeight > actualHeight ? maxHeight : actualHeight);
                            }
                            currentHeight += (maxHeight + orderlineBlock.LineSpace);
                        }
                    }
                }
            }
           
            if (StartWith(line, "line:"))
            {  
                LineParam lineParam = new LineParam(printLayout.Params().get(iCount), printLayout.ScaleFactor());

                if (lineParam.YOffset == 0)
                {
                    currentHeight = preHeight + lineParam.YOffset;
                }
                else
                {
                    currentHeight += (lineParam.Thickness + lineParam.YOffset);
                    preHeight = currentHeight - lineParam.Thickness;
                    //keep y position in YOffset. it will be drawed later.
                    lineParam.YOffset = currentHeight - lineParam.Thickness;
                }

                lines.add(lineParam);
            }
            iCount++;
            Log.i(Define.APP_CATALOG, "process printlayout line:" + iCount);
            if(iCount==14)
            {
              Log.i(Define.APP_CATALOG, "Stop");
            }
        }//end for

        Log.i(Define.APP_CATALOG, "befor draw line");

        //must draw line after bitmap.Iinvalidate
        Paint paint = new Paint();    	
    	paint.setColor(BrushColor); 


        for (LineParam lineParam : lines)
        {
        	//for(int icount=0; icount<lineParam.Thickness; icount++)
        	//{
        	//	canvas.drawLine(lineParam.StartX, lineParam.YOffset + icount, lineParam.StartX+lineParam.Width, lineParam.YOffset+icount, paint);
        	//}
        	for(int icount=0; icount<lineParam.Thickness; icount++)
        	{
        		int endX=0;
        		 for (int icountx = lineParam.StartX; icountx < lineParam.Width + lineParam.StartX && icountx < bitmap.getWidth(); icountx++)
        		 {

        			 if(icountx+lineParam.BlackLenth >lineParam.Width + lineParam.StartX || icountx+lineParam.BlackLenth>= bitmap.getWidth())
        			 {
        				endX= lineParam.Width + lineParam.StartX;
        			 }
        			 else
        			 {
        				 endX=icountx+lineParam.BlackLenth;
        			 }

        			 canvas.drawLine(icountx, lineParam.YOffset + icount, endX, lineParam.YOffset+icount, paint);
                     icountx += lineParam.BlackLenth;                   
                     icountx += lineParam.WhiteLength;
        		 }
        		//canvas.drawLine(lineParam.StartX, lineParam.YOffset + icount, lineParam.StartX+lineParam.Width, lineParam.YOffset+icount, paint);
        	}
        }
       /*
            for (LineParam lineParam : lines)
            {
                for(int icount=0; icount<lineParam.Thickness; icount++)
                {
                	canvas.drawLine(lineParam.StartX, lineParam.YOffset + icount, lineParam.StartX+lineParam.Width, lineParam.YOffset+icount, paint);
                }
            }
            */
    	}catch(Exception e)
    	{
    		Log.e(Define.APP_CATALOG, e.toString());
    		e.printStackTrace();
    	}
    	 return bitmap;
    }


    private static Table CreateTable()
    {
        Table table = new Table("Table1", false);

        Order order = new Order(table);
        order.OrderMenuNr = "1";
        order.OrderCount = 1;
        order.OrderMenuName = "Tomatensoep";
        order.OrderPrice = 2;
        table.TableOrders().add(order);

        order = new Order(table);
        order.OrderMenuNr = "2";
        order.OrderCount = 3;
        order.OrderMenuName = "Kippensoep";
        order.OrderPrice = 1.8;
        table.TableOrders().add(order);

        return table;

    }
  
/*
    static private TranslateTransform CreateTranslateTransform(double x, double y)
    {
       TranslateTransform tf = new TranslateTransform();
        tf.X=x;
        tf.Y=y;
        return tf;
    }
*/
   
/*
    static private TextView CreateTextBlock(String text, CommonSetting setting)
    {
    	TextView tb = new TextView();            
        tb.Width = setting.Width;            
        tb.Foreground = new SolidColorBrush(BrushColor);
        tb.FontSize = setting.Fontsize;
        tb.FontFamily = new FontFamily(setting.Fontname);
        tb.TextAlignment=setting.HorizontalAlignment;            
        tb.FontWeight = setting.Bold;
        tb.Text = text;
        
        int icount = 1;
        while (tb.ActualWidth > setting.Width && icount<text.Length)
        {
            tb.Text = text.Substring(0, text.Length - icount);
            icount++;
        }
        return tb;
    }
*/
    static private int GetRequiredBitmapHeight(Table table, LinkedHashMap<String, integer> dicTextHeight, PrintLayout printLayout)
    {
    	int requiredBitmapHeight = 0;
    	try
    	{       
        int iCount = 0;
        
        List<OrderLine> lstOrderlineField = new ArrayList<OrderLine>();
        OrderLineBlock orderlineBlock = null;
        //get orderline
        for (String line : printLayout.Lines())
        {
        	
            if (StartWith(line, "orderlinesblock:"))
            {            	
                orderlineBlock = new OrderLineBlock(printLayout.Params().get(iCount), printLayout.ScaleFactor());
            }
            if (StartWith(line, "orderline:"))
            {
                OrderLine ol = new OrderLine(printLayout.Params().get(iCount), orderlineBlock, printLayout.ScaleFactor());
                lstOrderlineField.add(ol);
            }
            iCount++;
        }

        iCount = 0;
        int currentHeight = 0;
        int preHeight = 0;
        for (String line : printLayout.Lines())
        {
        	int actualHeight=0;
            if (StartWith(line, "fieldname:"))
            {
                TextField txt = new TextField(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                String fieldValue = table.GetPropertyStringValue(txt.FieldName, txt.Format);

                //then try found in businessinfo.
                if (fieldValue.equals("Not Found"))
                    fieldValue = BusinessInfo.Current().GetPropertyStringValue(txt.FieldName);

                actualHeight=GetRequiredHeight(fieldValue, txt);

                if (txt.YOffset == 0)
                {
                    currentHeight = preHeight + actualHeight;
                }
                else
                {
                    if (txt.SkipIfEmpty && fieldValue.equals(""))
                    {

                    }
                    else
                    {
                        currentHeight += actualHeight + txt.YOffset;
                        preHeight = currentHeight - actualHeight;
                    }
                }
            }
            if (StartWith(line, "staticfield:"))
            {
                TextField txt = new TextField(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                
                actualHeight=GetRequiredHeight(txt.FieldName, txt);
                if (txt.YOffset == 0)
                {
                    currentHeight = preHeight + actualHeight;
                }
                else
                {
                    currentHeight += actualHeight + txt.YOffset;
                    preHeight = currentHeight - actualHeight;
                }                    
            }

            if (StartWith(line, "orderlinesblock:"))
            {
                orderlineBlock = new OrderLineBlock(printLayout.Params().get(iCount), printLayout.ScaleFactor()); 
                currentHeight += orderlineBlock.YOffset;

                if(printLayout.LayoutType==Define.LAYOUT_TYPE_KITCHEN && table.IsTakeAway==false) //kitchen restaurant
                {
                    currentHeight+=GetOrderLineBlockKitchenGroupHeight(table,1, lstOrderlineField, orderlineBlock);
                    currentHeight+=GetOrderLineBlockKitchenGroupHeight(table,2, lstOrderlineField, orderlineBlock);
                    currentHeight+=GetOrderLineBlockKitchenGroupHeight(table,3, lstOrderlineField, orderlineBlock);
                }
                else {
                    for (Order order : table.TableOrders())
                    {
                        if (IsOrderLineNeedToPrint(order, printLayout.LayoutType)) {
                            int maxHeight = 0;
                            for (OrderLine ol : lstOrderlineField) {
                                actualHeight = GetRequiredHeight(order.GetPropertyStringValue(ol.OrderFieldName), ol);
                                maxHeight = (maxHeight > actualHeight ? maxHeight : actualHeight);
                            }
                            currentHeight += (maxHeight + orderlineBlock.LineSpace);
                        }
                    }
                }
            }

            if (StartWith(line, "line:"))
            {
                LineParam lineParam = new LineParam(printLayout.Params().get(iCount), printLayout.ScaleFactor());
                
                if (lineParam.YOffset == 0)
                {
                    currentHeight = preHeight + lineParam.YOffset;
                }
                else
                {
                    currentHeight += (lineParam.Thickness) + lineParam.YOffset;
                    preHeight = currentHeight - lineParam.YOffset;
                }
            }
            iCount++;
        }
        requiredBitmapHeight = currentHeight;
       
    	}
    	catch(Exception e)
    	{
    		Log.e(Define.APP_CATALOG, e.toString());
    		e.printStackTrace();
    	}
        return requiredBitmapHeight;
    }

    static int GetOrderLineMaxHeight(Order order, List<OrderLine> lstOrderlineField )
    {
        int maxHeight = 0;
        int actualHeight=0;
        for (OrderLine ol : lstOrderlineField) {
            actualHeight = GetRequiredHeight(order.GetPropertyStringValue(ol.OrderFieldName), ol);
            maxHeight = (maxHeight > actualHeight ? maxHeight : actualHeight);
        }
        return  maxHeight;
    }

    static boolean StartWith(String line, String header)
    {
    	boolean flag=false;
    	if(line.length()>header.length())
    	{
    		if(line.substring(0, header.length()).toLowerCase().equals(header.toLowerCase()))
    		{
    			flag=true;
    		}
    	}
    	return flag;
    }
    
    static void DrawText(Canvas canvas, String text, CommonSetting setting, int currentHeight, List<LineParam> lines)
    {
    	text=setting.GetTextWithPrefixSuffix(text);
    	Paint paint=GetTextPaint(setting); 
    	Rect bounds = new Rect();
    	paint.getTextBounds(text, 0, text.length(), bounds);
    	
    	String temptxt=text;
    	//Integer icount=1;
    	while(setting.Width!=0 && bounds.width()>setting.Width && temptxt.length()>0)
    	{
    		temptxt=temptxt.substring(0, temptxt.length()-1);
    		paint.getTextBounds(temptxt, 0, temptxt.length(), bounds); 
    	}
    	if(setting.HorizontalAlignment==Paint.Align.RIGHT)
    	{
    		canvas.drawText(temptxt, setting.StartX + setting.Width-bounds.width(), currentHeight, paint);
    	}
    	else if(setting.HorizontalAlignment==Paint.Align.CENTER)
    	{
    		canvas.drawText(temptxt, setting.StartX +(setting.Width-bounds.width())/2 , currentHeight, paint);
    	}
    	else
    	{
    		canvas.drawText(temptxt, setting.StartX, currentHeight, paint);    	
    	}
    	
    	 //AddUnderline(bounds, setting, lines, currentHeight);
    }
    
    private static void AddUnderline(Rect bounds, CommonSetting setting, List<LineParam> lines, int currentHeight)
    {
        if (lines == null)
            return;

        if (setting.IsUnderline)
        {
            LineParam lp = new LineParam();
            lp.Width = bounds.width();
            if (setting.HorizontalAlignment == Paint.Align.CENTER)
            {
                lp.StartX = (int)(setting.Width - bounds.width() )/ 2 + setting.StartX;
            }
            else   if (setting.HorizontalAlignment == Paint.Align.LEFT)
            {
                lp.StartX = setting.StartX;
            }
            else  if (setting.HorizontalAlignment == Paint.Align.RIGHT)
            {
                lp.StartX = (int)(setting.Width - bounds.width()) + setting.StartX;
            }

            lp.Thickness = setting.UnderlineThickness;
            lp.YOffset = currentHeight + lp.YOffset + bounds.height() + setting.UnderlineSpace;
            lp.BlackLenth = lp.Width;
            lp.WhiteLength = 0;

            lines.add(lp);
        }
    }
    
    static Paint GetTextPaint(CommonSetting setting)
    {
    	Paint paint = new Paint();    	
    	paint.setColor(BrushColor);    	    	
    	paint.setTypeface(GetFont(setting));// your preference here
    	paint.setTextSize(setting.Fontsize);// have this the same as your text size 
    	if(setting.IsUnderline){
    		paint.setUnderlineText(true);
    	}
    	//paint.setTextAlign(setting.HorizontalAlignment);
    
    	return paint;
    }
    
    static int GetRequiredHeight(String text, CommonSetting setting)
    {
    	Paint paint =GetTextPaint(setting);
    	Rect bounds = new Rect();    	
    	int text_height = 0;
    	int text_width = 0;    	
    	
    	paint.getTextBounds(text, 0, text.length(), bounds);

    	text_height =  bounds.height();
    	text_width =  bounds.width();
    	return text_height;
    }
   
    static LinkedHashMap<String, Typeface> typeFaceCache =new LinkedHashMap<String, Typeface>();
    
    static Typeface GetFont(CommonSetting setting)
    {
    	//android has 4 build in fonts: default, serif, monospace, sans_serif.
    	Typeface typeface;
    	if(typeFaceCache.containsKey(setting.Fontname))
    	{
    		typeface=typeFaceCache.get(setting.Fontname);
    	}
    	else if(setting.Fontname.toLowerCase().equals("serif"))
      {
    		typeface=Typeface.SERIF;
      }else if (setting.Fontname.toLowerCase().equals("sans_serif"))
      {
    	  typeface=Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD|Typeface.ITALIC); //  Typeface.SANS_SERIF;
      }
    	
      else if (setting.Fontname.toLowerCase().equals("monospace"))
      {
    	  typeface= Typeface.MONOSPACE;
      }
      else if (setting.Fontname.toLowerCase().equals("arial"))
      {
    	  if(setting.Bold==Typeface.BOLD)
    	  {
    		  typeface= Typeface.createFromAsset(MyApplication.getAppContext().getAssets(), "fonts/arialbd.ttf");
    	  }
    	  else
    	  {
    		  typeface= Typeface.createFromAsset(MyApplication.getAppContext().getAssets(), "fonts/arial.ttf");
    	  }

      }
      else
      {
    	  typeface= Typeface.DEFAULT;
      }
    	if(!typeFaceCache.containsKey(setting.Fontname))
    	{
    		typeFaceCache.put(setting.Fontname, typeface);
    	}
    	return typeface;		
    }
    
   
    static Boolean IsOrderLineNeedToPrint(Order order, int layoutType) throws Exception
    {
    	Boolean flag=false;
        Product menu=Product.GetMenuByNumber(order.OrderMenuNr);
        if (menu.MenusIsPrint && layoutType == Define.LAYOUT_TYPE_RESTAURANT)
        {
            flag = true;
        }else
        if (menu.MenuIsPrintKitchen == true && layoutType == Define.LAYOUT_TYPE_KITCHEN)
        {
            flag = true;
        }else
            if (menu.MenuIsBarMenu == true && layoutType == Define.LAYOUT_TYPE_BAR)
        {
            flag = true;
        }
        return flag;
    }

    public static int GetOrderLineBlockKitchenGroupHeight(Table table, int kitchenGroup, List<OrderLine> lstOrderlineField, OrderLineBlock orderlineBlock)
    {
        int requiredHeight = 0;
        if(table.HasGroup(kitchenGroup))
        {
            for (Order order : table.TableOrders())
            {
                if(order.KitchenGroup==kitchenGroup)
                {
                    requiredHeight += ( GetOrderLineMaxHeight(order, lstOrderlineField)+ orderlineBlock.LineSpace);
                }
            }
            if(table.HasNextGroup(kitchenGroup))
            {
                //add line after
                requiredHeight+=orderlineBlock.LineSpace * 2 + 2;
            }
        }
        return  requiredHeight;
    }

    public  static int PrintOrderSubGroup(Table table, Order[] orders, int kitchenGroup,  PrintLayout printLayout, int currentHeight, OrderLineBlock orderlineBlock, Canvas canvas, List<OrderLine> lstOrderlineField, List<LineParam> lines) throws Exception {
       int actualHeight;
        int maxHeight=0;
        if(table.HasGroup(kitchenGroup))
        {
            for (Order order : orders) {
                if (order.KitchenGroup==kitchenGroup && IsOrderLineNeedToPrint(order, printLayout.LayoutType)) {
                    maxHeight = 0;
                    for (OrderLine ol : lstOrderlineField) {
                        actualHeight = GetRequiredHeight(order.GetPropertyStringValue(ol.OrderFieldName), ol);
                        DrawText(canvas, order.GetPropertyStringValue(ol.OrderFieldName), ol, currentHeight, lines);

                        maxHeight = (maxHeight > actualHeight ? maxHeight : actualHeight);
                    }
                    currentHeight += (maxHeight + orderlineBlock.LineSpace);
                }
            }
            if(table.HasNextGroup(kitchenGroup))
            {
                currentHeight+=orderlineBlock.LineSpace;
                LineParam lineParam = new LineParam();
                lineParam.StartX=orderlineBlock.StartX;
                lineParam.YOffset=currentHeight - maxHeight;
                lineParam.Thickness=2;
                lineParam.Width=orderlineBlock.Width;
                lineParam.BlackLenth = lineParam.Width;
                lineParam.WhiteLength = 0;

                currentHeight+=2 + orderlineBlock.LineSpace;
                            /*
                            if (lineParam.YOffset == 0)
                            {
                                currentHeight = preHeight + lineParam.YOffset;
                            }
                            else
                            {
                                currentHeight += (lineParam.Thickness + lineParam.YOffset);
                                preHeight = currentHeight - lineParam.Thickness;
                                //keep y position in YOffset. it will be drawed later.
                                lineParam.YOffset = currentHeight - lineParam.Thickness;
                            }
                            */
                lines.add(lineParam);
            }
        }
        return  currentHeight;
    }
}

class TextField extends  CommonSetting
{
    public TextField(LinkedHashMap<String, String> items, double scaleFactor)            
    {    
    	super(items, scaleFactor);
    }     
}


class OrderLineBlock extends CommonSetting
{ 
	public int LineSpace;
    public OrderLineBlock(LinkedHashMap<String, String> items, double scaleFactor)
    {
    	super(items, scaleFactor);
        LineSpace = Integer.parseInt(items.get("linespace"));
    }
}

class CommonSetting
{
    public String FieldName;
    public int Width;
    public int Fontsize;
    public String Fontname;
    public int StartX;
    public int YOffset;
    public Paint.Align HorizontalAlignment; //gravity in android e.g. Gravity.CENTER
    //public VerticalAlignment VerticalAlignment;
    public int Bold;
    public boolean SkipIfEmpty;
    public String Format;
    public String ScaleFactor;
    public Boolean HasPrefixOrSuffix=false;
    public String FieldText="";
    public Boolean IsUnderline = false;
    public int UnderlineSpace = 0;
    public int UnderlineThickness = 1;

    public CommonSetting(LinkedHashMap<String, String> items, double scaleFactor)
    {
        String firstField="";      
        Set set = items.keySet();
        Iterator itr = set.iterator(); 
        while(itr.hasNext()) { 
        	firstField = (String) itr.next();
        
        	FieldName=items.get(firstField);
        	break;
        }            

        if (FieldName.contains("[") && FieldName.contains("]"))
        {
            HasPrefixOrSuffix = true;
            FieldText = FieldName;
            int startIndex=FieldName.indexOf("[") + 1;
            int endIndex=FieldName.indexOf("]");
            FieldName = FieldName.substring(startIndex, endIndex);
        }
        if (firstField.toLowerCase().equals("staticfield"))
        {
            items.put(firstField, items.get(firstField).replace(Define.DOUBLE_POINT_REPLACER, ":"));
        }

       
        Width = Common.GetWidth(items.get("width"), scaleFactor); // Convert.ToInt32(items["width"]); 
        StartX = Common.GetWidth(items.get("startx"), scaleFactor); // Convert.ToInt32(items["startx"]);

        if (items.containsKey("fontsize"))
        {
            Fontsize = Integer.parseInt(items.get("fontsize"));
        }
        else
            Fontsize = 36;


        if (items.containsKey("fontname"))
        {
            Fontname = items.get("fontname");
        }
        else
            Fontname = "Arial";
        
       

        if (items.containsKey("yoffset"))
        {
            YOffset = Integer.parseInt(items.get("yoffset"));
        }
        else
            YOffset = 0;


        if (items.containsKey("bold"))
        {
            if (items.get("bold").equals("true"))
                Bold = Typeface.BOLD;
            else
                Bold = Typeface.NORMAL;
        }
        else
            Bold = Typeface.NORMAL;

        if (items.containsKey("ha"))
        {
            if (items.get("ha").equals("right"))
                HorizontalAlignment = Paint.Align.RIGHT; // Gravity.RIGHT;
            else if (items.get("ha").equals("center"))
                HorizontalAlignment = Paint.Align.CENTER;
            else //default
                HorizontalAlignment = Paint.Align.LEFT;
        }
        else
            HorizontalAlignment = Paint.Align.LEFT;

        //if (items.ContainsKey("va"))
        //{
        //    if (items["va"] == "center")
        //        VerticalAlignment = VerticalAlignment.Center;
        //    else if (items["va"] == "bottom")
        //        VerticalAlignment = VerticalAlignment.Bottom;
        //    else //default
        //        VerticalAlignment = VerticalAlignment.Top;
        //}
        //else
        //    VerticalAlignment = VerticalAlignment.Top;

        if (items.containsKey("skipifempty"))
        {
            if (items.get("skipifempty").equals("true"))
                SkipIfEmpty = true;
            else
                SkipIfEmpty = false;
        }
        else
            SkipIfEmpty = false;

        if (items.containsKey("format"))
        {
            Format= items.get("format");                  
        }
        else
        {
            Format = "";
        }
        if (items.containsKey("underline") && !items.get("underline").equals(""))
        {
            IsUnderline = true;
            UnderlineSpace=Integer.parseInt(items.get("underline"));
          
        }
        else
        {
            IsUnderline = false;
            UnderlineSpace = 0;
        }
        if (items.containsKey("underlinethickness") && !items.get("underlinethickness").equals(""))
        {               
            UnderlineThickness = Integer.parseInt(items.get("underlinethickness"));              
        }
        else
        {
            UnderlineThickness = 1;               
        }
        
    }
    
    //
    public String GetTextWithPrefixSuffix(String input)
    {
        String result = input;
        if (HasPrefixOrSuffix)
        {
           result =  FieldText.replace(FieldName, input).replace("[", "").replace("]", "").replace("^", ":");
        }
        return result;
    }
}

class OrderLine extends CommonSetting
{
    public String OrderFieldName;
    //public int Width;
    //public int StartX;

    public OrderLine(LinkedHashMap<String, String> items, OrderLineBlock ob, double scaleFactor)           
    {
    	 super(items, scaleFactor);
        //Width = Convert.ToInt32(items["width"]);
        //StartX = Convert.ToInt32(items["startx"]);
        OrderFieldName = items.get("orderline");
        Fontsize = ob.Fontsize;
        Fontname = ob.Fontname;
    }     
}

class LineParam
{
    public int Width;
    public int StartX;
    public int Thickness;
    public int YOffset;
    public int BlackLenth;
    public int WhiteLength;

    public LineParam(){}
    
    public LineParam(LinkedHashMap<String, String> items, double scaleFactor)
    {            
        Width= Common.GetWidth(items.get("width"), scaleFactor);
        StartX =  Integer.parseInt(items.get("startx"));
        Thickness = Integer.parseInt(items.get("thickness"));
        YOffset = Integer.parseInt(items.get("yoffset"));
        if(items.containsKey("blacklength"))
        {
        	BlackLenth=Integer.parseInt(items.get("blacklength"));
        }
        else
        {
        	BlackLenth=Width;
        }
        if(items.containsKey("whitelength"))
        {
        	WhiteLength=Integer.parseInt(items.get("whitelength"));
        }
        else
        {
        	WhiteLength=0;
        }
    }
}
