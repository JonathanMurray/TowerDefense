package game.objects;

public interface EntityAttributeListener {
	/**
	 * 
	 * @param entity
	 * @param attribute
	 * @param newValue double value, but if the attribute should actually be an int, the value should simply be rounded to closest int.
	 */
	void entityAttributeChanged(Entity entity, EntityAttribute attribute, double newValue);
}
