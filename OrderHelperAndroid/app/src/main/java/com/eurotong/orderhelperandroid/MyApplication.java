package com.eurotong.orderhelperandroid;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

//http://stackoverflow.com/questions/2002288/static-way-to-get-context-on-android
public class MyApplication extends  Application {
	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        MyApplication.context = getApplicationContext();
	    }

	    public static Context getAppContext() {
	        return MyApplication.context;
	    }
	    
	    public static boolean TablesLoadedFromStorage=false;
	    
	    //public static boolean TablesNeedUpdated=false;
	    
	    //public static boolean TableOrdersNeedUpdated=false;
	    
	    public static boolean NeedUpdate=true;
	    
	    
	    
	    /*
	     //Intent.FLAG_ACTIVITY_CLEAR_TOP stil create new instance of TablesOverviewActivity. it proberly not clear other activity, because it becomes slow after some time
	    public static void GoToHome()
	    {
	    	Intent intent = new Intent(MyApplication.context, TablesOverviewActivity.class);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);	    	
	    	MyApplication.context.startActivity(intent);
	    }
	    */
	  
}
