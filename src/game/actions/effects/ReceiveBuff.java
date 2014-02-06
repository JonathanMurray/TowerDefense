package game.actions.effects;

import game.actions.Parameters;
import game.buffs.Buff;
import game.objects.Entity;

public class ReceiveBuff implements Effect{
	
	private Buff buff;
	
	public ReceiveBuff(Buff buff){
		this.buff = buff;
	}

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		target.receiveBuff(buff);
		return true;
	}

}
