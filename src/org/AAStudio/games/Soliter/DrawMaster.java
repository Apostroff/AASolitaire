package org.AAStudio.games.Soliter;


import dalvik.system.PotentialDeadlockError;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DrawMaster {
	private static Bitmap bitmapFon;
	private static Resources res;
	public static boolean quality;
	public static Typeface mTypeface;
	public static Bitmap[] bmpTrump  = new Bitmap[4]; 
	public static Bitmap[] mCardBitmap = new Bitmap[52];
	public static Bitmap mCardHidden , mBigCardHidden;
	public static int screenWidth=0;
	public static int screenHeight = 0 ;
	private static Context mContext ;
	public static boolean portrait;
	public static Paint painttextBlack , painttextWhite;
	public static int mainKolodaY , mainKolodaX;
	private static boolean isShadow = true; 
	public static Bitmap[] bmKoloda = new Bitmap[5];
	public static Bitmap shadowBitmap;
	private static Paint erasePaint = new Paint();
	private static Bitmap scoreBitmap;
	private static Bitmap msgBitmap;
	private static Bitmap kingIcon;
	public static RectF[] bufferRect = new RectF[4];
	public static RectF[] resultRect = new RectF[4];
	public static void init(boolean qual , Context context , boolean shadow,int scrWidth ,int scrHeight  ){
		mContext = context;
		screenWidth = scrWidth;
        screenHeight = scrHeight;
        portrait = (scrHeight>scrWidth);
        res = context.getResources();
	
		//quality = qual;
		quality = AASoliterActivity.LOWQUALITY;
		isShadow =shadow;


		kingIcon = BitmapFactory.decodeResource(res, R.drawable.kingsoliter);
		mBigCardHidden = BitmapFactory.decodeResource(res, R.drawable.tuzpika);
		Card.setSize(mBigCardHidden.getWidth(), mBigCardHidden.getHeight());
		mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/dsgoose.ttf");
		//if (!qual)  prepareCards(res);
		if (!quality)  prepareCards(res);
		else prepareCardsLQ(res);
		
        painttextBlack = new Paint();
        painttextBlack.setColor(Color.BLACK);
        painttextBlack.setTextSize(10+Card.WIDTH/3);
        painttextBlack.setAntiAlias(true);
        
        painttextWhite = new Paint();
        painttextWhite.setColor(Color.WHITE);
        painttextWhite.setTextSize(5+Card.WIDTH/4);
        painttextWhite.setAntiAlias(true);
        painttextWhite.setTypeface(mTypeface);
        
        
        erasePaint.setAlpha(0);
        erasePaint.setStyle(Paint.Style.FILL);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        erasePaint.setAntiAlias(true);
        

        prepareShadowBitmap();
        
	}
	public static void onChangeQuality(){
		quality = AASoliterActivity.LOWQUALITY;
		if (!quality)  prepareCards(res);
		else prepareCardsLQ(res);

	}

	private static void prepareShadowBitmap() {
		shadowBitmap = Bitmap.createBitmap(Card.WIDTH+5, Card.HEIGHT+7, Config.ARGB_8888);
		Canvas canva = new Canvas(shadowBitmap);
		Paint paint =new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		canva.drawRect(new Rect(Card.WIDTH/10 , Card.WIDTH/10  ,shadowBitmap.getWidth() , shadowBitmap.getHeight() ), paint);
		//canva.drawColor(Color.BLACK);
		 
		 int[] linColors = { 0xff000000,0x1f000000};
		 float[] linPositions = { 0.90f, 1};
		 LinearGradient vertical = new LinearGradient(0, 0, 0, shadowBitmap.getHeight(), linColors, linPositions, Shader.TileMode.CLAMP);
		 paint.setShader(vertical);
		 paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		 canva.drawRect(0, 0, shadowBitmap.getWidth(), shadowBitmap.getHeight(), paint);
		 
		 LinearGradient gorizontal = new LinearGradient(0, 0, shadowBitmap.getWidth(), 0, linColors, linPositions, Shader.TileMode.CLAMP);
		 paint.setShader(gorizontal);
		 paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		 canva.drawRect(0, 0, shadowBitmap.getWidth(), shadowBitmap.getHeight(), paint);
		 
		 Path path = new Path();
		 int otspup = Card.WIDTH/10;
		 path.moveTo(Card.WIDTH-otspup , 0);
		 path.lineTo(shadowBitmap.getWidth(),0);
		 path.lineTo(shadowBitmap.getWidth(), (float) (shadowBitmap.getWidth()-Card.WIDTH+otspup*1.5));
		 path.lineTo(Card.WIDTH-otspup , 0);
		 path.close();
		 canva.drawPath(path, erasePaint);
		 
		 path = new Path();
		 path.moveTo( 0 , Card.HEIGHT-otspup);
		 path.lineTo(0,shadowBitmap.getHeight());
		 path.lineTo((float) (shadowBitmap.getWidth()-Card.WIDTH+otspup*1.5), shadowBitmap.getHeight());
		 path.lineTo(0 , Card.HEIGHT-otspup);
		 path.close();
		 canva.drawPath(path, erasePaint);
	}
	/*******************************************************************************
     * Заполняет массив карт битмапами для малых екранов
     */
  private static void prepareCardsLQ(Resources r) {
	    Paint cardFrontPaint = new Paint();//кисти для границы и фона 
	    Paint cardBorderPaint = new Paint();
	    cardBorderPaint.setAntiAlias(true);
	    
	    Bitmap[] bigsuits = new Bitmap[4];
	    Canvas canvas;		    
	    Bitmap bitmapQ = Bitmap.createBitmap(Card.WIDTH, Card.HEIGHT, Config.ARGB_8888);//битмап для картинки карты
	    int  i;
	    //значения красных карт
	    bigsuits[0] = BitmapFactory.decodeResource(r, R.drawable.spadesb);
	    bigsuits[1] = BitmapFactory.decodeResource(r, R.drawable.clubsb);
	    bigsuits[2] = BitmapFactory.decodeResource(r, R.drawable.diamondsb);
	    bigsuits[3] = BitmapFactory.decodeResource(r, R.drawable.heartsb);


	    cardBorderPaint.setARGB(255, 0, 0, 0);
	    cardFrontPaint.setARGB(255, 255, 255, 255);
	    cardFrontPaint.setAntiAlias(true);
	    RectF pos = new RectF();

        int roundwidth=Card.WIDTH/10;
        int otstup =(int)(roundwidth*0.3+2); 
        int textValueSize , textY ; 
        Bitmap[] suits = getSuits(r);
        String value;
	    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
	      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
	        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(Card.WIDTH, Card.HEIGHT, Bitmap.Config.ARGB_4444);
	        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
	        pos.set(0, 0, Card.WIDTH, Card.HEIGHT);
	        canvas.drawRoundRect(pos, roundwidth, roundwidth  , cardBorderPaint);
	        pos.set(1, 1, Card.WIDTH-1, Card.HEIGHT-1);
	        canvas.drawRoundRect(pos, roundwidth, roundwidth, cardFrontPaint);
	        switch (valueIdx+2) {		          
	          case Card.JACK:value = r.getString(R.string.jack);break;
	          case Card.QUEEN:value = r.getString(R.string.queen);break;
	          case Card.KING:value = r.getString(R.string.king);;break;
	          case Card.ACE:value = r.getString(R.string.ace);;break;
	          default: 	 value = Integer.toString(valueIdx+2) ;
	        }
	        switch (Card.WIDTH){
		        case 80:textValueSize = 40;textY =Card.WIDTH/3+roundwidth-2; break;
		        case 53:textValueSize = 22;textY =Card.WIDTH/3+roundwidth-2;break;
		        case 35:textValueSize = 15;textY =Card.WIDTH/3+roundwidth-2;break; 	
		        default:textValueSize = 15;textY =Card.WIDTH/3+roundwidth;
	        }
	        
	        //значения карт
	        Paint mTimePaint = new Paint(); 
	        mTimePaint.setTextSize(textValueSize);
	        if (suitIdx > 1)
	        	mTimePaint.setARGB(255, 255, 0, 0);
	        else	
	        	mTimePaint.setARGB(255, 0, 0, 0);
	        mTimePaint.setAntiAlias(true);
	        mTimePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
	        mTimePaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
	        canvas.drawBitmap(bigsuits[suitIdx], Card.WIDTH/4, Card.WIDTH/3+roundwidth, null);
	        
	        canvas.drawText(value, otstup, textY, mTimePaint);
	        canvas.drawText(value, Card.WIDTH-otstup-mTimePaint.measureText(value), Card.HEIGHT-otstup-2, mTimePaint);
	        
	        //масть		        
	        canvas.drawBitmap(suits[suitIdx], Card.WIDTH-suits[suitIdx].getWidth()-otstup,Card.WIDTH/10 , null);
	        canvas.drawBitmap(suits[suitIdx], otstup,Card.HEIGHT-suits[suitIdx].getHeight()-  Card.WIDTH/10 , null);
	        
	      }
	    }
	    
	    for (i=0;i<4;i++) bigsuits[i].recycle();		
	    bitmapQ.recycle();
	  }

	/*******************************************************************************
     * Заполняет массив карт битмапами c картинками
     */
  private static void prepareCards(Resources r) {
	    Paint cardFrontPaint = new Paint();//кисти для границы и фона
	    cardFrontPaint.setARGB(255, 255, 255, 255);
	    cardFrontPaint.setAntiAlias(true);

	    Paint cardBorderPaint = new Paint();
	    cardBorderPaint.setARGB(255, 0, 0, 0);
	    cardBorderPaint.setAntiAlias(true);
	   
	    Bitmap[] revsuits = new Bitmap[4];		
	    Bitmap[] redFont=getGedValues(r);  //значения красных карт 
	    Bitmap[] blackFont = getBlackValues(r); //значения черных карт
	    Canvas canvas;		    
	    Bitmap bitmapQ = Bitmap.createBitmap(Card.WIDTH, Card.HEIGHT, Config.ARGB_8888);//битмап для картинки карты
	    int fontWidth , fontHeight , i;
	    Bitmap[] suits = getSuits(r); 
	    for (i=0;i<4;i++){revsuits[i]=rotate(suits[i],180);}

	    RectF pos = new RectF();

        int roundwidth=Card.WIDTH/10;
        int otstup =(int)(roundwidth*0.3+2); 

	    int suitWidth = suits[0].getWidth();
        int[] suitX = {otstup/2+suitWidth,Card.WIDTH/2-suitWidth/2,Card.WIDTH-suitWidth*2-otstup/2};
        float tret=(Card.HEIGHT-Card.WIDTH/5-suitWidth)/3;
        int[] suitY = {Card.WIDTH/10,(int)(Card.WIDTH/10+tret),(int)(Card.WIDTH/10+tret*2),Card.HEIGHT-Card.WIDTH/10-suitWidth};
        
        int suitMidY = Card.HEIGHT/2 - suitWidth/2;
         
	    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
	      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
	        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(
	        		Card.WIDTH, Card.HEIGHT, Bitmap.Config.ARGB_8888);
	        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
	        pos.set(0, 0, Card.WIDTH, Card.HEIGHT);
	        canvas.drawRoundRect(pos, roundwidth, roundwidth  , cardBorderPaint);
	        pos.set(1, 1, Card.WIDTH-1, Card.HEIGHT-1);
	        canvas.drawRoundRect(pos, roundwidth, roundwidth, cardFrontPaint);
	        switch (valueIdx+1) {		          
	          case 2:		        	  
	            canvas.drawBitmap(suits[suitIdx], suitX[1], suitY[0], null); 
	            canvas.drawBitmap(revsuits[suitIdx], suitX[1], suitY[3], null);
	            break;
	          case 3:
	            canvas.drawBitmap(suits[suitIdx], suitX[1], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[1], suitMidY, null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[1], suitY[3], null);
	            break;
	          case 4:
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitY[0], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[0], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[2], suitY[3], null);
	            break;
	          case 5:
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[1], suitMidY, null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[0], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[2], suitY[3], null);
	            break;
	          case 6:
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitMidY, null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitMidY, null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[0], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[2], suitY[3], null);
	            break;
	          case 7:
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitMidY, null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitMidY, null);
	            canvas.drawBitmap(suits[suitIdx], suitX[1], (suitMidY+suitY[0])/2, null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[0], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[2], suitY[3], null);
	            break;
	          case 8:
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitY[0], null);
	            canvas.drawBitmap(suits[suitIdx], suitX[0], suitMidY, null);
	            canvas.drawBitmap(suits[suitIdx], suitX[2], suitMidY, null);
	            canvas.drawBitmap(suits[suitIdx], suitX[1], (suitMidY+suitY[0])/2, null);
	            canvas.drawBitmap(rotate(suits[suitIdx],180), suitX[0], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[2], suitY[3], null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[1], (suitY[3]+suitMidY)/2, null);
	            break;
	          case 9:
	            for ( i = 0; i < 4; i++) {
	              canvas.drawBitmap(suits[suitIdx], suitX[(i%2)*2], suitY[i/2], null);
	              canvas.drawBitmap(revsuits[suitIdx], suitX[(i%2)*2], suitY[i/2+2], null);
	            }
	            canvas.drawBitmap(suits[suitIdx], suitX[1], suitMidY, null);
	            break;
	          case 10:
	            for ( i = 0; i < 4; i++) {
	              canvas.drawBitmap(suits[suitIdx], suitX[(i%2)*2], suitY[i/2], null);
	              canvas.drawBitmap(revsuits[suitIdx], suitX[(i%2)*2], suitY[i/2+2], null);
	            }
	            canvas.drawBitmap(suits[suitIdx], suitX[1], (suitY[1]+suitY[0])/2, null);
	            canvas.drawBitmap(revsuits[suitIdx], suitX[1], (suitY[3]+suitY[2])/2, null);
	            break;
	          case Card.JACK:
	        	switch (suitIdx) {
	        	case 0: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.valetpika);break;
	        	case 1: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.valettrefa);break;
	        	case 2: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.valetbuba);break;
	        	case 3: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.valetchirva);break;
	        	}
	        	canvas.drawBitmap(bitmapQ,(Card.WIDTH-bitmapQ.getWidth())/2,(Card.HEIGHT-bitmapQ.getHeight())/2,null);  
	            break;
	          case Card.QUEEN:
	        	switch (suitIdx) {
	        	case 0: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.qeenpika);break;
	        	case 1: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.qeentrefa);break;
	        	case 2: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.qeenbuba);break;
	        	case 3: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.qeenchirva);break;
	        	}
	        	canvas.drawBitmap(bitmapQ,(Card.WIDTH-bitmapQ.getWidth())/2,(Card.HEIGHT-bitmapQ.getHeight())/2,null);  
	            break;
	          case Card.KING:
	        	switch (suitIdx) {
	        	case 0: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.kingpika);break;
	        	case 1: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.kingtrefa);break;
	        	case 2: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.kingbuba);break;
	        	case 3: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.kingchirva);break;
	        	}
	        	canvas.drawBitmap(bitmapQ,(Card.WIDTH-bitmapQ.getWidth())/2,(Card.HEIGHT-bitmapQ.getHeight())/2,null);  
	            break;
	          case Card.ACE:
	        	switch (suitIdx) {
	        	case 0: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.tuzpika);break;
	        	case 1: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.tuztrefa);break;
	        	case 2: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.tuzbuba);break;
	        	case 3: bitmapQ = BitmapFactory.decodeResource(r, R.drawable.tuzchirva);break;
	        	}
	        	canvas.drawBitmap(bitmapQ,0,0,null);  
	            break;
	        }
	        
	        //значения карт
	        fontWidth= redFont[valueIdx].getWidth();
	        fontHeight = redFont[valueIdx].getHeight();
	        if (suitIdx > 1) {
	          canvas.drawBitmap(redFont[valueIdx], otstup, otstup, null);
	          canvas.drawBitmap(rotate(redFont[valueIdx],180), Card.WIDTH-fontWidth-otstup, Card.HEIGHT-fontHeight-otstup,
	        		  null);
	        } else {
	          canvas.drawBitmap(blackFont[valueIdx], otstup, otstup, null);
	          canvas.drawBitmap(rotate(blackFont[valueIdx],180), Card.WIDTH-fontWidth-otstup, Card.HEIGHT-fontHeight-otstup,null);
	        }
	        
	        //масть		        
	        canvas.drawBitmap(suits[suitIdx], 2,fontHeight+1 , null);
	        canvas.drawBitmap(revsuits[suitIdx], Card.WIDTH-revsuits[suitIdx].getWidth()-2
	        		, Card.HEIGHT-fontHeight-revsuits[suitIdx].getHeight(), null);
	        if (isShadow) 	mCardBitmap[suitIdx*13+valueIdx] = drawShadersVertical(mCardBitmap[suitIdx*13+valueIdx]);
	        
	      }
	    }
	    for (i=0;i<4;i++) suits[i].recycle();
	    for (i=0;i<4;i++) revsuits[i].recycle();
	    for (i=0;i<13;i++) redFont[i].recycle();		
	    for (i=0;i<13;i++) blackFont[i].recycle();
	  }
    /*******************************************************************************
     * Рисует полутень на карте
     */
	  public static Bitmap drawShadersVertical(Bitmap card){
		  Bitmap newCard = Bitmap.createBitmap(card.getWidth(), card.getHeight(), Config.ARGB_8888);
		  Canvas canvas = new Canvas(newCard);
		  Paint paint = new Paint();
		  paint.setColor(Color.BLACK);
		  paint.setStyle(Style.FILL);
		  
		  
		  Canvas oldcanvas = new Canvas(card);
		  int w = card.getWidth();
		  int h = card.getHeight();
		  canvas.drawRoundRect(new RectF(0,0,w,h), (int )(w/10), (int)(w/10), paint);
		  int[] colors = {0xffffffff,0xffffffff ,  0xa8ffffff};
		  
	      float[] positions = {0,(float) 0.3 ,       1};	
	      Paint erasepaint  = new Paint();
	      
	      LinearGradient vertical = new LinearGradient(0, 0, w, 0, colors, positions, Shader.TileMode.CLAMP);
	
	      erasepaint.setShader(vertical);
	      erasepaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
	
	      oldcanvas.drawRect(0, 0, w, h, erasepaint);
	
	      canvas.drawBitmap(card, 0, 0, null);
	      card.recycle();
	      return newCard;
	}
    /*******************************************************************************
     * Возвращает фон 
     */
	public static Drawable getBackgroudLayout(int screenWidth,int screenHeight ){

        Bitmap backGroundBitmap = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backGroundBitmap) ;
        
        int width = bitmapFon.getWidth()-1;
        int height = bitmapFon.getHeight()-1;
        int i=0;
        while (i<screenWidth){
        	int j=0;
        	while (j<screenHeight){
        		canvas.drawBitmap(bitmapFon, i, j, null);
        		j=j+height;
        	}
        	i=i+width;
        }
        int x =screenWidth*2/3,y=3*screenHeight/5;
        int[] colors = {0xffffffff,  0x1f1f1f1f};
        float radius = (float) Math.sqrt((3*screenHeight/5)*(3*screenHeight/5) + (2*screenWidth/3)*(2*screenWidth/3))+screenWidth/5;
        
        float[] positions = {0,   1};

        Paint paint  = new Paint();
        RadialGradient shader = new RadialGradient(x, y, radius, colors, positions, Shader.TileMode.CLAMP);

        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
        Drawable  drawable = new BitmapDrawable(backGroundBitmap);

         
        return drawable;
	}
	
	private static Bitmap[] getSuits(Resources r){ 
		Bitmap[] suits = new Bitmap[4]; 
	    // Массив мастей
		suits[0] = BitmapFactory.decodeResource(r, R.drawable.spades);
	    suits[1] = BitmapFactory.decodeResource(r, R.drawable.clubs);
	    suits[2] = BitmapFactory.decodeResource(r, R.drawable.diamonds);
	    suits[3] = BitmapFactory.decodeResource(r, R.drawable.hearts);
	    return suits;
	}
	public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b
                    .getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b
                        .getHeight(), m, true);
                if (b != b2) {
                    //b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
	
	private static Bitmap[] getGedValues(Resources r){
		Bitmap[]  redFont = new Bitmap[13];
		redFont[0] = BitmapFactory.decodeResource(r, R.drawable.t);
	    redFont[1] = BitmapFactory.decodeResource(r, R.drawable.i2);
	    redFont[2] = BitmapFactory.decodeResource(r, R.drawable.i3);
	    redFont[3] = BitmapFactory.decodeResource(r, R.drawable.i4);
	    redFont[4] = BitmapFactory.decodeResource(r, R.drawable.i5);
	    redFont[5] = BitmapFactory.decodeResource(r, R.drawable.i6);
	    redFont[6] = BitmapFactory.decodeResource(r, R.drawable.i7);
	    redFont[7] = BitmapFactory.decodeResource(r, R.drawable.i8);
	    redFont[8] = BitmapFactory.decodeResource(r, R.drawable.i9);
	    redFont[9] = BitmapFactory.decodeResource(r, R.drawable.i10);
	    redFont[10] = BitmapFactory.decodeResource(r, R.drawable.v);
	    redFont[11] = BitmapFactory.decodeResource(r, R.drawable.d);
	    redFont[12] = BitmapFactory.decodeResource(r, R.drawable.k);
	    
	    return redFont;
	}
	/// -------------------возвращает массив черных значений---------------------------------- 
	private static Bitmap[] getBlackValues(Resources r){
		Bitmap[]  blackFont = new Bitmap[13];
		blackFont[0] = BitmapFactory.decodeResource(r, R.drawable.bt);
	    blackFont[1] = BitmapFactory.decodeResource(r, R.drawable.b2);
	    blackFont[2] = BitmapFactory.decodeResource(r, R.drawable.b3);
	    blackFont[3] = BitmapFactory.decodeResource(r, R.drawable.b4);
	    blackFont[4] = BitmapFactory.decodeResource(r, R.drawable.b5);
	    blackFont[5] = BitmapFactory.decodeResource(r, R.drawable.b6);
	    blackFont[6] = BitmapFactory.decodeResource(r, R.drawable.b7);
	    blackFont[7] = BitmapFactory.decodeResource(r, R.drawable.b8);
	    blackFont[8] = BitmapFactory.decodeResource(r, R.drawable.b9);
	    blackFont[9] = BitmapFactory.decodeResource(r, R.drawable.b10);
	    blackFont[10] = BitmapFactory.decodeResource(r, R.drawable.bv);
	    blackFont[11] = BitmapFactory.decodeResource(r, R.drawable.bd);
	    blackFont[12] = BitmapFactory.decodeResource(r, R.drawable.bk);
	    return blackFont; 
	}

	  public static void drawCard(Canvas canvas, Card card) {
		  	if (card==null ) return;
		    float x = card.x;
		    float y = card.y;
		    int idx = card.suit*13+(card.value-1);
		    canvas.drawBitmap(mCardBitmap[idx], x, y, null);
		  }

	  public static void drawDescCards(Canvas canvas , Koloda[] descCards , Card[] buffCards , Koloda[] resKolodas){
		  int startY = (int) (Card.HEIGHT*1.2);
		  int partX = screenWidth/8;
		  float deltaY;
		  for (int i = 0;i<8;i++)
			  if (descCards[i].getCount()>0){
				  deltaY=0.25f;
				  //Log.e("Длина колоды "+i,""+descCards[i].getCount());
				  int x = (int) ((i+0.5)*partX - Card.WIDTH/2);
				  int heightOfStack = (int) (((descCards[i].getCount()-1)*deltaY+1)*Card.HEIGHT);
				  // если высота стака больше места - уменьшаем дельту
				  if(heightOfStack>screenHeight-startY)
					  deltaY = ((float)(screenHeight-startY)/Card.HEIGHT-1)/(descCards[i].getCount()-1);

				  for(int j=0;j<descCards[i].getCount();j++){
					  descCards[i].getCard(j).x=x;
					  descCards[i].getCard(j).y=startY+j*deltaY*Card.HEIGHT;
					  canvas.drawBitmap(shadowBitmap,descCards[i].getCard(j).x ,descCards[i].getCard(j).y, null);
					  drawCard(canvas, descCards[i].getCard(j));
				  }
			  }
		  for(int i=0;i<4;i++)
			  if (buffCards[i]!=null){
				  canvas.drawBitmap(shadowBitmap,buffCards[i].x ,buffCards[i].y, null);
				  drawCard(canvas, buffCards[i]);
			  }
		  for(int i=0;i<resKolodas.length;i++)
			  if (resKolodas[i].getCount()>0){
				  Card topCard =resKolodas[i].getCard(resKolodas[i].getCount()-1); 
				  canvas.drawBitmap(shadowBitmap,topCard.x ,topCard.y, null);
				  drawCard(canvas, topCard);
			  }
		  
	  }
	    /*******************************************************************************
	     * Возвращает фон 
	     */
		public static Drawable getBackgroudLayoutSolid(int screenWidth,int screenHeight ){

	        Bitmap backGroundBitmap = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(backGroundBitmap) ;
	        Paint paint  = new Paint();
	        paint.setARGB(255, 93, 185, 78);
	        canvas.drawPaint(paint);
	        
	        int x =screenWidth/2,y=3*screenHeight/5;
	        int[] colors = {0xffffffff,  0x1f1f1f1f};
	        float radius = (float) Math.sqrt((3*screenHeight/5)*(3*screenHeight/5) + (2*screenWidth/3)*(2*screenWidth/3))+screenWidth/5;
	        
	        float[] positions = {0,   1};

	        
	        RadialGradient shader = new RadialGradient(x, y, radius, colors, positions, Shader.TileMode.CLAMP);

	        paint.setShader(shader);
	        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

	        canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
	        Drawable  drawable = new BitmapDrawable(backGroundBitmap);

	         
	        return drawable;
		}
		/**************************************
		 * Рисует 4 промежуточных ячейки и 4 ячейки для результата
		 * @param canvas
		 */
		public static void drawTop(Canvas canvas){
			canvas.drawBitmap(kingIcon, screenWidth/2-kingIcon.getWidth()/2,Card.HEIGHT*6/10-kingIcon.getHeight()/2 , null);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(255, 192, 192, 192);
			paint.setStrokeWidth(3);
			paint.setStyle(Style.STROKE);
			float r =Card.WIDTH/10;
			int endX  = screenWidth/2-kingIcon.getWidth()/2-4;
			int startX  = screenWidth/2+kingIcon.getWidth()/2+4;
			
			
			for(int i=0;i<4;i++){
				bufferRect[i] = new RectF(endX-Card.WIDTH*(i+1)-3*i+2,
						Card.HEIGHT/10+2,
						endX-Card.WIDTH*i-3*i-2,
						Card.HEIGHT*11/10-2);

				canvas.drawRoundRect(bufferRect[i], r, r, paint);

				resultRect[i] = new RectF(startX+Card.WIDTH*(i)+3*i+2,
						Card.HEIGHT/10+2,
						startX+Card.WIDTH*(i+1)+3*i-2,
						Card.HEIGHT*11/10-2);
				canvas.drawRoundRect(resultRect[i], r, r, paint);

			}
		}
		public static void drawAvaiblePlaces(Canvas canvas , Koloda[] cards){
			if (!DragingKoloda.is) return;
			if (DragingKoloda.koloda==null||DragingKoloda.koloda.getCount()==0) return;
			Card dragingCard =  DragingKoloda.koloda.getCard(0);
			for(int i=0;i<cards.length;i++)
				if (i!=DragingKoloda.column&&cards[i].getCount()>0){
					Card card = cards[i].getCard(cards[i].getCount()-1);
					if (card.getColor()!=dragingCard.getColor()&&card.value-1==dragingCard.value&&!card.isPointOnCard(DragingKoloda.x, DragingKoloda.y)) 
						drawOreol(canvas ,card.x , card.y );
						
				}
			
		}
		public static void drawOreol(Canvas canvas, float x, float y) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(150, 255,255,0);
			paint.setStrokeWidth(5);
			paint.setStyle(Style.STROKE);
			RectF rect  = new RectF(x-3,y-3 , x+Card.WIDTH+3 , y+Card.HEIGHT+3);
			canvas.drawRoundRect(rect, rect.width()/10, rect.width()/10, paint);
		}
		public static void drawOreol(Canvas canvas, float sx, float sy , float ey ) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(200, 0,250,154);
			paint.setStrokeWidth(5);
			paint.setStyle(Style.STROKE);
			RectF rect  = new RectF(sx-3,sy-3 , sx+Card.WIDTH+3 , ey+Card.HEIGHT+3);
			canvas.drawRoundRect(rect, rect.width()/10, rect.width()/10, paint);
		}
		
		public static void drawOreolYes(Canvas canvas, float x, float y) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(200, 0,250,154);
			paint.setStrokeWidth(5);
			paint.setStyle(Style.STROKE);
			RectF rect  = new RectF(x-1,y-1 , x+Card.WIDTH+1 , y+Card.HEIGHT+1);
			canvas.drawRoundRect(rect, rect.width()/10, rect.width()/10, paint);
		}
		public static void drawOreolNo(Canvas canvas, float x, float y) {
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setARGB(200, 255,0,0);
			paint.setStrokeWidth(5);
			paint.setStyle(Style.STROKE);
			RectF rect  = new RectF(x-1,y-1 , x+Card.WIDTH+1 , y+Card.HEIGHT+1);
			canvas.drawRoundRect(rect, rect.width()/8, rect.width()/8, paint);
			canvas.drawLine(x, y, x+Card.WIDTH, y+Card.HEIGHT, paint);
		}
		public static void onDragCards(Canvas canvas , Koloda[] cards ,Card[]  buffCards , Koloda[] resultCards ){
			if (!DragingKoloda.is) return;
			if (DragingKoloda.koloda==null||DragingKoloda.koloda.getCount()==0) return;
			Card dragingCard =  DragingKoloda.koloda.getCard(0);
			for(int i=0;i<cards.length;i++)
				if(cards[i].getCount()>0){
					Card card = cards[i].getCard(cards[i].getCount()-1);
					if (card.isPointOnCard(DragingKoloda.x, DragingKoloda.y)&&i!=DragingKoloda.column){
						if(card.getColor()!=dragingCard.getColor()&&card.value-1==dragingCard.value)
							drawOreolYes(canvas, card.x, card.y);
						else
							drawOreolNo(canvas, card.x, card.y);
					}
				}else{
					drawOreolYes(canvas, (int) ((i+0.5)*screenWidth/8 - Card.WIDTH/2), (int) (Card.HEIGHT*1.2));
				}
			// если над промежуточными
			for(int i=0;i<4;i++){
				if (bufferRect[i].contains(DragingKoloda.x, DragingKoloda.y)){
					if (buffCards[i]==null&&DragingKoloda.koloda.getCount()==1)
						drawOreolYes(canvas, bufferRect[i].left-2, bufferRect[i].top-2);
					else
						drawOreolNo(canvas, bufferRect[i].left-2, bufferRect[i].top-2);

				}
			}
			boolean f;
			// если над результами=)
			for(int i=0;i<4;i++){
				if (resultRect[i].contains(DragingKoloda.x, DragingKoloda.y)&&DragingKoloda.koloda.getCount()==1){
					if (resultCards[i].getCount()==0 )
						f = (DragingKoloda.koloda.getCard(0).value==Card.ACE);
					else
						f = resultCards[i].getCard(resultCards[i].getCount()-1).suit==DragingKoloda.koloda.getCard(0).suit&&
								resultCards[i].getCard(resultCards[i].getCount()-1).value+1==DragingKoloda.koloda.getCard(0).value;
					if (f)
						drawOreolYes(canvas, resultRect[i].left-2, resultRect[i].top-2);
					else
						drawOreolNo(canvas, resultRect[i].left-2, resultRect[i].top-2);
				}
			}
			
		}
		public static Rect emptyPlace(int no){
			return new Rect((int) ((no+0.5)*screenWidth/8 - Card.WIDTH/2),(int) (Card.HEIGHT*1.2),
					(int) ((no+0.5)*screenWidth/8 + Card.WIDTH/2),(int) (Card.HEIGHT*2.2));
		}
		public static void drawRasklad(Canvas canvas){
			String toPrint = res.getString(R.string.rasklad)+" "+GameView.number;
			painttextWhite.setColor(Color.BLACK);
			canvas.drawText(toPrint, screenWidth-painttextWhite.measureText(toPrint)-10,screenHeight-8 , painttextWhite);
			painttextWhite.setColor(Color.WHITE);
			canvas.drawText(toPrint, screenWidth-painttextWhite.measureText(toPrint)-8,screenHeight-10 , painttextWhite);
			
		}

}
