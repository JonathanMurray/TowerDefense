package game.buffs;

import game.objects.Entity;


public class CompositeBuff extends Buff{

	private Buff[] buffs;
	
	public CompositeBuff(Buff... buffs ) {
		super(getCompositeId(buffs));
		this.buffs = buffs;
		int duration = buffs[0].getTotalDuration();
		for(Buff b : buffs){
			if(b.getTotalDuration() != duration){
				throw new IllegalArgumentException("All buffs must have same duration");
			}
		}
	}
	
	private static String getCompositeId(Buff... buffs){
		String id = "";
		for(Buff b : buffs){
			id += b.getID();
		}
		return id;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		for(Buff b : buffs){
			carrier.receiveBuff(b);
		}
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		for(Buff b : buffs){
			carrier.loseBuff(b);
		}
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {}

	@Override
	protected int getTotalDuration() {
		return buffs[0].getTotalDuration();
	}

	@Override
	public Buff getCopy() {
		Buff[] copies = new Buff[buffs.length];
		int i = 0;
		for(Buff b : buffs){
			copies[i] = b.getCopy();
			i++;
		}
		return new CompositeBuff(copies);
	}

}
