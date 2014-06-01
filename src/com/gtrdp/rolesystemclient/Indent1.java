package com.gtrdp.rolesystemclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Indent1 extends Activity {

	// constants
	final String URL_ROOT = "http://guntur.web.id/rolesystem/v1/";
	final String URL_FIRST = "http://guntur.web.id/rolesystem/v1/indent_gejala";
	final String URL_SECOND = "http://guntur.web.id/rolesystem/v1/indent_gejala2";
	
	// layout
	Button btnLanjut;
	RadioGroup radioJawaban;
	RadioButton radioButtonJawaban;
	RoleSystemWSData rsData;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indent1);
		
		btnLanjut = (Button) findViewById(R.id.btnLanjutIndent);
		radioJawaban = (RadioGroup) findViewById(R.id.radioGroup1);
		
		// get intent
		Bundle b = getIntent().getExtras();
		List<NameValuePair> myEntities = new ArrayList<NameValuePair>();
		int i = 0;
		for(String key : b.keySet()) {
			Object value = b.get(key);
			myEntities.add(new BasicNameValuePair(key, value.toString()));
			i++;
			Log.d("Jumlah Entity", Integer.toString(i));
		}
		
		rsData = new RoleSystemWSData();
		
		try {
			if(i == 1)
				rsData = new DownloadQuestion(myEntities, Indent1.this).execute(URL_FIRST).get();
			else
				rsData = new DownloadQuestion(myEntities, Indent1.this).execute(URL_SECOND).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		btnLanjut.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int selectedID = radioJawaban.getCheckedRadioButtonId();
				
				radioButtonJawaban = (RadioButton) findViewById(selectedID);
				
				// self-intent
				Intent next = new Intent("com.gtrdp.rolesystemclient.Indent1");
				next.putExtra("kasusid", rsData.getKasusid());
				next.putExtra("role_identnext", rsData.getRole_identnext());
				next.putExtra("answer_identnext", rsData.getAnswer_identnext());
				next.putExtra("ke", rsData.getKe());
				
				if(radioButtonJawaban.getText().equals("YA"))
					next.putExtra("jawaban", "A");
				else
					next.putExtra("jawaban", "B");
				
				startActivity(next);
			}
		});
	}
	
	class DownloadQuestion extends AsyncTask<String, Void, RoleSystemWSData> {

		List<NameValuePair> myEntities = null;
		ProgressDialog myPg;
		ArrayAdapter<String> dataAdapter = null;
		
		public DownloadQuestion(List<NameValuePair> myEntities, Context context) {
			super();
			this.myEntities = myEntities;
			this.myPg = ProgressDialog.show(context, "Please wait...", "Retrieving data...", true, true);
		}
		
		@Override
		protected RoleSystemWSData doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = params[0];
			DefaultHttpClient myClient = new DefaultHttpClient();
			HttpEntity myEntity = null;
			HttpResponse myResponse = null;
			String response = null;
			JSONObject kasus = null;
			RoleSystemWSData rsData = new RoleSystemWSData();
			
			HttpPost myPost = new HttpPost(url);
			
			try {
				if(myEntities != null){
						myPost.setEntity(new UrlEncodedFormEntity(myEntities));
				}
				myResponse = myClient.execute(myPost);
				myEntity = myResponse.getEntity();
				response = EntityUtils.toString(myEntity);
				
				kasus = new JSONObject(response);
				
				if(kasus.getString("type").equals("question")) {
					rsData.setQuestion(kasus.getString("question"));
					rsData.setKasusid(kasus.getString("kasusid"));
					rsData.setRole_identnext(kasus.getString("role_identnext"));
					rsData.setAnswer_identnext(kasus.getString("answer_identnext"));
					rsData.setKe(kasus.getString("ke"));
				}else if(kasus.getString("type").equals("diagnosis")){
					Intent i = new Intent("com.gtrdp.rolesystemclient.Diagnosis");
					i.putExtra("diagnosis", kasus.getString("diagnosis"));
					i.putExtra("saran", kasus.getString("saran"));
					
					startActivity(i);
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				Toast.makeText(Indent1.this, e.toString(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return rsData;
		}

		@Override
		protected void onPostExecute(RoleSystemWSData result) {
			// TODO Auto-generated method stub
			TextView tvKe = (TextView) findViewById(R.id.tvTahapIdentifikasi);
			tvKe.setText("Tahap Identifikasi ke-" + result.getKe());
			
			TextView tvQuestion = (TextView) findViewById(R.id.tvPertanyaan);
			tvQuestion.setText(result.getQuestion());
			
			myPg.dismiss();	
		}
		
	}
}
