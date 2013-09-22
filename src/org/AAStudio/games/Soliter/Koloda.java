package org.AAStudio.games.Soliter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;


public class Koloda {	

	
	public static final int CLUB	=0; 
	public static final int DIAMOND	=1;
	public static final int HEART	=2;
	public static final int SPADE	=3;
	
	public static final int MAXPOS	=21;
	public static final int MAXCOL	=9;
	public static final int EMPTY =-1;
	
	private ArrayList<Card> cards;
	private int count;
	public boolean beat; 
	
	public Rect rect = new Rect(0,0,0,0);
	public Koloda(){				
		cards = new ArrayList<Card>();
	} 
	public Koloda(int minvalue)	{
		int  value , suit ;
		suit = 0; value =minvalue-1;
		count = (14-minvalue+1)*4;
		cards = new ArrayList<Card>();
		for ( int i=0;i<count;i++)
		{
				value++;
				if (value>14) {value=minvalue;suit++;}	
				if (suit>3){suit=0;}
				cards.add( new Card(value,suit));				
		}
	}
	public boolean removeCard(int value,int suite){
		for(int i=0;i<cards.size();i++)	{
			if((cards.get(i).value==value)&&(cards.get(i).suit==suite) ) 
				{
					cards.remove(i);
					return true; 
				}
		}
		return false;
	}
	public boolean removeCard(Card card){
		for(int i=0;i<cards.size();i++)	{
			if((cards.get(i).value==card.value)&&(cards.get(i).suit==card.suit) ) {
					cards.remove(i);
					return true; 
				}
		}
		return false;
	}
	
	public void removeCard(int no){ cards.remove(no); }
	public void sort()	{		Collections.sort(cards);	}
	/**
	 * Возвращает рандомнуй карту и удаляет ее из колоды
	 * @return Card  
	 */
	public Card getRandomCard() {
		if (cards.size()==0) return null;
		java.util.Random rand = new java.util.Random();
		int no=rand.nextInt(cards.size());
		Card card =cards.get(no);
		cards.remove(no);
		return card;
	}
	public void addCard(Card card) 	{ 		cards.add(card);	}
	public void addCard(Card card , int pos) 	{ 		cards.add(pos,card );	}
	public int getCount() {return cards.size();}
	
	public void placeKoloda() {
		Card card;
		// если карта одна в колоде
		if (cards.size()==0) 
			return;
		else if(cards.size()!=0){

			float step = rect.width()/cards.size();
			for(int i=0;i<cards.size();i++) {
				card = cards.get(i);
				card.setPosition((int)(rect.left+step*(i+0.5)-Card.WIDTH/2), rect.top);		
			}
		}
	}
	public Card findCardbyPos(float x,float y) {
		
		for(int i=cards.size()-1;i>=0;i--) {			
			if (cards.get(i).isPointOnCard(x, y)) return cards.get(i); 
		}
		return null;
		
	}
	public void drawKoloda(Canvas canvas)	{
		for(int i=0;i<cards.size();i++) {
			canvas.drawBitmap(DrawMaster.shadowBitmap ,	cards.get(i).x,cards.get(i).y , null );
			canvas.drawBitmap(DrawMaster.mCardBitmap[cards.get(i).suit*13+(cards.get(i).value-2)],	cards.get(i).x,cards.get(i).y , null); 
		}
			
	}
	public Card getCard(int no){
		if (no<cards.size())
			return cards.get(no);
		else 
			return null;		
	}
	/**
	 * Возвращает минимальную карту по заданной масти , если масти нет - возвращает null
	 * @param suite int
	 * @return Card
	 */
	public Card getMinCardBySuite(int suite){
		int  i=0,j , mino;
		Card min;		
		while (i<cards.size()&&(cards.get(i).suit!=suite)) {			
			i++;
		}
		if (i==cards.size()) return null;
		
		min = cards.get(i);mino=i;
		for (j=i+1;j<cards.size();j++)		
			if ((cards.get(j).value< min.value)&&(cards.get(j).suit==suite))
				{min = cards.get(j);mino=j; }
		return min;
	}
	
