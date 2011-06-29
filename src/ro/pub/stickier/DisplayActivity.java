package ro.pub.stickier;

import ro.pub.stickier.asyntask.CacheUpdaterTask;
import ro.pub.stickier.asyntask.FeedGenerateRequest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends Activity {
	
	int fIndex;
	int bIndex;
	
	public Button next;
	public Button back;
	public TextView message;
	public TextView status;
	
	public TextView getFeedStatus() {
		return status;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.display);
	    
	    fIndex = 0;
	    bIndex = 0;
	    
	    next = (Button) findViewById(R.id.next);
	    back = (Button) findViewById(R.id.back);
	    
	    message = (TextView)findViewById(R.id.message);
	    status = (TextView)findViewById(R.id.feedStatus);
	    
	    if (!(Application.cache.getCacheId() != null && Application.cache.getCacheId().equals("stickier")))
	    	new FeedGenerateRequest("sticker",this).execute();
	    
	    next.setOnClickListener(new NextClick());
	    back.setOnClickListener(new BackClick());
	    back.setEnabled(false);
	    
	    //new CacheUpdaterTask(this,"sticker").execute();
	    
	};
	
	@Override
	protected void onPause() {
		super.onPause();	
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();	
		fIndex = bIndex = 0;	
	}
	
	class NextClick implements OnClickListener {
		
		public void onClick(View v) {

			if (Application.cache.size() == 0)
				return;
			
			back.setEnabled(true);
			if(fIndex == Application.cache.size() - 1)
				next.setEnabled(false);

			if (fIndex == bIndex){
				message.setText(Application.cache.get(fIndex));
				if (fIndex < Application.cache.size() - 1)
					fIndex++;
				return;
			}

			message.setText(Application.cache.get(fIndex));
			if (fIndex < Application.cache.size() - 1){
				bIndex++;
				fIndex++;
			}
		}

	}
	
	class BackClick implements OnClickListener {
			
			public void onClick(View v) {
				
				if (Application.cache.size() == 0)
					return;
				
				message.setText(Application.cache.get(bIndex));
				if (bIndex >0 ){
					bIndex--;
					fIndex--;
				}
				
				next.setEnabled(true);
				if(bIndex == 0)
					back.setEnabled(false);
				
			}		
	}
	
}
