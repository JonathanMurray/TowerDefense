package game;

public enum TechUpgrade {

	towerRange(1), towerHealth(2);

	private int amount;

	TechUpgrade(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return amount;
	}

}
