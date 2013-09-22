package org.AAStudio.games.Soliter;

class Card implements Comparable<Card> {
	public static final int PIKA = 0;// пики
	public static final int TREFA = 1;//трефы
	public static final int BUBA = 2;//бубны
	public static final int SHIRVA = 3;//червы


	public static final int BLACK = 0;
	public static final int RED = 1;
	
	public static final int ACE = 1;
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;
	public static final String TEXT[] = {"2","3","4","5","6", "7", "8", "9", "10", "J", "Q", "K", "A" };
	public static int WIDTH = 80;
	public static int HEIGHT = 116;
	public static int TRUMP=0;
	public int value;
	public int suit;
	public float x=0;
	public float y=0;

	public static void setTrump(int trump )  {TRUMP=trump; }
	public static void setSize(int width,int height) {
		  WIDTH = width;
		  HEIGHT = height;		  
	}
	public Card(int ivalue, int isuit) {
	    value = ivalue;
	    suit =isuit;
	}

	public Card(int inumber) {
		int mSuit  = inumber%4;
	    value = inumber / 4+1;
	    switch(mSuit){
		    case Koloda.CLUB : suit = TREFA;  break;
		    case Koloda.DIAMOND :suit = BUBA; break;
		    case Koloda.HEART : suit = SHIRVA;break;
		    case Koloda.SPADE : suit = PIKA;break;
	    }
	    
	}
	public int getPoints(){
		switch (value){
			case ACE: return 11;
			case 10: return 10;
			case KING: return 4;
			case QUEEN: return 3;
			case JACK: return 2;
			default :return 0;
		}
	}
	public int getPower(){
		switch (value){
			case ACE: return 14;
			case 10: return 13;
			case KING: return 12;
			case QUEEN: return 11;
			case JACK: return 10;
			default :return value;
		}
	}
	public void setPosition(int nx, int ny){
		x = nx;
		y=ny;
	}
	
	  public int getWeight()
	  {
		  if (this.suit==Card.TRUMP)
		  	  return ((suit+1)*100+getPower());
		   else
			  return ((suit+1)*10000+getPower());
	  }
	  //@Override
	   public int compareTo(Card card) {
	        return (this.getWeight() -card.getWeight()) ;
	    }
	  public boolean isPointOnCard(float tx,float ty)
	  {
		if ((x<=tx)&&(y<=ty)&&(x+WIDTH>=tx)&&(y+HEIGHT>=ty))
			return true;
		else
			return false;
		
	  }
	  /**************************************************
	   * Возвращает стоимость карты , умноженую на 10 в случаи если это козырь
	   * @return
	   */
	  public int getStrength(){
		  if (suit==TRUMP)
			  return getPower()*10;
		  else 
			  return getPower();
	  }
	  public boolean equals(Card card){
		  return (this.value==card.value&&this.suit==card.suit);
	  }
	  public int getColor(){
		  if (suit ==PIKA||suit==TREFA )
			  return BLACK;
		  else 
			  return RED;
			  
	  }
}




	 