package messages;

public enum UserInputMessage implements MessageType {
	CLIENT_PRESSED_KEYS,
	CLIENT_PRESSED_LEFT_MOUSE,
	CLIENT_PRESSED_RIGHT_MOUSE,
	
	//Dialog
	PRESSED_REPLACE_ABILITY,
	PRESSED_ADD_ABILITY,
	
	//Top-left buttons
	PRESSED_SELECT_TOWER,
	PRESSED_UNLOCK_TOWER,
	
	//Top-right buttons
	PRESSED_BUY_ITEM,
	
	
	//Bottom-left buttons
	PRESSED_USE_ABILITY,
	
	//Bottom-right buttons
	PRESSED_USE_ITEM;
	
	
	
	
}
