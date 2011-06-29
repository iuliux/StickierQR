package ro.pub.stickier;

import ro.pub.stickier.asyntask.FeedGenerateRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WaitingActivity extends Activity {

	TextView status;
	
	public TextView getStatus() {
		return status;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.issuing_request);
		
		status = (TextView) findViewById(R.id.status);
		
		if (!(Application.cache.getCacheId() != null && Application.cache.getCacheId().equals("stickier")))
	    	new FeedGenerateRequest("sticker",this).execute();
		else {
			if (Application.cache.size()>0){
				startActivity(new Intent(this,DisplayActivity.class));
			} else {
				status.setText("Empty feed");
			}
		}
		
	}
	
}
