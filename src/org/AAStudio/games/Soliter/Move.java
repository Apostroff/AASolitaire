package org.AAStudio.games.Soliter;

import android.util.Log;

public class Move {
	public static final int TABLE=0;
	public static final int BUFFER=1;
	public static final int HOME=2;
	public int placeTo;
	public Koloda koloda;
	public int posFrom;
	public int posTo;
	public Move(Koloda tkoloda,int from,int to,int placement){
		this.koloda= new Koloda();
		for(int i=0;i<tkoloda.getCount();i++)	this.koloda.addCard(tkoloda.getCard(i));
		posFrom = from;
		posTo=to;
		placeTo = placement;
		Log.e("LOAG_ADD" , "карт="+tkoloda.getCount()+" from "+from+" to "+to+" куда "+placement);
	}
}
