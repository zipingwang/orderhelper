package com.eurotong.orderhelperandroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SocketTest extends Activity implements OnClickListener {
Button btnSendRestsoft;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.eurotong.orderhelperandroid.R.layout.activity_socket_test);
        btnSendRestsoft =(Button)findViewById(com.eurotong.orderhelperandroid.R.id.btnSendRestsoft);
        btnSendRestsoft.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(com.eurotong.orderhelperandroid.R., menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==com.eurotong.orderhelperandroid.R.id.btnSendRestsoft)
		{
			new MyAsyncTask().execute();
		}
		
	}
}
class MyAsyncTask extends AsyncTask<Void, Void, Void>
{

    ProgressDialog mProgressDialog;
    @Override
    protected void onPostExecute(Void result) {
        //mProgressDialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        //mProgressDialog = ProgressDialog.show(ActivityName.this, "Loading...", "Data is Loading...");
    }

    @Override
    protected Void doInBackground(Void... params) {
    	Socket sock;
		try {
			sock = new Socket("192.168.1.6",10000);
		
			OutputStream os = sock.getOutputStream();  
			String txt = "id:60bc4976-1dd2-4e9b-b771-577c9bc97630@@051207637@@@ORDER@@@20120928004@@@wang2@@vital2@@noordlaan@@8800@@roeselare@@051207637@@0477130089@@vital@@Mr@@vital.wang@eurotong.com@@@Option1@@Today@@18:25@@@3@2@@@0 ";
			byte[] bytes= txt.getBytes();
			os.write(bytes);
			os.flush();
			Thread.sleep(1000);
			sock.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(Define.APP_CATALOG, e.toString());
			e.printStackTrace();
		}
        return null;
    }
}

