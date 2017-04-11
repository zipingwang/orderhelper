package com.eurotong.orderhelperandroid;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.R.bool;
import android.graphics.Point;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.view.MenuCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderMenuActivity extends Activity implements OnClickListener, TextWatcher, OnTouchListener {
	Table _table=null;
	
	//Button btnExit;
	//Button btnSave;
	LinearLayout menusContainer;
	//Button btnAllMenu;
	Button btnMenuGroup;
	//Button btnMenuBar;
	Button btnCurrentOrders;
	Button btnCloseKeyboard;
	Button btnExtra;
	Button btnPrint;
	Button btnGoHome;
	
	EditText editTextInput;
	
	List<VMMenuGroup> _lstVMMenuGroup = null;
	
	float textCountSize=20;
	final Context context = this;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
	    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_order_menu);
	        menusContainer=(LinearLayout)findViewById(R.id.MenusContainer);
	        
	        btnGoHome=(Button)findViewById(R.id.btnGoHome);
	        btnGoHome.setOnClickListener(this);
	        
	        btnExtra=(Button)findViewById(R.id.btnExtra); 
	        btnExtra.setOnClickListener(this);
	        
	        btnCurrentOrders=(Button)findViewById(R.id.btnCurrentOrders);
	        btnCurrentOrders.setOnClickListener(this);
	        
	        btnPrint=(Button)findViewById(R.id.btnPrint);
	        btnPrint.setOnClickListener(this);
	        
	        btnMenuGroup=(Button)findViewById(R.id.btnMenuGroup);
	        btnMenuGroup.setOnClickListener(this);
	    
	        btnCloseKeyboard=(Button)findViewById(R.id.btnCloseKeyboard);
	        btnCloseKeyboard.setOnClickListener(this);
	        
	        editTextInput=(EditText)findViewById(R.id.editTextInput);
	        editTextInput.addTextChangedListener(this); 
	        editTextInput.setOnTouchListener(this);
	        
	        _table=Common.GetTableFromIntent(this);
	       
	        int totalmenuitem=InitVMMenuGroup(); 
	        
	        ShowCurrentOrders();
	        
	        /*
	        if(totalmenuitem>50)
	        {
	        	MakeMenuGroupButtons();
	        }
	        else
	        {
	        	BindingMenuList();
	        }
	        */
	        //load menu hints
	        ProductHints.Current();
	        
	        //http://stackoverflow.com/questions/2150656/how-to-set-focus-on-a-view-when-a-layout-is-created-and-displayed
	    	//close soft input when start up
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }

    @Override
    public void onResume()
    {
    	try
    	{
	    	super.onResume();
	    	//http://stackoverflow.com/questions/2150656/how-to-set-focus-on-a-view-when-a-layout-is-created-and-displayed
	    	//close soft input when start up
			//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    	
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_order_menu, menu);
        return true;
    }
    
    
    int InitVMMenuGroup()
    {
    	int count=0;
        _lstVMMenuGroup = new ArrayList<VMMenuGroup>();
        try {
        	 VMMenuGroup group=null;
			for(ProductGroup menugroup:Product.MenuGroups())
			{
				group= new VMMenuGroup();
			    group.GroupName = menugroup.Name;
			    _lstVMMenuGroup.add(group);
			    for(Product menu : menugroup.Menus)
			    {
			        VMMenulistOrder item = new VMMenulistOrder();
			        item.MenuName = menu.MenuName;
			        item.MenuNr = menu.MenuNr;
			        item.OrderCount = GetOrderCount(menu.MenuNr);
			        item.MenuIsBarMenu=menu.MenuIsBarMenu;
			        group.VMMenus.add(item);
			        count++;
			    }
			}	
			if(Product.MenuGroups().size()>1)
			{
				group= new VMMenuGroup();
			    group.GroupName = getText(R.string.all_menus).toString();
			    _lstVMMenuGroup.add(group);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return count;
    }
    
    
    private void BindingMenuList()
    {
    	menusContainer.removeAllViews();
        
        for (VMMenuGroup menugroup: _lstVMMenuGroup)
        {
        	menusContainer.addView(CreateMenuGroupLine(menugroup));
            for (VMMenulistOrder menu : menugroup.VMMenus)
            {            
            	View element = CreateMenuLine(menu);
                menusContainer.addView(element);                   
            }
        }
        return;     
    }
    
     
    private View _headView;
	private View GetHeadView()
	{
		FillHeaderView();
		return _headView;
	}

	private void FillHeaderView()
	{
		/*
		if(_table.IsTakeAway && BusinessInfo.Current().getHasTakeProcent()) {
			if (_headView == null) {
				_headView = (View) getLayoutInflater().inflate(R.layout.table_header_takeaway, null);
			}
			TextView textViewTableNr = (TextView) _headView.findViewById(R.id.txtTableNr);
			textViewTableNr.setText(_table.TableNr);
			TextView textViewTableTotalTakeAway = (TextView) _headView.findViewById(R.id.txtTotalTakeAway);
			textViewTableTotalTakeAway.setText(_table.TotalTakeAwayString());
		}
		else {
			if (_headView == null) {
				_headView = (View) getLayoutInflater().inflate(R.layout.table_header, null);
			}
			TextView textViewTableNr = (TextView) _headView.findViewById(R.id.txtTableNr);
			textViewTableNr.setText(_table.TableNr);
			TextView textViewTableTotal = (TextView) _headView.findViewById(R.id.txtTableTotal);
			textViewTableTotal.setText(_table.TotalString());
		}
		*/
		if (_headView == null) {
				_headView = (View) getLayoutInflater().inflate(R.layout.table_header, null);
			}
			TextView textViewTableNr = (TextView) _headView.findViewById(R.id.txtTableNr);
			textViewTableNr.setText(_table.TableNr);
			TextView textViewTableTotal = (TextView) _headView.findViewById(R.id.txtTableTotal);
			textViewTableTotal.setText(_table.TotalString());
	}

    private View CreateMenuGroupLine(VMMenuGroup group)
    {
    	View menugroupRow=(View)getLayoutInflater().inflate(R.layout.order_menu_group_row, null);
		TextView textViewMenuGroupName=(TextView)menugroupRow.findViewById(R.id.txtMenuGroupName);
		textViewMenuGroupName.setText(group.GroupName);
		return menugroupRow;
    }
    
    private View  CreateMenuLine(VMMenulistOrder menu)
    {
    	View menuRow=null;
    	try
    	{
	    	//menuRow = (View)getLayoutInflater().inflate(R.layout.order_menu_row, null);	
	    	menuRow = (View)getLayoutInflater().inflate(R.layout.order_menu_row_simple, null);	
	    	
	    	
			TextView textViewMenuNr=(TextView)menuRow.findViewById(R.id.txtOrderMenuNr);
			if(textViewMenuNr!=null)
			{
				textViewMenuNr.setText(menu.MenuNr);
			}
			TextView textViewOrderMenuName=(TextView)menuRow.findViewById(R.id.txtOrderMenuName);
			textViewOrderMenuName.setText(menu.MenuName);
			TextView textViewOrderCount=(TextView)menuRow.findViewById(R.id.txtOrderCount);
			textViewOrderCount.setText(Common.FormatDoubleToNoDecimal(menu.OrderCount));
			
			
			Button btnAddOne=(Button)menuRow.findViewById(R.id.btnAddOne);
			Button btnRemoveOne=(Button)menuRow.findViewById(R.id.btnRemoveOne);
			btnAddOne.setTag(menu);
			btnRemoveOne.setTag(menu);
			
			textCountSize=textViewOrderCount.getTextSize();
			
			SetStyleForOrderedMenu(menu, textViewOrderCount);
			
			btnAddOne.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AddOrder(v, 1);				
				}
			});
			btnRemoveOne.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
						AddOrder(v, -1);
				}
			});	
    	}
    	 catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return menuRow;
    }
    
    private void MakeMenuGroupButtons()
    {
    	 menusContainer.removeAllViews();    	
    	 try {
			for (VMMenuGroup group : _lstVMMenuGroup)
			    {			 
				 Button btn=new Button(this);
				 btn.setText(group.GroupName);
				 btn.setTag(group);
				 btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						MakeMenuGroupList(v);
					}
				});
			   menusContainer.addView(btn);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
    
    private void MakeMenuGroupList(View v)
    {
    	 menusContainer.removeAllViews();
    	 VMMenuGroup group= (VMMenuGroup)v.getTag();
    	 if(group!=null)
    	 {
    		 if(group.GroupName.equals(getText(R.string.all_menus)))
			 {
    			 BindingMenuList();
			 }
    		 else
    		 {
	    		menusContainer.addView(CreateMenuGroupLine(group)); 
			     try {
			    	 for (VMMenulistOrder menu : group.VMMenus)
					    {			       
					        View element = CreateMenuLine(menu);
					        menusContainer.addView(element);
					       // _lstMenulistOrder.add(item);
					    }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		 }
    	 }
    }
    
    private void BingMenuBarList()
    {
        menusContainer.removeAllViews();
        for (VMMenuGroup menugroup : _lstVMMenuGroup)
        {        	
            for (VMMenulistOrder menu : menugroup.VMMenus)
            {
                if (menu.MenuIsBarMenu == true)
                {
                    View element = CreateMenuLine(menu);
                    menusContainer.addView(element);
                }
            }
        }
        return;
    }
    
    private void AddOrder(View v, int orderCount)
    {
    	VMMenulistOrder menu=(VMMenulistOrder)v.getTag();
    	LinearLayout viewParent= (LinearLayout)v.getParent();
    	
		//TextView viewMenuNr=(TextView)viewParent.findViewById(R.id.txtOrderMenuNr);
		//String menuNr=viewMenuNr.getText().toString();	
		TextView viewOrderCount=(TextView)viewParent.findViewById(R.id.txtOrderCount);
		if(menu!=null)
		{
		try {
			menu.OrderCount += orderCount;
			if(menu.OrderCount<0)
			{
				menu.OrderCount=0;
			}
			viewOrderCount.setText(Common.FormatDoubleToNoDecimal(menu.OrderCount));
			SetStyleForOrderedMenu(menu, viewOrderCount);
			
			SaveTableOrders();

			FillHeaderView();

			//clear input text
			SetEditTextInputText("");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(Define.APP_CATALOG, e.toString());
		}
		}	
    }
    
    private double GetOrderCount(String menuNr)
    {
    	
    	double orderCount=0;
        for (Order order : _table.TableOrders())
        {
            if (order.OrderMenuNr.equals(menuNr))
            {
            	orderCount= order.OrderCount;
            	break;
            }
        }
        return orderCount;
        //return Common.FormatDoubleToNoDecimal(orderCount);
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnGoHome)
		{
			GotoTableOrderView();
			//finish();
		}
		if(v.getId()==R.id.btnPrint)
		{
			 // custom dialog
			final Dialog dialog = new Dialog(context);
			
			dialog.setContentView(R.layout.print_command_selection);
			dialog.setTitle(R.string.msg_please_select_operation);
 
			// set the custom dialog components - text, image and button
			Button btnPrintBill=(Button)dialog.findViewById(R.id.btnPrintBill);			
			Button btnPrintKitchen=(Button)dialog.findViewById(R.id.btnPrintKitchen);
			Button btnExit=(Button)dialog.findViewById(R.id.btnExit);
			Button btnPrintBar=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnPrintBar);
			Button btnPrintPreview=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnPrintPreview);
			if(!User.Current().HasRight(Define.UR_DEBUG_PRINT_LAYOUT))
			{
				btnPrintPreview.setVisibility(View.GONE);
			}
			// if button is clicked, close the custom dialog
			btnPrintBill.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {								
					 if(User.Current().HasRight(Define.UR_PRINT))
			           {
			        	   DoPrintPos(_table, PrintLayout.Current(), Setting.Current().NumberOfPrints);	
			        	   dialog.dismiss();
			           }
				}
			});
			
			btnPrintKitchen.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {								
					if(User.Current().HasRight(Define.UR_PRINT_KITCHEN))
			        {
						DoPrintPos(_table, PrintLayout.KitchenLayout(), Setting.Current().NumberOfPrintsKitchen);
						dialog.dismiss();
			        }
				}
			});
			
			btnPrintBar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(User.Current().HasRight(Define.UR_PRINT_BAR))
			        {
						DoPrintPos(_table, PrintLayout.BarLayout(), Setting.Current().NumberOfPrintsBar);
						dialog.dismiss();
			        }
				}
			});
			btnPrintPreview.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {					
					Intent i = new Intent(context, PrintPreview.class);
					i.putExtra(Define.TABLE_NR, _table.TableNr);
					startActivity(i);
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
		if(v.getId()==R.id.btnExtra)
		{
            // custom dialog
			final Dialog dialog = new Dialog(context);
			
			dialog.setContentView(R.layout.table_order_extra);
			dialog.setTitle(R.string.msg_please_select_operation);
 
			// set the custom dialog components - text, image and button
			Button btnViewTableDetail=(Button)dialog.findViewById(R.id.btnViewTableDetail);			
			Button btnDeleteTable=(Button)dialog.findViewById(R.id.btnDeleteTable);
			Button btnExit=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnExit);
			Button btnAddChildTable=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnAddChildTable);
			
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
			            InitVMMenuGroup();
			            ShowCurrentOrders();
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
		//else if(v.getId()==R.id.btnSave)
		///{
		//	SaveTableOrders();
		//	GotoTableOrderView();
			//finish(); //finish means close. want to refresh tableorder when close this view. so instead use gototableorderview()
		//}	
		//else if(v.getId()==R.id.btnAllMenu)
		//{
		//	BindingMenuList();
		//}
		else if(v.getId()==R.id.btnMenuGroup)
		{
			MakeMenuGroupButtons();
		}
		//else if(v.getId()==R.id.btnMenuBar)
		//{
		//	BingMenuBarList();
		//}
		if(v.getId()==R.id.btnCurrentOrders)
		{
			ShowCurrentOrders();
			//finish();
		}
		else if(v.getId()==R.id.btnCloseKeyboard)
		{
			//close soft keyboard
			//http://www.workingfromhere.com/blog/2011/04/27/close-hide-the-android-soft-keyboard/
			InputMethodManager imm = (InputMethodManager)getSystemService(MyApplication.getAppContext().INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editTextInput.getWindowToken(), 0);
			View filling=menusContainer.findViewById(R.id.hint_fill);
			if(filling!=null)
			{
				filling.setVisibility(View.GONE);
			}
			SetEditTextInputText("");
			ShowCurrentOrders();
		}
	}
	
	void SaveTableOrders()
	{
		_table.TableOrders().clear();
        for(VMMenuGroup menugroup:_lstVMMenuGroup)
        {                
            for (VMMenulistOrder menu : menugroup.VMMenus)
            {
                if (menu.OrderCount > 0)
                {
                    try {
						_table.AddOrder(menu.MenuNr, menu.OrderCount);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
        TableHelper.SaveTablesToStorage();
	}
	
	void GotoTableOrderView()
	{
		finish();
		//Intent i = new Intent(getApplicationContext(), TableOrderActivity.class);
		//i.putExtra(Define.TABLE_NR, _table.TableNr);
		//startActivity(i);		
	}
	
	void SetStyleForOrderedMenu(VMMenulistOrder menu, TextView textViewOrderCount)
	{
		if(menu.OrderCount>0)
		{
			textViewOrderCount.setTextColor(Color.RED);			
			//textViewOrderCount.setTextSize((float) (textCountSize*1.5));
			//textViewOrderCount.setTextSize((float) (textCountSize));
			textViewOrderCount.setTypeface(null, Typeface.BOLD);	
		}
		else
		{
			textViewOrderCount.setTextColor(Color.BLACK);			
			//textViewOrderCount.setTextSize((float) (textCountSize));
			textViewOrderCount.setTypeface(null, Typeface.NORMAL);	
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		try
		{
			Boolean flag=false;
			String key=editTextInput.getText().toString().trim().toLowerCase();
			if(key.contains("c"))
			{
				key=key.replace('c', 'c');
				flag=true;
			}
			if(key.contains("c"))
			{
				key=key.replace('c', 'c');
				flag=true;
			}
			if(key.contains("("))
			{
				key=key.replace('(', 'c');
				flag=true;
			}
			if(key.contains("<"))
			{
				key=key.replace('<', 'c');
				flag=true;
			}
			
			int pos=key.indexOf("0");
			if(pos>0)
			{
				if(key.charAt(pos-1)>='0'  && key.charAt(pos-1) <='9')
				{
					key=key.replace('o', '0');
					flag=true;
				}
				else
				{
					key=key.replace('0', 'o');
					flag=true;
				}
			}
			else
				if(pos==0)
				{
					key=key.replace('0', 'o');
					flag=true;
				}
			if(flag==true)
			{
				SetEditTextInputText(key);
			}
			
			Product[] menus=null;			
			menus = ProductHints.Current().GetProductsByKeywords(key);		
			menusContainer.removeAllViews();
			View filling = (View)getLayoutInflater().inflate(R.layout.order_menu_hint_fill_row, null);

			//http://stackoverflow.com/questions/1016896/android-how-to-get-screen-dimensions
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			filling.setMinimumHeight(height/2);

	    	menusContainer.addView(filling);
	    	
			if(menus!=null && menus.length>0)
			{	
				for(Product possiblemenu: menus)
				{
			        for (VMMenuGroup menugroup: _lstVMMenuGroup)
			        {		        	
			            for (VMMenulistOrder menu : menugroup.VMMenus)
			            {    
			            	if(possiblemenu.MenuNr.equals(menu.MenuNr))
			            	{
				            	View element = CreateMenuLine(menu);
				                menusContainer.addView(element);         
			            	}
			            }
			        }			       
				}
			}
			else
			{
				//if(editTextInput.getText().toString().trim()!="")
				if(!editTextInput.getText().toString().trim().isEmpty()) {
					VMMenuGroup mg = new VMMenuGroup();
					mg.GroupName = getText(R.string.msg_no_mached_menu_found).toString();
					View element = CreateMenuGroupLine(mg);
					menusContainer.addView(element);
				}
			}
		}
		catch(Exception ex)
		{
			Log.e(Define.APP_CATALOG, ex.toString());
		}		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/*
		View filling=menusContainer.findViewById(R.id.hint_fill);
		if(filling!=null)
		{
			filling.setVisibility(View.VISIBLE);
		}
		return false;
		*/
		afterTextChanged(null);
		return  false;
	}
	
	private void ShowCurrentOrders()
	{
		menusContainer.removeAllViews();
		
		menusContainer.addView(GetHeadView());
		
        for(VMMenuGroup menugroup:_lstVMMenuGroup)
        {                
            for (VMMenulistOrder menu : menugroup.VMMenus)
            {
                if (menu.OrderCount > 0)
                {
                    try {
                    	menusContainer.addView(CreateMenuLine(menu));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        }
	}
	
	private void SetEditTextInputText(String txt)
	{
		editTextInput.removeTextChangedListener(this);
		editTextInput.setText(txt);
		editTextInput.setSelection(editTextInput.getText().length());
		editTextInput.addTextChangedListener(this);	
	}
	private void DoPrintPos(Table table, PrintLayout printlayout, int numberOfPrints) //( printpos)	   
    {
		//async is true
		//DoPrintPos_Sync(table, printlayout, numberOfPrints, true);

		DoPrintPos_Sync(table, printlayout, numberOfPrints, Setting.Current().IsAsyncPrinting);
    }
	
	Boolean _busy=false;
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
                            _table.TableNr = _table.TableNr + "   (Duplicate)";
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
	   
}
