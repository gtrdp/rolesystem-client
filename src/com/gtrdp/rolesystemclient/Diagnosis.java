package com.gtrdp.rolesystemclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Diagnosis extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_diagnosis);
		
		Intent i = getIntent();
		
		TextView tvDiagnosis = (TextView) findViewById(R.id.tvDiagnosis);
		TextView tvSaran= (TextView) findViewById(R.id.tvSaran);
		
		tvDiagnosis.setText(i.getExtras().getString("diagnosis"));
		tvSaran.setText(i.getExtras().getString("saran"));
	}
}
