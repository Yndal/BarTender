package control.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import model.ADDb.Action;
import model.ADDb.Drink;
import model.ADDb.Drink.Alcoholic;
import model.ADDb.Drink.Carbonated;
import model.ADDb.Glass;
import model.ADDb.Ingredient;
import model.ADDb.IngredientProperties;
import model.ADDb.Occasion;
import model.ADDb.Skill;
import model.ADDb.Tag;
import model.ADDb.Taste;
import model.ADDb.Tool;
import model.ADDb.Video;
import model.ADDb.Video.VideoType;

public class JSONTranslator {
	public static ListResult<Drink> json2Drinks(String json) throws ParseException{
		List<Drink> drinks = new ArrayList<Drink>();

		JSONParser parser = new JSONParser();

		JSONObject jsonObj = (JSONObject) parser.parse(json);
		JSONArray result = (JSONArray) jsonObj.get("result");
		//int totalResult = (int) ((long) jsonObj.get("totalResult"));
		String previous = (String) jsonObj.get("previous");
		String next = (String) jsonObj.get("next");



		if(result != null && !result.equals(""))
			drinks = result2drinks(result);

		ListResult<Drink> listResult = new ListResult<Drink>(drinks, next, previous);

		return listResult;
	}

	public static ListResult<Glass> json2Glasses(String json) throws ParseException{
		List<Glass> glasses = new ArrayList<Glass>();

		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(json);
		JSONArray result = (JSONArray) jsonObj.get("result");
		//int totalResult = (int) ((long) jsonObj.get("totalResult"));
		String next = (String) jsonObj.get("next");
		String previous = (String) jsonObj.get("previous");

		if(result != null && !result.equals(""))
			glasses = result2glasses(result);

		ListResult<Glass> listResult = new ListResult<Glass>(glasses, next, previous);

		return listResult;
	}

	public static ListResult<Ingredient> json2Ingredients(String json) throws ParseException {	
		List<Ingredient> ingredients = new ArrayList<Ingredient>();

		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(json);
		JSONArray result = (JSONArray) jsonObj.get("result");
		//int totalResult = (int) ((long) jsonObj.get("totalResult"));
		String next = (String) jsonObj.get("next");
		String previous = (String) jsonObj.get("previous");

		if(result != null && !result.equals(""))
			ingredients = result2ingredients(result);

		ListResult<Ingredient> listResult = new ListResult<Ingredient>(ingredients, next, previous);

		return listResult;

	}


	private static List<Drink> result2drinks(JSONArray result){
		List<Drink> drinks = new ArrayList<Drink>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = result.listIterator();

		while(it.hasNext()){
			JSONObject next = it.next();
			Drink drink = json2drink(next);
			drinks.add(drink);
		}

		return drinks;
	}

	private static List<Glass> result2glasses(JSONArray result){
		List<Glass> glasses = new ArrayList<Glass>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = result.listIterator();

		while(it.hasNext()){
			JSONObject next = it.next();
			glasses.add(json2glass(next));
		}

		return glasses;
	}

	private static List<Ingredient> result2ingredients(JSONArray result){
		List<Ingredient> ingredients = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = result.listIterator();

		while(it.hasNext()){
			JSONObject next = it.next();
			ingredients.add(json2Ingredient(next));
		}

		return ingredients;
	}

	private static Skill json2skill(JSONObject json){
		String id = (String) json.get("id");
		String name = (String) json.get("name");
		int value = (int) ((long) json.get("value"));

		Skill skill = new Skill(id, name, value);
		return skill;
	}
	
	public static Drink json2drink(String json) throws ParseException{
		JSONParser parser = new JSONParser();

		JSONObject jsonObj = (JSONObject) parser.parse(json);
		
		return json2drink(jsonObj);
	}
	
	public static Ingredient json2ingredient(String json) throws ParseException{
		JSONParser parser = new JSONParser();

		JSONObject jsonObj = (JSONObject) parser.parse(json);
		
		return json2Ingredient(jsonObj);
	}

