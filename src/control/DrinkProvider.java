package control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.ADDb.Action;
import model.ADDb.Drink;
import model.ADDb.Glass;
import model.ADDb.Ingredient;
import model.ADDb.IngredientProperties;
import model.ADDb.Occasion;
import model.ADDb.Tag;
import model.ADDb.Taste;
import model.ADDb.Tool;

public class DrinkProvider{
	public interface DrinkProviderListener {
		void onAvailableDrinksUpdated(List<String> drinkIds);
		void onDrinkProviderCleared();
	}

	private static final Set<DrinkProviderListener> drinkProviderListener = new HashSet<>();

	private final HashMap<String, List<String>> actions = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> glasses = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> ingredients = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> occasions = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> skills = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> tags = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> tastes = new HashMap<String, List<String>>();
	private final HashMap<String, List<String>> tools = new HashMap<String, List<String>>();

	//Keep track of how many drink there have been added
	private final HashSet<String> addedDrinkIds = new HashSet<>();

	private final Set<String> availableGlasses = new HashSet<>();
	private final Set<String> availableIngredients = new HashSet<>();

	public DrinkProvider(DrinkProviderListener listener){
		addDrinkProviderListener(listener);
	}
	public boolean addDrinkProviderListener(DrinkProviderListener listener){
		return drinkProviderListener.add(listener);
	}

	public boolean removeDrinkProviderListener(DrinkProviderListener listener){
		return drinkProviderListener.remove(listener);
	}

	private void availableDrinksUpdated(List<String> drinkIds){
		synchronized (drinkProviderListener) {
			for(DrinkProviderListener listener : drinkProviderListener)
				listener.onAvailableDrinksUpdated(drinkIds);
		}
	}

	private void updateAvailableDrinks(){
		List<String> drinkIds = findDrinks(null, new ArrayList<String>(availableGlasses), new ArrayList<String>(availableIngredients), null, null, null, null, null);
		availableDrinksUpdated(drinkIds);
	}

	public List<String> findDrinks(
			List<String> actionIds, 
			List<String> glassIds, 
			List<String> ingredientsPropertyIds, 
			List<String> occasionIds, 
			List<String> skillIds, 
			List<String> tagNames, 
			List<String> tasteIds, 
			List<String> toolIds){

		//Start with all drinks
		Set<String> result = new HashSet<>(addedDrinkIds);

		if(actionIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : actionIds){
				List<String> drinkIds = actions.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(glassIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : glassIds){
				List<String> drinkIds = glasses.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(ingredientsPropertyIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : ingredientsPropertyIds){
				List<String> drinkIds = ingredients.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(occasionIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : occasionIds){
				List<String> drinkIds = occasions.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(skillIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : skillIds){
				List<String> drinkIds = skills.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(tagNames != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : tagNames){
				List<String> drinkIds = tags.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(tasteIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : tasteIds){
				List<String> drinkIds = tastes.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}

		if(toolIds != null){
			Set<String> tempIds = new HashSet<>();
			for(String id : toolIds){
				List<String> drinkIds = tools.get(id);
				if(drinkIds != null)
					tempIds.addAll(drinkIds);
			}
			result = getSetFromCommonValues(result, tempIds);
		}


		return new ArrayList<String>(result);
	}

	private Set<String> getSetFromCommonValues(Set<String> s1, Set<String> s2){
		Set<String> finalSet = new HashSet<>();

		for(String s : s1)
			if(s2.contains(s))
				finalSet.add(s);

		return finalSet;
	}

	private HashSet<String> initializeDrinkIds(){
		return new HashSet<String>();
	}


	public void addDrink(Drink drink){
		if(drink == null)
			return;

		System.out.println("DrinkProvider: Adding drink " + drink.getName());
		String drinkId = drink.getId();

		addedDrinkIds.add(drinkId);

		if(drink.getActions() != null){
			for(Action action : drink.getActions()){
				if(actions.get(action.getId()) == null)
					actions.put(new String(action.getId()), new ArrayList<String>());
				actions.get(action.getId()).add(drinkId);
			}
		}

		if(drink.getIngredientProperties() != null){
			for(IngredientProperties ip : drink.getIngredientProperties()){
				if(ingredients.get(ip) == null)
					ingredients.put(new String(ip.getId()), new ArrayList<String>());
				ingredients.get(ip.getId()).add(drinkId);
			}
		}

		if(drink.getOccasions() != null){
			for(Occasion oc : drink.getOccasions()){
				if(occasions.get(oc) == null)
					occasions.put(new String(oc.getId()), new ArrayList<String>());
				occasions.get(oc.getId()).add(drinkId);
			}
		}

		if(drink.getTags() != null){
			for(Tag tag : drink.getTags()){
				if(tag.getName() != null){
					if(tags.get(tag.getName()) == null)
						tags.put(new String(tag.getName()), new ArrayList<String>());
					tags.get(tag.getName()).add(drinkId);
				}
			}
		}

		if(drink.getTastes() != null){
			for(Taste taste : drink.getTastes()){
				if(tastes.get(taste.getId()) == null)
					tastes.put(new String(taste.getId()), new ArrayList<String>());
				tastes.get(taste.getId()).add(drinkId);
			}
		}

		if(drink.getTools() != null){
			for(Tool tool : drink.getTools()){
				if(tools.get(tool.getId()) == null)
					tools.put(new String(tool.getId()), new ArrayList<String>());
				tools.get(tool.getId()).add(drinkId);
			}
		}


		if(drink.getServedIn() != null){
			if(glasses.get(drink.getServedIn().getId()) == null)
				glasses.put(new String(drink.getServedIn().getId()), new ArrayList<String>());
			glasses.get(drink.getServedIn().getId()).add(drinkId);
		}

		if(drink.getSkill() != null){
			if(skills.get(drink.getSkill().getId()) == null)
				skills.put(new String(drink.getSkill().getId()), new ArrayList<String>());
			skills.get(drink.getSkill().getId()).add(drinkId);
		}
	}

	public void addDrinks(List<Drink> drinks){
		for(Drink drink : drinks) 
			addDrink(drink);
	}

	public void removeDrink(Drink drink){
		//TODO

	}

	public void addIngredient(Ingredient ingredient){
		availableIngredients.add(ingredient.getId());
		updateAvailableDrinks();
	}

	public void removeIngredient(String ingredientId){
		availableIngredients.remove(ingredientId);
		updateAvailableDrinks();
	}

	public void addGlass(Glass glass){
		availableGlasses.add(glass.getId());
		updateAvailableDrinks();
	}

	public void removeGlass(String glassId){
		availableGlasses.remove(glassId);
		updateAvailableDrinks();
	}


	/*public void clear(){
		actions.clear();
		glasses.clear();
		ingredients.clear();
		occasions.clear();
		skills.clear();
		tags.clear();
		tastes.clear();
		tools.clear();

		synchronized (drinkProviderListener) {
			for(DrinkProviderListener listener : drinkProviderListener)
				listener.onDrinkProviderCleared();
		}
	}*/		

	public int getTotalAmount(){
		return addedDrinkIds.size();
	}
}