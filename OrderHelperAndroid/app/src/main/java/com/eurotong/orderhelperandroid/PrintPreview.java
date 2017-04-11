package com.eurotong.orderhelperandroid;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import android.R.layout;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TableLayout;
import android.widget.Toast;

public class PrintPreview extends Activity implements OnClickListener {

	Button btnPrint;
	Button btnPrintKitchen;
	Button btnPrintBar;
	Button btnExit;
	ImageView imageViewTable;
	TableLayout LayoutTest;
	Button btnDownload;
	Button btnRedrawPrint;
	Button btnRedrawKintchPrint;
	Button btnRedrawBarPrint;
	
	Table _table;
	Boolean _busy = false;
	DateTime _preTap;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_preview);
        
        btnPrint=(Button)findViewById(R.id.btnPrint);
        btnPrintKitchen=(Button)findViewById(R.id.btnPrintKitchen);
        btnPrintBar=(Button)findViewById(R.id.btnPrintBar);
        btnExit=(Button)findViewById(R.id.btnExit);
        imageViewTable=(ImageView)findViewById(R.id.imageViewTable);
        LayoutTest=(TableLayout)findViewById(R.id.LayoutTest);
        btnDownload=(Button)findViewById(R.id.btnDownload);
        btnRedrawPrint=(Button)findViewById(R.id.btnRedrawPrint);
        btnRedrawKintchPrint=(Button)findViewById(R.id.btnRedrawKintchPrint);
        btnRedrawBarPrint=(Button)findViewById(R.id.btnRedrawBarPrint);
        
        if(User.Current().HasRight(Define.UR_PRINT))
		{
        	btnPrint.setVisibility(View.VISIBLE);
		}
		else
		{
			btnPrint.setVisibility(View.GONE);
		}
        
        if(User.Current().HasRight(Define.UR_PRINT_BAR))
		{
        	btnPrintBar.setVisibility(View.VISIBLE);
		}
		else
		{
			btnPrintBar.setVisibility(View.GONE);
		}
        
        if(User.Current().HasRight(Define.UR_PRINT_KITCHEN))
 		{
        	btnPrintKitchen.setVisibility(View.VISIBLE);
 		}
 		else
 		{
 			btnPrintKitchen.setVisibility(View.GONE);
 		}
        		
        //set listener
        btnPrint.setOnClickListener(this);
        btnPrintKitchen.setOnClickListener(this);
        btnPrintBar.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnRedrawPrint.setOnClickListener(this);
        btnRedrawKintchPrint.setOnClickListener(this);
        btnRedrawBarPrint.setOnClickListener(this);
        
        _table=Common.GetTableFromIntent(this);
        
        DoDrawPrintPreview(MyApplication.getAppContext().getResources().getConfiguration().orientation, PrintLayout.Current());       
        _preTap = new DateTime();
        _preTap= _preTap.minusDays(1);
        
        if(User.Current().HasRight(Define.UR_DEBUG_PRINT_LAYOUT))
        {
        	LayoutTest.setVisibility(View.VISIBLE);         
        }
        else
        {
        	LayoutTest.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_print_preview, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnPrint)
		{			
           if(User.Current().HasRight(Define.UR_PRINT))
           {
        	   DoPrintPos(_table, PrintLayout.Current(), Setting.Current().NumberOfPrints);	
           }
		}
		else if(v.getId()==R.id.btnPrintKitchen)
		{
			if(User.Current().HasRight(Define.UR_PRINT_KITCHEN))
           {
			DoPrintPos(_table, PrintLayout.KitchenLayout(), Setting.Current().NumberOfPrintsKitchen);
           }
		}
		else if(v.getId()==R.id.btnPrintBar)
		{
			if(User.Current().HasRight(Define.UR_PRINT_BAR))
           {
			DoPrintPos(_table, PrintLayout.BarLayout(), Setting.Current().NumberOfPrintsBar);
           }
		}
		else if(v.getId()==R.id.btnExit)
		{
			//close current activity
			finish();
		}
		else if(v.getId()==R.id.btnDownload)
		{
			Common.downloadFiles(false);
		}
		else if(v.getId()==R.id.btnRedrawPrint)
		{
			 DoDrawPrintPreview(MyApplication.getAppContext().getResources().getConfiguration().orientation, PrintLayout.Current());
		}
		else if(v.getId()==R.id.btnRedrawKintchPrint)
		{
			 DoDrawPrintPreview(MyApplication.getAppContext().getResources().getConfiguration().orientation, PrintLayout.KitchenLayout());
		}
		else if(v.getId()==R.id.btnRedrawBarPrint)
		{
			 DoDrawPrintPreview(MyApplication.getAppContext().getResources().getConfiguration().orientation, PrintLayout.BarLayout());
		}
	}
	
	private void DoPrintPos(Table table, PrintLayout printlayout, int numberOfPrints) //( printpos)	   
    {
		DoPrintPos_Sync(table, printlayout, numberOfPrints, Setting.Current().IsAsyncPrinting);
    }
		
		/*
	   private void DoPrintPos_async(Table table, PrintLayout printlayout, int numberOfPrints, Boolean isAsync) //( printpos)	   
       {
		 
		   DateTime now=new DateTime();
		   int maxWaitTime=15000; //15 secondes
		   if(now.isBefore(_preTap.plusMillis(maxWaitTime)))
		   {
			   return;
		   }
		   _preTap=now;
		   
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
                           result = printpos.PrintPosLan();
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
                   if (result.toLowerCase().equals("success"))
	               {
	                   Common.GetToastShort(R.string.msg_print_success).show();
	                  
	               }
	               else
	               {
	            	   Common.GetToastShort(R.string.msg_print_error).show();
	            	   
	               }
                   
                   //at least wait Setting.Current().PrintTwoWaitTime + Setting.Current().getPrintwaittime()
                   DateTime thisMoment= new DateTime();
                   Interval timespan=new Interval(_preTap, thisMoment);
                   long elapsedTime= timespan.toDurationMillis();
                   if(elapsedTime>(Setting.Current().PrintTwoWaitTime + Setting.Current().getPrintwaittime()) * numberOfPrints)
                   {
                	   _preTap=_preTap.minusDays(1);
                   }
                   else
                   {
                	   _preTap=_preTap.minusMillis((int) (maxWaitTime - (Setting.Current().PrintTwoWaitTime + Setting.Current().getPrintwaittime())* numberOfPrints));
                   }                  
               }
               else
               {
               	Common.GetToastLong(R.string.msg_print_error_no_license).show();
               }
               _busy = false;
           }            
       }
	   
	   */
		
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
                	   msg=getText(R.string.msg_print_success).toString();	                  
	               }
	               else
	               {
	            	   msg=getText(R.string.msg_print_error).toString();            	   
	               }  
                 //Common.GetToastShort(R.string.msg_print_success).show();
            	   new AlertDialog.Builder(this)
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
	   
	   void DoDrawPrintPreview(int orientation, PrintLayout printlayout)
	   {
		   if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			   imageViewTable.setMaxWidth(PrintLayout.Current().PrintWidth);
			   imageViewTable.setMinimumWidth(PrintLayout.Current().PrintWidth);
			   imageViewTable.setScaleType(ScaleType.FIT_START);
		    } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
		    	 imageViewTable.setMaxWidth(PrintLayout.Current().DeviceWidth);
				 imageViewTable.setMinimumWidth(PrintLayout.Current().DeviceWidth);				
				 imageViewTable.setScaleType(ScaleType.CENTER_INSIDE);
		    }
		   	 printlayout.IsPrintMode=true;		         
	         Bitmap bitmap= RestsoftBitmap.SetupRenderedTextBitmap(_table, printlayout);
	         if(bitmap!=null)
	         {
	         	Log.i(Define.APP_CATALOG, "bitmap is not null");
	         	imageViewTable.setImageBitmap(bitmap);
	         	
	         }
	         {
	         	Log.i(Define.APP_CATALOG, "bitmap is null");
	         } 
	   }
	   
	 //http://stackoverflow.com/questions/5726657/how-to-detect-orientation-change-in-layout-in-android
		@Override
		public void onConfigurationChanged(Configuration newConfig) {
		    super.onConfigurationChanged(newConfig);

		    //http://stackoverflow.com/questions/1016896/android-how-to-get-screen-dimensions
		    Display display = getWindowManager().getDefaultDisplay();
		    Point size = new Point();
		    display.getSize(size);
		    int width = size.x;
		    int height = size.y;
		    
		    DoDrawPrintPreview(newConfig.orientation, PrintLayout.Current());
		  }
}
