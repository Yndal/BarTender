package model.ADDb;

import java.io.Serializable;

import model.ADDb.interfaces.*;

public class Occasion implements Id, Name, Description, LanguageBranch, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 280294516135423668L;
	
	private final String id;
	private final String text;
	private String name;
	private String description;
	private String languageBranch;
	private DiscreteDatetime start;
	private DiscreteDatetime end;
	
	public Occasion(String id, String text){
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguageBranch() {
		return languageBranch;
	}

	public void setLanguageBranch(String languageBranch) {
		this.languageBranch = languageBranch;
	}

	public DiscreteDatetime getStart() {
		return start;
	}

	public void setStart(DiscreteDatetime start) {
		this.start = start;
	}

	public DiscreteDatetime getEnd() {
		return end;
	}

	public void setEnd(DiscreteDatetime end) {
		this.end = end;
	}

	public String getText() {
		return text;
	}
	
}
