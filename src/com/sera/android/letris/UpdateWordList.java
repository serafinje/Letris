package com.sera.android.letris;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sera.android.letris.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class UpdateWordList extends Activity {
	final String TAG="UpdateWordList";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Context context=this.getBaseContext();
		File f = context.getFilesDir();
		File words = new File(f,"/words.txt");
		BufferedReader buf=null;
		try {
			//InputStream is = context.getAssets().open("words.txt");
			BufferedReader br= new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.words)));
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(words));
			while (br.ready()) {
				String s = br.readLine();
				dos.writeBytes(s);
			}
			dos.close();
			
			buf = new BufferedReader(new FileReader(words));
			while (buf.ready()) {
				String s = buf.readLine();
				Log.w(TAG,s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (buf!=null) try { buf.close(); } catch(Exception e) { e.printStackTrace(); }
		}
        
    }

}