	private static Drink json2drink(JSONObject json){

		String description = (String) json.get("description");
		String story = (String) json.get("story");
		String color = (String) json.get("color");
		int rating = (int) ((long) json.get("rating"));
		JSONObject skillObj = (JSONObject) json.get("skill");
		JSONArray videoArray = (JSONArray) json.get("videos");
		boolean isAlcoholic = (boolean) json.get("isAlcoholic");
		boolean isCarbonated = (boolean) json.get("isCarbonated");
		//boolean isHot = (boolean) next.get("isHot");          
		JSONArray tagArray = (JSONArray) json.get("tags");
		JSONObject servedIn = (JSONObject) json.get("servedIn");
		JSONArray ingredientArray = (JSONArray) json.get("ingredients");
		JSONArray tasteArray = (JSONArray) json.get("tastes");
		JSONArray occasionArray = (JSONArray) json.get("occasions");
		JSONArray toolArray = (JSONArray) json.get("tools");
		//JSONArray drinkTypeArray = (JSONArray) next.get("drinkTypes");
		JSONArray actionArray = (JSONArray) json.get("actions");
		JSONArray brandArray = (JSONArray) json.get("brands");
		String languageBranch = (String) json.get("languageBranch");
		String id = (String) json.get("id");
		String name = (String) json.get("name");
		String descriptionPlain = (String) json.get("descriptionPlain");

		Skill skill = json2skill(skillObj);
		Glass glass = json2glass(servedIn);
		List<Video> videos = json2Videos(videoArray);
		List<Tag> tags = json2Tags(tagArray);
		List<IngredientProperties> ingredientProperties = json2Ingredients(ingredientArray);
		List<Taste> tastes = json2Tastes(tasteArray);
		List<Occasion> occasions = json2Occasions(occasionArray);
		List<Tool> tools = json2Tools(toolArray);
		//List<DrinkType> drinkTypes = json2DrinkTypes(drinkTypeArray);
		List<Action> actions = json2Actions(actionArray);
		List<String> brands = json2Brands(brandArray);


		//Create new instance of Drink
		Drink drink = new Drink();

		if(description != null) 
			drink.setDescription(description);
		if(story != null)
			drink.setStory(story);
		if(color != null)
			drink.setColor(color);
		drink.setRating(rating);
		if(skill != null)
			drink.setSkill(skill);
		if(videos != null)
			drink.setVideos(videos);

		drink.setIsAlchoholic(isAlcoholic ? Alcoholic.Alcoholic : Alcoholic.NotAlcoholic);
		drink.setIsCarbonated(isCarbonated ? Carbonated.Carbonated : Carbonated.Uncarbonated);
		//drink.setIsHot(isHot);          
		drink.setTags(tags);
		if(servedIn != null)
			drink.setServedIn(glass);
		drink.setIngredientProperties(ingredientProperties);
		drink.setTastes(tastes);
		drink.setOccasions(occasions);
		drink.setTools(tools);
		//drink.setDrinkTypes(drinkTypes);
		drink.setActions(actions);
		drink.setBrands(brands);

		if(languageBranch != null)
			drink.setLanguageBranch(languageBranch);
		if(id != null)
			drink.setId(id);
		if(name != null)
			drink.setName(name);
		if(descriptionPlain != null)
			drink.setDescriptionPlain(descriptionPlain);

		try {
			URL url = new URL("http://assets.absolutdrinks.com/drinks/" + id + ".png");
			BufferedImage image = ImageIO.read(url);
			drink.setImage(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return drink;
	}

	private static Glass json2glass(JSONObject json){
		String id = (String) json.get("id");
		String text = (String) json.get("text");
		String name = (String) json.get("name");
		String description = (String) json.get("description");
		String languageBranch = (String) json.get("languageBranch");
		JSONArray videosArray = (JSONArray) json.get("videos");

		List<Video> videos;
		if(videosArray != null)
			videos = json2Videos(videosArray);
		else 
			videos = new ArrayList<Video>(0);

		Glass glass = new Glass(id, text, name, description, languageBranch, videos);
		try {
			URL url = new URL("http://assets.absolutdrinks.com/glasses/" + id + ".png");
			BufferedImage image = ImageIO.read(url);
			glass.setImage(new ImageIcon(image));

		} catch (IOException e) {
			e.printStackTrace();
		}

		return glass;
	}

	private static Ingredient json2Ingredient(JSONObject json){
		String id = (String) json.get("id");
		String name = (String) json.get("name");
		String description = (String) json.get("description");
		String type = (String) json.get("type");
		String languageBranch = (String) json.get("languageBranch");
		boolean isCarbonated = (boolean) json.get("isCarbonated");
		boolean isAlcoholic = (boolean) json.get("isAlcoholic");
		boolean isBaseSpirit = (boolean) json.get("isBaseSpirit");
		boolean isJuice = (boolean) json.get("isJuice");

		Ingredient ingredient = new Ingredient(id, name, languageBranch, description, isCarbonated, isAlcoholic, isBaseSpirit, isJuice, type);

		/*try {
			URL url = new URL("http://assets.absolutdrinks.com/ingredients/" + id + ".png");
			BufferedImage image = ImageIO.read(url);
			ingredient.setImage(new ImageIcon(image));

		} catch (IOException e) {
			e.printStackTrace();
		}*/

		return ingredient;
	}


	private static List<Video> json2Videos(JSONArray json){
		ArrayList<Video> videos = new ArrayList<Video>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String link = (String) obj.get("video");
			VideoType type = VideoType.getType((String) obj.get("type"));

			Video video = new Video(link, type);
			videos.add(video);
		}

		return videos;
	}

	private static List<Tag> json2Tags(JSONArray json){
		ArrayList<Tag> tags = new ArrayList<Tag>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String owner = (String) obj.get("Owner");
			String name = (String) obj.get("Name");

			Tag tag = new Tag(owner, name);

			tags.add(tag);
		}

		return tags;
	}

