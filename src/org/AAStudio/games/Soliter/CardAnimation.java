package org.AAStudio.games.Soliter;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class CardAnimation {
	public static final int COUNTS_FRAMES =11;
	public static final int CARD_BACK=0;
	public static final int CARD_FACE =1;
	
	public static final int MAIN_KOLODA=-3; 
	public static final int MAIN_TABLE=-2;
	public static final int MAIN_TICKS=-1;
	  
	
	public int startY;
	public int startX;
	public int startFace ;
	
	public int endY;
	public int endX;
	public int endFace ;

	public Card card;
	
	public int direction;
	
	public  void draw(Canvas canvas ,int noFrame){
		if (noFrame==0) return ;
		int x ,y; 
		x = (int) (startX+ (float)((endX-startX)/COUNTS_FRAMES)*noFrame);
		y = (int) (startY+ (float)((endY-startY)/COUNTS_FRAMES)*noFrame);
		
		if (startFace==endFace){ // если ниче не нужно переворачивать 
			if(startFace==CARD_BACK) canvas.drawBitmap(DrawMaster.mBigCardHidden, x, y, null);
			else canvas.drawBitmap(DrawMaster.mCardBitmap[card.suit*13+(card.value-1)], x, y, null);
		}else{// если инужно переворачивать
			float k=0;
			float half  = (float)COUNTS_FRAMES/2;
			if (noFrame<half) k = (float)(half-noFrame)/half;
			else k = (float)(noFrame-half)/half;

			Rect rect1 = new Rect(0,0,Card.WIDTH,Card.HEIGHT);
			x+=(int) (Card.WIDTH*(1-k)/2);
			Rect rect2 = new Rect(x,y,x+(int) (Card.WIDTH*k),(int) (y+Card.HEIGHT));
			if (startFace==CARD_BACK&&noFrame<COUNTS_FRAMES/2||endFace==CARD_BACK&&noFrame>COUNTS_FRAMES/2)
				canvas.drawBitmap(DrawMaster.mBigCardHidden, rect1, rect2, null);
			else {
				canvas.drawBitmap(DrawMaster.mCardBitmap[card.suit*13+(card.value-1)], rect1, rect2, null);
			}
		}
		
	}
}

