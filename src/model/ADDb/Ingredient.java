package model.ADDb;

import java.io.Serializable;

import javax.swing.ImageIcon;

import model.ADDb.interfaces.*;

public class Ingredient implements Id, Name, LanguageBranch, Description, Serializable{
	private static final long serialVersionUID = -9141168029368522599L;
	
	private final String id;
	private final String name;
	private final String languageBranch;
	private final String description;
	private final boolean isCarbonated;
	private final boolean isAlcoholic;
	private final boolean isBaseSpirit;
	private final boolean isJuice;
	private final String type;
	private ImageIcon image;
	
	public Ingredient(String id, String name, String languageBranch, String description,
			boolean isCarbonated, boolean isAlcoholic, boolean isBaseSpirit, boolean isJuice, String type){
		this.id = id;
		this.name = name;
		this.languageBranch = languageBranch;
		this.description = description;
		this.isCarbonated = isCarbonated;
		this.isAlcoholic = isAlcoholic;
		this.isBaseSpirit = isBaseSpirit;
		this.isJuice = isJuice;
		this.type = type;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the languageBranch
	 */
	public String getLanguageBranch() {
		return languageBranch;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return the isCarbonated
	 */
	public boolean isCarbonated() {
		return isCarbonated;
	}
	/**
	 * @return the isAlcoholic
	 */
	public boolean isAlcoholic() {
		return isAlcoholic;
	}
	/**
	 * @return the isBaseSpirit
	 */
	public boolean isBaseSpirit() {
		return isBaseSpirit;
	}
	/**
	 * @return the isJuice
	 */
	public boolean isJuice() {
		return isJuice;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}
}
