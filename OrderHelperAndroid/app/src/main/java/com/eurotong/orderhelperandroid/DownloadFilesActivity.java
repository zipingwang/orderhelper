package com.eurotong.orderhelperandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class DownloadFilesActivity extends Activity implements OnClickListener {

	Button btnStartDownloadFiles;
	Button btnSaveCustomerID;
	EditText editTextCustomerID;
	EditText editTextServerAddress;
	Button btnGoBack;
    CheckBox cbxDownloadMenuFile;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_files);
        btnStartDownloadFiles=(Button)findViewById(R.id.btnStartDownloadFiles);
        btnStartDownloadFiles.setOnClickListener(this);
        btnSaveCustomerID=(Button)findViewById(R.id.btnSaveCustomerID);
        btnSaveCustomerID.setOnClickListener(this);
        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        cbxDownloadMenuFile=(CheckBox)findViewById(R.id.cbkDownloadMenu);
        btnGoBack.setOnClickListener(this);
        editTextCustomerID=(EditText)findViewById(R.id.editTextCustomerID);
    	editTextServerAddress=(EditText)findViewById(R.id.editTextServerAddress);
    	
    	editTextCustomerID.setText(Customer.Current().CustomerID);
    	editTextServerAddress.setText(Customer.Current().ServerAddress);

        cbxDownloadMenuFile.setChecked(!Common.ExistsInStorage(Define.MENU_FILE_NAME));
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnStartDownloadFiles)
		{
            if(cbxDownloadMenuFile.isChecked() && Common.ExistsInStorage(Define.MENU_FILE_NAME))
            {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.msg_menufile_already_exists)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Common.downloadFiles(true);
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
            else
			    Common.downloadFiles(cbxDownloadMenuFile.isChecked());
		}	
		if(v.getId()==R.id.btnGoBack)
		{
			finish();
		}
		if(v.getId()==R.id.btnSaveCustomerID)
		{
			try
			{
			String serverAddress= editTextServerAddress.getText().toString().trim(); // txtServerAddress.Text.Trim(); 
            long id=-1;
            String idString=editTextCustomerID.getText().toString().trim();
            Boolean isValid = false;
            id=Long.parseLong(idString);
            if (idString.length()>5 && idString.length()<20)
            {
                long mod;
                long remainder;
                mod = id / 100;
                remainder = mod % Define.MOD;
                if (Long.parseLong(idString.substring(idString.length() - 2, idString.length())) == remainder)
                {
                    isValid = true;
                    Customer.Current().CustomerID = idString;
                    Customer.Current().ServerAddress =serverAddress;
                    try {
						Common.SaveToIsolatedStorage(Customer.Current().ToString(), Define.CUSTOMER_ID_FILE);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    Common.GetToastLong(getString(R.string.msg_save_success)).show();		                    
                }               
            }
            if(!isValid)
            {
            	Common.GetToastLong("客户号码无效").show();
            }
			}catch(Exception e)
			{
				Common.GetToastLong("保存客户号码发生错误").show();
				Log.e(Define.APP_CATALOG, e.toString());
			}
			
		}
	}

  


}
