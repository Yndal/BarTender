package model.ADDb;

import java.io.Serializable;
import java.util.List;

import model.ADDb.interfaces.Id;

public class IngredientProperties implements Id, Serializable {
	
	public static class IngredientPropertyAmount {
		public enum AmountType {
			Part,
			Splash,
			
			Undefined
		}
		
		private final double amount;
		private final AmountType amountType;
		
		private IngredientPropertyAmount(double amount, AmountType amountType){
			this.amount = amount;
			this.amountType = amountType;
		}

		public double getAmount(){
			return amount;			
		}
		
		public AmountType getAmountType() {
			return amountType;
		}
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6974853453156589474L;
	
	private String id;
	private String type;
	private String text;
	private String textPlain;
	
	public IngredientProperties(String id, String type, String text, String textPlain){
		this.id = id;
		this.type = type;
		this.text = text;
		this.textPlain = textPlain;
	}
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getText() {
		return text;
	}
	public String getTextPlain() {
		return textPlain;
	}
	
	public List<IngredientPropertyAmount> getAsPropertyAmount(){
		
		
		
		
		return null;
	}
}
