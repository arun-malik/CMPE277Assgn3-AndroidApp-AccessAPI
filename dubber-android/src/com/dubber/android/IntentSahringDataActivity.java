package com.dubber.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class IntentSahringDataActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intent_sahring_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.intent_sahring_data, menu);
		return true;
	}

}
