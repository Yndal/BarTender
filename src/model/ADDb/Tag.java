package model.ADDb;

import java.io.Serializable;

import model.ADDb.interfaces.Name;

public class Tag implements Name, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9215119079236688252L;
	
	private final String name;
	private final String owner;
	private int count;
	
	public Tag(String owner, String name){
		this.owner = owner;
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}
}
