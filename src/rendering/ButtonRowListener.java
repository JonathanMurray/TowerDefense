package rendering;

interface ButtonRowListener {
	void buttonWasPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex);
	void buttonMouseOver(ButtonRow sourceRow, Button sourceButton, int buttonIndex);
	void buttonWasUnlockPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex);
}
