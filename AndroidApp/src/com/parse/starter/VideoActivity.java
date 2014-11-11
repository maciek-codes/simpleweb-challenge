package com.parse.starter;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;

public class VideoActivity extends Activity implements Session.SessionListener, Publisher.PublisherListener,
Subscriber.VideoListener{

	private static final String LOGTAG = "demo-hello-world";
	private Session mSession;
	private Publisher mPublisher;
	private Subscriber mSubscriber;
	private ArrayList<Stream> mStreams;
	protected Handler mHandler = new Handler();

	private RelativeLayout mPublisherViewContainer;
	private RelativeLayout mSubscriberViewContainer;

	// Spinning wheel for loading subscriber view
	private ProgressBar mLoadingSub;

	private boolean resumeHasRun = false;

	private boolean mIsBound = false;
	ServiceConnection mConnection;
	
	CountDownTimer mCountDown;
	int mLengthOfCallRequired = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOGTAG, "ONCREATE");
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		mPublisherViewContainer = (RelativeLayout) findViewById(R.id.publisherview);
		mSubscriberViewContainer = (RelativeLayout) findViewById(R.id.subscriberview);
		mLoadingSub = (ProgressBar) findViewById(R.id.loadingSpinner);

		mStreams = new ArrayList<Stream>();
		sessionConnect();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Remove publisher & subscriber views because we want to reuse them
		if (mSubscriber != null) {
			mSubscriberViewContainer.removeView(mSubscriber.getView());
		}
		reloadInterface();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mSession != null) {
			mSession.onPause();

			if (mSubscriber != null) {
				mSubscriberViewContainer.removeView(mSubscriber.getView());
			}
		}

		if(mConnection == null){	    
			mConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName className,IBinder binder){

				}

				@Override
				public void onServiceDisconnected(ComponentName className) {
					mConnection = null;
				}

			};
		}

		if(!mIsBound){
			mIsBound = true;
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		if(mIsBound){
			unbindService(mConnection);
			mIsBound = false;
		}

		if (!resumeHasRun) {
			resumeHasRun = true;
			return;
		} else {
			if (mSession != null) {
				mSession.onResume();
			}
		}

		reloadInterface();
	}

	@Override
	public void onStop() {
		super.onStop();

		if(mIsBound){
			unbindService(mConnection);
			mIsBound = false;
		}

		if(mIsBound){
			unbindService(mConnection);
			mIsBound = false;
		}
		if (isFinishing()) {
			if (mSession != null) {
				mSession.disconnect();
			}
		}
	}

	@Override
	public void onDestroy() {
		if(mIsBound){
			unbindService(mConnection);
			mIsBound = false;
		}

		if (mSession != null)  {
			mSession.disconnect();
		}

		restartAudioMode();

		super.onDestroy();
		finish();
	}

	@Override
	public void onBackPressed() {
		if (mSession != null) {
			mSession.disconnect();
		}

		restartAudioMode();

		super.onBackPressed();
	}

	public void reloadInterface() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mSubscriber != null) {
					attachSubscriberView(mSubscriber);
				}
			}
		}, 500);
	}

	public void restartAudioMode() {
		AudioManager Audio =  (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		Audio.setMode(AudioManager.MODE_NORMAL);
		this.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
	}

	private void sessionConnect() {
		if (mSession == null) {
			mSession = new Session(VideoActivity.this,
					OpenTokConfig.API_KEY, OpenTokConfig.SESSION_ID);
			mSession.setSessionListener(this);
			mSession.connect(OpenTokConfig.TOKEN);
		}
	}

	@Override
	public void onConnected(Session session) {
		Log.i(LOGTAG, "Connected to the session.");
		if (mPublisher == null) {
			mPublisher = new Publisher(VideoActivity.this, "publisher");
			mPublisher.setPublisherListener(this);
			attachPublisherView(mPublisher);
			mSession.publish(mPublisher);
		}
	}

	@Override
	public void onDisconnected(Session session) {
		Log.i(LOGTAG, "Disconnected from the session.");
		if (mPublisher != null) {
			mPublisherViewContainer.removeView(mPublisher.getView());
		}

		if (mSubscriber != null) {
			mSubscriberViewContainer.removeView(mSubscriber.getView());
		}

		mPublisher = null;
		mSubscriber = null;
		mStreams.clear();
		mSession = null;
	}

	private void subscribeToStream(Stream stream) {
		mSubscriber = new Subscriber(VideoActivity.this, stream);
		mSubscriber.setVideoListener(this);
		mSession.subscribe(mSubscriber);
		// start loading spinning
		mLoadingSub.setVisibility(View.VISIBLE);
	}

	private void unsubscribeFromStream(Stream stream) {
		mStreams.remove(stream);
		if (mSubscriber.getStream().equals(stream)) {
			mSubscriberViewContainer.removeView(mSubscriber.getView());
			mSubscriber = null;
			if (!mStreams.isEmpty()) {
				subscribeToStream(mStreams.get(0));
			}
		}
	}

	private void attachSubscriberView(Subscriber subscriber) {
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				getResources().getDisplayMetrics().widthPixels, getResources()
				.getDisplayMetrics().heightPixels);
		mSubscriberViewContainer.addView(mSubscriber.getView(), layoutParams);
		subscriber.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
				BaseVideoRenderer.STYLE_VIDEO_FILL);
	}

	private void attachPublisherView(Publisher publisher) {
		mPublisher.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
				BaseVideoRenderer.STYLE_VIDEO_FILL);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				320, 240);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
				RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
				RelativeLayout.TRUE);
		layoutParams.bottomMargin = dpToPx(8);
		layoutParams.rightMargin = dpToPx(8);
		mPublisherViewContainer.addView(mPublisher.getView(), layoutParams);
	}

	@Override
	public void onError(Session session, OpentokError exception) {
		Log.i(LOGTAG, "Session exception: " + exception.getMessage());
	}

	@Override
	public void onStreamReceived(Session session, Stream stream) {

		if (!OpenTokConfig.SUBSCRIBE_TO_SELF) {
			mStreams.add(stream);
			if (mSubscriber == null) {
				subscribeToStream(stream);
			}
		}
	}

	@Override
	public void onStreamDropped(Session session, Stream stream) {
		if (!OpenTokConfig.SUBSCRIBE_TO_SELF) {
			if (mSubscriber != null) {
				unsubscribeFromStream(stream);
			}
		}
	}

	@Override
	public void onStreamCreated(PublisherKit publisher, Stream stream) {
		if (OpenTokConfig.SUBSCRIBE_TO_SELF) {
			mStreams.add(stream);
			if (mSubscriber == null) {
				subscribeToStream(stream);
			}
		}
	}

	@Override
	public void onStreamDestroyed(PublisherKit publisher, Stream stream) {
		if ((OpenTokConfig.SUBSCRIBE_TO_SELF && mSubscriber != null)) {
			unsubscribeFromStream(stream);
		}
	}

	@Override
	public void onError(PublisherKit publisher, OpentokError exception) {
		Log.i(LOGTAG, "Publisher exception: " + exception.getMessage());
	}

	@Override
	public void onVideoDataReceived(SubscriberKit subscriber) {
		Log.i(LOGTAG, "First frame received");

		// stop loading spinning
		mLoadingSub.setVisibility(View.GONE);
		attachSubscriberView(mSubscriber);
		
		mCountDown = new CountDownTimer(mLengthOfCallRequired, 1000) {

		     public void onTick(long millisUntilFinished) {
		         // Toast.makeText(getApplicationContext(), "You have been connected for " + (mLengthOfCallRequired-millisUntilFinished), Toast.LENGTH_SHORT).show();
		     }

		     public void onFinish() {
		         Toast.makeText(getApplicationContext(), "You've spoken for " + mLengthOfCallRequired + "! You're now friends.", Toast.LENGTH_SHORT).show();
		     }
		  }.start();

	}

	/**
	 * Converts dp to real pixels, according to the screen density.
	 * 
	 * @param dp
	 *            A number of density-independent pixels.
	 * @return The equivalent number of real pixels.
	 */
	private int dpToPx(int dp) {
		double screenDensity = this.getResources().getDisplayMetrics().density;
		return (int) (screenDensity * (double) dp);
	}

	@Override
	public void onVideoDisabled(SubscriberKit subscriber, String reason) {
		Log.i(LOGTAG,
				"Video disabled:" + reason);
		
		mCountDown.cancel();
	}

	@Override
	public void onVideoEnabled(SubscriberKit subscriber, String reason) {
		Log.i(LOGTAG,"Video enabled:" + reason);		
	}

	@Override
	public void onVideoDisableWarning(SubscriberKit subscriber) {
		Log.i(LOGTAG, "Video may be disabled soon due to network quality degradation. Add UI handling here.");	
	}

	@Override
	public void onVideoDisableWarningLifted(SubscriberKit subscriber) {
		Log.i(LOGTAG, "Video may no longer be disabled as stream quality improved. Add UI handling here.");
	}


}
