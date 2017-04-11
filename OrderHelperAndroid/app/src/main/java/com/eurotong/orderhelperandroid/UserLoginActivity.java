package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class UserLoginActivity extends Activity implements OnClickListener {

	EditText editTextUserName;
	EditText editTextUserPassword;
	Button btnLogin;
    Button btnGoBack;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        
        editTextUserName=(EditText)findViewById(R.id.editTextUserName);
        editTextUserPassword=(EditText)findViewById(R.id.editTextUserPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_login, menu);
        return true;
    }

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.btnLogin)
		{
			User.Current().UserName=editTextUserName.getText().toString();
			User.Current().Password=editTextUserPassword.getText().toString();
			finish();
		}
        else if(v.getId()==R.id.btnGoBack)
        {
            finish();
        }
	}
}
