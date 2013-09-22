package org.AAStudio.games.Soliter;



import java.util.ArrayList;
import java.util.Random;
//6264
import android.R.anim;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.util.EventLogTags.Description;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends View {
	
	private Resources res;
	Context mContext;
	Koloda[] tableCards = new Koloda[8];
	Koloda[] resultCards = new Koloda[4];
	Card[] buffer = new Card[4];
	Koloda startKoloda = new Koloda();
	int screenHeight , screenWidth;
	
	public boolean avaibleForTouch=true;
	int freeBuffer=0,freeCells=0;
	CardTimer task;
	FastStack<Move> stackMoves  = new FastStack<Move>();
	public static int number=0;
	private boolean isEndGameAnim = false;
	Card[] dropingCards = new Card[4]; 
	ArrayList<Salut> saluts = new ArrayList<Salut>();
	public boolean isGameRuning=false;
	private Dialog endGameDlg , enterNumberDlg;
	Button btNewGame , btContinie , btExit , btOk  , btCancel , btReload;
	TextView tvendGameTitle;
	CardsDropingAnim animEnd;
	EditText editNumber;
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		for(int i=0;i<8;i++) tableCards[i] = new Koloda();
		for(int i=0;i<resultCards.length;i++) resultCards[i] = new Koloda();
		mContext = context;
		res = getResources();
		Salut.init(getResources());
	}
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		DrawMaster.drawTop(canvas);
		DrawMaster.drawRasklad(canvas);
		DrawMaster.drawDescCards(canvas , tableCards , buffer, resultCards);
		if (DragingKoloda.avaibleForDrag) {
			DrawMaster.drawAvaiblePlaces(canvas , tableCards);
			DrawMaster.onDragCards(canvas , tableCards , buffer , resultCards);
		}
		if (ILikeToMovit.isMoving) ILikeToMovit.draw(canvas);
		DragingKoloda.draw(canvas);
		if (isEndGameAnim){
			for(int i=0;i<4;i++)
				if (dropingCards[i]!=null ) DrawMaster.drawCard(canvas, dropingCards[i]);
	//		SuitsForBooom.draw(canvas);
//			for(int i=0;i<saluts.size();i++) saluts.get(i).draw(canvas);
		}
	}
	
	public void newGame(){
		if (isAnyCards()) Statistic.addLose();
		buffer = new Card[4];
		resultCards = new Koloda[4];
		DragingKoloda.isToSelected = false;
		DragingKoloda.is = false;
		DragingKoloda.isdraging = false;
		
		for (int i=0;i<4;i++) resultCards[i] =new Koloda();
		Random rand = new Random();
		for(int i=0;i<8;i++) tableCards[i] = new Koloda();
		number = rand.nextInt(10000);
		tableCards = Koloda.shuffle(number);
		stackMoves = new FastStack<Move>();
		saluts.clear();
		SuitsForBooom.clear();
		avaibleForTouch = true;
		invalidate();
	}
	public void newGame(int n){
		if (isAnyCards()) Statistic.addLose();
		if (animEnd!=null) animEnd.cancel(true);
		buffer = new Card[4];
		resultCards = new Koloda[4];
		for (int i=0;i<4;i++) resultCards[i] =new Koloda();
		for(int i=0;i<8;i++) tableCards[i] = new Koloda();
		number = n;
		tableCards = Koloda.shuffle(n);
		stackMoves = new FastStack<Move>();
		DragingKoloda.isToSelected = false;
		DragingKoloda.is = false;
		DragingKoloda.isdraging = false;
		saluts.clear();
		SuitsForBooom.clear();
		invalidate();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		//DrawMaster.init(true,getContext(),true, w, h);
		DrawMaster.init(AASoliterActivity.LOWQUALITY,getContext(),true, w, h);
		// TODO Auto-generated method stub
		screenHeight =h;
		screenWidth = w;
		//setBackgroundDrawable(DrawMaster.getBackgroudLayoutSolid(w, h));

		super.onSizeChanged(w, h, oldw, oldh);		
		invalidate();
	}
	public void onQualityChange(boolean q){
		DrawMaster.init(AASoliterActivity.LOWQUALITY,getContext(),true, screenWidth, screenHeight);
		invalidate();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction()==MotionEvent.ACTION_DOWN ){
			if (isEndGameAnim) {
				isEndGameAnim=false;
				Statistic.addWin();
				showGameOverDialog(true);
				return false;
			}
			if (!avaibleForTouch) return false;
			if (DragingKoloda.isToSelected) return true;
			findOnDesc(event.getX(),event.getY());
			findCardonBuffer(event.getX(),event.getY());
			if (DragingKoloda.is&&DragingKoloda.koloda.getCount()>0) {
				DragingKoloda.startx =event.getX(); 
				DragingKoloda.starty =event.getY();
				DragingKoloda.isdraging= false;
				invalidate();

				return true;
			} 
		}
		if (event.getAction()==MotionEvent.ACTION_MOVE ) {
			// если перетянули дальше
			
			if (DragingKoloda.is&&DragingKoloda.avaibleForDrag){//если можно двигать карты
				if (DragingKoloda.checkdraging(event)) DragingKoloda.move( (int)event.getX(), (int)event.getY());
			}
			//else findOnDesc(event.getX(),event.getY());
			invalidate();
			return true;
		}
		if (event.getAction()==MotionEvent.ACTION_UP ) {
			if (DragingKoloda.is&&!DragingKoloda.isToSelected&&!DragingKoloda.avaibleForDrag){
				DragingKoloda.reset();
				invalidate();
			}
				
			if (!DragingKoloda.is&&!DragingKoloda.isToSelected) return false;
			if (DragingKoloda.isdraging||DragingKoloda.isToSelected){
				dropDragingCards(event.getX(),event.getY());
				invalidate();
				DragingKoloda.reset();
				return true;
			}else 	onSelectTo();
			
		}
		return false;
	}
	private void onSelectTo() {
		if (!DragingKoloda.avaibleForDrag) {
			DragingKoloda.reset();
			return; 
		}
		DragingKoloda.isToSelected = true;
		if (DragingKoloda.column<0) return;
		for(int i=tableCards[DragingKoloda.column].getCount()-1;i>=0;i--){
			Card lastDragingCard = DragingKoloda.koloda.getCard(0);
			Card currentCard = tableCards[DragingKoloda.column].getCard(i);
			Log.e("драг карта "+lastDragingCard.suit+"/"+lastDragingCard.value,"наст="+currentCard.suit+"/"+currentCard.value);
			if (currentCard!=null&&lastDragingCard!=null&&
					lastDragingCard.getColor()!=currentCard.getColor()&&
					lastDragingCard.value+1==currentCard.value){
				currentCard.y = lastDragingCard.y-Card.HEIGHT/4;
				DragingKoloda.koloda.addCard(currentCard , 0);
				
				tableCards[DragingKoloda.column].removeCard(currentCard);
				invalidate();
			}else break;
		}
	}
	private void findOnDesc(float x, float y) {
		DragingKoloda.koloda = new Koloda();
		boolean isStreet;
		for(int i=0;i<tableCards.length;i++){
			isStreet=true;
			for(int j=tableCards[i].getCount()-1;j>=0;j--) {
				if (j<tableCards[i].getCount()-1){//проверяем дорожку
					if (tableCards[i].getCard(j).getColor()==tableCards[i].getCard(j+1).getColor()||
							tableCards[i].getCard(j).value!=tableCards[i].getCard(j+1).value+1) isStreet=false; 
						
				}
				if (tableCards[i].getCard(j).isPointOnCard(x, y)){
					if (j==tableCards[i].getCount()-1){//если карта верхняя - пусть чувак ее шатает по экрану
						DragingKoloda.avaibleForDrag=true;
						DragingKoloda.koloda.addCard(tableCards[i].getCard(j));
						tableCards[i].removeCard(j);
						DragingKoloda.column=i;
						DragingKoloda.is =true;
						DragingKoloda.x =  (int) x;
						DragingKoloda.y =  (int) y;

						return ;
						
					}else{// если карта выбрато , но она не верхняя 
						if (isStreet){// если дорожка - тоже пусть катает по екрану
							for(int k=j;k<tableCards[i].getCount();k++) DragingKoloda.koloda.addCard(tableCards[i].getCard(k));
							for(int k=tableCards[i].getCount()-1;k>=j;k--) tableCards[i].removeCard(k);
							DragingKoloda.avaibleForDrag=true;
							DragingKoloda.column=i;
							DragingKoloda.is =true;
							DragingKoloda.x =  (int) x;
							DragingKoloda.y =  (int) y;
							return ;
						}else{// карту нельзя перемещать
							Card card =new Card(tableCards[i].getCard(j).value,tableCards[i].getCard(j).suit);
							card.y =tableCards[i].getCard(j).y- Card.HEIGHT*2/4;
							card.x = tableCards[i].getCard(j).x;
							DragingKoloda.avaibleForDrag=false;
							DragingKoloda.koloda.addCard(card);
							DragingKoloda.column=i;
							DragingKoloda.is =true;
							DragingKoloda.x =  (int) x;
							DragingKoloda.y =  (int) y;
 
							return ;
						}
					
					}
				}
			}
		}
	}
	public void dropDragingCards(float x , float y){		
		if (!DragingKoloda.is||!DragingKoloda.avaibleForDrag) return ;
		isGameRuning =true;
		DragingKoloda.avaibleForDrag=false;
		if (canDropOnTable( x , y)){
			unnecessaryOut();
			return ;
		}
		if (canDropOnBuffer( x , y)){
			unnecessaryOut();
			return ;
		}
		if (canDropOnResult( x , y)){
			unnecessaryOut();
			return ;
		}
		// если мы тут - то тупо возвращаем карты на прежнее место
		DragingKoloda.returnCards(tableCards , buffer);
	}
	/**************************************************************
	 * можно ли положить на стол 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canDropOnTable(float x , float y){
		if (DragingKoloda.koloda==null||DragingKoloda.koloda.getCount()==0) return false;
		
		Card dragingCard =  DragingKoloda.koloda.getCard(0);
		for(int i=0;i<tableCards.length;i++)
			if (i!=DragingKoloda.column){
				if (tableCards[i].getCount()>0){// в колоне есть карты 
					Card card  = tableCards[i].getCard(tableCards[i].getCount()-1);
					if (card.isPointOnCard(x, y))
						for(int r=0;r<DragingKoloda.koloda.getCount();r++){
							dragingCard =  DragingKoloda.koloda.getCard(r);
							if (card.getColor()!=dragingCard.getColor()&&card.value-1==dragingCard.value){
								calcFreeCallsAndBuffers(i);					
								int maxCardsToMove = (int) ((freeBuffer+1)*Math.pow(2, freeCells));
								if (DragingKoloda.koloda.getCount()-r>maxCardsToMove){
									Toast.makeText(mContext, res.getString(R.string.noenoughfree)+maxCardsToMove, 5).show();
									return false;
								}else {
									if (r>0)
										for(int j =0 ;j<r;j++) {
											tableCards[DragingKoloda.column].addCard(DragingKoloda.koloda.getCard(0));
											DragingKoloda.koloda.removeCard(0);
										}
									stackMoves.add(new Move(DragingKoloda.koloda, DragingKoloda.column, i, Move.TABLE));
									if (DragingKoloda.isToSelected) {
										animateMove(i, tableCards[i]);
										return true;
									}
									else
										for(int j =0;j<DragingKoloda.koloda.getCount();j++){
											tableCards[i].addCard(DragingKoloda.koloda.getCard(j));
										}
								
									DragingKoloda.reset();
									DragingKoloda.avaibleForDrag=false;
									return true;
								}
							}
					}
				}else {// в пустую ячейку
					if (DrawMaster.emptyPlace(i).contains((int)x, (int)y)){
						calcFreeCallsAndBuffers(i);
						int maxCardsToMove = (int) ((freeBuffer+1)*Math.pow(2, freeCells));
						if (DragingKoloda.koloda.getCount()>maxCardsToMove){
							Toast.makeText(mContext, res.getString(R.string.noenoughfree)+maxCardsToMove, 5).show();
							return false;
						}else {
							stackMoves.add(new Move(DragingKoloda.koloda, DragingKoloda.column, i, Move.TABLE));
							if (DragingKoloda.isToSelected) {
								animateMove(i, tableCards[i]);
								return true;
							}
							else
								for(int j =0;j<DragingKoloda.koloda.getCount();j++){
									tableCards[i].addCard(DragingKoloda.koloda.getCard(j));
								}
							DragingKoloda.reset();
							return true;
						}
					}
				}
			}
		
		return false;
	}
	/******************************************************************************
	 * Можно ли положить на промежточную яейку
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean canDropOnBuffer(float x , float y){
		
		for(int i=0;i<4;i++)
			if(DrawMaster.bufferRect[i].contains(x, y)&&buffer[i]==null&&i!=-(DragingKoloda.column+1)){
				if (DragingKoloda.koloda.getCount()>1){
					while (DragingKoloda.koloda.getCount()>1){
						tableCards[DragingKoloda.column].addCard(DragingKoloda.koloda.getCard(0));
						DragingKoloda.koloda.removeCard(0);
					}
				}
				stackMoves.add(new Move(DragingKoloda.koloda, DragingKoloda.column, i, Move.BUFFER));
				if (DragingKoloda.isToSelected) {
					animateMove(-(i+1), null);
				}else{
					buffer[i]=DragingKoloda.koloda.getCard(0);
					buffer[i].x =DrawMaster.bufferRect[i].left-2; 
					buffer[i].y =DrawMaster.bufferRect[i].top-2;
					
					DragingKoloda.reset();
				} 
				return true;
			} 
		return false;
	}
	/*************************************************************************************************************************************************
	 * Можно ли положить карту на результирующую ячейку
	 * @param x
	 * @param y
	 * @return
	 **************************************************************************************************************************************************/
	private boolean canDropOnResult(float x , float y){
		boolean f;
		for(int i=0;i<4;i++){
			if(DrawMaster.resultRect[i].contains(x, y)){
				if (DragingKoloda.koloda.getCount()>1){
					while (DragingKoloda.koloda.getCount()>1){
						tableCards[DragingKoloda.column].addCard(DragingKoloda.koloda.getCard(0));
						DragingKoloda.koloda.removeCard(0);
					}
				}
				if (resultCards[i].getCount()==0 )
					f = (DragingKoloda.koloda.getCard(0).value==Card.ACE);
				else
					f = resultCards[i].getCard(resultCards[i].getCount()-1).suit==DragingKoloda.koloda.getCard(0).suit&&
							resultCards[i].getCard(resultCards[i].getCount()-1).value+1==DragingKoloda.koloda.getCard(0).value;
				
				if (f){
					stackMoves.add(new Move(DragingKoloda.koloda, DragingKoloda.column, i, Move.HOME));
					if (DragingKoloda.isToSelected) {
						animateMove(i+8, resultCards[i]);
					}else{
						Card card = DragingKoloda.koloda.getCard(0);
						resultCards[i].addCard(card);
						card.x =DrawMaster.resultRect[i].left-2; 
						card.y =DrawMaster.resultRect[i].top-2;
						DragingKoloda.reset();
					}
					return true;
				}
			}
		}
		return false;
		
	}
	/**********************************************************************************************************************************************************
	 * Можно ли положить карту на промежуточную ячейку
	 * @param x
	 * @param y
	 ********************************************************************************************************************************************************/
	public void findCardonBuffer(float x , float y){
		for(int i=0;i<4;i++)
			if (buffer[i]!=null&&buffer[i].isPointOnCard(x, y)){
				DragingKoloda.avaibleForDrag=true;
				DragingKoloda.koloda.addCard(buffer[i]);
				buffer[i]=null;
				DragingKoloda.column=-(i+1);
				DragingKoloda.is =true;
				DragingKoloda.x =  (int) x;
				DragingKoloda.y =  (int) y;
				return ;
			}
	}
	private int getMinColorValue(int color){
		int min=Card.KING+1;
		for(int i=0;i<tableCards.length;i++)
			for(int j=0;j<tableCards[i].getCount();j++)
				if (tableCards[i].getCard(j).value<min&&tableCards[i].getCard(j).getColor()==color) min =tableCards[i].getCard(j).value; 
			
		for (int i=0;i<4;i++)
			if (buffer[i]!=null)
				if (buffer[i].value<min&&buffer[i].getColor()==color) min =buffer[i].value;
		
		return min;
	}
	/****
	 * 
	 * @return
	 */
	private boolean avaibleForGoHome(Card card){
		int cellNo=-1;
		for(int i=0;i<4;i++)
			if(resultCards[i].getCount()>0&&resultCards[i].getCard(0).suit==card.suit) 
				return resultCards[i].getCard(resultCards[i].getCount()-1).value+1==card.value;
			
		return card.value==Card.ACE;
	}
	/*******************
	 * 
	 * @return
	 */
	private int getMinNoAvaible(){
		int[] min = {getMinColorValue(Card.RED) ,  getMinColorValue(Card.BLACK)};
		
		for(int i=0;i<tableCards.length;i++)
			if (tableCards[i].getCount()>0){
				Card lastCard =tableCards[i].getCard(tableCards[i].getCount()-1); 
				if (lastCard.value<=min[lastCard.getColor()]&&avaibleForGoHome(lastCard)) return i;
			}
		for(int i=0;i<4;i++)
			if (buffer[i]!=null&&buffer[i].value<=min[buffer[i].getColor()]&&avaibleForGoHome(buffer[i])) return -(i+1);
		return -10;
	}
	/******************************
	 * Пробуем выкинуть бесполезные карты
	 */
	private void unnecessaryOut(){ 
		if (ILikeToMovit.isMoving) return;
		int noout = getMinNoAvaible();
		Log.e("unnecessaryOut" , ""+noout);
		if (noout==-10) { 
			if (isEndGame())
				startEndGameEnim();
			else
				avaibleForTouch =true;
			return;
		}
		avaibleForTouch = false;
		CardAnimation anim = new CardAnimation();
		ILikeToMovit.cards.clear();
		if (noout>=0){			
			anim.card = tableCards[noout].getCard(tableCards[noout].getCount()-1);
			tableCards[noout].removeCard(tableCards[noout].getCount()-1);
		}else{
			int i = -(noout+1);
			anim.card = buffer[i];
			buffer[i] = null;
		}
		anim.startX = (int) anim.card.x;
		anim.startY = (int) anim.card.y;
		int no = getNoResCell(anim.card.suit);
		anim.endX = (int) (DrawMaster.resultRect[no].left-2);
		anim.endY = (int) (DrawMaster.resultRect[no].top-2);
		anim.card.x =DrawMaster.resultRect[no].left-2; 
		anim.card.y =DrawMaster.resultRect[no].top-2;

		anim.startFace = CardAnimation.CARD_FACE;
		anim.endFace = CardAnimation.CARD_FACE;
		ILikeToMovit.cards.add(anim);
		ILikeToMovit.toKoloda  = resultCards[no];
		ILikeToMovit.direction = 1;
		
		Koloda tkoloda = new Koloda();
		tkoloda.addCard(anim.card);
		stackMoves.add(new Move(tkoloda, noout, no, Move.HOME));

		
		task = new CardTimer();
		task.execute();
		
	}
	/*************************************************************************************
	 * Возвращает номер домашней ячейки для масти 
	 * @param suit
	 * @return 
	 */
	private int getNoResCell(int suit){
		int emptyCell = -1 ;
		for(int i=0;i<4;i++){
			if(resultCards[i].getCount()>0&&resultCards[i].getCard(0).suit==suit) 	return i;
			if(resultCards[i].getCount()==0&&emptyCell == -1) emptyCell =i; 
		}
		return emptyCell;	
	}
	/**********************************************************************
	 * Асинк Таск для анимации карты
	 * @author Apostroff
	 *
	 */
	public class CardTimer extends AsyncTask<Void, Integer, Void>{
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			ILikeToMovit.isMoving=false;
			ILikeToMovit.noFrame=0;
			
			 
			switch(ILikeToMovit.direction){
				case 1:
					ILikeToMovit.finishMove();
					unnecessaryOut();
					break;
				case -1:

					if (ILikeToMovit.columnsTo>=0){
						for(int i=0;i<ILikeToMovit.cards.size();i++)
							tableCards[ILikeToMovit.columnsTo].addCard(ILikeToMovit.cards.get(i).card);
					}else{
						int i = -(ILikeToMovit.columnsTo+1);
						buffer[i]=ILikeToMovit.cards.get(0).card;
						buffer[i].x = ILikeToMovit.cards.get(0).endX;
						buffer[i].y = ILikeToMovit.cards.get(0).endY;
					}
					avaibleForTouch =true;
					break;
				case 2:
					if (ILikeToMovit.columnsTo>=0){
						ILikeToMovit.finishMove();
						if (ILikeToMovit.columnsTo>=8){
							int no=ILikeToMovit.columnsTo-8;
							resultCards[no].getLastCard().x = DrawMaster.resultRect[no].left-2; 
							resultCards[no].getLastCard().y = DrawMaster.resultRect[no].top-2;
						}
					}else {
						int no = -(ILikeToMovit.columnsTo+1);
						buffer[no] =ILikeToMovit.cards.get(0).card;
						buffer[no].x = DrawMaster.bufferRect[no].left-2; 
						buffer[no].y = DrawMaster.bufferRect[no].top-2;
					}
					unnecessaryOut();
					break;
					
			}
			invalidate();
		}
			
			
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			ILikeToMovit.noFrame=values[0].intValue();
			invalidate();
		}
		@Override
		protected Void doInBackground(Void... params) {
			int progress = 0; 
			int delay = 15;
			long startTime = System.currentTimeMillis(), curTime;
			ILikeToMovit.isMoving=true;
			// если ход - задержка до 
			
			while (!isCancelled()&&progress <= CardAnimation.COUNTS_FRAMES){
				curTime = System.currentTimeMillis();
				if (curTime>(startTime+delay)){
					startTime = curTime;	
					publishProgress(progress);
					progress++;
				}
			}


			return null;
		}
		
	}
	private void calcFreeCallsAndBuffers(int column){
		int freebuffers=0;
		int freeTableCells=0;
		for(int i=0;i<4;i++)  if (buffer[i]==null) freebuffers++;
		for(int i=0;i<tableCards.length;i++){
			if(tableCards[i].getCount()==0&&i!=column) freeTableCells++;
			if(tableCards[i].getCount()==1&&freebuffers>0){
				freeTableCells++;
				freebuffers--;
			}
		}
		this.freeBuffer=freebuffers;
		this.freeCells=freeTableCells;
	}
	public boolean canUndo() {
		// TODO Auto-generated method stub
		return !stackMoves.empty();
	}
	public void doUndo() {
		Log.e("ваще ваще перед undo" , (buffer[0]==null)?"null":""+buffer[0].value);
		if (!avaibleForTouch) return;
		DragingKoloda.returnCards(tableCards, buffer);
		DragingKoloda.reset();
		ILikeToMovit.cards.clear();
		avaibleForTouch=false;
		Move move  = stackMoves.pop();

		Log.e("undo",""+move.posFrom+">"+move.posTo+" карт= "+move.koloda.getCount()+" колода  "+move.placeTo);

		Point pointTo; 
		if (move.posFrom>=0) 
			pointTo  = getCoord(Move.TABLE , move.posFrom);
		else 
			pointTo  = getCoord(Move.BUFFER , -(move.posFrom+1));
		
		for (int i=0;i<move.koloda.getCount();i++){
			CardAnimation anim = new CardAnimation();
			anim.card= move.koloda.getCard(i);
			Log.e("undo" , "карта ="+anim.card.suit+"/"+anim.card.value+" координаты:"+anim.card.x+":"+anim.card.y);
			anim.startX =(int) anim.card.x; 
			anim.startY =(int) anim.card.y;
			anim.endFace=CardAnimation.CARD_FACE;
			
			anim.endX =pointTo.x;
			anim.endY =pointTo.y;

			anim.startFace=CardAnimation.CARD_FACE;
			ILikeToMovit.cards.add(anim);
			switch (move.placeTo){
				case Move.HOME:resultCards[move.posTo].removeCard(anim.card);break;
				case Move.BUFFER:buffer[move.posTo]=null;break;
				case Move.TABLE:tableCards[move.posTo].removeCard(anim.card);break;
			}
			
		}

		ILikeToMovit.direction=-1;
		ILikeToMovit.columnsTo=move.posFrom;
		task = new CardTimer();
		task.execute();
	}
	public void animateMove(int to , Koloda kolodaTo){
		avaibleForTouch=false;
		ILikeToMovit.isMoving = true;
		ILikeToMovit.cards.clear();
		Point pointTo;
		if (to>=0){ 
			if (to<8)
				pointTo  = getCoord(Move.TABLE , to);
			else
				pointTo = getCoord(Move.HOME , to-8);
		}else 
			pointTo  = getCoord(Move.BUFFER , -(to+1));
		
		for (int i=0;i<DragingKoloda.koloda.getCount();i++){
			CardAnimation anim = new CardAnimation();
			anim.card= DragingKoloda.koloda.getCard(i);
			anim.startX =(int) anim.card.x; 
			anim.startY =(int) anim.card.y;
			anim.startFace=CardAnimation.CARD_FACE;
			anim.endFace=CardAnimation.CARD_FACE;
		
			anim.endX =pointTo.x;
			anim.endY =pointTo.y;
			ILikeToMovit.cards.add(anim);
			
		}
		DragingKoloda.koloda.clear();
		ILikeToMovit.direction= 2;
		
		ILikeToMovit.toKoloda = kolodaTo; 
		ILikeToMovit.columnsTo=to;
		DragingKoloda.reset();
		task = new CardTimer();
		task.execute();
	}
	private Point getCoord(int place , int column){
		switch (place){
		case Move.TABLE:
			if (tableCards[column].getCount()==0) 
				return new Point(DrawMaster.emptyPlace(column).left ,DrawMaster.emptyPlace(column).top );
			else {
				Card lastcard = tableCards[column].getCard(tableCards[column].getCount()-1);
				return new Point( (int)lastcard.x , (int) lastcard.y+Card.WIDTH/2 );
			}
		case Move.BUFFER: return new Point((int)DrawMaster.bufferRect[column].left-2,(int)DrawMaster.bufferRect[column].top-2);	
		case Move.HOME :  return new Point((int)DrawMaster.resultRect[column].left-2,(int)DrawMaster.resultRect[column].top-2);
		}
		return null;
	}
	public void simEndGame(){
		resultCards = new Koloda[4];
		for (int i=0;i<4;i++) {
			resultCards[i] =new Koloda();
			for(int j=0;j<13;j++){
				Card card = new Card(j+1,i);
				card.x  = DrawMaster.resultRect[i].left-2;
				card.y  = DrawMaster.resultRect[i].top-2;
				resultCards[i].addCard(card);
				
			}
		}
		stackMoves = new FastStack<Move>();
		invalidate();
		startEndGameEnim();
	}
	public void startEndGameEnim(){
		animEnd  = new CardsDropingAnim();
		animEnd.execute();
		
	}
	public class CardsDropingAnim extends AsyncTask<Void, Integer, Void>{

		int[] ex = new int[4];
		int x ,y ;
		float[] sx  = new float[4], sy= new float[4];
		float ey = screenHeight-Card.HEIGHT;
		float[] endAngle = new float[4];
		float startAngle = (float) (Math.PI/2);
		float[] a =  new float[4];
		@Override
		protected void onPostExecute(Void result) {
			isEndGameAnim = false;
			Statistic.addWin();
			showGameOverDialog(true);
			
			super.onPostExecute(result);
		}		
		@Override
		protected void onProgressUpdate(Integer... values) {
			float[] curAngle = new float[4];
			for(int i=saluts.size()-1;i>=0;i--) 
				if (saluts.get(i).frame>Salut.FRAMES)
					saluts.remove(i);
				else
					saluts.get(i).nextFrameByAngle();
			
			for(int j=0;j<4;j++) {
				
				curAngle[j]  = startAngle+(endAngle[j]-startAngle)*values[0]/frames;
				dropingCards[j].x = (float) (a[j]*Math.cos(curAngle[j])+sx[j]); 
				//dropingCards[j].y = (float) (screenHeight-Card.HEIGHT-(screenHeight-sy[j]-Card.HEIGHT)*Math.sin(curAngle[j]));
				dropingCards[j].y = (float) (screenHeight-(screenHeight-sy[j])*Math.sin(curAngle[j]));
			//	ArrayList<Salut> news = SuitsForBooom.testCollision(dropingCards[j].x, dropingCards[j].y);
			//	for(int i=0;i<news.size();i++) 	saluts.add(news.get(i));
			}
			invalidate();
			super.onProgressUpdate(values);
		}
		public final int frames = 30;
		public int progress;
		@Override
		protected Void doInBackground(Void... params) {
			ex = new int[4];
			isEndGameAnim = true;
			Random rand = new Random();
			int delay = 10;
			long startTime , curTime;
			for(int i=12;i>=0&&!isCancelled();i--){
				for(int j=0;j<4;j++) {
					dropingCards[j] = resultCards[j].getCard(i);
					ex[j] = rand.nextInt((DrawMaster.screenWidth/4-Card.WIDTH))+DrawMaster.screenWidth/4*j;
					sx[j] = dropingCards[j].x;
					sy[j] = dropingCards[j].y;
					resultCards[j].removeCard(i);
					endAngle[j] = (sx[j]>ex[j])?(float)Math.PI:0;
					a[j] = Math.abs(ex[j]-sx[j]);
				}
				progress =0;startTime = System.currentTimeMillis();
				while (!isCancelled()&&progress <= frames){
					curTime = System.currentTimeMillis();
					if (curTime>(startTime+delay)){
						startTime = curTime;	
						publishProgress(progress);
						progress++;
					}
				}

				startTime = System.currentTimeMillis()+delay;
				while (!isCancelled()&&System.currentTimeMillis()< startTime);
				// добавляем на месте карт - масти
			//	for(int j=0;j<4;j++) SuitsForBooom.add(dropingCards[j].suit ,ex[j] );
				
			}
			return null;
		}
	
	}
	public boolean isEndGame(){
		for(int i=0;i<4;i++)
			if (resultCards[i].getCount()!=13) return false;
		return true;
	}
	public void showGameOverDialog(boolean win){
		isGameRuning = !win;
		if (animEnd!=null) animEnd.cancel(true);
		if (endGameDlg == null) {
			endGameDlg = new Dialog(mContext);
			endGameDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			endGameDlg.setContentView(R.layout.endgame_layout);

            Window window = endGameDlg.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            
            tvendGameTitle =(TextView)endGameDlg.findViewById(R.id.tvGameOverTitle);
            
            btNewGame = (Button)endGameDlg.findViewById(R.id.btEndGameNewGame);
            btNewGame.setOnClickListener(onNewGameClick);
            btContinie  = (Button)endGameDlg.findViewById(R.id.btEndGameContinue);
            btContinie.setOnClickListener(onCustom);
            btExit= (Button)endGameDlg.findViewById(R.id.btEndGameExit);  
   		}
		endGameDlg.setCancelable(!win);
        if (win){
        	tvendGameTitle.setText(R.string.win);
            btExit.setOnClickListener(onExit);
            btExit.setText(R.string.exit);
            
        }else{
        	tvendGameTitle.setText(R.string.game);
        	btExit.setOnClickListener(onCancelEndDlg);
        	btExit.setText(R.string.cancel);
        	
        }

		btContinie.setText(res.getString(R.string.customgame));
        endGameDlg.show();
	}
	OnClickListener onNewGameClick = new OnClickListener() {
		public void onClick(View v) {
			endGameDlg.dismiss();
			newGame();
			
		}
	};
	OnClickListener onExit = new OnClickListener() {
		public void onClick(View v) {
			endGameDlg.dismiss();
			((AASoliterActivity)mContext).finish();
		}
	};
	OnClickListener onCustom  = new OnClickListener() {
		public void onClick(View v) {
			endGameDlg.dismiss();
			showEnterDlg();
		}
	};
	OnClickListener onCancelEndDlg = new OnClickListener() {
		public void onClick(View v) {
			endGameDlg.dismiss();
			
		}
	};
	
	private void showEnterDlg() {
		if (enterNumberDlg == null) {
			enterNumberDlg = new Dialog(mContext);
			enterNumberDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			enterNumberDlg.setContentView(R.layout.customgame);

            Window window = enterNumberDlg.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            enterNumberDlg.setCancelable(false);
            
            btOk = (Button)enterNumberDlg.findViewById(R.id.btCustomOk);
            btOk.setOnClickListener(onOk);
            
            btCancel  = (Button)enterNumberDlg.findViewById(R.id.btCustomCancel);
            btCancel.setOnClickListener(onCancel);
            
            editNumber = (EditText)enterNumberDlg.findViewById(R.id.editCustomNumber);
            
            
		}
		enterNumberDlg.show();		
		
	}
	OnClickListener onOk = new OnClickListener() {
		public void onClick(View v) {
			enterNumberDlg.dismiss();
			String strNumber =editNumber.getText().toString();
			try {
			if (strNumber.length()>4)
				newGame(Integer.decode(strNumber.substring(0, 4)));
			else 
				newGame(Integer.decode(strNumber));
			}catch (NumberFormatException e){
				Log.e("ошибка " , "  "+e.toString());
			}
		}
	};
	
	OnClickListener onCancel = new OnClickListener() {
		public void onClick(View v) {
			enterNumberDlg.dismiss();
			
		}
	};
	
	private boolean isAnyCards(){
		for(int i=0;i<4 ;i++) if (buffer[i]!=null) return true;
		for(int i=0;i<tableCards.length;i++) if (tableCards[i].getCount()>0) return true;
		return false;
	}
}
