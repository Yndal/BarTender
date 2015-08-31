package control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import control.Controller.ControllerStatusListener.Status;
import control.DrinkProvider.DrinkProviderListener;
import control.dispenser.drink.IngredientAmount;
import control.dispenser.robotArm.RobotArm;
import model.Amount;
import control.storage.LocalStorage;
import control.web.ADDb;
import control.web.ADDb.DrinksUpdatedListener;
import control.web.ADDb.GlassesUpdatedListener;
import control.web.ADDb.IngredientsUpdatedListener;
import model.ADDb.Action;
import model.ADDb.Drink;
import model.ADDb.Glass;
import model.ADDb.Ingredient;
import model.ADDb.IngredientProperties;
import model.ADDb.Occasion;
import model.ADDb.Skill;
import model.ADDb.Tag;
import model.ADDb.Taste;
import model.ADDb.Tool;
import model.Amount.QuantityType;

public final class Controller implements DrinksUpdatedListener, GlassesUpdatedListener, IngredientsUpdatedListener, DrinkProviderListener{
	public interface DrinkMakerListener {
		void onDrinkStarted(Drink drink);
		void onDrinkProgress(Drink drink, int currentIngredientLevel);
		void onDrinkMade(Drink drink, int number);
		void onIngredientAlmostEmpty(Ingredient ingredient);
		void onIngredientEmpty(Ingredient ingredient);
	}

	public interface ControllerStatusListener{ 
		public enum Status{
			InitializationStarted("Initialization started"),
			InitializationEnded("Initialization ended"),
			UnderMaintananceStarted("Under maintanance"),
			UnderMaintananceEnded("Under maintanance"),
			ShuttingDown("Shutting down!"),

			SearchingForDrinksStarted("Searching for drinks"),
			SearchingForDrinksEnded("Search for drinks completed"),
			MakingDrinkStarted("Making drink! :o) Yaay"),
			MakingDrinkEnded("Drink is served");

			private final String description;

			private Status(String descr){
				description = descr;
			}

			public String getDescription() {
				return description;
			}
		}
		void onStatusUpdated(Status status);
		//		void onIngredientAlmostEmpty(Ingredient ingredient);
		//		void onIngredientEmpty(Ingredient ingredient);
		//		void onLoadedDrinksFromLocalStorage(List<Drink> drinks);
		//		void onCollectedDrinksFromInternet(List<Drink> drinks);
		//		void onAvailableDrinksUpdated(List<Drink> drinks);	
	}

	public interface AvailableDrinkListener {
		void onAvailableDrinksUpdated(List<String> drinkIds);
		//void onTotalDrinksUpdated(int totalAmount);
		void onDrinksUpdated(List<String> drinkIds, int totalUpdated, int totalAmount);

	}

	private static Controller instance;
	private static final ExecutorService executor = Executors.newWorkStealingPool();
	private final DrinkProvider drinkProvider = new DrinkProvider(this);
	private final ADDb addb;
	private RobotArm robotArm;

	private static Set<DrinkMakerListener> drinkMakerListeners = Collections.newSetFromMap(new ConcurrentHashMap<DrinkMakerListener, Boolean>());
	private static Set<ControllerStatusListener> controllerStatusListeners = Collections.newSetFromMap(new ConcurrentHashMap<ControllerStatusListener, Boolean>());
	private static Set<AvailableDrinkListener> availableDrinkListeners = Collections.newSetFromMap(new ConcurrentHashMap<AvailableDrinkListener, Boolean>());

	private static int drinkCounter = 0;


	private static List<String> availableDrinksIds = new ArrayList<>();
	private static List<String> availableGlassesIds = new ArrayList<>();
	private static List<String> availableIngredientsIds = new ArrayList<>();
	
	private static Set<String> updatedDrinks = new HashSet<>();

	//private static HashSet<String> allIngredientIds = new HashSet<>(); //The set uses HashCode to compare keys - consider to define the hashcode...
	private static HashMap<String, String> allIngredientName2Ids = new HashMap<>();
	private static HashMap<Integer, String> addedIngredients = new HashMap<>(); //Physical position, ingredient id

	@SuppressWarnings("unused")
	private static boolean initialized = false;

