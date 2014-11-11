package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button signupButton;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ParseAnalytics.trackAppOpened(getIntent());
		
		signupButton = (Button)this.findViewById(R.id.signup_btn);
		
		signupButton.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		// Logs 'install' and 'app activate' App Events.
		AppEventsLogger.activateApp(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Logs 'app deactivate' App Event.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onClick(View v) {
		
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d("MyApp", "User signed up and logged in through Facebook!");
				} else {
					Log.d("MyApp", "User logged in through Facebook!");
			    }
			}
		}); // loginIn
		
	}
}
