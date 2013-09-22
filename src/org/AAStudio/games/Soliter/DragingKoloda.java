package org.AAStudio.games.Soliter;

import android.graphics.Canvas;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.MotionEvent;

public class DragingKoloda {
	private static final int RANGE_DRAG = 20;
	public static Koloda koloda;
	public static int column;
	public static boolean is = false;
	public static int x;
	public static int y;
	public static boolean isdraging;
	public static boolean avaibleForDrag;
	public static float startx;
	public static float starty;
	public static boolean isToSelected;
	public static void draw(Canvas canvas){
		if (!is) return;

		for(int j=0;j<koloda.getCount();j++){
			canvas.drawBitmap(DrawMaster.shadowBitmap,koloda.getCard(j).x ,koloda.getCard(j).y, null);
			DrawMaster.drawCard(canvas, koloda.getCard(j));
		}
		// если не перетаскиваем - подсвечиваем последнюю карту
		if (!isdraging&&koloda.getCount()>0&&DragingKoloda.avaibleForDrag) {
			Card cardLast = koloda.getCard(koloda.getCount()-1);
			Card cardFirst = koloda.getCard(0);
			DrawMaster.drawOreol(canvas,cardFirst.x , cardFirst.y ,cardLast.y );
		}
	}
	public static void move(int newX , int newY){
		int deltaX =newX-x, deltaY = newY-y;
		x =  newX;
		y = newY;
		for(int i=0;i<koloda.getCount();i++){
			koloda.getCard(i).x+=deltaX;
			koloda.getCard(i).y+=deltaY;
		}
			
	}
	public static void returnCards(Koloda[] desckolodas , Card[] bufferCards){
		if (koloda.getCount()>0){
			if (column>=0)
				for (int i=0;i<koloda.getCount();i++)
					desckolodas[column].addCard(koloda.getCard(i));
			else {
				int i = -(column+1);
				bufferCards[i] = koloda.getCard(0);
				if (bufferCards[i]!=null){ 
					bufferCards[i].x =DrawMaster.bufferRect[i].left-2; 
					bufferCards[i].y =DrawMaster.bufferRect[i].top-2;
				}
			}
		}
		is = false;
		koloda.clear();
	}
	public static boolean  checkdraging(MotionEvent event){
		if (isdraging) return true;
		if (isToSelected)return false;
		float deltaX = (event.getX()-startx);
		float deltaY = (event.getY()-starty);
		float r = (float) Math.sqrt(deltaX*deltaX+deltaY*deltaY );
		isdraging  = (r>RANGE_DRAG);
		return isdraging; 
	}
	public static void reset(){
		is = false;
		isdraging=false;
		isToSelected=false;
		avaibleForDrag = false;
		koloda.clear();
	}
}
