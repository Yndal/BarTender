package control.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import control.Controller;
import model.ADDb.Drink;
import model.ADDb.Glass;
import model.ADDb.Ingredient;

public class LocalStorage {
	private static class Constants{
		private static final String FILE_SEPARATOR = System.getProperty("file.separator");
		private static final String FILE_ENDING = "btd";

		private static final String WINDOWS_SAVING_PATH = "Windows path";
		private static final String OSX_SAVING_PATH = "OSX path";
		private static final String LINUX_SAVING_PATH = "Linux path";
		private static final String BACK_UP_SAVING_PATH = "Back-up path";
		private static final String SAVING_PATH;


		private static final String SAVES_FOLDER ;
		private static final String DRINKS_FOLDER;
		private static final String GLASSES_FOLDER;
		private static final String INGREDIENTS_FOLDER;
		private static final String CONFIGURATION_FOLDER;

		private static final String GLASS_FILE_ENDING = "gls";
		private static final String DRINK_FILE_ENDING = "dnk";
		private static final String INGREDIENT_FILE_ENDING = "ing";

		private static final String CONFIGURATION_NAME = "Configuration";

		static{
			//First figure out which OS is being used and define the root folder according to this
			String osName = System.getProperty("os.name");
			if(osName.contains("Mac"))
				SAVING_PATH = Constants.OSX_SAVING_PATH + FILE_SEPARATOR;
			else if(osName.contains("Windows"))
				SAVING_PATH = Constants.WINDOWS_SAVING_PATH + FILE_SEPARATOR;
			else if(osName.contains("Linux"))
				SAVING_PATH = Constants.LINUX_SAVING_PATH + FILE_SEPARATOR;
			else
				SAVING_PATH = Constants.BACK_UP_SAVING_PATH + FILE_SEPARATOR;

			//Define subfolders based on the root folder
			SAVES_FOLDER = SAVING_PATH + "saves" + FILE_SEPARATOR;
			DRINKS_FOLDER = SAVES_FOLDER + "drinks";
			GLASSES_FOLDER = SAVES_FOLDER + "glasses";
			INGREDIENTS_FOLDER = SAVES_FOLDER + "ingredients";
			CONFIGURATION_FOLDER = SAVES_FOLDER + "configurations";

			//Create the root and subfolders
			new File(Constants.DRINKS_FOLDER).mkdirs();
			new File(Constants.GLASSES_FOLDER).mkdirs();
			new File(Constants.INGREDIENTS_FOLDER).mkdirs();
			new File(Constants.CONFIGURATION_FOLDER).mkdirs();

			System.out.println("Saving path for LocalStorage: " + SAVING_PATH);
		}
	}


		public static void saveDrink(Drink drink, boolean overwriteIfExists) throws IOException{
		String filename = Constants.DRINKS_FOLDER + Constants.FILE_SEPARATOR + drink.getId() + "." + Constants.DRINK_FILE_ENDING;
		File file = new File(filename);

		//Delete if already exists
		if(overwriteIfExists && file.exists())
			deleteDrink(drink.getId());

		//Save the glass
		serializeObjectToFile(drink, filename);	
	}

	public static void saveDrinks(List<Drink> drinks, boolean overwriteIfExists) throws IOException{
		if(drinks != null)
			for(Drink drink : drinks)
				saveDrink(drink, overwriteIfExists);
	}
	
	public static List<String> getDrinkIds(){
		File drinksFolder = new File(Constants.DRINKS_FOLDER);
		File[] drinkNames = drinksFolder.listFiles();

		List<String> ids = new ArrayList<>();
		for(File file : drinkNames){
			if(file.getName().endsWith("." + Constants.DRINK_FILE_ENDING)){
				String id = file.getName().replace("." + Constants.DRINK_FILE_ENDING, "");
				ids.add(id);
			}
		}
		
		return ids;
	}

