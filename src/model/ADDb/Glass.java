package model.ADDb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import model.ADDb.interfaces.*;

public class Glass implements Id, Name, Description, LanguageBranch, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3576046935507683348L;

	private static final String UNSET_VALUE = "<unset>";
	
	private final String id;
	private final String text;
	private String name;
	private String description;
	private String languageBranch;
	private List<Video> videos;
	private static final HashMap<String, ImageIcon> images = new HashMap<>();
	
	
	public Glass(String id, String text){
		this(id, text, UNSET_VALUE, UNSET_VALUE, UNSET_VALUE, new ArrayList<Video>());
	}
	
	public Glass(String id, String text, String name, String description, String languageBranch, List<Video> videos){
		this.id = id;
		this.text = text;
		this.name = name;
		this.description = description;
		this.languageBranch = languageBranch;
		this.videos = videos;
	}
	
	
	public String getId() {
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

	public List<Video> getVideos() {
		return videos;
	}

	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}

	public String getText() {
		return text;
	}

	public ImageIcon getImage() {
		return images.get(id);
	}

	public void setImage(ImageIcon image) {
		images.put(id, image);
	}
}