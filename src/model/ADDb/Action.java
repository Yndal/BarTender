package model.ADDb;

import java.io.Serializable;
import java.util.List;

import model.ADDb.interfaces.*;

public class Action implements Id, Name, LanguageBranch, Description, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4384194414808419149L;
	
	private final String id;
	private final String text;
	private String name;
	private String languageBranch;
	private String description;
	private List<Tool> tools;
	private List<Video> videos;
	
	public Action(String id, String text){
		this.id = id;
		this.text = text;
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

	public List<Tool> getTools() {
		return tools;
	}

	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}
}
