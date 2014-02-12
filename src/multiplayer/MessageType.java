package multiplayer;

import java.io.Serializable;

public enum MessageType implements Serializable{
	ADD_VISUAL_EFFECT,
	REMOVE,
	UPDATE_PHYSICS,
	
	CLIENT_READY,
	
	TOWER_WAS_ADDED,
	TOWER_WAS_UNLOCKED,
	ITEM_WAS_ADDED,
	ITEM_WAS_REMOVED,
	MONEY_WAS_UPDATED,
	PLAYER_LIFE_WAS_UPDATED,
	
}

 