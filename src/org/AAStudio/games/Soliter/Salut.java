package org.AAStudio.games.Soliter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Salut {
	public static Bitmap[] bigsuits = new Bitmap[4];
	public static Bitmap[] smallsuits = new Bitmap[4];
	public static final int FRAMES=21;
	public static final int SPEED=30;
	private static Resources res;
	public boolean isBig;
	public float x ;
	public float y;
	public float ex ;
	public float ey;
	public float sx ;
	public float sy;
	public int frame;
	public int suit;
	public double angle;
	public int speed;
	public Salut(float startX , float startY , float endX , float endY , int s , boolean big){
		ex = endX;
		ey = endY;
		sx = startX;
		sy = startY;
		x = startX;
		y = startY;
		frame=0; 
		suit = s;
		isBig = big;
	}
	public Salut(float startX , float startY , double a, int s , int speedByFrame ){
		angle=a;
		sx = startX;
		sy = startY;
		x = startX;
		y = startY;
		frame=0; 
		suit = s;
		isBig = false;
		speed = speedByFrame;
	}
	public void nextFrame(){
		frame++;
		x = (int) (sx+ (float)((ex-sx)/FRAMES)*frame);
		y = (int) (sy+ (float)((ey-sy)/FRAMES)*frame);
	}
	public void nextFrameByAngle(){
		frame++;
		x = (float) (sx+SPEED*frame*Math.cos(angle));
		y = (float) (sy-SPEED*frame*Math.sin(angle));
	}
	
	
	public static void init(Resources r){
		res = r;
		
		smallsuits[Card.SHIRVA] = BitmapFactory.decodeResource(r, R.drawable.exp_chirva);
		smallsuits[Card.TREFA]	= BitmapFactory.decodeResource(r, R.drawable.exp_bigtrefa);
		smallsuits[Card.BUBA]	= BitmapFactory.decodeResource(r, R.drawable.exp_bigbuba);
		smallsuits[Card.PIKA]	= BitmapFactory.decodeResource(r, R.drawable.exp_bigpika);
	}
	public void draw(Canvas canvas){
		if (frame<=FRAMES) canvas.drawBitmap(smallsuits[suit], x, y, null);
	}
	
	
}
