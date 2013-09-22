package org.AAStudio.games.Soliter;

import android.content.SharedPreferences;

public class Statistic {
	public static final String CUR_WIN="CurrentWin"; 
	public static final String CUR_LOSE="CurrentLose";

	public static final String TOTAL_WIN="CurrentWin"; 
	public static final String TOTAL_LOSE="CurrentLose";

	public static final String IN_ROW_WIN="CurrentWin"; 
	public static final String IN_ROW_LOSE="CurrentLose";

	
	public static int currentWin =0;
	public static int currentLose=0;
	public static int totalWin=0;
	public static int totalLose=0;
	public static int inRowWin=0;
	public static int inRowLose=0;
	public static void addWin(){
		currentWin++;
		totalWin++;
		inRowLose=0;
		inRowWin++;
	}
	public static void addLose(){
		currentLose++;
		totalLose++;
		inRowLose++;
		inRowWin=0;
	}
	public static void load(SharedPreferences prefs) {
		totalWin = prefs.getInt(TOTAL_WIN, 0);
		totalLose = prefs.getInt(TOTAL_LOSE, 0);
		currentWin =0;
		currentLose =0;
		inRowWin = prefs.getInt(IN_ROW_WIN, 0);
		inRowLose = prefs.getInt(IN_ROW_LOSE, 0);
	}
	public static void save(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(TOTAL_WIN, totalWin);
		editor.putInt(TOTAL_LOSE, totalLose);
		
		editor.putInt(IN_ROW_WIN, inRowWin);
		editor.putInt(IN_ROW_LOSE, inRowLose);
		editor.commit();
	}
	public static void reset(){
		currentWin =0;
		currentLose=0;
		totalWin=0;
		totalLose=0;
		inRowWin=0;
		inRowLose=0;
	}

}
