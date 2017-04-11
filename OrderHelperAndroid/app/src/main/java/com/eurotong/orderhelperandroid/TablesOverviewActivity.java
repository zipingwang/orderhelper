package com.eurotong.orderhelperandroid;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;


import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.util.TypedValue.*;

public class TablesOverviewActivity extends BaseActivity implements OnClickListener {
	Button btnAddTable;
	Button btnAddTableTakeAway;
	Button btnExtra;
	Button btnShowAd;
	final Context context = this;
	Timer timer;
	TimerTask timerTask;

	public boolean wasInBackground;

	//we are going to use a handler to be able to run in our TimerTask
	final Handler handler = new Handler();
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_tables_overview);
			btnAddTable = (Button) findViewById(R.id.btnAddTable);
			btnAddTableTakeAway = (Button) findViewById(R.id.btnAddTableTakeAway);
			btnAddTable.setOnClickListener(this);
			btnAddTableTakeAway.setOnClickListener(this);
			btnExtra = (Button) findViewById(R.id.btnExtra);
			btnExtra.setOnClickListener(this);
			btnShowAd = (Button) findViewById(R.id.btnShowAd);
			btnShowAd.setOnClickListener(this);
			//generateTablesView();

			btnShowAd.setVisibility(View.GONE);

		} catch (Exception ex) {
			Log.e(Define.APP_CATALOG, ex.toString());
		}
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	//activity life cycle
	//http://developer.android.com/reference/android/app/Activity.html
	//onResume is always called.
	@Override
	public void onResume() {
		super.onResume();
		if (Common.TablesLoadedFromStorage == false) {
			try {
				TableHelper.LoadTablesFromStorage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Common.TablesLoadedFromStorage = true;
			//for testing
			//TableHelper.InitTableListForTest(80, 15);
		}

		Log.e(Define.APP_CATALOG, "onresume");
		//if(MyApplication.NeedUpdate==true)
		//{
		generateTablesView();
		//}


		//onResume we start our timer so it can start when the app comes from the background
		startTimer();
	}

	@Override
	protected void onPause() {
		super.onPause();
		timerTask.cancel();
		timer.cancel();
		timerTask = null;
		timer = null;
	}

	public void startTimer() {

		//set a new Timer
		timer = new Timer();
		//initialize the TimerTask's job
		initializeTimerTask();
		//schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 2000, 10000); //
	}


	public void initializeTimerTask() {
		timerTask = new TimerTask() {
			public void run() {
				//use a handler to run a toast that shows the current timestamp
				handler.post(new Runnable() {
					public void run() {
						/*
						//get the current timeStamp
						Calendar calendar = Calendar.getInstance();
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
						final String strDate = simpleDateFormat.format(calendar.getTime());
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(getApplicationContext(), strDate, duration);
						toast.show();
						*/

						wasInBackground = true;

						String adContent = "";
						try {
							adContent = Common.downloadContentFromInternet(Common.GetBaseUrl() + "ad.txt");
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (adContent.isEmpty())
							btnShowAd.setVisibility(View.GONE);
						else {
							btnShowAd.setVisibility(View.VISIBLE);
							btnShowAd.setText(adContent);
						}
					}
				});
			}
		};
	}

	private void generateTablesView_works_without_scrollview() {
		LinearLayout tablesContainer = (LinearLayout) findViewById(R.id.tablesContainer);

		tablesContainer.removeAllViews();
		Table[] tables = new Table[TableHelper.TableList().size()];
		TableHelper.TableList().toArray(tables);
		Arrays.sort(tables);
		for (Table table : tables) {
			ListView lv = new ListView(this);
			Order[] orders = new Order[table.TableOrders().size()];
			table.TableOrders().toArray(orders);
			Arrays.sort(orders);
			TableOrderAdapter adapter = null;
			try {
				adapter = new TableOrderAdapter(this,
						R.layout.table_order, orders);
			} catch (Exception e) {
				Log.e(Define.APP_CATALOG, e.toString());
				e.printStackTrace();
			}
			View header = (View) getLayoutInflater().inflate(R.layout.table_header, null);
			TextView textViewTableNr = (TextView) header.findViewById(R.id.txtTableNr);
			textViewTableNr.setText(table.TableNr);
			header.setOnClickListener(this);
			header.setTag(table);
			lv.addHeaderView(header);
			lv.setAdapter(adapter);
			tablesContainer.addView(lv);
		}
	}

	private void generateTablesView() {
		MyApplication.NeedUpdate = false;
		//DoGenerateListDetailView();
		DoGenerateSimpleTableOverview();
	}

	private void DoGenerateSimpleTableOverview() {
		try {
			LinearLayout tablesContainer = (LinearLayout) findViewById(R.id.tablesContainer);

			tablesContainer.removeAllViews();
			Table[] tables = TableHelper.GetSortedTableList();

			int numberOfItemPerHorizontalLine = 2;
			int icount = 0;

			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			int buttonWidth = 180;
			int buttonHeight = 180;

			int marginHorizontal = 20;
			numberOfItemPerHorizontalLine = width / (buttonWidth + marginHorizontal * 2);

			LinearLayout line = new LinearLayout(this);
			line.setOrientation(LinearLayout.HORIZONTAL);
			for (Table table : tables) {

				if (icount == numberOfItemPerHorizontalLine) {
					tablesContainer.addView(line);
					icount = 0;
					line = new LinearLayout(this);
					line.setOrientation(LinearLayout.HORIZONTAL);
				}

				Button btnTable = new Button(this);

				//btnTable.setWidth(180);
				//btnTable.setHeight(180);
				btnTable.setTag(table);
				btnTable.setText(table.TableNr);
				//http://stackoverflow.com/questions/9494037/how-to-set-text-size-of-textview-dynamically-for-different-screens
				//use COMPLEX_UNIT_SP, text e.g ''123' display same physical width on screen with different resolution.
				btnTable.setTextSize(COMPLEX_UNIT_SP, 18); //(10, 30).setTextSize(30);
				//btnTable.setPadding(20, 20, 20, 20);
				btnTable.setOnClickListener(this);
				if (table.IsTakeAway) {
					btnTable.setBackgroundColor(Color.argb(255, 0, 128, 255));
				} else {
					btnTable.setBackgroundColor(Color.argb(255, 255, 128, 0));
				}
				btnTable.setTextColor(Color.WHITE);

				//http://stackoverflow.com/questions/4259467/in-android-how-to-make-space-between-linearlayout-children
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
				layoutParams.setMargins(marginHorizontal, 10, marginHorizontal, 10);
				line.addView(btnTable, layoutParams);
				icount++;
			}

			if (line.getChildCount() > 0) {
				tablesContainer.addView(line);
			}

			if (tables.length == 0) {
				tablesContainer.addView(CreateTextViewLine(getText(R.string.msg_no_orders_yet).toString()));
			}

		} catch (Exception e) {
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
	}

	private void DoGenerateListDetailView() {
		LinearLayout tablesContainer = (LinearLayout) findViewById(R.id.tablesContainer);

		tablesContainer.removeAllViews();
		Table[] tables = new Table[TableHelper.TableList().size()];
		TableHelper.TableList().toArray(tables);
		Arrays.sort(tables);
		for (Table table : tables) {
			//ListView lv=new ListView(this);
			Order[] orders = new Order[table.TableOrders().size()];
			table.TableOrders().toArray(orders);
			Arrays.sort(orders);
			TableOrderAdapter adapter = null;
			try {
				adapter = new TableOrderAdapter(this,
						R.layout.table_order, orders);
			} catch (Exception e) {
				Log.e(Define.APP_CATALOG, e.toString());
				e.printStackTrace();
			}
			View header = (View) getLayoutInflater().inflate(R.layout.table_header, null);
			TextView textViewTableNr = (TextView) header.findViewById(R.id.txtTableNr);
			textViewTableNr.setText(table.TableNr);
			//lv.addHeaderView(header);
			//lv.setAdapter(adapter);
			tablesContainer.addView(header);
			header.setOnClickListener(this);
			header.setTag(table);
			for (int icount = 0; icount < adapter.getCount(); icount++) {
				View item = adapter.getView(icount, null, null);
				tablesContainer.addView(item);
			}
		}
	}

	@Override
	public void onClick(View view) {
		try {
			if (view.getId() == R.id.btnAddTable) {
				if (Common.IsAllowedToAddTable())
					startActivity(new Intent(this, TableDetailActivity.class));
			} else if (view.getId() == R.id.btnAddTableTakeAway) {
				if (Common.IsAllowedToAddTable()) {
					Intent i = new Intent(view.getContext(), TableDetailActivity.class);
					i.putExtra(Define.TAKEAWAYTABLE, "True");
					startActivity(i);
				}
			} else if (view.getId() == R.id.btnShowAd) {
				Intent i = new Intent(view.getContext(), AdDetailActivity.class);
				startActivity(i);
			} else if (view.getId() == R.id.btnExtra) {
				startActivity(new Intent(this, AdvancedOperationActivity.class));
			/*
			 // custom dialog
			final Dialog dialog = new Dialog(context);			
			dialog.setContentView(R.layout.tables_overview_extra);
			dialog.setTitle("tofix");
 
			// set the custom dialog components - text, image and button
			Button btnDownloadMenu=(Button)dialog.findViewById(R.id.btnDownloadMenu);
			Button btnEditBusinessInfo=(Button)dialog.findViewById(R.id.btnEditBusinessInfo);
			Button btnParameter=(Button)dialog.findViewById(R.id.btnParameters);
			Button btnExit=(Button)dialog.findViewById(com.eurotong.orderhelperandroid.R.id.btnExit);
			Button btnLogin=(Button)dialog.findViewById(R.id.btnLogin);
			Button btnSaveCustomerID=(Button)dialog.findViewById(R.id.btnSaveCustomerID);
			Button btnDeleteAllTables=(Button)dialog.findViewById(R.id.btnDeleteAllTables);
			Button btnAbout=(Button)dialog.findViewById(R.id.btnAbout);
			
			final EditText editTextCustomerID=(EditText)dialog.findViewById(R.id.editTextCustomerID);
			final EditText editTextServerAddress=(EditText)dialog.findViewById(R.id.editTextServerAddress);
			
			editTextCustomerID.setText(Customer.Current().CustomerID);
			editTextServerAddress.setText(Customer.Current().ServerAddress);
			
			// if button is clicked, close the custom dialog
			btnDownloadMenu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					downloadFile(Define.MENU_FILE_NAME);
		            downloadFile(Define.BUSINESS_INFO_FILE_NAME);
		            downloadFile(Define.PRINT_LAYOUT_NAME);
		            downloadFile(Define.PRINT_LAYOUT_KITCHEN_NAME);
		            downloadFile(Define.SETTING_FILE_NAME);
		            downloadFile(Define.PRINT_LAYOUT_BAR_NAME);
		            downloadFile(Define.DEVICE_FILE);
					//dialog.dismiss();
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
					dialog.dismiss();
				}
			});
			btnLogin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {								
					dialog.dismiss();
				}
			});
			btnSaveCustomerID.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					try
					{
					String serverAddress= editTextServerAddress.getText().toString().trim(); // txtServerAddress.Text.Trim(); 
		            long id=-1;
		            String idString=editTextCustomerID.getText().toString().trim();
		            Boolean isValid = false;
		            id=Long.parseLong(idString);
		            if (idString.length()>5 && idString.length()<15)
		            {
		                long mod;
		                long remainder;
		                mod = id / 100;
		                remainder = mod % 99;
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
		                    Common.GetToastLong(getString(R.string.msg_save_success));		                    
		                }               
		            }
		            if(!isValid)
		            {
		            	Common.GetToastLong(getString(R.string.msg_download_error));
		            }
					}catch(Exception e)
					{
						Common.GetToastLong(getString(R.string.msg_download_error));	
						Log.e(Define.APP_CATALOG, e.toString());
					}
				}
			});
			dialog.show();
			*/
			} else if (view.getTag() != null && (Table) view.getTag() != null) {
				//finish();
				//Intent i = new Intent(getApplicationContext(), TableOrderActivity.class);
				Intent i = new Intent(getApplicationContext(), OrderMenuActivity.class);
				i.putExtra(Define.TABLE_NR, ((Table) view.getTag()).TableNr);
				startActivity(i);
			}
		}
		catch (Exception e) {
			Log.e(Define.APP_CATALOG, e.toString());
			int i=0;
			i=i/0;
		}
	}

	//http://stackoverflow.com/questions/5726657/how-to-detect-orientation-change-in-layout-in-android
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		//http://stackoverflow.com/questions/1016896/android-how-to-get-screen-dimensions
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		// Checks the orientation of the screen
		//if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		//    Toast.makeText(this, "landscape:width:" +Integer.toString(width) + "height:" + Integer.toString(height), Toast.LENGTH_SHORT).show();
		//} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
		//    Toast.makeText(this, "portrait:width:" + Integer.toString(width) + "height:" + Integer.toString(height), Toast.LENGTH_SHORT).show();
		//}
	}

	//use same layout as it in menu group
	private View CreateTextViewLine(String text) {
		View menugroupRow = (View) getLayoutInflater().inflate(R.layout.order_menu_group_row, null);
		TextView textViewMenuGroupName = (TextView) menugroupRow.findViewById(R.id.txtMenuGroupName);
		textViewMenuGroupName.setText(text);
		return menugroupRow;
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"TablesOverview Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.eurotong.orderhelperandroid/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"TablesOverview Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.eurotong.orderhelperandroid/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}
