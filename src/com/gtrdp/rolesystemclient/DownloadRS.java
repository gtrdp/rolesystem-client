package com.gtrdp.rolesystemclient;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.sax.StartElementListener;
import android.util.Log;

public class DownloadRS extends AsyncTask<String, Void, String> {

	ArrayList<HttpEntity> myEntities = null;
	ProgressDialog myPg;
	
	public DownloadRS(ArrayList<HttpEntity> myEntities, Context context) {
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
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		myPg.dismiss();
		Log.i("hasilny", result.toString());
	}
	
}