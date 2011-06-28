package ro.pub.stickier;

import java.util.ArrayList;

public class Cache<E> implements BareList<E> {

	private ArrayList<E> cache;
	
	private String cacheId;
	
	public Cache(){
		cache = new ArrayList<E>();
	}
	
	public String getCacheId() {
		return cacheId;
	}

	public void setCacheId(String cacheId) {
		this.cacheId = cacheId;
	}

	public synchronized E get(int position) throws IndexOutOfBoundsException {
		return cache.get(position);
	}

	public synchronized void add(E element) {
		cache.add(element);	
	}

	public synchronized void clear() {
		cache.clear();
	}
	
	public synchronized void reset(){
		clear();
		cacheId = null;
	}

	public synchronized int size() {
		return cache.size();
	}
	
	
}