	public void clear()	{
		cards.clear();
	}
	public boolean contains(Card card){
		for (int i=0;i<cards.size();i++)
			if (cards.get(i).value==card.value) return true;			
		return false;
	}
	/**
	 * Задает границы расположения колоды на екране 
	 * @param startX
	 * @param endX
	 * @param y
	 * @param fisionType
	 */
	public void setRect(int startX , int endX , int y ){
		rect = new Rect(startX ,y , endX , y );
		placeKoloda();
	}
	/***
	 * Возвращает координаты на следующей карты 
	 * @return Point
	 */
	public  Point getPointForNextCard(){
		if (rect==null) return null;
		if (rect.width()==0) return new Point(rect.left , rect.top);
		float step = rect.width()/4;
		return new Point( (int) (rect.left+step*(cards.size()+0.5)-Card.WIDTH/2)  , rect.top);
		
	}
	/**************************************
	 * Возвращает позицию карты в колоде
	 * @param card
	 * @return int позиция
	 */
	public int findPosByCard(Card card){
		for(int i=0;i<cards.size();i++)
			if (cards.get(i).suit==card.suit&&cards.get(i).value==card.value) return i;
		return -1;
	}
	/***
	 * Возвращает координаты заданной по номеру карты
	 * @return Point
	 */
	public  Point getPointForCard2(int no){
		if (rect==null) return null;
		if (rect.width()==0) return new Point(rect.left , rect.top);
		float step = rect.width()/4;
		return new Point( (int) (rect.left+step*(no+0.5)-Card.WIDTH/2)  , rect.top);
		
	}
	public Koloda clone(){
		Koloda t=new Koloda();
		for(int i=0;i<cards.size();i++){
			t.addCard(cards.get(i));
			
		}
		return t;
	}
	public void copy(Koloda t){
		cards.clear();
		for(int i=0;i<t.getCount();i++)
			cards.add(t.getCard(i));
			
	}
	/************************************
	 * Возвращает карту больше чем указанная , но той же масти
	 * @param card
	 * @return Card
	 */
	public Card getMinCardBySuite(Card card)
	{

		if (cards.size()==0) return null;
		int  i=0,j,mino;
		Card min=null;
		
		while (i<cards.size()&&((cards.get(i).suit!=card.suit)||(cards.get(i).getPower()<card.getPower()))){			
			i++;
		}
		if (i==cards.size()) return null;
		
		min = cards.get(i);mino=i;
		for (j=i+1;j<cards.size();j++)		
			if ((cards.get(j).getPower()< min.getPower())&&(cards.get(j).suit==card.suit)&&(cards.get(j).getPower()>card.getPower()))
				{min = cards.get(j);mino=j;}
		cards.remove(mino);
		if (min==null)
			Log.e("Пробуем бить карту "+card.suit+"-"+card.value,"нипалучилось");
		else
			Log.e("Пробуем бить карту "+card.suit+"-"+card.value,""+min.suit+"-"+min.value);
		return min;
	}
	public Card getMinCard(Card card){
		if (cards.size()==0) return null;
		int  i=0,j,mino;
		Card min=null;
		// пробуем побить той же мастью
		while (i<cards.size()&&((cards.get(i).suit!=card.suit)||(cards.get(i).getWeight()<card.getWeight()))) 	i++;
		if (i<cards.size()) {
			min = cards.get(i);mino=i;
			for (j=i+1;j<cards.size();j++)		
				if ((cards.get(j).getPower()< min.getPower())&&(cards.get(j).suit==card.suit)&&(cards.get(j).getPower()>card.getPower()))
					{min = cards.get(j);mino=j;}
			cards.remove(mino);
			return min;
		}
		// если мы тут значит по масти ниче нет 
		if (card.suit==Card.TRUMP) return null;
		for (i=0;i<cards.size();i++){
			if(cards.get(i).suit==Card.TRUMP){
				if (min==null)
					min =cards.get(i);
				else {
					if (min.getPower()>cards.get(i).getPower()) 
						min =cards.get(i);
				}
				
			}
		}
		if (min!=null) removeCard(min);
		return min;
	}
	@Override
	public String toString(){
		if (cards.size()==0) return "";
		StringBuilder tstr = new StringBuilder();
		tstr.append(""+cards.get(0).suit+cards.get(0).value);
		
		for (int i=1;i<cards.size();i++)
			tstr.append(","+cards.get(i).suit+cards.get(i).value);
		return tstr.toString();
	}
	public Point getCenter(){
		return new Point(rect.centerX() , rect.top);
	}
	public Card getMinCard() {
		if (cards.size()==0) return null;
		Card min=cards.get(0);
		for (int i=1;i<cards.size();i++){
			if (min.getStrength()>cards.get(i).getStrength())
					min = cards.get(i);
		}

		return min;
	}
	public Point getPointForCard(int no, int selectedCount) {
		if (rect==null) return null;
		if (rect.width()==0) return new Point(rect.left , rect.top);
		
		float step = rect.width()/selectedCount;
		return new Point( (int) (rect.left+step*(no+0.5)-Card.WIDTH/2)  , rect.top);		
		
	}
	public int getPoints(){
		int sum=0;
		for(int i=0;i<cards.size();i++) sum+= cards.get(i).getPoints();
		return sum; 
	}
	public Koloda getTreshKoloda() {
		// треш карды - те которые стоят 0 и она одна по масти
		int[] suits = new int[4];
		for(int i=0;i<cards.size();i++) suits[cards.get(i).suit]++;
		Koloda tKoloda = new Koloda();
		for(int i=0;i<cards.size();i++)
			if (cards.get(i).suit!=Card.TRUMP&&cards.get(i).getPoints()<3&&suits[cards.get(i).suit]<2) tKoloda.addCard(cards.get(i));
		
		return tKoloda;
	}
	/*****************************************************
	 * Комбинация бура ли?(Комбинация из 4 карт козырей)
	 * @return boolean
	 */
	public boolean isBura(){
		if (cards.size()!=4) return false;
		for (int i=0;i<cards.size();i++) 
			if (cards.get(i).suit!=Card.TRUMP) return false;
		return true;
	}
	/**********************************************************
	 * Комбинация молодка  ли?(Комбинация из 4 карт одной масти)
	 * @return boolean
	 */
	public boolean isMolodka(){
		if (cards.size()!=4) return false;
		int suit = cards.get(0).suit;
		if (suit==Card.TRUMP) return false;
		for (int i=1;i<cards.size();i++) 
			if (cards.get(i).suit!=suit) return false;
		return true;
	}
	/*********************************************************
	 * Комбинация москва ли?(Комбинация из 3 тузов, включая козырной)
	 * @return
	 */
	public boolean isMoscow(){
		if (cards.size()!=4) return false;
		// ищем козырный туз
		boolean isAceTrump = false;
		for (int i=0;i<cards.size();i++)
			if (cards.get(i).suit==Card.TRUMP&&cards.get(i).value==Card.ACE) {
				isAceTrump = true;
				break;
			}
		// если не нашли - это не москва
		if (!isAceTrump) return false;
		int aces = 0;
		for (int i=0;i<cards.size();i++) if (cards.get(i).value==Card.ACE) aces++;
		return (aces==3) ;
		
	}
	/****************************************************************
	 * является ли Комбинация из 4 десяток или тузов, включая козырной туз или десятку 
	 * @return boolean
	 */
	public boolean isFourEnds(){
		// проверяем на десятки
		if (cards.size()!=4) return false;
		boolean isfourtens=true;
		for(int i=0;i<cards.size();i++) if (cards.get(i).value!=10) {isfourtens = false;break;}
		
		boolean isfouraces=true;
		for(int i=0;i<cards.size();i++) if (cards.get(i).value!=Card.ACE) {isfouraces = false;break;}
		
		
		return (isfouraces||isfourtens);
	}
	/************
	 * 										мешает колоду
	 */
	
