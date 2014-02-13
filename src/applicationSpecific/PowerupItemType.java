package applicationSpecific;

import game.Entities;
import game.OfflineGamePlayState;
import game.Player;
import game.ResourceLoader;
import game.SoundWrapper;
import game.Sounds;
import game.objects.AnimationBasedVisualEffect;
import game.objects.HeroInfo;
import game.objects.PowerupItem;
import game.objects.Unit;

import java.awt.Point;


public enum PowerupItemType {

	/*
	 * STRENGTH( new PowerupAction(){ public void execute(PowerupItem item){
	 * HeroInfo.changeStrength(5); HeroInfo.changeIntelligence(-2);
	 * HeroInfo.changeDexterity(-2); } }, "strengthItem.png", 8000 ),
	 * 
	 * DEXTERITY( new PowerupAction(){ public void execute(PowerupItem item){
	 * HeroInfo.changeDexterity(5); HeroInfo.changeIntelligence(-2);
	 * HeroInfo.changeStrength(-2); } }, "dexterityItem.png", 8000 ),
	 * 
	 * INTELLIGENCE( new PowerupAction(){ public void execute(PowerupItem item){
	 * HeroInfo.changeIntelligence(5); HeroInfo.changeStrength(-2);
	 * HeroInfo.changeDexterity(-2); } }, "intelligenceItem.png", 8000 ),
	 */
	
	/*
	HEALTH(new PowerupAction() {
		public void execute(PowerupItem item) {
			HeroInfo.getHero().gainHealth(35);

			Point effectLocation = item.getPixelCenterLocation();
			GamePlayState.addSpecialEffect(new AnimationSpecialEffect(effectLocation,
					ResourceLoader.createTileScaledAnimation(false,
							"bubble2.png", "bubble.png")));
			SoundContainer sound = ResourceLoader.createSound("bell.wav");
			Sounds.play(sound);
		}
	}, "healthPowerup.png", 30000),

	MANA(new PowerupAction() {
		public void execute(PowerupItem item) {
			HeroInfo.getHero().changeMana(35);

			Point effectLocation = item.getPixelCenterLocation();
			GamePlayState.addSpecialEffect(new AnimationSpecialEffect(effectLocation,
					ResourceLoader.createTileScaledAnimation(false,
							"bubble2.png", "bubble.png")));
			SoundContainer sound = ResourceLoader.createSound("bell.wav");
			Sounds.play(sound);
		}
	}, "manaPowerup.png", 30000),

	BOTH(new PowerupAction() {
		public void execute(PowerupItem item) {
			HeroInfo.getHero().gainHealth(35);
			HeroInfo.getHero().changeMana(35);

			Point effectLocation = item.getPixelCenterLocation();
			GamePlayState.addSpecialEffect(new AnimationSpecialEffect(effectLocation,
					ResourceLoader.createTileScaledAnimation(false,
							"bubble2.png", "bubble.png")));
			SoundContainer sound = ResourceLoader.createSound("bell.wav");
			Sounds.play(sound);
		}
	}, "bothPowerup.png", 60000),

	AOE_DEATH(new PowerupAction() {
		public void execute(PowerupItem item) {
			Point heroLocation = HeroInfo.getHero().getLocation();
			for (Unit enemy : Entities.getEntitiesWithinRange(heroLocation, 3.5)) {
				enemy.die(true);
			}
			SoundContainer sound = ResourceLoader.createSound("bomb.wav");
			Sounds.play(sound);
			Point effectLocation = item.getPixelCenterLocation();
			GamePlayState.addSpecialEffect(new AnimationSpecialEffect(effectLocation, 6,
					ResourceLoader.createTileScaledAnimation(false,
							"explosion.png")));
		}

	}, "aoeDeath.png", 60000),
*/
//	COINS(
//
//	new PowerupAction() {
//		public void execute(PowerupItem item) {
//			Player.gainMoney(3);
//			Sounds.play(ResourceLoader.createSound("ting.wav"));
//		}
//
//	}, "coins.png", 20000);
//
//	public PowerupAction action;
//	public String spriteRef;
//	public int duration;
//
//	PowerupItemType(PowerupAction action, String spriteRef,
//			int timeUntilDisappear) {
//		this.action = action;
//		this.spriteRef = spriteRef;
//		this.duration = timeUntilDisappear;
//	}

}
