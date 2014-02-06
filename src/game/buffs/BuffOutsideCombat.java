package game.buffs;

import game.EntityHealthListener;
import game.objects.Entity;
import game.objects.Hero;

import org.newdawn.slick.Animation;

public class BuffOutsideCombat extends Buff implements
		EntityHealthListener {
	
	private Buff buff;

	public BuffOutsideCombat(Buff buff) {
		super(buff.id + "_OUTSIDE_COMBAT");
		this.buff = buff;
	}

	@Override
	protected void continuousEffect(Entity target, int delta) {
	}

	

	@Override
	public void applyEffectOn(Entity target) {
		target.addHealthListener(this);
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		if (newHealth < oldHealth) {
			entity.loseBuff(buff);
			entity.loseBuff(this);
			entity.removeHealthListener(this);
		}
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getTotalDuration() {
		return buff.getTotalDuration();
	}

	@Override
	public Buff getCopy() {
		return new BuffOutsideCombat(buff.getCopy());
	}

}
