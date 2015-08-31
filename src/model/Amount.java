package model;



public class Amount{
	public enum QuantityType {
		ml("ml"),
		cl("cl"),
		dl("dl"),
		l("l"),
		pieces("pieces");
		
		private final String name;
		
		private QuantityType(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
	}

	private int amount;
	private QuantityType type;
	
	public Amount(){
		this(0, QuantityType.ml);
	}
	
	public Amount(int amount, QuantityType type){
		this.amount = amount;
		this.type = type;
	}
 
	public int getAmount(){
		return amount;
	}
	
	public QuantityType getQuantityType(){
		return type;
	}
}