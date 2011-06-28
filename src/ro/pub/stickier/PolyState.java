package ro.pub.stickier;

public class PolyState {
	public float x;
	public float y;
	public String sticker;
	
	public PolyState(float[] pts, String sticker){
		x = pts[0];
		y = pts[1];
		this.sticker = sticker;
	}
	
	public PolyState(float x, float y, String sticker){
		this.x = x;
		this.y = y;
		this.sticker = sticker;
	}
	
	public PolyState copy(){
		PolyState ret = new PolyState(x, y, sticker);
		return ret;
	}
}
