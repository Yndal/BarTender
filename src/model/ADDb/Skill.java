package model.ADDb;

import java.io.Serializable;

public class Skill implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5200380395042690884L;
	
	private String id;
	private String name;
	private int value;
	
	public Skill(String id, String name, int value){
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
	
}
