package control.web;

import java.util.List;

public class ListResult <T>{
	private final List<T> objects;
	private String nextHttpRequest; //null if not available
	private final String previousHttpRequest; //null if not available
	
	public ListResult(List<T> objs, String nextHttp, String previousHttp){
		objects = objs;
		nextHttpRequest = nextHttp; 
		previousHttpRequest = previousHttp;
	}
	
	public String getNextHttpRequest(){
		return nextHttpRequest;
	}
	
	public void setNextHttpRequest(String nextRequest){
		nextHttpRequest = nextRequest;
	}
	
	public String getPreviousHttpRequest(){
		return previousHttpRequest;
	}
	
	public List<T> getList(){
		return objects;
	}
}
