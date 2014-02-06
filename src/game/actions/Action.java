package game.actions;
import game.objects.Entity;


public interface Action {
	boolean execute(Entity actor, Parameters context);
}
