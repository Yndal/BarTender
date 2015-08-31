package control.dispenser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import control.dispenser.DispenserListener.State;
import control.dispenser.drink.IngredientAmount;
import model.ADDb.Glass;

public abstract class Dispenser {
	private final Set<DispenserListener> dispenserListeners = new HashSet<>();
	
	
	public boolean addDispenserListener(DispenserListener listener){
		return dispenserListeners.add(listener);
	}
	
	public boolean removeDispenserListener(DispenserListener listener){
		return dispenserListeners.remove(listener);
	}
	
	protected void stateChanged(State state){
		synchronized (dispenserListeners) {
			for(DispenserListener dl : dispenserListeners)
				dl.onStateChanged(state);
		}
	}
	
	//public void makeDrink(Drink drink);
	abstract public void makeDrink(Glass glass, List<IngredientAmount> ingredientAmounts);

}
