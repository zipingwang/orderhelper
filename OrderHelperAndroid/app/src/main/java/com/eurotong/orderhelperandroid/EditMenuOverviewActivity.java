package com.eurotong.orderhelperandroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.provider.CalendarContract.Colors;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EditMenuOverviewActivity extends Activity implements OnClickListener {

	LinearLayout menusContainer;
	Button btnConfirm;
	List<VMMenuGroup> _lstVMMenuGroup = null;
	Button btnAddNewMenu;
	Button btnAddNewGroup;
	Button btnGoBack;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try
    	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu_overview);
        
        menusContainer=(LinearLayout)findViewById(R.id.MenusContainer);        
        btnConfirm=(Button)findViewById(R.id.btnConfirm);
        btnAddNewMenu=(Button)findViewById(R.id.btnAddNewMenu);
        btnAddNewGroup=(Button)findViewById(R.id.btnAddNewGroup);
			btnGoBack=(Button)findViewById((R.id.btnGoBack));
        btnConfirm.setOnClickListener(this);
        btnAddNewMenu.setOnClickListener(this);
        btnAddNewGroup.setOnClickListener(this);
			btnGoBack.setOnClickListener(this);
       
    	}
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit_menu_overview, menu);
        return true;
    }

  //activity life cycle
    //http://developer.android.com/reference/android/app/Activity.html
    //onResume is always called.
    @Override
    public void onResume()
    {
    	super.onResume();
    	InitVMMenuGroup();
        BindingMenuList();    	
    }
    
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnConfirm)
		{
			StringBuilder sb=new StringBuilder();
			
			try {
				 for (VMMenuGroup menugroup: _lstVMMenuGroup)
			     {
					 for(ProductGroup menugroupOri:Product.MenuGroups())
					 {
						 if(menugroup.GroupName.equals(menugroupOri.Name))
						 {
							 sb.append(menugroupOri.toString());
							 sb.append(Define.NEW_LINE);		
							 break;
						 }
					 }
			        
		            for (VMMenulistOrder menu : menugroup.VMMenus)
		            {          
		            	Product m= Product.GetMenuByNumber(menu.MenuNr);
		            	sb.append(m.toString());    
		            	sb.append(Define.NEW_LINE);		
		            }
			     }				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
			try {
				Common.SaveToIsolatedStorage(sb.toString(), Define.MENU_FILE_NAME);
				Product.Reload();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.finish();
		}	
		else if(v.getId()==R.id.btnAddNewGroup)
		{
			  Intent i = new Intent(this, EditMenuGroupActivity.class);
			  i.putExtra(Define.GROUP_NAME, "");
			  startActivity(i);
		}
		else if(v.getId()==R.id.btnAddNewMenu)
		{
			  Intent i = new Intent(this, EditMenuActivity.class);
			 // i.putExtra(Define.MENU_NR, "");
			  startActivity(i);
		}
		else if(v.getId()==R.id.btnGoBack)
		{
			finish();
		}
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
				        item.OrderCount = 0;
				        item.MenuIsBarMenu=menu.MenuIsBarMenu;
				        group.VMMenus.add(item);				        
				        count++;
				    }
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
	        	View element = CreateLine(menugroup);	        	
	        	menusContainer.addView(element);
	            for (VMMenulistOrder menu : menugroup.VMMenus)
	            {            
	            	element = CreateLine(menu);	            	
	                menusContainer.addView(element);                   
	            }
	        }
	        return;     
	    }
	    /*
	  private View CreateMenuGroupLine(VMMenuGroup group)
	    {
	    	View menugroupRow=(View)getLayoutInflater().inflate(R.layout.edit_menu_row, null);
			TextView textViewMenuGroupName=(TextView)menugroupRow.findViewById(R.id.txtOrderMenuName);
			menugroupRow.setBackgroundColor(Color.BLUE);
			textViewMenuGroupName.setText(group.GroupName);
			TextView textViewMenuNr=(TextView)menugroupRow.findViewById(R.id.txtMenuNr);
			if(textViewMenuNr!=null)
			{
				textViewMenuNr.setText("");
			}
			menugroupRow.setTag(group);
			return menugroupRow;
	    }
	    */
	    private View  CreateLine(Object obj)
	    {
	    	View menuRow=null;
	    	try
	    	{	
	    		Boolean isMenu=true;
	    		VMMenulistOrder menu=null;
	    		VMMenuGroup menuGroup=null;
	    		if(obj instanceof VMMenulistOrder)
	    		{
	    			isMenu=true;
	    			menu=(VMMenulistOrder)obj;
	    		}
	    		else
	    		{
	    			isMenu=false;
	    			menuGroup=(VMMenuGroup)obj;
	    		}
	    		
		    	menuRow = (View)getLayoutInflater().inflate(R.layout.edit_menu_row, null);			    	
		    	menuRow.setTag(obj);
		    	
		    	Button btnModify=(Button)menuRow.findViewById(R.id.btnModify);
				Button btnMoveUp=(Button)menuRow.findViewById(R.id.btnMoveUp);
				Button btnMoveDown=(Button)menuRow.findViewById(R.id.btnMoveDown);
				
				TextView textViewMenuNr=(TextView)menuRow.findViewById(R.id.txtMenuNr);
			
				if(isMenu)
				{
					TextView textViewOrderMenuName=(TextView)menuRow.findViewById(R.id.txtOrderMenuName);
					textViewOrderMenuName.setText(menu.MenuName);
					textViewMenuNr.setText(menu.MenuNr);
					
				}
				else
				{
					TextView textViewOrderMenuName=(TextView)menuRow.findViewById(R.id.txtOrderMenuName);
					textViewOrderMenuName.setText(menuGroup.GroupName);
					textViewOrderMenuName.setTextColor(Color.RED);
					textViewMenuNr.setText("");
					//menuRow.setBackgroundColor(Color.BLUE);
				}
				
			
				btnModify.setTag(obj);
				btnMoveUp.setTag(obj);
				btnMoveDown.setTag(obj);
				
								
				btnModify.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Object obj=v.getTag();
						if(obj instanceof VMMenulistOrder)
				    	{
							VMMenulistOrder menu=(VMMenulistOrder)obj;
				    		 Intent i = new Intent(v.getContext(), EditMenuActivity.class);
							 i.putExtra(Define.MENU_NR, menu.MenuNr);
							 startActivity(i);	
				    	}
				    	else if (obj instanceof VMMenuGroup)
				    	{
				    		VMMenuGroup group=(VMMenuGroup)obj;
				    		 Intent i = new Intent(v.getContext(), EditMenuGroupActivity.class);
							 i.putExtra(Define.GROUP_NAME, group.GroupName);
							 startActivity(i);		
				    	}
						 
					}
				});
				btnMoveUp.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {											
						MovePosition(-1, v.getTag());
					}
				});	
				btnMoveDown.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {											
						MovePosition(1, v.getTag());
					}
				});	
	    	}
	    	 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return menuRow;
	    }
	    
	    void MovePosition(int relativePos, Object obj)
	    {
	    	 int posInMenusContainer=-1;
	    	 for(int icount=0; icount<menusContainer.getChildCount(); icount++)
	    	 {
	    		 if(menusContainer.getChildAt(icount).getTag()==obj)
	    		 {
	    			 posInMenusContainer=icount;
	    			 break;
	    		 }	    		 
	    	 }
	    	if(obj instanceof VMMenulistOrder)
	    	{
	    		
	    	}
	    	else if (obj instanceof VMMenuGroup)
	    	{
	    		
	    	}
	    	 int index=-1;
	    	 index=_lstVMMenuGroup.indexOf(obj);
	    	 if(index>=0) //group
	    	 {
	    		 if(index+relativePos>=0 && index+relativePos<_lstVMMenuGroup.size())
	    		 {
		    		 _lstVMMenuGroup.remove(index);
		    		 _lstVMMenuGroup.add(index+relativePos, (VMMenuGroup)obj);
		    		 BindingMenuList();
	    		 }
	    	 }
	    	 else //not group but menu item
	    	 {
	    		int groupCount=0;
		    	for (VMMenuGroup menugroup: _lstVMMenuGroup)
		        {				
					 index=menugroup.VMMenus.indexOf(obj);
					 if(index>=0) //found
					 {
						 if(index+relativePos>=0 && index+relativePos<menugroup.VMMenus.size())
			    		 {
						 	 menugroup.VMMenus.remove(index);
							 menugroup.VMMenus.add(index+relativePos, (VMMenulistOrder)obj);
							 
							 menusContainer.removeViewAt(posInMenusContainer);
						   	 menusContainer.addView(CreateLine((VMMenulistOrder)obj), posInMenusContainer+relativePos);
						   	 	
							 break;
						 }	
						 else
						 {
							 /*
							    Builder confirmDeleteTableDialog = new AlertDialog.Builder(this);
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
								*/
							 if(index+relativePos<0) //move up out side group
							 {
								 if(groupCount>0)
								 {
									 VMMenuGroup upGroup=_lstVMMenuGroup.get(groupCount-1);
									 menugroup.VMMenus.remove(index);
									 upGroup.VMMenus.add((VMMenulistOrder)obj);
									 
									 menusContainer.removeViewAt(posInMenusContainer);
								   	 menusContainer.addView(CreateLine((VMMenulistOrder)obj), posInMenusContainer+relativePos);
								 }
								 
							 }
							 else if(index+relativePos>=menugroup.VMMenus.size()) //move down out side group
							 {
								 if(groupCount<_lstVMMenuGroup.size()-1)
								 {
									 VMMenuGroup downGroup=_lstVMMenuGroup.get(groupCount+1);
									 menugroup.VMMenus.remove(index);
									 downGroup.VMMenus.add(0, (VMMenulistOrder)obj);
									 
									 menusContainer.addView(CreateLine((VMMenulistOrder)obj), posInMenusContainer+relativePos+1);
									 menusContainer.removeViewAt(posInMenusContainer);
								   	
								 }
								 
							 }
							 
						 }
						 break;
						 
					 }//if(index>=0) //found
					 groupCount++;
		        }
		   	 	
	    	 }
	    	
	    }
}
