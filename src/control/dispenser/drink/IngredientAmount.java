package control.dispenser.drink;

public class IngredientAmount{
	public enum AmountType{
		mL,
		cL,
		dL,
		L,
		pieces
	}
	
	private final int ingredientPosition;
	private final int amount;
	private final AmountType amountType;
	
	public IngredientAmount(int ingredientPosition, int amount, AmountType amountType){
		this.ingredientPosition = ingredientPosition;
		this.amount = amount;
		this.amountType =  amountType;
	}

	public int getIngredientPosition() {
		return ingredientPosition;
	}

	public int getAmount() {
		return amount;
	}
	
	public AmountType getAmountType() {
		return amountType;
	}
}