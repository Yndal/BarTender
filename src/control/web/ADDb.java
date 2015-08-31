package control.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

import control.Controller;
import model.ADDb.Drink;
import model.ADDb.Glass;
import model.ADDb.Ingredient;

public class ADDb {
	public interface DrinksUpdatedListener {
		void onGotUpdatedDrinks(List<Drink> drinks);
	}
	public interface GlassesUpdatedListener {
		void onGotUpdatedGlasses(List<Glass> glasses);
	}
	public interface IngredientsUpdatedListener {
		void onGotUpdatedIngredients(List<Ingredient> ingredients);
	}


	private final static String ADDB_URL = "https://addb.absolutdrinks.com";
	//private final static String ADDB_ASSET_URL = "https://assets.absolutdrinks.com";
	private final static String API_KEY = "d171e5e6bb534b4f8b1603a5377a78bc";
	//private final static String API_SECRET = "4196e573b335441ca4f86425562618a1";

	private final Set<DrinksUpdatedListener> drinksUpdatedListeners = new HashSet<>();
	private final Set<GlassesUpdatedListener> glassesUpdatedListeners = new HashSet<>();
	private final Set<IngredientsUpdatedListener> ingredientsUpdatedListeners = new HashSet<>();
	private final Controller controller;

	public ADDb(Controller controller){
		this.controller = controller;
	}

