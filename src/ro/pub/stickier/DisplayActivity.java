package ro.pub.stickier;

import ro.pub.stickier.asyntask.CacheUpdaterTask;
import ro.pub.stickier.asyntask.FeedGenerateRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static ro.pub.stickier.Application.*;

public class DisplayActivity extends Activity {
	
	public int index; 
	
	public Button next;
	public Button back;
	public TextView message;
	public TextView status;
	private ImageView waiting;
	private ImageView wrong;
	private Animation mFadeAnim;
	
	public View before, after;
	
	public String stickerId;
	
	public void initView(){
		
		index = 0;
	    back.setEnabled(false);
	    message.setText(Application.cache.get(index));
	    
	    waiting.clearAnimation();
	    before.setVisibility(View.GONE);
	    after.setVisibility(View.VISIBLE);
	    if (Application.cache.size() == 1)
	    	next.setEnabled(false);
	    
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.display);
	    
	    stickerId = getIntent().getStringExtra("sticker");
	    
	    after = (View)findViewById(R.id.after);
		before = (View)findViewById(R.id.before);
		
	    next = (Button) findViewById(R.id.next);
	    back = (Button) findViewById(R.id.back);
	    message = (TextView)findViewById(R.id.message);
	    status = (TextView) findViewById(R.id.status);
	    waiting = (ImageView) findViewById(R.id.display_waiting);
	    wrong = (ImageView) findViewById(R.id.display_wrong);
	    
	    mFadeAnim = AnimationUtils.loadAnimation(this, R.anim.slow_fade);
	    
	    next.setOnClickListener(new NextClick());
	    back.setOnClickListener(new BackClick());
	    
	    if (cache.getCacheId() != null && cache.getCacheId().equals(stickerId)){
	    	if (cache.size()>0){
	    		initView();
	    		return;
	    	} else {
	    		showWrong();
	    	}
	    } else {
	    	new FeedGenerateRequest(stickerId,this).execute();
	    }
	    
	    after.setVisibility(View.GONE);
	    
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		waiting.clearAnimation();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		waiting.startAnimation(mFadeAnim);
	}
	
	public void showWrong(){
		status.setText(getString(R.string.display_empty_feed));
		waiting.clearAnimation();
		waiting.setVisibility(View.GONE);
		wrong.setVisibility(View.VISIBLE);
	}
	
	class NextClick implements OnClickListener {
				
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