	public static List<Drink> loadDrinks() {
		File drinksFolder = new File(Constants.DRINKS_FOLDER);
		File[] drinkNames = drinksFolder.listFiles();

		List<Drink> drinks = new ArrayList<Drink>();
		for(File file : drinkNames){
			if(file.getName().endsWith("." + Constants.DRINK_FILE_ENDING)){
				try {
					System.out.println(file.getPath());
							
					drinks.add((Drink) deserializeFileToObject(file.getPath()));
				} catch (IOException | ClassNotFoundException exp){
					exp.printStackTrace();
				}
			}
		}

		return drinks;
	}

	public static Drink loadDrink(String drinkId) throws FileNotFoundException  {
		File file = new File(Constants.DRINKS_FOLDER + Constants.FILE_SEPARATOR + drinkId + "." + Constants.DRINK_FILE_ENDING);
		if(!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

		try{
			Drink drink = (Drink) deserializeFileToObject(file.getPath());
			return drink;
		} catch (IOException | ClassNotFoundException exp){
			throw new FileNotFoundException("Error loading: " + file.getAbsolutePath() + "; Reason: " + exp.getMessage());
		}		
	}

	public static void deleteDrink(String drinkId) throws FileNotFoundException {
		try {
			Files.deleteIfExists(Paths.get(Constants.DRINKS_FOLDER + Constants.FILE_SEPARATOR + drinkId));
		} catch (InvalidPathException | IOException exp) {
			exp.printStackTrace();
			throw new FileNotFoundException("Error deleting: " + drinkId);
		}
	}

	public static void saveGlass(Glass glass, boolean overwriteIfExists) throws IOException{
		String filename = Constants.GLASSES_FOLDER + Constants.FILE_SEPARATOR + glass.getId() + "." + Constants.GLASS_FILE_ENDING;
		File file = new File(filename);

		//Delete if already exists
		if(overwriteIfExists && file.exists())
			deleteGlass(glass);

		//Save the glass
		serializeObjectToFile(glass, filename);	
	}

	public static void saveGlasses(List<Glass> glasses, boolean overwriteIfExists) throws IOException{
		if(glasses != null)
			for(Glass glass : glasses)
				saveGlass(glass, overwriteIfExists);
	}

	public static List<String> getGlassIds(){
		File glassesFolder = new File(Constants.GLASSES_FOLDER);
		File[] glassNames = glassesFolder.listFiles();

		List<String> ids = new ArrayList<>();
		for(File file : glassNames){
			if(file.getName().endsWith("." + Constants.GLASS_FILE_ENDING)){
				String id = file.getName().replace("." + Constants.GLASS_FILE_ENDING, "");
				ids.add(id);
			}
		}
		
		return ids;
	}
	
	public static Glass loadGlass(String glassId) throws FileNotFoundException{
		File file = new File(Constants.GLASSES_FOLDER + Constants.FILE_SEPARATOR + glassId + "." + Constants.GLASS_FILE_ENDING);
		if(!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
		try{
			Glass glass = (Glass) deserializeFileToObject(file.getPath());
			return glass;
		} catch (IOException | ClassNotFoundException exp){
			throw new FileNotFoundException("Error loading: " + file.getAbsolutePath());
		}
	}

	public static List<Glass> loadGlasses() {
		File glassesFolder = new File(Constants.GLASSES_FOLDER);
		File[] glassNames = glassesFolder.listFiles();

		List<Glass> glasses = new ArrayList<Glass>();
		for(File file : glassNames){
			if(file.getName().endsWith("." + Constants.GLASS_FILE_ENDING)){
				try {
					glasses.add((Glass) deserializeFileToObject(file.getPath()));
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}

		return glasses;
	}

	public static void deleteGlass(Glass glass){
		try {
			Files.deleteIfExists(Paths.get(Constants.GLASSES_FOLDER + Constants.FILE_SEPARATOR + glass.getId()));
		} catch (InvalidPathException | IOException exp) {
			exp.printStackTrace();
		}
	}

	public static void saveIngredient(Ingredient ingredient, boolean overwriteIfExists) throws IOException{
		String filename = Constants.INGREDIENTS_FOLDER + Constants.FILE_SEPARATOR + ingredient.getId() + "." + Constants.INGREDIENT_FILE_ENDING;
		File file = new File(filename);

		//Delete if already exists
		if(overwriteIfExists && file.exists())
			deleteIngredient(ingredient);

		//Save the ingredient
		serializeObjectToFile(ingredient, filename);	
	}

	public static void saveIngredients(List<Ingredient> ingredients, boolean overwriteIfExists) throws IOException{
		if(ingredients != null)
			for(Ingredient ing : ingredients)
				saveIngredient(ing, overwriteIfExists);
	}
	
	public static List<String> getIngredientIds(){
		File ingredientsFolder = new File(Constants.INGREDIENTS_FOLDER);
		File[] ingredientNames = ingredientsFolder.listFiles();

		List<String> ids = new ArrayList<>();
		for(File file : ingredientNames){
			if(file.getName().endsWith("." + Constants.INGREDIENT_FILE_ENDING)){
				String id = file.getName().replace("." + Constants.INGREDIENT_FILE_ENDING, "");
				ids.add(id);
			}
		}
		
		return ids;
	}
	
	public static Ingredient loadIngredient(String ingredientId) throws FileNotFoundException{
		File file = new File(Constants.INGREDIENTS_FOLDER + Constants.FILE_SEPARATOR + ingredientId + "." + Constants.INGREDIENT_FILE_ENDING);
		if(!file.exists())
			throw new FileNotFoundException("File not found: " + file.getAbsolutePath());

		try{
			Ingredient ingredient = (Ingredient) deserializeFileToObject(file.getPath());
			return ingredient;
		} catch (IOException | ClassNotFoundException exp){
			throw new FileNotFoundException("Error loading: " + file.getAbsolutePath());
		}
	}
	public static List<Ingredient> loadIngredients() throws ClassNotFoundException, IOException{
		File ingredientsFolder = new File(Constants.INGREDIENTS_FOLDER);
		File[] ingredientNames = ingredientsFolder.listFiles();

		List<Ingredient> ingredients = new ArrayList<Ingredient>();
		for(File file : ingredientNames){
			try {
				if(file.getName().endsWith("." + Constants.INGREDIENT_FILE_ENDING))
					ingredients.add((Ingredient) deserializeFileToObject(file.getPath()));
			} catch (IOException | ClassNotFoundException exp){
				exp.printStackTrace();
			}
		}

		return ingredients;
	}

	public static void deleteIngredient(Ingredient ingredient){
		try {
			Files.deleteIfExists(Paths.get(Constants.INGREDIENTS_FOLDER + Constants.FILE_SEPARATOR + ingredient.getId()));
		} catch (InvalidPathException ipe){
			ipe.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void deleteSavedConfiguration(){
		File configFolder = new File(Constants.CONFIGURATION_FOLDER);
		String[] configs = configFolder.list();
		for(String config : configs){
			try {
				Files.deleteIfExists(Paths.get(config));
			} catch (InvalidPathException ipe){
				ipe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void saveConfiguration(Controller controller) throws IOException{
		String filename = Constants.CONFIGURATION_FOLDER + Constants.FILE_SEPARATOR + Constants.CONFIGURATION_NAME + "." + Constants.FILE_ENDING;
		serializeObjectToFile(controller, filename);
	}

	public static Controller loadConfiguration() throws ClassNotFoundException, IOException{
		String filename = Constants.CONFIGURATION_FOLDER + Constants.FILE_SEPARATOR + Constants.CONFIGURATION_NAME + "." + Constants.FILE_ENDING;
		return	(Controller) deserializeFileToObject(filename);	
	}

	private static void serializeObjectToFile(Object object, String path) throws IOException{
		FileOutputStream fos = new FileOutputStream(path);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(object);

		out.close();
	}

	private static Object deserializeFileToObject(String path) throws IOException, ClassNotFoundException{
 		FileInputStream fis = new FileInputStream(path);
		ObjectInputStream in = new ObjectInputStream(fis);
		Object object = in.readObject();
		in.close();

		return object; 			
	}
}
