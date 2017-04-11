package com.eurotong.orderhelperandroid;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TableOrderAdapter extends ArrayAdapter<Order>{
	

	    Context context; 
	    int layoutResourceId;    
	    Order data[] = null;
	    
	    public TableOrderAdapter(Context context, int layoutResourceId, Order[] data) throws Exception {		  
		        super(context, layoutResourceId, data);
		        this.layoutResourceId = layoutResourceId;
		        this.context = context;
		        this.data = data;
		   
	        
	    }

	    @Override
	    public int getCount()
	    {
	    	return data.length;
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        OrderHolder holder = null;
	        
	        if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new OrderHolder();
	            holder.txtOrderMenuNr = (TextView)row.findViewById(R.id.txtOrderMenuNr);
	            holder.txtOrderCount = (TextView)row.findViewById(R.id.txtOrderCount);
	            holder.txtOrderMenuName = (TextView)row.findViewById(R.id.txtOrderMenuName);
	            holder.txtOrderPriceString = (TextView)row.findViewById(R.id.txtOrderPriceString);
	            holder.txtOrderSubTotalString = (TextView)row.findViewById(R.id.txtOrderSubTotalString);	            
	            
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (OrderHolder)row.getTag();
	        }
	        
	        Order order = data[position];
	        holder.txtOrderMenuNr.setText(order.OrderMenuNr);
	        holder.txtOrderCount.setText( new DecimalFormat("#").format(order.OrderCount));
	        holder.txtOrderMenuName.setText(order.OrderMenuName);
	        holder.txtOrderPriceString.setText(order.OrderPriceString());
	        holder.txtOrderSubTotalString.setText(order.OrderSubTotalString());
	        
	        return row;
	    }
	    
	   
	    
	    static class OrderHolder
	    {    
	        TextView txtOrderMenuNr;
	        TextView txtOrderCount;
	        TextView txtOrderMenuName;
	        TextView txtOrderPriceString;
	        TextView txtOrderSubTotalString;
	    }
	}


