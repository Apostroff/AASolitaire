package org.AAStudio.games.Soliter;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class SuitsForBooom {
	public static ArrayList<Rect> rects  = new ArrayList<Rect>();
	public static ArrayList<Integer> suits = new ArrayList<Integer>();
	public static void add(int suit,float x ){

		suits.add(suit);
		int left = (int) (x+Card.WIDTH/2-Salut.bigsuits[suit].getWidth()/2);
		int right = left+Salut.bigsuits[suit].getWidth();
		rects.add(new Rect(left,DrawMaster.screenHeight-Salut.bigsuits[suit].getHeight(),right,DrawMaster.screenHeight));
	}
	public static void clear(){
		rects.clear();
		suits.clear();
	}
	public static void draw(Canvas canvas){
		for (int i=0;i<rects.size();i++)
			canvas.drawBitmap(Salut.bigsuits[suits.get(i)], rects.get(i).left, rects.get(i).top, null);
	}
	/*********************
	 * передаем левый верхний угол карты, проверяем на попадание нижним
	 * @param x
	 * @param y
	 */
	public static ArrayList<Salut> testCollision(float x , float y){
		 ArrayList<Salut> res = new ArrayList<Salut>();
		 int rightX = (int)x+Card.WIDTH;
		 int bottomY  = (int)y+Card.HEIGHT;
		 int centerX  = (int)x+Card.WIDTH/2;
		 Random rand  = new Random();
		 for (int i=rects.size()-1;i>=0;i--)
			 if (rects.get(i).contains((int) x, bottomY)||
					 rects.get(i).contains(rightX, bottomY)||
					 rects.get(i).contains(centerX, bottomY)){
				 int count = rand.nextInt(20)+10;
				 for (int j=0;j<count;j++){
					 double cAngle = Math.PI/count*(j+0.5);
					 res.add(new Salut(rects.get(i).centerX(),rects.get(i).centerY(),cAngle,suits.get(i),50));
				 }
				 
				 rects.remove(i);

			 }
		 return res;
	}
}
