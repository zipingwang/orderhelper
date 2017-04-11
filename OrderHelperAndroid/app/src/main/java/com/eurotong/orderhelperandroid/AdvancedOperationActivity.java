package com.eurotong.orderhelperandroid;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AdvancedOperationActivity extends Activity {

	Button btnDownloadMenu;
	Button btnEditBusinessInfo;
	Button btnParameter;
	Button btnExit;
	Button btnLogin;		
	Button btnDeleteAllTables;
	Button btnAbout;
	Button btnEditMenu;
	Button btnReport;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_operation);
        
        // set the custom dialog components - text, image and button
		btnDownloadMenu=(Button)findViewById(R.id.btnDownloadMenu);
		btnEditBusinessInfo=(Button)findViewById(R.id.btnEditBusinessInfo);
		btnParameter=(Button)findViewById(R.id.btnParameters);
		btnExit=(Button)findViewById(com.eurotong.orderhelperandroid.R.id.btnExit);
		btnLogin=(Button)findViewById(R.id.btnLogin);		
		btnDeleteAllTables=(Button)findViewById(R.id.btnDeleteAllTables);
		btnAbout=(Button)findViewById(R.id.btnAbout);		
		btnEditMenu=(Button)findViewById(R.id.btnEditMenu);	
		btnReport=(Button)findViewById(R.id.btnReport);

		SetVisibility();
		
		// if button is clicked, close the custom dialog
		btnDownloadMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(v.getContext(), DownloadFilesActivity.class);
				startActivity(i);
			}
		});
		btnEditBusinessInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent i = new Intent(context, TableDetailActivity.class);					
				//startActivity(i);							
			}
		});
		btnParameter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Intent i = new Intent(context, TableDetailActivity.class);					
				//startActivity(i);						
			}
		});
		btnExit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {								
				finish();
			}
		});
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {								
				Intent i=new Intent(v.getContext(), UserLoginActivity.class);
				startActivity(i);
			}
		});
		btnDeleteAllTables.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				
				new AlertDialog.Builder(v.getContext())
		           .setMessage(R.string.msg_are_your_sure_to_delete_all_tables)
		           .setCancelable(false)
		           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int id) {
		            	TableHelper.TableList().clear();
		            	TableHelper.SaveTablesToStorage();			            	
		            	finish();
		            	//Intent i=new Intent(MyApplication.getAppContext(), TablesOverviewActivity.class);
						//startActivity(i);
		               }
		           })
		           .setNegativeButton(R.string.no, null)
		           .show();
			}
		});
		btnAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent i=new Intent(v.getContext(), VersionInfoActivity.class);
				startActivity(i);
			}
		});
		btnEditMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				Intent i=new Intent(v.getContext(), EditMenuOverviewActivity.class);
				startActivity(i);
			}
		});
		btnReport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i=new Intent(v.getContext(), ReportActivity.class);
				startActivity(i);
			}
		});
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	//when user login closed(finish), then goes to here.
    	SetVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_advanced_operation, menu);
        return true;
    }
    
    private void SetVisibility()
    {
    	if(User.Current().HasRight(Define.UR_DELETE_ALL_TABLES))
		{
			btnDeleteAllTables.setVisibility(View.VISIBLE);
		}
		else
		{
			btnDeleteAllTables.setVisibility(View.GONE);
		}
		
		if(User.Current().HasRight(Define.UR_CONFIGURE_PARAMETERS))
		{
			btnParameter.setVisibility(View.VISIBLE);
		}
		else
		{
			btnParameter.setVisibility(View.GONE);
		}
		
		if(User.Current().HasRight(Define.UR_ADVANCED_OPERATION))
		{
			btnDownloadMenu.setVisibility(View.VISIBLE);
			btnEditBusinessInfo.setVisibility(View.VISIBLE);
			btnEditMenu.setVisibility(View.VISIBLE);
			btnAbout.setVisibility(View.VISIBLE);
			btnReport.setVisibility(View.VISIBLE);
		}
		else
		{
			btnDownloadMenu.setVisibility(View.GONE);
			btnEditBusinessInfo.setVisibility(View.GONE);
			btnEditMenu.setVisibility(View.GONE);
			btnAbout.setVisibility(View.GONE);
			btnReport.setVisibility(View.GONE);
		}
    }
}
