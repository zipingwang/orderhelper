package com.eurotong.orderhelperandroid;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class EditMenuActivity extends Activity implements OnClickListener {
	Button btnSave;
	Button btnCancel;
	Button btnDelete;
	EditText txtMenuNr;
	EditText txtMenuName;
	EditText txtMenuNameChinese;
	EditText txtMenuPriceTakeAway;
	EditText txtMenuPrice;
	EditText txtMenuTax;
	CheckBox cbxPrint;
	CheckBox cbxPrintToBar;
	CheckBox cbxPrintToKitchen;
	Spinner ddbMenugroup;
	
	Product _menu=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnDelete=(Button)findViewById(R.id.btnDelete);
        txtMenuNr=(EditText)findViewById(R.id.txtMenuNr);
        txtMenuName=(EditText)findViewById(R.id.txtMenuName);
        txtMenuNameChinese=(EditText)findViewById(R.id.txtMenuNameChinese);
        txtMenuPrice=(EditText)findViewById(R.id.txtMenuPrice);
		txtMenuPriceTakeAway=(EditText)findViewById(R.id.txtMenuPriceTakeAway);
        txtMenuTax=(EditText)findViewById(R.id.txtMenuTax);
		cbxPrint=(CheckBox)findViewById(R.id.cbxPrint);
        cbxPrintToBar=(CheckBox)findViewById(R.id.cbxPrintToBar);
        cbxPrintToKitchen=(CheckBox)findViewById(R.id.cbxPrintToKitchen);
        ddbMenugroup=(Spinner)findViewById(R.id.ddbMenugroup);
    	
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        
        Bundle extras = getIntent().getExtras(); 
	     if(extras!=null)
	     {
	    	 String menuNr= extras.getString(Define.MENU_NR);        
	    	 try {
				_menu= Product.GetMenuByNumber(menuNr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        if(_menu!=null)
	        {
	        	txtMenuNr.setText(_menu.MenuNr);
	        	txtMenuName.setText(_menu.MenuName);
	        	txtMenuNameChinese.setText(_menu.MenuNameZH);
	        	txtMenuPrice.setText(Common.FormatDouble(_menu.MenuPrice));
				txtMenuPriceTakeAway.setText(Common.FormatDouble(_menu.MenuPriceTakeAway));
	        	txtMenuTax.setText(Common.FormatDouble(_menu.MenuTax));
				cbxPrint.setChecked(_menu.MenusIsPrint);
	        	cbxPrintToBar.setChecked(_menu.MenuIsBarMenu);
	        	cbxPrintToKitchen.setChecked(_menu.MenuIsPrintKitchen);	        	
	        }
	     }
        
        List<String> SpinnerArray =  new ArrayList<String>();
        try {
        		int selectedIndex=0;
        	
        		for(ProductGroup pg:Product.MenuGroups())
				{				
				  SpinnerArray.add(pg.Name);
				}
        		 if(_menu!=null)
		         {
					for(ProductGroup pg:Product.MenuGroups())
					{				
					 
						  if(pg.Menus.indexOf(_menu)>=0)
						  {
							  break;
						  }
			          
					  selectedIndex++;
					}
		          }
        	
			 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerArray);
		     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);        
		     ddbMenugroup.setAdapter(adapter);
		     ddbMenugroup.setSelection(selectedIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_menu, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnCancel)
		{
			finish();
		}
		else if(v.getId()==R.id.btnSave)
		{
			String menuNr=txtMenuNr.getText().toString().trim();
			String newgroup=	ddbMenugroup.getSelectedItem().toString();
			ProductGroup newProductGroup= ProductGroup.GetGroupByName(newgroup);
			    
			if(menuNr.equals(""))
			{
				Common.GetToastLong(R.string.msg_menu_nr_cannot_be_empty);
				return;
			}
			if(_menu==null) //it it new
			{
				//check is menunr already exists;
				//Product menu=null;
				Boolean newOne=true;
				try {
					_menu = Product.GetMenuByNumber(menuNr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(_menu!=null)
				{
					newOne=false;
					Common.GetToastLong(R.string.msg_menunr_already_exists).show();
					return;
				}
				else
				{
					_menu=new Product();
					newOne=true;
				}
		
			    try {					
					if(newOne)
					{
						newProductGroup.Menus.add(_menu);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				//existing one
			
			}
			_menu.MenuNr=txtMenuNr.getText().toString().trim();
			_menu.MenuName=txtMenuName.getText().toString().trim();
			_menu.MenuNameZH=txtMenuNameChinese.getText().toString().trim();
			_menu.MenuPrice=Common.GetDoubleResult(txtMenuPrice.getText().toString().trim().replace(",", "."), 0);
			_menu.MenuPriceTakeAway=Common.GetDoubleResult(txtMenuPriceTakeAway.getText().toString().trim().replace(",", "."), 0);
			_menu.MenuTax=Common.GetDoubleResult(txtMenuTax.getText().toString().trim().replace(",", "."), 0);
			_menu.MenuIsBarMenu=cbxPrintToBar.isChecked();
			_menu.MenusIsPrint=cbxPrint.isChecked();
			_menu.MenuIsPrintKitchen=cbxPrintToKitchen.isChecked();
			if(_menu.MenuKeywords==null || _menu.MenuKeywords.equals(""))
			{
				_menu.MenuKeywords=_menu.MenuNr;
			}
			else
			{
				if(!_menu.MenuKeywords.contains(_menu.MenuNr))
				{
					_menu.MenuKeywords=_menu.MenuKeywords + Define.MENU_KEYWORDS_SEPEROTOR + _menu.MenuNr;
				}
			}
			
			ProductGroup oldProductGroup=_menu.GetGroup();
			if(!newgroup.equals(oldProductGroup.Name))
			{
				oldProductGroup.Menus.remove(_menu);
				
				newProductGroup.Menus.add(_menu);
			}
			TableHelper.SaveWholeMenu();
			finish();
		}else if(v.getId()==R.id.btnDelete)
		{
			if(_menu!=null)
			{
				Builder confirmDeleteDialog = new AlertDialog.Builder(this);
				confirmDeleteDialog.setTitle(R.string.msg_are_you_sure_to_delete);
				confirmDeleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	_menu.GetGroup().Menus.remove(_menu);
							TableHelper.SaveWholeMenu();
			            	 dialog.dismiss();
			            	 finish();
			            	
			            }
			        });
				confirmDeleteDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	dialog.dismiss();
			            }
			        });
				confirmDeleteDialog.show();				
							
			}			
		}
			
		
	}
}