	private static List<IngredientProperties> json2Ingredients(JSONArray json){
		ArrayList<IngredientProperties> ingredients = new ArrayList<IngredientProperties>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String id = (String) obj.get("id");
			String type = (String) obj.get("type");
			String text = (String) obj.get("text");
			String textPlain = (String) obj.get("textPlain");

			IngredientProperties ip = new IngredientProperties(id, type, text, textPlain);

			ingredients.add(ip);
		}

		return ingredients;
	}

	private static List<Taste> json2Tastes(JSONArray json){
		ArrayList<Taste> tastes = new ArrayList<Taste>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String id = (String) obj.get("id");
			String text = (String) obj.get("text");

			Taste taste = new Taste(id, text);

			tastes.add(taste);
		}

		return tastes;		
	}

	private static List<Occasion> json2Occasions(JSONArray json){
		ArrayList<Occasion> occasions = new ArrayList<Occasion>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String id = (String) obj.get("id");
			String text = (String) obj.get("text");

			Occasion occasion = new Occasion(id, text);

			occasions.add(occasion);
		}

		return occasions;
	}

	private static List<Tool> json2Tools(JSONArray json){
		ArrayList<Tool> tools = new ArrayList<Tool>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String id = (String) obj.get("id");
			String text = (String) obj.get("text");

			Tool tool = new Tool(id, text);

			tools.add(tool);
		}

		return tools;
	}

	private static List<Action> json2Actions(JSONArray json){
		ArrayList<Action> actions = new ArrayList<Action>();
		@SuppressWarnings("unchecked")
		ListIterator<JSONObject> it = json.listIterator();
		while(it.hasNext()){
			JSONObject obj = it.next();
			String id = (String) obj.get("id");
			String text = (String) obj.get("text");

			Action action = new Action(id, text);

			actions.add(action);
		}

		return actions;
	}

	private static List<String> json2Brands(JSONArray json){
		List<String> brands = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		ListIterator<String> it = json.listIterator();
		while(it.hasNext())
			brands.add(it.next());

		return brands;
	}
}
