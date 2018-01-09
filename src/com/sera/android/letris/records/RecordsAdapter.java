package com.sera.android.letris.records;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sera.android.letris.R;
import com.sera.android.letris.records.DAO.Record;

/**
 * Adapter personalizado para manejar una lista de records 
 * @author Sera
 */
public class RecordsAdapter extends BaseAdapter
{
	// Activity en la que está corriendo la aplicación. La necesitamos para sacar información del contexto y llamar a las funcionalidades necesarias.
	private Activity parentActivity;
	private ArrayList<Record> records;

	// Variable interna, para manejar la ListView
	private LayoutInflater inflater;

	public RecordsAdapter(Activity parent,ArrayList<Record> rs) {
		this.parentActivity=parent;
		this.inflater = (LayoutInflater)parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.records = rs;
	}
	
	public int count = 0; /* Cantidad inicial de elementos visibles */
	public int getCount() {
		if (records==null) {
			return 0;
		} else {
			return records.size();
		}
	}

	public Record getItem(int pos) {
		return records.get(pos);
	}

	public long getItemId(int pos) { 
		return records.get(pos).getId();
	}

	public View getView(final int pos, View recordView, ViewGroup parent)
	{
		recordView = inflater.inflate(R.layout.recordline, null);
		final ViewHolder holder = new ViewHolder();
		holder.player = (TextView)recordView.findViewById(R.id.listaRecordsNombre);
		holder.record = (TextView)recordView.findViewById(R.id.listaRecordsPuntos);
		holder.recordDate = (TextView)recordView.findViewById(R.id.listaRecordsFecha);
		
		Record r = getItem(pos);
		holder.player.setText(r.getPlayer());
		holder.record.setText(""+r.getRecord());
		holder.recordDate.setText(r.getLocaleRecordDate(this.parentActivity));

		recordView.setTag(holder);
		return recordView;
	}

	
	
	
	public static class ViewHolder {
		public TextView player;
		public TextView record;
		public TextView recordDate;
	}

}