	public List<Drink> getTestDrinks(){
		String params = "/drinks/rating/80/";
		String request = addDomanAndCredentials(params);
		String json = new RestClient().createJSONRequest(request);
		ListResult<Drink> result = null;
		try {
			result = JSONTranslator.json2Drinks(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<Drink> drinks = result.getList();
		if(drinks == null)
			drinks = new ArrayList<Drink>();

		return drinks;
	}

	/*
	 * Collect all drinks and notify DrinksUpdatedListeners
	 */
	public void updateAllDrinks(){
		String params = "/drinks/";
		String request = addDomanAndCredentials(params);
		submitDrinksRequest(request);		
	}

	public void updateAllGlasses(){
		String params = "/glasses/";
		String request = addDomanAndCredentials(params);
		submitGlassesRequest(request);
	}

	public void updateAllIngredients(){
		String params = "/ingredients/";
		String request = addDomanAndCredentials(params);
		submitIngredientsRequest(request);		
	}

	/*
	 * Add request in the pool of the executor, which should be translated to Drinks
	 */
	private void submitDrinksRequest(String request){
		controller.execute(new Runnable() {
			@Override
			public void run() {
				String response = new RestClient().createJSONRequest(request);
				try{
					ListResult<Drink> listResult = JSONTranslator.json2Drinks(response);
					gotDrinks(listResult.getList());
					if(listResult.getNextHttpRequest() != null){
						String req = listResult.getNextHttpRequest();
						listResult.setNextHttpRequest(changePageSize(req, 1));
						submitDrinksRequest(listResult.getNextHttpRequest());
					}
				} catch (ParseException pe){}
			}

			private String changePageSize(String req, int pageSize) {
				String target = "pageSize=";
				String regex = target + "\\d+";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(req);
				//matcher.find();
				
				return matcher.replaceAll(target + pageSize);				
			}
		});
	}

	/*
	 * Add request in the pool of the executor, which should be translated to Glasses
	 */
	private void submitGlassesRequest(String request){
		controller.execute(new Runnable() {
			@Override
			public void run() {
				String response = new RestClient().createJSONRequest(request);
				try{
					ListResult<Glass> listResult = JSONTranslator.json2Glasses(response);
					gotGlasses(listResult.getList());
					if(listResult.getNextHttpRequest() != null)
						submitGlassesRequest(listResult.getNextHttpRequest());
				} catch (ParseException pe){}
			}
		});
	}

	/*
	 * Add request in the pool of the executor, which should be translated to Ingredients
	 */
	private void submitIngredientsRequest(String request){
		controller.execute(new Runnable() {
			@Override
			public void run() {
				String response = new RestClient().createJSONRequest(request);
				try{
					ListResult<Ingredient> listResult = JSONTranslator.json2Ingredients(response);
					gotIngredients(listResult.getList());
					if(listResult.getNextHttpRequest() != null)
						submitIngredientsRequest(listResult.getNextHttpRequest());
				} catch (ParseException pe){}
			}
		});
	}

	/*
	 * Notify all DrinksUpdatedListeners about the freshly collected drinks
	 */
	private void gotDrinks(List<Drink> drinks){
		synchronized (drinksUpdatedListeners) {
			for(DrinksUpdatedListener dul : drinksUpdatedListeners)
				dul.onGotUpdatedDrinks(drinks);
		}
	}

	/*
	 * Notify all GlassesUpdatedListeners about the freshly collected glasses
	 */
	private void gotGlasses(List<Glass> glasses){
		synchronized (glassesUpdatedListeners) {
			for(GlassesUpdatedListener gul : glassesUpdatedListeners)
				gul.onGotUpdatedGlasses(glasses);
		}
	}

	/*
	 * Notify all IngredientsUpdatedListeners about the freshly collected ingredients
	 */
	private void gotIngredients(List<Ingredient> ingredients){
		synchronized (ingredientsUpdatedListeners) {
			for(IngredientsUpdatedListener iul : ingredientsUpdatedListeners)
				iul.onGotUpdatedIngredients(ingredients);
		}
	}
	
	public Drink getDrink(String id){
		String params = "/drinks/" + id;
		String request = addDomanAndCredentials(params);
		
		String response = new RestClient().createJSONRequest(request);
		try{
			Drink drink = JSONTranslator.json2drink(response);
			return drink;
		} catch (ParseException pe){}

		return null;
	}
	
	public Ingredient getIngredient(String id){
		String params = "/ingredients/" + id;
		String request = addDomanAndCredentials(params);
		
		String response = new RestClient().createJSONRequest(request);
		try{
			Ingredient ingredient = JSONTranslator.json2ingredient(response);
			return ingredient;
		} catch (ParseException pe){}

		return null;
	}


	public boolean addDrinksUpdatedListener(DrinksUpdatedListener dul){
		return drinksUpdatedListeners.add(dul);
	}

	public boolean removeDrinksUpdatedListener(DrinksUpdatedListener dul){
		return drinksUpdatedListeners.remove(dul);
	}

	public boolean addGlassesUpdatedListener(GlassesUpdatedListener gul){
		return glassesUpdatedListeners.add(gul);
	}

	public boolean removeGlassesUpdatedListener(GlassesUpdatedListener gul){
		return glassesUpdatedListeners.remove(gul);
	}

	public boolean addIngredientsUpdatedListener(IngredientsUpdatedListener iul){
		return ingredientsUpdatedListeners.add(iul);
	}

	public boolean removeIngredientsUpdatedListener(IngredientsUpdatedListener iul){
		return ingredientsUpdatedListeners.remove(iul);
	}



	/*public List<Drink> getDrinksContaining(List<Ingredient> ingredients, List<IngredientProperties> ingredientTypes, List<Taste> tastes, List<Occasion> occasions, List<Tool> tools, List<Integer> ratings, List<Skill> skills, Alcoholic withAlcohol, Carbonated isCarbonated, List<Glass> glasses, List<DrinkColor> colors){
		StringBuilder params = new StringBuilder("/drinks/");

		if(ingredients != null)
			//Add ingredients
			for(Ingredient i : ingredients)
				params.append("with/" + i.getId() + "/");

		if(ingredientTypes != null)
			for(IngredientProperties ip : ingredientTypes)
				params.append("withtype/" + ip.getId() + "/");

		if(tastes != null)
			for(Taste t : tastes)
				params.append("tasting/" + t.getId() + "/");

		if(occasions != null)
			for(Occasion oc : occasions)
				params.append("for/" + oc.getId() + "/");

		if(tools != null)
			for(Tool t : tools)
				params.append("madewith/" + t.getId() + "/");

		if(ratings != null)
			for(Integer i : ratings)
				params.append("rating/" + i + "/");

//		if(skills != null)
//			for(Skill s : skills)
//				params.append("skill/" + s.getSkillValue() + "/");
		if(withAlcohol != null){
			switch(withAlcohol){
			case NotAlcoholic:
				params.append("not/");
				//Missing a break on purpose here...
			case Alcoholic:
				params.append("alcoholic/");
				break;
			default:
				break;
			}
		}

		if(isCarbonated != null){
			switch(isCarbonated){
			case Uncarbonated:
				params.append("not/");
				//Missing a break on purpose here...
			case Carbonated:
				params.append("carbonated/");
				break;
			default:
				break;
			}
		}

		if(glasses != null)
			for(Glass g : glasses)
				params.append("servedin/" + g.getId() + "/");

		if(colors != null)
			for(DrinkColor c : colors)
				params.append("colored/" + c.toString());


		String json = addDomanAndCredentials(params.toString());
		List<Drink> drinks = Translator.json2Drinks(json);

		return drinks;
	}*/


	private static String addDomanAndCredentials(String parameters){		
		//Add credentials
		return ADDB_URL + parameters + "?apiKey=" + API_KEY;
	}

	/*public List<Drink> searchByName(String name){
		List<Drink> drinks = new ArrayList<Drink>();
		String httpName = name.replace(' ', '-');
		String params = "/quickSearch/drinks/" + httpName + "/";
		String json = addDomanAndCredentials(params);
		if(json != "")
			drinks = Translator.json2Drinks(json);

		return drinks;
	}*/



}
