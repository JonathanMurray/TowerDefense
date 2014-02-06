package rendering;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

class TextBox {

	private Rectangle rect;
	private String text;
	
//	static void main(String[] args) {
//		System.out.println(splitTextIntoLines("1\n 2\nabc abc abcd a b", 5));
//		//System.out.println(splitTextIntoLines("123 56 8 x   ab", 2));
//		//System.out.println(splitTextIntoLines("This is a very handy and useful spell. it can be used to deal with many foes attacking you at the same time.", 20));
//	}
	
	TextBox(Rectangle rect, String text){
		this(rect, text, (int)(rect.getWidth()/9));
	}
	
	TextBox(Rectangle rect, String text, int charactersPerLine) {
		this.rect = rect;		
		this.text = splitTextIntoLines(text, charactersPerLine);
	}
	
	static String splitTextIntoLines(String text, int maxCharactersPerLine){
		StringBuilder textWithLineBreaks = new StringBuilder();
		int beginningOfThisLine = 0;
		while(beginningOfThisLine < text.length()){
			
			if(text.charAt(beginningOfThisLine) == ' '){
				beginningOfThisLine ++;
			}
			if(beginningOfThisLine + maxCharactersPerLine >= text.length() - 1){
				textWithLineBreaks.append(text.substring(beginningOfThisLine));
				break;
			}
			
			if(text.subSequence(beginningOfThisLine, beginningOfThisLine+maxCharactersPerLine).toString().contains("\n")){
				int lineBreakLocalInd = text.subSequence(beginningOfThisLine, beginningOfThisLine+maxCharactersPerLine).toString().indexOf('\n');
				textWithLineBreaks.append(text.subSequence(beginningOfThisLine, beginningOfThisLine + lineBreakLocalInd + 1));
				beginningOfThisLine = beginningOfThisLine + lineBreakLocalInd + 1;
				continue;
			}
			
			if(text.charAt(beginningOfThisLine + maxCharactersPerLine) == ' '){
				textWithLineBreaks.append(text.subSequence(beginningOfThisLine, beginningOfThisLine + maxCharactersPerLine) + "\n");
				beginningOfThisLine += maxCharactersPerLine;
			}else if(text.subSequence(beginningOfThisLine, beginningOfThisLine + maxCharactersPerLine).toString().contains(" ")){
				for(int lineBreakIndex = beginningOfThisLine + maxCharactersPerLine - 1; lineBreakIndex >= beginningOfThisLine; lineBreakIndex --){
					if(text.charAt(lineBreakIndex) == ' '){
						textWithLineBreaks.append(text.subSequence(beginningOfThisLine, lineBreakIndex) + "\n");
						beginningOfThisLine = lineBreakIndex + 1;
						break;
					}
				}
			}else{
				for(int lineBreakIndex = beginningOfThisLine + maxCharactersPerLine + 1; lineBreakIndex < text.length(); lineBreakIndex ++){
					if(text.charAt(lineBreakIndex) == ' ' ){
						textWithLineBreaks.append(text.subSequence(beginningOfThisLine, lineBreakIndex) + "\n");
						beginningOfThisLine = lineBreakIndex + 1;
						break;
					}
				}
			}
		}
		return textWithLineBreaks.toString();
	}

	

	void render(GUIContext container, Graphics g) {
//		System.out.println("render textbox (" + this + ") font = " + g.getFont()); //TODO
		float alpha = 0.75f;
		g.setColor(new Color(0f, 0f, 0f, alpha));
		g.fill(rect);
		g.setColor(new Color(1f, 1f, 1f, 1));
		g.drawRoundRect(rect.getX(), rect.getY(), rect.getWidth(),
				rect.getHeight(), 4);		
		g.drawString(text, rect.getX() + 3, rect.getY() + 3);

	}

}
