package com.sera.android.letris;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.sera.android.letris.records.RecordsAdapter;
import com.sera.android.letris.records.DAO.LetrisDAO;
import com.sera.android.letris.records.DAO.Record;

public class LetrisMainMenu extends Activity implements OnClickListener
{
	private static final String TAG = null;
	private LetrisDAO ldao;

	/**
     * Invoked when the Activity is created.
     * 
     * @param savedInstanceState a Bundle containing state saved from a previous
     *        execution, or null if this is a new execution
     */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Usamos el layout del fichero res/layout/mainmenu.xml
        setContentView(R.layout.mainmenu);

		// Bloque de lectura de records
        if (ldao==null) {
        	ldao = new LetrisDAO(this);
        }
        ArrayList<Record> records = ldao.getRecords(); 

        RecordsAdapter adapter = new RecordsAdapter(this,records);
        ListView lvRecords = (ListView)findViewById(R.id.listaRecords);
        lvRecords.setAdapter(adapter);

        Button bv = (Button)findViewById(R.id.botonEmpezar);
        bv.setOnClickListener(this);
    }
	
	@Override
	public void onStart()
	{
		super.onStart();
		ThreadCargaPalabras tcp = new ThreadCargaPalabras();
		tcp.start();
	}
	
	/**
	 * Pulsacion de boton
	 */
	public void onClick(View v)
	{
		Intent myIntent = new Intent();
		myIntent.setClassName("com.sera.android.letris", "com.sera.android.letris.LetrisMain");
		startActivity(myIntent);
	}
	
	
	private class ThreadCargaPalabras extends Thread
	{
		public void run()
		{
			TreeSet<String> sDiccionario = new TreeSet<String>();
			BufferedReader buf=null;
			FileInputStream fileIS=null;
			try {
				InputStream is = getResources().openRawResource(R.raw.words);
				buf = new BufferedReader(new InputStreamReader(is));
				String palabra="";
				Log.w(TAG,"-------------------- Inicio lectura fichero de palabras:");
				while ((palabra=buf.readLine())!=null) {
					palabra = palabra.toUpperCase();
					sDiccionario.add(palabra);
				}
				Log.w(TAG,"-------------------- Fin lectura fichero de palabras:");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileIS!=null) try { fileIS.close(); } catch(Exception e) { e.printStackTrace(); }
				if (buf!=null) try { buf.close(); } catch(Exception e) { e.printStackTrace(); }
			}
			
			LetrisThread.sDiccionario = sDiccionario;
		
		}
	}
}
