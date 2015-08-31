package control.dispenser.drink;

import java.util.List;

import model.ADDb.Glass;

public class Drink {
	private final Glass glass;
	private final List<IngredientAmount> amounts;
	
	public Drink(Glass glass, List<IngredientAmount> amounts){
		this.glass = glass;
		this.amounts = amounts;
	}
	
	public Glass getGlass(){
		return glass;
	}
	
	public List<IngredientAmount> getIngredientAmounts(){
		return amounts;
	}
}
