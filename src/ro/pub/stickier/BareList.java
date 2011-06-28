package ro.pub.stickier;

public interface BareList<E> {

	public E get(int position) throws IndexOutOfBoundsException;
	
	public void add(E element);
	
	public void clear();
	
	public int size();
	
}
