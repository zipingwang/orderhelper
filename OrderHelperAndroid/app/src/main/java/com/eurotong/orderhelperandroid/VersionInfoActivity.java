package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VersionInfoActivity extends Activity implements OnClickListener {

	TextView textviewVersion;
	Button btnGoBack;
	EditText editTextDeviceID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
	    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_version_info);
	        
	        textviewVersion=(TextView)findViewById(R.id.textviewVersion);
	        editTextDeviceID=(EditText)findViewById(R.id.editTextDeviceID);
	        btnGoBack=(Button)findViewById(R.id.btnGoBack);
	        btnGoBack.setOnClickListener(this);
	        
	        String deviceID= Common.GetDeviceUniqueID();
	        editTextDeviceID.setText(deviceID);
	        String versionInfo = "";
	        versionInfo = versionInfo + getText(R.string.lbl_software_version);
	        versionInfo = versionInfo + Define.VERSION;
	        versionInfo = versionInfo + Define.NEW_LINE;
	
	        versionInfo = versionInfo + getText(R.string.lbl_setting_version);
	        versionInfo = versionInfo + Setting.Current().get_version();
	        versionInfo = versionInfo + Define.NEW_LINE;
	        
	        versionInfo = versionInfo + getText(R.string.lbl_ip_address);
	        versionInfo = versionInfo + Common.GetIpAddress();
	        versionInfo = versionInfo + Define.NEW_LINE;
	        
	        versionInfo = versionInfo + getText(R.string.lbl_wifi_id);
	        versionInfo = versionInfo + Common.GetWifiSSID();
	        versionInfo = versionInfo + Define.NEW_LINE;
	    	
	        textviewVersion.setText(versionInfo);	    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_version_info, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnGoBack)
		{
			finish();
		}		
	}
}
