package com.gtrdp.rolesystemclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	// constants
	final String URL_ROOT = "http://guntur.web.id/rolesystem/v1/";
	final String URL_FIRST = "http://guntur.web.id/rolesystem/v1/indent_gejala";
	final String URL_SECOND = "http://guntur.web.id/rolesystem/v1/indent_gejala2";
	
	// layout
	Spinner spinnerKasus;
	Button btnLanjut;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		spinnerKasus = (Spinner) findViewById(R.id.spinnerKasus);
		btnLanjut = (Button) findViewById(R.id.button1);
		
		new DownloadKasus(null, MainActivity.this).execute(URL_ROOT);
		
		btnLanjut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent("com.gtrdp.rolesystemclient.Indent1");
				i.putExtra("kasus", "K" + (spinnerKasus.getSelectedItemPosition()+1));
				startActivity(i);
			}
		});
	}
	
	class DownloadKasus extends AsyncTask<String, Void, String> {

		ArrayList<HttpEntity> myEntities = null;
		ProgressDialog myPg;
		ArrayAdapter<String> dataAdapter = null;
		
		public DownloadKasus(ArrayList<HttpEntity> myEntities, Context context) {
			super();
			this.myEntities = myEntities;
			this.myPg = ProgressDialog.show(context, "Please wait...", "Retrieving data...", true, true);
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			DefaultHttpClient myClient = new DefaultHttpClient();
			HttpEntity myEntity = null;
			HttpResponse myResponse = null;
			String response = null;
			JSONObject kasus = null;
			List<String> pilihan = new ArrayList<String>();
			
			HttpPost myPost = new HttpPost(url);
			if(myEntities != null){
				for(HttpEntity foo : myEntities){
					myPost.setEntity(foo);
				}
			}
			
			try {
				myResponse = myClient.execute(myPost);
				myEntity = myResponse.getEntity();
				response = EntityUtils.toString(myEntity);
				
				kasus = new JSONObject(response);
				
				JSONObject kasusNama = kasus.getJSONObject("case");
				
				pilihan.add(kasusNama.getString("K1"));
				pilihan.add(kasusNama.getString("K2"));
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			dataAdapter = new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_spinner_item, pilihan);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			myPg.dismiss();
			spinnerKasus.setAdapter(dataAdapter);
			Log.d("Downloader", "selesai");	
		}
		
	}
}