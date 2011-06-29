package ro.pub.stickier;

import ro.pub.stickier.asyntask.CacheUpdaterTask;
import ro.pub.stickier.asyntask.FeedGenerateRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends Activity {
	
	//public int fIndex;
	//public int bIndex;
	
	public int index; 
	
	public Button next;
	public Button back;
	public TextView message;
	public TextView status;
	
	public View before, after;
	
	public void initView(){
	    //fIndex = 1;
	    //bIndex = -1;
		index = 0;
	    back.setEnabled(false);
	    message.setText(Application.cache.get(index));
	    
	    before.setVisibility(View.GONE);
	    after.setVisibility(View.VISIBLE);
	    if (Application.cache.size() == 1)
	    	next.setEnabled(false);
	    
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.display);
		
	    after = (View)findViewById(R.id.after);
		before = (View)findViewById(R.id.before);
		
	    next = (Button) findViewById(R.id.next);
	    back = (Button) findViewById(R.id.back);
	    message = (TextView)findViewById(R.id.message);
	    status = (TextView) findViewById(R.id.status);
	    
	    next.setOnClickListener(new NextClick());
	    back.setOnClickListener(new BackClick());
	    
	    Application.cache.reset();
	    
		if (!(Application.cache.getCacheId() != null && Application.cache.getCacheId().equals("stickier")))
	    	new FeedGenerateRequest("sticker",this).execute();
		else {
			if (Application.cache.size()>0){
				status.setText("Already in cache");
				initView();
				return;
			} else {
				status.setText("Empty feed");
			}
		}
	    
		after.setVisibility(View.GONE);
	    
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
	}
	
	class NextClick implements OnClickListener {
		
//		public void onClick(View v) {
//			
//			back.setEnabled(true);
//			if(fIndex == Application.cache.size() - 1)
//				next.setEnabled(false);
//
//			message.setText(Application.cache.get(fIndex));
//			if (fIndex < Application.cache.size() - 1){
//				bIndex++;
//				fIndex++;
//			}
//		}
		
		public void onClick(View v){
			message.setText(Application.cache.get(++index));
			
			if (index < Application.cache.size() - 1){
				next.setEnabled(true);
			} else {
				next.setEnabled(false);
			}
			
			if (index > 0){
				back.setEnabled(true);
			} else {
				back.setEnabled(false);
			}
		}

	}
	
	class BackClick implements OnClickListener {
			
//			public void onClick(View v) {
//				
//				message.setText(Application.cache.get(bIndex));
//				if (bIndex >0 ){
//					bIndex--;
//					fIndex--;
//				}
//				
//				next.setEnabled(true);
//				if(bIndex == 0)
//					back.setEnabled(false);
//				
//			}	
		
		public void onClick(View v){
			message.setText(Application.cache.get(--index));
			
			if (index < Application.cache.size() - 1){
				next.setEnabled(true);
			} else {
				next.setEnabled(false);
			}
			
			if (index > 0){
				back.setEnabled(true);
			} else {
				back.setEnabled(false);
			}
		}
	}
	
}