	//SUIT(card)      ((card) % 4)
	//VALUE(card)     ((card) / 4)
	public static Koloda[] shuffle (int gamenumber){
	    int  i, j;                // generic counters
	    int  col, pos;
	    int  wLeft = 52;          // cards left to be chosen in shuffle
	    int deck[] = new int[52];            // deck of 52 unique cards
	    int[][] card = new int[9][21];
	    for (col = 0; col < MAXCOL; col++)          // clear the deck
	        for (pos = 0; pos < MAXPOS; pos++)
	            	card[col][pos] = EMPTY;
	    /* shuffle cards */
	    for (i = 0; i < 52; i++)      // put unique card in each deck loc.
	        deck[i] = i;
	    Random rand  = new Random(gamenumber);
	    Koloda[] result = new Koloda[8];
	    for(i=0;i<8;i++) result[i] = new Koloda();
	    for (i = 0; i < 52; i++) {
	        j = (int) Math.abs(rand.nextInt()% wLeft);
	        
	        result[i%8].addCard(new Card(deck[j]));
	        card[(i%8)+1][i/8] = deck[j];
	        
	        deck[j] = deck[--wLeft];
	    }
	    
	    
	    return result;
	}
	public Card getLastCard(){
		return getCard(cards.size()-1);
	}
}
