package org.AAStudio.games.Soliter;

import java.util.ArrayList;

import android.graphics.Canvas;

public class ILikeToMovit {
	public static boolean isMoving=false;
	public static ArrayList<CardAnimation> cards  = new ArrayList<CardAnimation>();
	public static Koloda toKoloda ;
	public static int  noFrame;
	public static boolean over;
	public static int direction;
	public static int columnsTo;
	public static void draw(Canvas canvas){
		if (noFrame==0) return ;
		for(int i=0;i<cards.size();i++)
			cards.get(i).draw(canvas , noFrame);
	}

	public static void finishMove(){
		for(int i=0;i<cards.size();i++)
			toKoloda.addCard(cards.get(i).card);
		
		cards.clear();
	}
}
