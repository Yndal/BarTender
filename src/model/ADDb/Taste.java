package model.ADDb;

import java.io.Serializable;

import model.ADDb.interfaces.*;

public class Taste implements Id, Name, LanguageBranch, Description, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7587029374030106380L;
	private final String id;
	private final String text;
	private String name;
	private String languageBranch;
	private String description;

	public Taste(String id, String text){
		this.id = id;
		this.text = text;
	}
	
	
	public String getId(){
		return id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getLanguageBranch() {
		return languageBranch;
	}


	public void setLanguageBranch(String languageBranch) {
		this.languageBranch = languageBranch;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getText() {
		return text;
	}
	
	
}