	private Controller(){
		addb = new ADDb(this);
		executor.submit(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				initialize(); 
				initialized = true;
				return null;
			}			
		});
		executor.submit(new Runnable(){
			@Override
			public void run() {
				robotArm = RobotArm.getInstance();
			}			
		});
	}



	public boolean addControllerStatusListener(ControllerStatusListener listener){
		return controllerStatusListeners.add(listener);
	}

	public boolean removeControllerStatusListener(ControllerStatusListener listener){
		return controllerStatusListeners.remove(listener);
	}


	public boolean addDrinkMakerListener(DrinkMakerListener listener){
		return drinkMakerListeners.add(listener);
	}

	public boolean removeDrinkMakerListener(DrinkMakerListener listener){
		return drinkMakerListeners.remove(listener);
	}

	public boolean addAvailableDrinkAmountListener(AvailableDrinkListener listener){
		return availableDrinkListeners.add(listener);
	}

	public boolean removeAvailableDrinkAmountListener(AvailableDrinkListener listener){
		return availableDrinkListeners.remove(listener);
	}

	private void drinkStarted(Drink drink){
		synchronized (drinkMakerListeners) {
			for(DrinkMakerListener dl : drinkMakerListeners)
				dl.onDrinkStarted(drink);
		}
	}

	private void drinkMade(Drink drink){
		synchronized (drinkMakerListeners) {
			for(DrinkMakerListener dl : drinkMakerListeners)
				dl.onDrinkMade(drink, drinkCounter);
		}
	}

	private void initialize(){
		statusUpdated(Status.InitializationStarted);
		//Load configuration/Setup
		tryLoadConfiguration();

		//Load drinks, ingredients and glasses
		//List<Glass> glasses = LocalStorage.loadGlasses();
		//System.out.println("Controller loaded " + glasses.size() + " glasses");

		//			List<Ingredient> ings = LocalStorage.loadIngredients();
		//			System.out.println("Controller loaded " + ings.size() + " ingredients");
		//	
		//List<Drink> drinks = LocalStorage.loadDrinks();
		//System.out.println("Controller loaded " + drinks.size() + " drinks");

		//Listen for when everything is being updated
		addb.addDrinksUpdatedListener(this);
		addb.addGlassesUpdatedListener(this);
		addb.addIngredientsUpdatedListener(this);

		/*
		 * Load drink information stored locally
		 */
		//Glasses
		List<String> glassIds = LocalStorage.getGlassIds();
		availableGlassesIds.addAll(glassIds);
		
		//TODO Adding all glasses to don't make it a limiting factor when finding available drinks
		for(String id : glassIds){
			try {
				drinkProvider.addGlass(LocalStorage.loadGlass(id));
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		System.out.println("Glasses loaded from local storage.");
		
		//Ingredients
		List<String> ingredientsIds = LocalStorage.getIngredientIds();
		availableIngredientsIds.addAll(ingredientsIds);

		try {
			List<Ingredient> ingredients = LocalStorage.loadIngredients();
			for(Ingredient i : ingredients)
				allIngredientName2Ids.put(i.getName(), i.getId());
			//allIngredientIds.add(i.getId());			
		} catch (ClassNotFoundException | IOException e1) {
			// Too bad...
		}
		System.out.println("Ingredients loaded from local storage.");



		//Drinks
		List<String> drinkIds = LocalStorage.getDrinkIds();
		int counter = 0;
		for(String id : drinkIds){
			try {
				Drink drink = LocalStorage.loadDrink(id);
				drinkProvider.addDrink(drink);
				
				//Force GC to run and free up a bunch of resources 
				//TODO This is untested on the UDOO board
				if(40 < counter++){
					Runtime.getRuntime().gc();
					counter = 0;
				}
			} catch (FileNotFoundException e) {
				try{
					System.out.print("Unable to load drink with id " + id + ". Trying to delete this... ");
					LocalStorage.deleteDrink(id);
					System.out.println(" File has been deleted!");
				} catch (FileNotFoundException fnfe){
					System.out.println("File (drink with id: " + id + " could NOT be deleted either: Please remove this manually!");
				}
			}
		}
		System.out.println("Drinks loaded from local storage.");
		updatedDrinks.clear();

		//Update all drinks, ingredients, ...
		addb.updateAllDrinks();
		addb.updateAllGlasses();
		addb.updateAllIngredients();

		//TODO Remove this before release
		List<Ingredient> ingredients;
		try {
			ingredients = LocalStorage.loadIngredients();
			int posCounter = 0;
			for(int i=0; i<ingredients.size() && i<50; i++)
				addIngredient(ingredients.get(i), posCounter++, new Amount(1, QuantityType.l));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		//Done :-)
		statusUpdated(Status.InitializationEnded);
		System.out.println("Controller initialzed!");
	}

	public void stop(){
		statusUpdated(Status.ShuttingDown);
		//Save current settings
		saveConfiguration();

		//Free all resources
		executor.shutdown();

	}

	private void tryLoadConfiguration(){
		//Load from local hard drive
		Controller con = null;
		try{
			con = LocalStorage.loadConfiguration();
		} catch (IOException | ClassNotFoundException e){			
		}
		if(con != null)
			instance = con;
	}

	private boolean saveConfiguration(){
		//Save to local hard drive
		try {
			LocalStorage.saveConfiguration(instance);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void clearConfiguration(){
		//Want to stop all ongoing processes??
		//executor in Controller and ??? should cancel all Futures

		//Reset all settings
		//		drinkProvider.clear();
		availableDrinksIds.clear();
		availableGlassesIds.clear();
		availableIngredientsIds.clear();

		//Delete local copy
		LocalStorage.deleteSavedConfiguration();
	}

	public Future<?> execute(Runnable task){
		return executor.submit(task);
	}

	public <E> Future<E> execute(Callable<E> task){
		return executor.submit(task);
	}

	public List<Drink> getTestDrinksFromLocalStorage(){	
		String[] ids = {"42nd-street",
				"67",
				"76",
				//"100-wallbanger",
				"a-1",
				"a-b-c",
				"a-blossomed-tree",
				"a-day-at-the-beach",
				//				"a-glass-of-champagne",
				//				"a-glass-of-milk",
				//				"a-glass-of-water",
				"a-j",
				"a-m",
				//				"a-peach-summer",
				"a-sunny-day",
				"a-taste-of-honey",
				//				"a-twist-of-level",
		"a-veranda-moment"};

		List<Drink> drinks = new ArrayList<>();
		for(String id : ids)
			try {
				drinks.add(LocalStorage.loadDrink(id));
			} catch (FileNotFoundException e) {				
			}
		return drinks;		
	}

	public List<Drink> getTestDrinksFromADDb(){
		return addb.getTestDrinks();
	}

	public List<Drink> searchDrinks(List<Action> actions, List<Glass> glasses, List<IngredientProperties> ingredientProperties, List<Occasion> occasions, List<Skill> skills, List<Tag> tags, List<Taste> tastes, List<Tool> tools){
		//TODO This should be done online as the localStorage already provide this service as an event listener
		statusUpdated(Status.SearchingForDrinksStarted);
		List<String> actionIds = new ArrayList<>();
		List<String> glassIds = new ArrayList<>();
		List<String> ingredientsPropertyIds = new ArrayList<>();
		List<String> occasionIds = new ArrayList<>();
		List<String> skillIds = new ArrayList<>();
		List<String> tagNames = new ArrayList<>();
		List<String> tasteIds = new ArrayList<>();
		List<String> toolIds = new ArrayList<>();

		List<String> drinkIds = drinkProvider.findDrinks(actionIds, glassIds, ingredientsPropertyIds, occasionIds, skillIds, tagNames, tasteIds, toolIds);
		List<Drink> drinks = new ArrayList<>();
		for(String id : drinkIds){
			try {
				drinks.add(LocalStorage.loadDrink(id));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		statusUpdated(Status.SearchingForDrinksEnded);
		return drinks; 
	}

	public boolean saveDrink(Drink drink, boolean overwriteIfExists){
		//Save locally
		try {
			LocalStorage.saveDrink(drink, overwriteIfExists);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean saveDrinks(List<Drink> drinks, boolean overwriteIfExists){
		boolean result = true;
		for(Drink drink : drinks)
			result &= saveDrink(drink, overwriteIfExists);
		return result;
	}

	public boolean saveIngredient(Ingredient ingredient, boolean overwriteIfExists){
		//Save locally
		try {
			LocalStorage.saveIngredient(ingredient, overwriteIfExists);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean saveIngredients(List<Ingredient> ingredients, boolean overwriteIfExists){
		boolean result = true;
		for(Ingredient ingredient : ingredients)
			result &= saveIngredient(ingredient, overwriteIfExists);
		return result;
	}


	public boolean saveGlass(Glass glass, boolean overwriteIfExists){
		//Save locally
		try {
			LocalStorage.saveGlass(glass, overwriteIfExists);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean saveGlasses(List<Glass> glasses, boolean overwriteIfExists){
		boolean result = true;
		for(Glass glass : glasses)
			result &= saveGlass(glass, overwriteIfExists);
		return result;
	}

	public void deleteDrinkLocally(String drinkId) throws FileNotFoundException{
		LocalStorage.deleteDrink(drinkId);
	}

	public void deleteIngredientLocally(Ingredient ingredient){
		LocalStorage.deleteIngredient(ingredient);
	}

	public void deleteGlassLocally(Glass glass){
		LocalStorage.deleteGlass(glass);
	}

	public List<Drink> getAvailableDrinks(){
		List<Drink> drinks = new ArrayList<>();
		for(String id : availableDrinksIds)
			try {
				drinks.add(LocalStorage.loadDrink(id));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return drinks;
	}

	public List<Glass> getAvailableGlasses(){
		List<Glass> glasses = new ArrayList<>();
		for(String id : availableGlassesIds)
			try {
				glasses.add(LocalStorage.loadGlass(id));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return glasses;
	}

	public List<Ingredient> getAvailableIngredients(){
		List<Ingredient> ings = new ArrayList<>();
		for(String id : availableIngredientsIds)
			try {
				ings.add(LocalStorage.loadIngredient(id));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return ings;
	}

	public List<Ingredient> getIngredients(){
		List<Ingredient> result = new ArrayList<>();
		try {
			result = LocalStorage.loadIngredients();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return result;
	}


	public List<Ingredient> findIngredientsWithNameContaining(String namePart){
		List<Ingredient> ingredients = new ArrayList<>();
		for(String id : allIngredientName2Ids.keySet()){
			try {
				Ingredient ing = LocalStorage.loadIngredient(id);
				if(ing.getName().contains(namePart))
					ingredients.add(ing);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		}

		return ingredients;
	}


	public void addIngredient(Ingredient ingredient, int position, Amount amount) throws IllegalArgumentException{
		if(ingredient == null || position == -1 || amount == null || amount.getAmount() == 0)
			throw new IllegalArgumentException("Invalid parameters: Ingredient (" + ingredient + "), position (" + position + "), amount (" + amount.getAmount() + " " + amount.getQuantityType() + ")");

		//An ingredient (i.e. a bottle of juice) that can be used to make a drink
		if(addedIngredients.containsKey(position))
			throw new IllegalArgumentException("Position " + position + " is already occupied by (id: " + addedIngredients.get(position) + ")");

		addedIngredients.put(position, ingredient.getId());
		drinkProvider.addIngredient(ingredient);

		/*TODO List<String> drinks = drinkProvider.findDrinks(null, availableGlassesIds, availableIngredientsIds, null, null, null, null, null);
		if(availableDrinksIds.size() < drinks.size()){
			availableDrinksIds = drinks;
			availableDrinksUpdated(availableDrinksIds);
		}*/
	}

	public Drink getDrink(String id){
		Drink drink = null;
		try {
			drink = LocalStorage.loadDrink(id);
			return drink;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		drink = addb.getDrink(id);
		
		return drink;
	}
	
	public void removeIngredient(Ingredient ingredient) throws IllegalArgumentException{
		//TODO if(!drinkOrganizer.getAddedIngredients().contains(ingredient))
		//			throw new IllegalArgumentException("Ingredient has not been added and can therefore not be removed");

		//An ingredient (i.e. a bottle of juice) that can be used to make a drink
	}

	public List<Boolean> getAvailableIngredientPositions(){
		//TODO Get these data from the hardware
		List<Boolean> list = new LinkedList<>();
		for(int i=0; i<10; i++)
			list.add(i%2==0);
		return list;
	}

	public boolean makeDrink(Drink drink){
		statusUpdated(Status.MakingDrinkStarted);
		if(!availableDrinksIds.contains(drink))
			return false;
		drinkStarted(drink);
		System.out.println("Ordered drink: " + drink.getName());

		List<IngredientAmount> amounts = convertIngredientProperties2IngredientAmounts(drink);

		robotArm.makeDrink(drink.getServedIn(), amounts);


		//These 3 lines should be fired from the RobotArm!!
		drinkCounter++;
		drinkMade(drink);
		statusUpdated(Status.MakingDrinkEnded);

		return true;
	}

	private List<IngredientAmount> convertIngredientProperties2IngredientAmounts(Drink drink){


		return null;
	}


	public int getNumberOfDrinksMade(){
		return drinkCounter;
	}

	public static Controller getInstance(){
		if(instance == null)
			instance = new Controller();

		return instance;
	}



	@Override
	public void onGotUpdatedIngredients(List<Ingredient> ingredients) {
//		System.out.println("Controller got " + ingredients.size() + " ingredients from ADDb");
		saveIngredients(ingredients, true);	
		for(Ingredient i : ingredients)
			availableIngredientsIds.add(i.getId());
		//availableDrinksUpdated(availableDrinksIds);
	}



	@Override
	public void onGotUpdatedGlasses(List<Glass> glasses) {
//		System.out.println("Controller got " + glasses.size() + " glasses from ADDb");
		saveGlasses(glasses, true);
	}



	@Override
	public void onGotUpdatedDrinks(List<Drink> drinks) {
//		System.out.println("Controller got " + drinks.size() + " drinks from ADDb");
		saveDrinks(drinks, true);
		
		List<String> ids = new ArrayList<>();
		for(Drink d : drinks)
			ids.add(d.getId());
		updatedDrinks.addAll(ids);
		drinksUpdated(ids, updatedDrinks.size(), drinkProvider.getTotalAmount());
		drinkProvider.addDrinks(drinks);
	//	totalDrinksUpdated(drinkProvider.getTotalAmount());
	}

	private void availableDrinksUpdated(List<String> drinkIds){
		synchronized (availableDrinkListeners) {
			//System.out.println("Controller: availableDrinksUpdated");
			for(AvailableDrinkListener adal : availableDrinkListeners)
				adal.onAvailableDrinksUpdated(drinkIds);
		}
	}

	/*private void totalDrinksUpdated(int size){
		synchronized (availableDrinkListeners) {
			for(AvailableDrinkListener adal : availableDrinkListeners)
				adal.onTotalDrinksUpdated(size);
		}
	}*/
	
	private void drinksUpdated(List<String> drinkIds, int totalUpdated, int total){
		synchronized (availableDrinkListeners) {
			for(AvailableDrinkListener adal : availableDrinkListeners)
				adal.onDrinksUpdated(drinkIds, totalUpdated, total);
		}
		
	}

	private void statusUpdated(Status status){
		synchronized (controllerStatusListeners) {
			for(ControllerStatusListener listener : controllerStatusListeners)
				listener.onStatusUpdated(status);
		}
	}

	public Map<String, String> getIngredientName2IdMap() {
		return allIngredientName2Ids;
	}



	public Ingredient getIngredientFromId(String ingredientId) {
		try {
			return LocalStorage.loadIngredient(ingredientId);
		} catch (FileNotFoundException e) {
			//Hmmm
		}			
		
		return addb.getIngredient(ingredientId);
	}



	public String getIngredientIdFromName(String name) {
		for(String i : allIngredientName2Ids.keySet())
			if(i.equals(name))
				return allIngredientName2Ids.get(i);

		return null;
	}



	@Override
	public void onAvailableDrinksUpdated(List<String> drinkIds) {
		updatedDrinks.addAll(drinkIds);
		availableDrinksUpdated(drinkIds);
		
	}



	@Override
	public void onDrinkProviderCleared() {
		//Do nothing for now
	}
	
	public static void main(String[] args) throws Exception{
		Controller controller = new Controller();
		
		System.in.read();
		
	}
}
