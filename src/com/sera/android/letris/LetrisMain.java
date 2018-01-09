package com.sera.android.letris;

import com.sera.android.letris.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class LetrisMain extends Activity 
{
	// Opciones de menu
    private static final int MENU_START = 0;
    private static final int MENU_STOP = 1;

    // La pantalla y el hilo del programa
	LetrisDrawingSurface mLetrisView;
	LetrisThread mLetrisThread;
 
	
	/**********************************************************************************/
	/** Eventos del ciclo de vida   **/
	/**********************************************************************************/
	/**
     * Creación de la Activity .
     * 
     * @param savedInstanceState Bundle que contiene el estado de la ejecucion anterior
     *        y null si es una nueva
     */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // Usamos el layout del fichero res/layout/main.xml
        setContentView(R.layout.main);
 
        //---- Componentes de main.xml
        mLetrisView = (LetrisDrawingSurface)findViewById(R.id.letrisGameSurface);
        mLetrisThread = mLetrisView.getThread();
    
        // Cogemos el TextView definido en el layout, para usarlo en el Thread
        mLetrisThread.setTextView((TextView) findViewById(R.id.messagesTextView));
        mLetrisThread.setState(LetrisStatus.STATE_RUNNING,null);
        mLetrisThread.doStart();
    }
    
    /**
     * Invocada cuando la Activity pierde el foco.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mLetrisThread.pause(); // Pausar el juego cuando la Activity se pausa
    }
    
    
    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     * 
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        // Aqui deberiamos invocar a un procedimiengo de guardado del thread, en el Bundle
        //mLunarThread.saveState(outState);
        Log.w(this.getClass().getName(), "******* Invocado SaveInstanceState!");
    }

    
    /*****************************************************************************************/
    /** Eventos de Cambio de estado del juego     **/
    /*****************************************************************************************/
    public void backToMain()
    {
		// Tras terminar el juego, nos vamos a la pantalla principal
		Intent myIntent = new Intent();
		myIntent.setClassName("com.sera.android.letris", "com.sera.android.letris.LetrisMainMenu");
		startActivity(myIntent);
    }

    public void doLose()
    {
    	mLetrisThread.setState(LetrisStatus.STATE_LOSE,"Game Over");
    	mLetrisThread.setRunning(false);
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.backToMain();
    }


    /*****************************************************************************************/
    /** Eventos de Acciones de menu     **/
    /*****************************************************************************************/
    
    /**
     * Invoked during init to give the Activity a chance to set up its Menu.
     * 
     * @param menu the Menu to which entries may be added
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);

        return true;
    }    


    /**
     * Invoked when the user selects an item from the Menu.
     * 
     * @param item the Menu entry which was selected
     * @return true if the Menu item was legit (and we consumed it), false
     *         otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	AlertDialog alert = null;
    	
        switch (item.getItemId()) {
            case MENU_START:
				builder.setMessage(getResources().getText(R.string.menu_start)+"?");
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.strSi, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
		            	mLetrisThread.doStart();
					}
				});
				builder.setNegativeButton(R.string.strNo, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				alert = builder.create();
				alert.show();

                return true;

            case MENU_STOP:
				builder.setMessage(getResources().getText(R.string.menu_stop)+"?");
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.strSi, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
		            	mLetrisThread.setState(LetrisStatus.STATE_EXIT, getText(R.string.message_stopped));
		            	backToMain();
					}
				});
				builder.setNegativeButton(R.string.strNo, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				alert = builder.create();
				alert.show();

            	//mLetrisThread.setState(LetrisStatus.STATE_EXIT, getText(R.string.message_stopped));
                return true;
        }

        return false;
    }
    
    
}
