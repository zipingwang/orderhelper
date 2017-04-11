package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EditMenuGroupActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener {

	EditText txtGroupName;
	Button btnSave;
	Button btnCancel;
	Button btnDelete;
	String _oldGroupName;
	Spinner spinnerKitchenGroup;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_group);
        txtGroupName=(EditText)findViewById(R.id.txtGroupName);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnDelete=(Button)findViewById(R.id.btnDelete);
		spinnerKitchenGroup=(Spinner)findViewById(R.id.MenuGroupKitchenOrder);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
		spinnerKitchenGroup.setOnItemSelectedListener(this);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.KitchenGroup, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
		spinnerKitchenGroup.setAdapter(adapter);

        
        Bundle extras = getIntent().getExtras(); 
	     if(extras!=null)
	     {
	    	 _oldGroupName= extras.getString(Define.GROUP_NAME);        
	        if(_oldGroupName!=null && !_oldGroupName.equals(""))
	        {
	        	txtGroupName.setText(_oldGroupName);
	        }
	     }

		ProductGroup pg= ProductGroup.GetGroupByName(_oldGroupName);
		if(pg!=null)
			spinnerKitchenGroup.setSelection(pg.KitchenGroup);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_menu_group, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnSave)
		{
			String newGroupName=txtGroupName.getText().toString().trim();
			if(_oldGroupName!=null && !_oldGroupName.equals(""))
			{
				//existing one
				try {
					for(ProductGroup pg:Product.MenuGroups())
					{
						if(pg.Name.equals(_oldGroupName))
						{
							pg.Name=newGroupName;
							pg.KitchenGroup=(int)spinnerKitchenGroup.getSelectedItemId();
							for(Product menu:pg.Menus)
							{
								menu.MenuKitchenGroup=pg.KitchenGroup;
								if(pg.KitchenGroup==0)
									menu.MenuIsPrintKitchen=false;
								else
									menu.MenuIsPrintKitchen=true;
							}
							TableHelper.SaveWholeMenu();
							break;
						}
					}
					finish();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else //new one
			{
				//check if already exist
				try {
					Boolean alreadyExist=false;
					for(ProductGroup pg:Product.MenuGroups())
					{
						if(pg.Name.equals(newGroupName))
						{
							//already exist
							alreadyExist=true;
							Common.GetToastLong(R.string.msg_menunr_already_exists).show();
							break;
						}
					}
					if(alreadyExist==false)
					{
						ProductGroup pg=new ProductGroup();
						pg.Name=newGroupName;
						Product.MenuGroups().add(pg);
						TableHelper.SaveWholeMenu();
						finish();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		}else if(v.getId()==R.id.btnCancel)
		{
			finish();
		}
		else if(v.getId()==R.id.btnDelete)
		{
			if(_oldGroupName!=null && !_oldGroupName.equals(""))
			{
				//existing one
				Builder confirmDeleteDialog = new AlertDialog.Builder(this);
				confirmDeleteDialog.setTitle(R.string.msg_are_you_sure_to_delete);
				confirmDeleteDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int whichButton) {
			            	try {
								ProductGroup oldGroup= ProductGroup.GetGroupByName(_oldGroupName);
								if(oldGroup!=null)
								{
									Product.MenuGroups().remove(oldGroup);
									TableHelper.SaveWholeMenu();
								}				
								
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
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

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {


	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}
}
