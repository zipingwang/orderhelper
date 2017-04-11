package com.eurotong.orderhelperandroid;

import android.util.Log;


public class ESCPOSCommand
{

    //STAR Command line
   public static byte[] GetSTARBitmapByteArray(BitmapData data)
        {
            byte[] resultBytes = new byte[0];
            byte[] dots = data.Dots;
           
            //set second byte to 0 because image width never larger then 8*256.
            byte[] lineHeader = new byte[3];
            lineHeader[0]= (byte)98;
            lineHeader[1]=(byte)((data.Width + 7) / 8);
            lineHeader[2]=(byte)0;

            int dataLineWidth = (data.Width + 7) / 8 +3;
            int starLineWidth=dataLineWidth+3;
            byte[] STARDataBody=new byte[data.Height * starLineWidth];
           
            for (int y = 0; y < data.Height; y++)
            {
                //bw.Write(lineHeader);
                for(int icount=0; icount< lineHeader.length; icount++)
                {
                    STARDataBody[y * starLineWidth + icount] = lineHeader[icount];
                }
                for (int x = 0; x < (data.Width + 7); x += 8)
                {
                    byte slice = 0;
                    for (int b = 0; b < 8; b++)
                    {
                        int i = (y * data.Width) + x + b;

                        byte v = (byte)0;
                        if (i < dots.length && (x + b) < data.Width)
                        {
                            v = dots[i];
                        }

                        slice |= (byte)(v << (7 - b));
                    }
                    STARDataBody[y * starLineWidth + 3 + x/8] = slice;
                }
            }

            //write end command
            //bw.Write(STAREndCommand);

            //byte[] cutMode = new byte[] { 27, 100, 49};
            //bw.Write(cutMode);

            //resultBytes = ms.ToArray();
            //ms.Close();
            resultBytes = new byte[STARBeginCommand().length + STARDataBody.length + STAREndCommand().length];
            for(int icount=0; icount<STARBeginCommand().length; icount++)
            {
            	resultBytes[icount]=STARBeginCommand()[icount];
            }
            for(int icount=0; icount<STARDataBody.length; icount++)
            {
            	resultBytes[STARBeginCommand().length + icount]=STARDataBody[icount];
            }
            for(int icount=0; icount<STAREndCommand().length; icount++)
            {
            	resultBytes[STARBeginCommand().length + STARDataBody.length+ icount]=STAREndCommand()[icount];
            }          
            return resultBytes;
        }
   


