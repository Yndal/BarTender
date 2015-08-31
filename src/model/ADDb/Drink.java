package model.ADDb;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

import javax.swing.ImageIcon;

import model.ADDb.interfaces.Description;
import model.ADDb.interfaces.Id;
import model.ADDb.interfaces.LanguageBranch;
import model.ADDb.interfaces.Name;

public class Drink implements Id, Name, Description, LanguageBranch, Serializable {
	private static final long serialVersionUID = 5518045169178028180L;

	public enum DrinkColor{
		Red("red"),
		Pink("pink"),
		Yellow("yellow"),
		Brown("brown"),
		Blue("brown"),
		Green("green"),
		Purple("purple"),
		Transparent("transparent"),
		White("white");

		private final String stringValue;

		private DrinkColor(String sVal){
			this.stringValue = sVal;
		}

		@Override
		public String toString(){
			return stringValue;
		}
	}

	public enum Carbonated{
		Carbonated,
		Uncarbonated,
		Unknown
	}

	public enum Alcoholic{
		Alcoholic,
		NotAlcoholic,
		Unknown
	}

	/*	public class SerializableBufferedImage extends BufferedImage implements Serializable{
		private static final long serialVersionUID = -8371302226565297412L;

		public SerializableBufferedImage(){			
		}

		public SerializableBufferedImage(int width, int height, int imageType){ 
			super(width, height, imageType);
		}

		public SerializableBufferedImage(int width, int height, int imageType, IndexColorModel cm){
			super(width, height, imageType, cm);
		}
		public SerializableBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
			super(cm, raster, isRasterPremultiplied, properties);
		}

	}*/

	private String id;
	private String name;
	private String description;
	private String descriptionPlain;
	private String story;
	private String color;
	private String languageBranch;
	private int rating;
	private Skill skill;
	private Alcoholic isAlchoholic;
	private Carbonated isCarbonated;
	private boolean isHot;
	private Glass servedIn;
	private ImageIcon image;
	// private List<IngredientAmount> ingredients
	private List<IngredientProperties> ingredientProperties;
	private List<Taste >tastes;
	private List<Occasion> occasions;
	private List<Tool> tools;
	private List<?> drinkTypes;
	private List<Action> actions;
	private List<String> brands;
	private List<Tag> tags;
	private List<Video> videos;


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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescriptionPlain() {
		return descriptionPlain;
	}
	public void setDescriptionPlain(String descriptionPlain) {
		this.descriptionPlain = descriptionPlain;
	}
	public String getStory() {
		return story;
	}
	public void setStory(String story) {
		this.story = story;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getLanguageBranch() {
		return languageBranch;
	}
	public void setLanguageBranch(String languageBranch) {
		this.languageBranch = languageBranch;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public Skill getSkill() {
		return skill;
	}
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	public List<Video> getVideos() {
		return videos;
	}
	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}
	public Alcoholic getIsAlchoholic() {
		return isAlchoholic;
	}
	public void setIsAlchoholic(Alcoholic isAlchoholic) {
		this.isAlchoholic = isAlchoholic;
	}
	public Carbonated getIsCarbonated() {
		return isCarbonated;
	}
	public void setIsCarbonated(Carbonated isCarbonated) {
		this.isCarbonated = isCarbonated;
	}
	public boolean isHot() {
		return isHot;
	}
	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}
	public Glass getServedIn() {
		return servedIn;
	}
	public void setServedIn(Glass servedIn) {
		this.servedIn = servedIn;
	}
	public BufferedImage getImage() {
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(image.getIconWidth(), image.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = bimage.createGraphics();
		image.paintIcon(null, graphics, 0, 0);
		graphics.dispose();

		return bimage;		
	}
	public void setImage(BufferedImage image) {
		this.image = new ImageIcon(image);
	}
	public void setImage(ImageIcon image){
		this.image = image;
	}
	public List<IngredientProperties> getIngredientProperties() {
		return ingredientProperties;
	}
	public void setIngredientProperties(List<IngredientProperties> ingredientProperties) {
		this.ingredientProperties = ingredientProperties;
	}
	public List<Taste> getTastes() {
		return tastes;
	}
	public void setTastes(List<Taste> tastes) {
		this.tastes = tastes;
	}
	public List<Occasion> getOccasions() {
		return occasions;
	}
	public void setOccasions(List<Occasion> occasions) {
		this.occasions = occasions;
	}
	public List<Tool> getTools() {
		return tools;
	}
	public void setTools(List<Tool> tools) {
		this.tools = tools;
	}
	public List<?> getDrinkTypes() {
		return drinkTypes;
	}
	public void setDrinkTypes(List<?> drinkTypes) {
		this.drinkTypes = drinkTypes;
	}
	public List<Action> getActions() {
		return actions;
	}
	public void setActions(List<Action> actions) {
		this.actions = actions;
	}
	public List<String> getBrands() {
		return brands;
	}
	public void setBrands(List<String> brands) {
		this.brands = brands;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}


}
