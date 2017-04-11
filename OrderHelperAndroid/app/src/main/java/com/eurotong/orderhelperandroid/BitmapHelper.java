package com.eurotong.orderhelperandroid;

import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class BitmapHelper {
	   //#region ESC/POS
       //public static void PrintBitmap(BitmapData data, 
       public static BitmapData GetBitmapData(Bitmap bitmap)
       {    	   
           //BitmapHelper.Create1BitBitmapFileInIsolateStorage(bitmap, "img2.bmp");
           //var threshold = 127;
           int index = 0;
           int bitmapWidth= bitmap.getWidth() ;
           int bitmapHeight= bitmap.getHeight();
           int dimensions = bitmapWidth * bitmapHeight;
           //var dots = new System.Collections.BitArray(dimensions);
           //dots.SetAll(false);
           byte[] dots = new byte[dimensions];
           IntBuffer buffer=IntBuffer.allocate(dimensions);
           bitmap.copyPixelsToBuffer(buffer);
           int[] pixels=buffer.array();
           
           int b = 0;
           int g = 0;
           int r = 0;
           int a=0;
           int threshHold= Setting.Current().BlackDepth;
           for(int y=0; y<bitmapHeight; y++)
           {
	           for(int x=0; x<bitmapWidth; x++)
	           {
	        	 dots[y*  bitmapWidth + x] = (byte)0; // true;
	        	
	        	if(pixels[y*  bitmapWidth + x]!=0)
	        	{	        		
	        		 //must add & 0xFF otherwise 255 gives -1
	        		 a = (pixels[y*  bitmapWidth + x] & 0xFF000000) >> 24 & 0xFF;
	           		 /*this is windows a rgb order
	        		 b = (pixels[y*  bitmapWidth + x] & 0x0000FF) & 0xFF;
	                 g = (pixels[y*  bitmapWidth + x] & 0x00FF00) >> 8 & 0xFF;
	                 r = (pixels[y*  bitmapWidth + x] & 0xFF0000) >> 16 & 0xFF;	 
	                 */
	           		//not same order as windows argb
	                 r = (pixels[y*  bitmapWidth + x] & 0x0000FF) & 0xFF;
	                 g = (pixels[y*  bitmapWidth + x] & 0x00FF00) >> 8 & 0xFF;
	                 b = (pixels[y*  bitmapWidth + x] & 0xFF0000) >> 16 & 0xFF;	
	                 if(r>threshHold)
	                 {
	                	 dots[y*  bitmapWidth + x] = (byte)1; // true;
	                 }
	        	}
	        	else
	        	{
	        		// b,g r, is zero but a is also 0 zo it is 100% transparant, it is white
	        	}
	           }
       		}           
         
           BitmapData bitmapData=new BitmapData();
           bitmapData.Dots=dots;
           bitmapData.Height = bitmap.getHeight();
           bitmapData.Width= bitmap.getWidth()	;	   
           return bitmapData;          
       }
       
       public static boolean IsSingnificentBit(byte r, byte g, byte b)
       {
           //if (r == (byte)0 && g == (byte)0 && b == (byte)0)
           //if (r == (byte)255 && g == (byte)255 && b == (byte)255)
    	   boolean flag;
           if (r > Setting.Current().BlackDepth)
           {
        	   flag= true;
           }
           else
           {
               flag= false;
           }
           return flag;
       }
//#endregion
}