    public static byte[] STARBeginCommand()
    {
        
            //test line byte[] beginCommand = new byte[] { 27, 42, 114, 82, 27, 42, 114, 65, 27, 42, 114, 67, 27, 42, 114, 84, 50, 0, 27, 42, 114, 81, 49, 0, 27, 42, 114, 80, 48, 0, 27, 42, 114, 109, 108, 48, 0, 27, 42, 114, 109, 114, 48, 0, 27, 42, 114, 70, 51, 0, 27, 42, 114, 69, 51, 0 };
            byte[] beginCommand = new byte[] 
            { 27, 42, 114, 82, 27, 42, 114, 65, 27, 42, 114, 67, 27, 42, 114, 84, 50, 0, 27, 42, 114, 81, 49, 0, 27, 42, 114, 80, 48, 0, 27, 42, 114, 109, 108, 48, 0, 27, 42, 114, 109, 114, 48, 0, 27, 42, 114, 70, 57, 0, 27, 42, 114, 69, 57, 0 };
            //esc   *   r   R  esc   *   r  A   esc *     r  C  esc   *   r    T  2?  null esc *   r  Q    1  null esc *  r    P   0  null esc *   r   m    l   0  null esc *    r   m    r    0  null esc *  r   F   9   null esc *  r    E  9   null
            return beginCommand;
        
    }
    public static byte[] STAREndCommand()
    {
       
            byte[] endCommand = new byte[] { 27, 42, 114, 66 };
            // test line byte[] endCommand = new byte[] {27,  12,  4, 27,  12,  0, 27, 42, 114, 66 };
            return endCommand;
       
    }
    
    
    //ESC/POS printer
    public static byte[] GetESCPOSBitmapByteArray(BitmapData data)
    {
    	byte[] resultBytes = new byte[0];
    
    	try
    	{
        int offset = 0;       
        byte[] dots = data.Dots;
        byte[] nLnH =new byte[2];
        // e.g width=576, nLnH[0] is 64, nLnH[1] is 2
        nLnH[0]=(byte)(data.Width & 0x00FF);        
        nLnH[1]=(byte)((data.Width & 0xFF00) >> 8); 
        // Set the line spacing to 24 dots, the height of each "stripe" of the
        // image that we're drawing.
        byte[] ESCPOSBeginRasterCommand=new byte[]{(byte)AsciiControlChars.Escape, (byte)'3', (byte)24};     
        byte[] ESCPOSRowHeader=new byte[]{(byte)AsciiControlChars.Escape, (byte)'*', (byte)33, nLnH[0], nLnH[1]};
        int ESCPOSDataWidth= (data.Width*24 +23)/24 * 3 + ESCPOSRowHeader.length + 1; //5 is row header width; 1 is row foot;        
        int ESCPOSDataHeight=(data.Height+23)/24;
        byte[] ESCPOSData=new byte[ESCPOSDataWidth*ESCPOSDataHeight];       
        int iCount=0;
        while (offset < data.Height)
        {
        	for(int iheader=0; iheader<ESCPOSRowHeader.length; iheader++)
        	{
        		ESCPOSData[iCount]=ESCPOSRowHeader[iheader];
        		iCount++;
        	}
            

            for (int x = 0; x < data.Width; ++x)
            {
                for (int k = 0; k < 3; ++k)
                {
                    byte slice = 0;

                    for (int b = 0; b < 8; ++b)
                    {
                        int y = (((offset / 8) + k) * 8) + b;

                        // Calculate the location of the pixel we want in the bit array.
                        // It'll be at (y * width) + x.
                        int i = (y * data.Width) + x;

                        // If the image is shorter than 24 dots, pad with zero.
                        //bool v = false;
                        //if (i < dots.Length)
                        //{
                        //    v = dots[i];
                        //}

                        //slice |= (byte)((v ? 1 : 0) << (7 - b));
                        byte v = (byte)0;
                        if (i < dots.length)
                        {
                            v = dots[i];
                        }

                        slice |= (byte)(v << (7 - b));
                    }
                    ESCPOSData[iCount]= slice;
                    iCount++;
                    if(iCount>=ESCPOSData.length)
                    {
                    	Log.i(Define.APP_CATALOG, "index out of range");
                    }
                   
                }
            }
            
            offset += 24;
            ESCPOSData[iCount]= (byte)AsciiControlChars.Newline;
            iCount++;           
        }

        byte[] ESCPOSSetLineSpaceCommand=new byte[]{(byte)AsciiControlChars.Escape, (byte)'3', (byte)30};
        // Restore the line spacing to the default of 30 dots.
        //bw.Write(AsciiControlChars.Escape);
        //bw.Write('3');
        //bw.Write((byte)30);

        //GS V m n
        //cut pager
        //n is how large space between last print place and cut.
        //65 is full cut, 66 is patial cut. but for A10 it is same.
        //byte[] cutPage = new byte[4] {29, 86, 65, 10};
        //bw.Write(cutPage);
        byte[] ESCPOSCutPageCommand=new byte[]{(byte)29, (byte)86, (byte)65, (byte)10};
        
        resultBytes=new byte[ESCPOSBeginRasterCommand.length + ESCPOSData.length +ESCPOSSetLineSpaceCommand.length+ ESCPOSCutPageCommand.length ];
        
        for(int icount=0; icount<ESCPOSBeginRasterCommand.length; icount++)
        {
        	resultBytes[icount]=ESCPOSBeginRasterCommand[icount];
        }
        
        for(int icount=0; icount<ESCPOSData.length; icount++)
        {
        	resultBytes[icount + ESCPOSBeginRasterCommand.length]=ESCPOSData[icount];
        }
        
        for(int icount=0; icount<ESCPOSSetLineSpaceCommand.length; icount++)
        {
        	resultBytes[icount + ESCPOSBeginRasterCommand.length + ESCPOSData.length]=ESCPOSSetLineSpaceCommand[icount];
        }
        
        for(int icount=0; icount<ESCPOSCutPageCommand.length; icount++)
        {
        	resultBytes[icount + ESCPOSBeginRasterCommand.length + ESCPOSData.length + ESCPOSSetLineSpaceCommand.length]=ESCPOSCutPageCommand[icount];
        }
        
     
    	}
    	catch(Exception e)
    	{
    		Log.e(Define.APP_CATALOG, e.toString());
    	}
        return resultBytes;
    }
   
}
