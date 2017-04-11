package com.eurotong.orderhelperandroid;

import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class TableOrderActivity extends Activity implements OnClickListener, TextWatcher {

	Button btnGoHome;
	Table _table;
	Button btnShowMenu;
	Button btnAddOrder;
	Button btnRemoveOrder;
	Button btnExtra;
	
	EditText editTextMenuNr;
	TextView textViewMenuHint;
	ListView lvTableOrders;
	final Context context = this;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_table_order);
        
        btnGoHome=(Button)findViewById(R.id.btnGoHome);
        btnGoHome.setOnClickListener(this);
        btnShowMenu=(Button)findViewById(R.id.btnShowMenu);
        btnShowMenu.setOnClickListener(this);
        btnAddOrder=(Button)findViewById(R.id.btnAddOrder);
        btnAddOrder.setOnClickListener(this);
        btnRemoveOrder=(Button)findViewById(R.id.btnRemoveOrder);
        btnRemoveOrder.setOnClickListener(this);
        textViewMenuHint=(TextView)findViewById(R.id.textViewMenuHint);
        editTextMenuNr=(EditText)findViewById(R.id.editTextMenuNr);
        editTextMenuNr.addTextChangedListener(this);      
        
        btnExtra=(Button)findViewById(R.id.btnExtra);
        btnExtra.setOnClickListener(this);
        
        lvTableOrders=(ListView)findViewById(R.id.lvTableOrders);
        
        textViewMenuHint.setVisibility(View.INVISIBLE);
     
        _table=Common.GetTableFromIntent(this);
    	}
    	catch(Exception ex)
    	{
    		Log.e(Define.APP_CATALOG, ex.toString());
    	}
    	
    	//make table order list view.
    	generateTableOrder();
    }

    //activity life cycle
    //http://developer.android.com/reference/android/app/Activity.html
    //onResume is always called.
    @Override
    public void onResume()
    {
    	super.onResume();
    	//http://stackoverflow.com/questions/2150656/how-to-set-focus-on-a-view-when-a-layout-is-created-and-displayed
    	//close soft input when start up
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	Log.e(Define.APP_CATALOG, "onresume");
    	//if(MyApplication.NeedUpdate==true)
    	//{
    		generateTableOrder();
    	//}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_table_order, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		try {
			//hide soft keybord
			//http://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
			InputMethodManager imm = (InputMethodManager)getSystemService(
				      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editTextMenuNr.getWindowToken(), 0);
			
	  
		if(v.getId()==R.id.btnGoHome)
		{
			//Intent intent = new Intent(this, TablesOverviewActivity.class);
	    	//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);	    	
	    	//this.startActivity(intent);
			//MyApplication.GoToHome();
			finish();
			//Intent i = new Intent(this, TablesOverviewActivity.class);
			//startActivity(i);			
		}	
		else if(v.getId()==R.id.btnShowMenu)
		{
			//finish();
			Intent i = new Intent(context, OrderMenuActivity.class);
			i.putExtra(Define.TABLE_NR, _table.TableNr);
			startActivity(i);	
			//generateTableOrder();
		}	
		else if(v.getId()==R.id.btnAddOrder)
		{		
			try
			{	
			DoOrder(1);			
			editTextMenuNr.removeTextChangedListener(TableOrderActivity.this);
			editTextMenuNr.setText("");
			editTextMenuNr.addTextChangedListener(TableOrderActivity.this);	
			}
			catch(Exception e)
			{
			 Log.e(Define.APP_CATALOG, e.toString());
			}
		}	
		else if(v.getId()==R.id.btnRemoveOrder)
		{
			DoOrder(-1);
			editTextMenuNr.removeTextChangedListener(TableOrderActivity.this);
			editTextMenuNr.setText("");
			editTextMenuNr.addTextChangedListener(TableOrderActivity.this);	
		}		
		else if(v.getId()==R.id.btnExtra)
		{
			            // custom dialog
						final Dialog dialog = new Dialog(context);
						//final String tableNr=_table.TableNr;
						dialog.setContentView(R.layout.table_order_extra);
						dialog.setTitle(R.string.msg_please_select_operation);
			 
						// set the custom dialog components - text, image and button
						Button btnViewTableDetail=(Button)dialog.findViewById(R.id.btnViewTableDetail);
						Button btnPrintPreview=(Button)dialog.findViewById(R.id.btnPrintPreview);
						Button btnDeleteTable=(Button)dialog.findViewById(R.id.btnDeleteTable);
						Button btnExit=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnExit);
						Button btnAddChildTable=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnAddChildTable);
						btnPrintPreview.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {								
								Intent i = new Intent(getApplicationContext(), PrintPreview.class);
								i.putExtra(Define.TABLE_NR, _table.TableNr);
								startActivity(i);
								dialog.dismiss();
							}
						});
						
						// if button is clicked, close the custom dialog
						btnViewTableDetail.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {								
								Intent i = new Intent(context, TableDetailActivity.class);
								i.putExtra(Define.TABLE_NR, _table.TableNr);
								startActivity(i);
								dialog.dismiss();
							}
						});
						
						btnAddChildTable.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {								
								 	Table table;
						            if (_table.ParentTable != null)
						            {
						                table = _table.ParentTable.CreateChildTable();
						            }
						            else
						            {
						                table = _table.CreateChildTable();
						            }
						            TableHelper.TableList().add(table);
						            TableHelper.SaveTablesToStorage();
						            _table = table;
						           generateTableOrder();
								dialog.dismiss();
							}
						});
						
						btnDeleteTable.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Builder confirmDeleteTableDialog = new AlertDialog.Builder(context);
								confirmDeleteTableDialog.setTitle(R.string.msg_are_you_sure_to_delete_this_order);
								confirmDeleteTableDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int whichButton) {
							            	TableHelper.DeleteTableByNumber(_table.TableNr);
							            	 TableHelper.SaveTablesToStorage();
							            	 dialog.dismiss();
							            	 finish();
							            	 //MyApplication.GoToHome();
							            	 //finish();
											//Intent i = new Intent(context, TablesOverviewActivity.class);								
											//startActivity(i);
											//dialog.dismiss();
							            }
							        });
								confirmDeleteTableDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int whichButton) {
							                //
							            }
							        });
								confirmDeleteTableDialog.show();	
								dialog.dismiss();
							}
						});
						btnExit.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {								
								dialog.dismiss();
							}
						});
						dialog.show();
		}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
	}

	private void DoOrder(int count)
	{
		try
		{
		 String menuNr=editTextMenuNr.getText().toString().trim();
		 if(menuNr.equals(""))
		 {
			 return;
		 }
		 Product menu=null;			
		 menu = Product.GetMenuByNumber(menuNr);		
		 if(menu!=null)
		 {				
			_table.AddOrder(menuNr, count);
			   if(count>0)
               {
                  textViewMenuHint.setText(getText(R.string.msg_add_menu_order_success) + "  " + menu.MenuName);
               }
               else
               {
            	   textViewMenuHint.setText(getText(R.string.msg_minus_menu_order_success) + "  " + menu.MenuName);
               }           
			TableHelper.SaveTablesToStorage();
			generateTableOrder();
		 }
		 
		}
		catch(Exception e)
		{
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
	}
	
	//boolean tableOrderAdapterSetted=false;
	
	
	private View _headView;
	private View GetHeadView()
	{
		if(_headView==null)
		{
			_headView=(View)getLayoutInflater().inflate(R.layout.table_header, null);	
			
		}
		return _headView;
	}
	private boolean _headViewAdded=false;
	
	//generate orders for this table
	private void generateTableOrder()
	{
		try
		{
		ListView lv=lvTableOrders;
		
		
		Order[] orders=new Order[_table.TableOrders().size()];		
		_table.TableOrders().toArray(orders);
		Arrays.sort(orders);
		
		//get adapter for table orders
		TableOrderAdapter adapter=null;
		try {
			adapter = new TableOrderAdapter(this, 
			        R.layout.table_order, orders);
		} catch (Exception e) {
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
				
		
		//header for list view
		View header = GetHeadView(); // (View)getLayoutInflater().inflate(R.layout.table_header, null);	
		TextView textViewTableNr= (TextView)header.findViewById(R.id.txtTableNr);
		textViewTableNr.setText(_table.TableNr);
		TextView textViewTableTotal= (TextView)header.findViewById(R.id.txtTableTotal);
		textViewTableTotal.setText(_table.TotalString());
		
		if(_headViewAdded==false)
		{
			lv.addHeaderView(header);
			_headViewAdded=true;
		}
		
		//set adapter
		lv.setAdapter(adapter);		
		}
		catch(Exception e)
		{
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void afterTextChanged(Editable arg0) {
		try
		{
			textViewMenuHint.setVisibility(View.VISIBLE);
			String menuNr=editTextMenuNr.getText().toString().trim();
			Product menu=null;			
			menu = Product.GetMenuByNumber(menuNr);		
			if(menu!=null)
			{				
				textViewMenuHint.setText(menu.MenuName);
			}
			else
			{				
				textViewMenuHint.setText(menuNr + " " + getText(R.string.msg_this_product_nr_not_exist));
			}
		}
		catch(Exception ex)
		{
			Log.e(Define.APP_CATALOG, ex.toString());
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	
	
}
