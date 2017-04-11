package com.eurotong.orderhelperandroid;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TableDetailActivity extends Activity implements OnClickListener {

	Button btnSave;
	Button btnGoBack;
	EditText editTextTableNr;
	EditText editTextTablePersonCount;
	EditText editTextTableSequenceNumber;
	EditText editTextTableOrderTimeDay;
	EditText editTextTableOrderTimeHour;
	EditText editTextTablePrintTimeDay;
	EditText editTextTablePrintTimeHour;
	EditText editTextTableOperator;
	
	
	
	Table _table=null;
	Boolean _isNewTalbe=true;
	boolean _isTakeAway=false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_table_detail);
    	
         super.onCreate(savedInstanceState);
         this.requestWindowFeature(Window.FEATURE_NO_TITLE);
         // hide statusbar of Android
         // could also be done later
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
         setContentView(R.layout.activity_table_detail);
         //http://mobile-development.org/index.php/android/android-how-to-run-application-in-fullscreen-mode
         // set window flags to display in fullscreen
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);  
         

         
        _table=Common.GetTableFromIntent(this);

		Bundle extras = getIntent().getExtras();
		if(extras!=null) {
			String isTakeAway = extras.getString(Define.TAKEAWAYTABLE);
			if(isTakeAway!=null && !isTakeAway.isEmpty())
				_isTakeAway=true;
			else
			    _isTakeAway=false;
		}

		btnSave=(Button)findViewById(R.id.btnSave);
        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        btnSave.setOnClickListener(this);
        btnGoBack.setOnClickListener(this);
        editTextTableNr=(EditText)findViewById(R.id.editTextTableNr);
        editTextTablePersonCount=(EditText)findViewById(R.id.editTextTablePersonCount);
        editTextTableSequenceNumber=(EditText)findViewById(R.id.editTextTableSequenceNumber);
        editTextTableOrderTimeDay=(EditText)findViewById(R.id.editTextTableOrderTimeDay);
        editTextTableOrderTimeHour=(EditText)findViewById(R.id.editTextTableOrderTimeHour);
        editTextTablePrintTimeDay=(EditText)findViewById(R.id.editTextTablePrintTimeDay);
        editTextTablePrintTimeHour=(EditText)findViewById(R.id.editTextTablePrintTimeHour);
        editTextTableOperator=(EditText)findViewById(R.id.editTextTableOperator);
       
        
        if(_table!=null)
        {     
        	_isNewTalbe=false;
        	 SetTextBoxValue( editTextTableNr, _table.TableNr);
             SetTextBoxValue( editTextTablePersonCount, _table.TablePersonCount);
             SetTextBoxValue(editTextTableSequenceNumber, _table.TableSequenceNumber);            
             SetTextBoxValue(editTextTableOrderTimeDay, _table.TableOrderTimeDay);
             SetTextBoxValue(editTextTableOrderTimeHour, _table.TableOrderTimeHour);
             SetTextBoxValue(editTextTablePrintTimeDay, _table.TablePrintTimeDay);
             SetTextBoxValue(editTextTablePrintTimeHour, _table.TablePrintTimeHour);
             SetTextBoxValue(editTextTableOperator, _table.TableOperator);
        }
        else
        {
        	_isNewTalbe=true;
        	DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        	Date date = new Date();
        	String datetime=dateFormat.format(date);
        	SetTextBoxValue(editTextTableOrderTimeDay,  datetime);
            SetTextBoxValue(editTextTablePrintTimeDay, datetime);
            DateFormat hourFormat = new SimpleDateFormat("hhmmss");
            SetTextBoxValue(editTextTableOrderTimeHour, hourFormat.format(date));
            SetTextBoxValue(editTextTablePrintTimeHour, hourFormat.format(date));
            SetTextBoxValue(editTextTableOperator, User.Current().UserName);
			if(_isTakeAway)
				SetTextBoxValue(editTextTableNr, TableHelper.GetNextTakeAwayTableNumber());
        }
    }

    private void SetTextBoxValue(EditText textBox, String value)
    {
        textBox.setText(value);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_table_detail, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnGoBack)
		{
		  finish();
		}
		else if(v.getId()==R.id.btnSave)
		{
			final String tableNr=editTextTableNr.getText().toString().trim();
			if(tableNr.equals(""))
			{
				new AlertDialog.Builder(this)
		           .setMessage(R.string.msg_table_nr_must_fill_in)
		           .setCancelable(false)
		           .setPositiveButton("OK", null)
		           .show();
				return;
			}
			if(_table==null)
			{
				//check if tablenr already exists
				boolean exists=false;
				for(Table table:TableHelper.TableList())
				{
					if(table.TableNr.equals(tableNr))
					{
						exists=true;					
						break;
					}				
				}	
				if(exists==true)
				{
					new AlertDialog.Builder(this)
			           .setMessage(R.string.msg_table_already_exists)
			           .setCancelable(false)
			           .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
			            	  GoToNextActivity(tableNr);			            	 
			               }
			           })
			           .setNegativeButton(R.string.no, null)
			           .show();
				}
				else
				{
					_table=new Table(tableNr, _isTakeAway);

					TableHelper.TableList().add(_table);
				}
			}
			if(_table!=null)
			{
				_table.TableNr=tableNr;
				_table.TablePersonCount=getTextString(R.id.editTextTablePersonCount);	
				_table.TableSequenceNumber=getTextString(R.id.editTextTableSequenceNumber);	
	            _table.TableOperator=getTextString(R.id.editTextTableOperator);	
	            _table.TableOrderTimeDay=getTextString(R.id.editTextTableOrderTimeDay);	
	            _table.TableOrderTimeHour=getTextString(R.id.editTextTableOrderTimeHour);	
	            _table.TablePrintTimeDay=getTextString(R.id.editTextTablePrintTimeDay);	
	            _table.TablePrintTimeHour=getTextString(R.id.editTextTablePrintTimeHour);
	            TableHelper.SaveTablesToStorage();
	            GoToNextActivity(tableNr);	
			}
			
		}	
	}
	
	private void GoToNextActivity(String tableNr)
	{	
		 finish();
		 if(_isNewTalbe)
		 {    //TODO: navigate to TableOrder activity
	            //Intent i = new Intent(TableDetailActivity.this, TableOrderActivity.class);
	            Intent i = new Intent(TableDetailActivity.this, OrderMenuActivity.class);
				i.putExtra(Define.TABLE_NR, tableNr);
				startActivity(i);
		 }
	}
	
	private String getTextString(int id)
	{
		EditText editText=(EditText)findViewById(id);
		return editText.getText().toString().trim();
	}
}
