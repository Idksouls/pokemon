public class Pokeball {
	private String type;
	private double catchRate;
	
    public Pokeball(String type, double catchRate) {
        this.type = type;
        this.catchRate = catchRate;
    }
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getCatchRate() {
		return catchRate;
	}
	public void setCatchRate(double catchRate) {
		this.catchRate = catchRate;
	}
	
	public boolean catchPokemon(Pokemon pokemon) {
		return Math.random() < catchRate;
	}

}
