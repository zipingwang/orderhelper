package com.eurotong.orderhelperandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ReportActivity extends Activity implements View.OnClickListener {

    Button btnGoBack;
    TextView txtReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        btnGoBack=(Button)findViewById(R.id.btnGoBack);
        txtReport=(TextView)findViewById(R.id.txtReport);
        btnGoBack.setOnClickListener(this);

        double takeAwayTotal=0;
        double restaurentTotal=0;
        int takeAwayCount=0;
        int restaurantCount=0;
        for(Table table:TableHelper.TableList())
        {
            if(table.IsTakeAway)
            {
                takeAwayCount++;
                takeAwayTotal+=table.Total();
            }
            else
            {
                restaurantCount++;
                restaurentTotal+=table.Total();
            }
        }

        StringBuilder sb=new StringBuilder();
        sb.append("餐楼:");
        sb.append(restaurantCount + "桌. ");
        sb.append(Common.FormatDouble(restaurentTotal));
        sb.append(Define.NEW_LINE);
        sb.append(Define.NEW_LINE);
        sb.append("外卖:");
        sb.append(takeAwayCount + "张. ");
        sb.append(Common.FormatDouble(takeAwayTotal));
        sb.append(Define.NEW_LINE);
        sb.append(Define.NEW_LINE);
        sb.append("总计:");
        sb.append(Common.FormatDouble(restaurentTotal + takeAwayTotal));
        txtReport.setText(sb.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnGoBack)
        {    finish();
        }
    }
}
