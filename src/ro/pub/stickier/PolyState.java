package ro.pub.stickier;

public class PolyState {
	public float[] ul;
	public float[] ur;
	public float[] bl;
	public String sticker;
	
	public PolyState(float[] pts, String sticker){
		ul = new float[]{pts[2], pts[3]};
		ur = new float[]{pts[4], pts[5]};
		bl = new float[]{pts[0], pts[1]};
		this.sticker = sticker;
	}
	
	public PolyState(float[] uleft, float[] uright, float[] bleft, String sticker){
		ul = new float[]{uleft[0], uleft[1]};
		ur = new float[]{uright[0], uright[1]};
		bl = new float[]{bleft[0], bleft[1]};
		this.sticker = sticker;
	}
	
	public PolyState(float ulX, float ulY, float urX, float urY, float blX, float blY, String sticker){
		ul = new float[]{ulX, ulY};
		ur = new float[]{urX, urY};
		bl = new float[]{blX, blY};
		this.sticker = sticker;
	}
	
	public PolyState copy(){
		PolyState ret = new PolyState(ul, ur, bl, sticker);
		return ret;
	}
}
