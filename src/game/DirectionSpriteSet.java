package game;


import java.util.HashMap;

import org.newdawn.slick.Animation;

public class DirectionSpriteSet {

	private HashMap<Direction, Animation> directionSprites = new HashMap<Direction, Animation>();

	public DirectionSpriteSet(Animation down, Animation left, Animation right,
			Animation up) {
		directionSprites.put(Direction.DOWN, down);
		directionSprites.put(Direction.LEFT, left);
		directionSprites.put(Direction.RIGHT, right);
		directionSprites.put(Direction.UP, up);
	}

	public Animation getSprite(Direction direction) {
		return directionSprites.get(direction);
	}
	
	public DirectionSpriteSet getCopy(){
		return new DirectionSpriteSet(directionSprites.get(Direction.DOWN).copy(),
				directionSprites.get(Direction.LEFT).copy(),
				directionSprites.get(Direction.RIGHT).copy(),
				directionSprites.get(Direction.UP).copy());
	}

}
