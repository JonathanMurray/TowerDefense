package rendering;


import java.awt.Point;

import org.newdawn.slick.gui.GUIContext;


class MouseDialog extends Dialog {

	static MouseDialog createDialogWithReplacement(HUD hud,
			GUIContext container, String title, Point upperLeft,
			DialogChoice replacement, DialogChoice[] old) {
		if (replacement == null) {
			throw new IllegalArgumentException();
		}
		return new MouseDialog(hud, container, title, upperLeft, replacement,
				old);
	}

	static MouseDialog createDialog(HUD hud, GUIContext container,
			String title, Point upperLeft, DialogChoice[] choices) {
		return new MouseDialog(hud, container, title, upperLeft, null, choices);
	}

	private MouseDialog(HUD hud, GUIContext container, String title,
			Point upperLeft, DialogChoice replacement, DialogChoice[] old) {
		super(hud, container, title, upperLeft, replacement, true, old);
	}

	@Override
	public void buttonWasUnlockPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		// TODO Auto-generated method stub
		
	}
}
