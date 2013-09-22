package org.AAStudio.games.Soliter;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AASoliterActivity extends Activity {
	public static boolean LOWQUALITY=false;
	
	public final int IDM_NEW_GAME=0;
	public final int IDM_REGAME=1;
	public final int IDM_STAT=2;
	public final int IDM_HELP=3;	
	public final int IDM_SETTINGS=4;
	public final int IDM_EXIT=5;
	
	public static final String API_KEY="1347081667741312636";
	public static final String API_ID  = "68343";
	
	private GameView gameView;
	LinearLayout layout ;
	private Dialog statDlg;
	private Button btStatReset , btStatOk;
	private TextView tvCurLose , tvCurWin , tvTotalWin , tvTotalLose , tvInRowWin , tvInRowLose;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //�������� ���������
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = (LinearLayout)findViewById(R.id.layout_activity);
        Display display = getWindowManager().getDefaultDisplay();
        
        layout.setBackgroundDrawable(DrawMaster.getBackgroudLayoutSolid(display.getWidth(), display.getHeight()));
        DrawMaster.init(AASoliterActivity.LOWQUALITY,this,true, display.getWidth(), display.getHeight());
        gameView  = (GameView)findViewById(R.id.gameView);

        /// �������
        new RunAd().execute();
        
    }
    public class RunAd extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
	        

			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			gameView.showGameOverDialog(false);	
			super.onPostExecute(result);
		}
    	
    }
	  @Override
	public void onBackPressed() {
		  if (gameView.canUndo()) 
		  {
			  gameView.doUndo();
		  }
		  else
		  {
			//  gameView.simEndGame();
			  showaskforexit();
		  }
	}
	public void showaskforexit(){
		Builder showAsk  = new AlertDialog.Builder(this);
	   	showAsk.setIcon(android.R.drawable.ic_dialog_alert);
	   	showAsk.setMessage(R.string.askfromexit);
	   	showAsk.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
	            	finish();
	            }

	        })
	        .setNegativeButton(R.string.no, null)
	        .show();

	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(Menu.NONE,IDM_NEW_GAME,Menu.NONE,getResources().getString(R.string.newgame));
    	menu.add(Menu.NONE,IDM_REGAME,Menu.NONE,getResources().getString(R.string.regame));
    	menu.add(Menu.NONE,IDM_SETTINGS,Menu.NONE,getResources().getString(R.string.settings));
    	menu.add(Menu.NONE,IDM_STAT,Menu.NONE,getResources().getString(R.string.statistic));
    	menu.add(Menu.NONE,IDM_HELP,Menu.NONE,getResources().getString(R.string.help));
    	menu.add(Menu.NONE,IDM_EXIT,Menu.NONE,getResources().getString(R.string.exit));
    	
    	return(super.onCreateOptionsMenu(menu));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()) {
    	case IDM_NEW_GAME :     		
    		gameView.showGameOverDialog(false);
    		break;
    	case IDM_REGAME :    		
    		gameView.newGame(gameView.number);
    		break;
    	case IDM_STAT :
    		showStat();
    		break;
    	case IDM_HELP :
    		Intent intent = new Intent();
    		intent.setClass(getBaseContext(), RulesActivity.class);
    		startActivity(intent);
    		break;
    	case IDM_SETTINGS :
    		Intent intent2 = new Intent();
    		intent2.setClass(getBaseContext(), PreferencesActivity.class);
    		startActivity(intent2);
    		break;
    	case IDM_EXIT : 
    		//if (gameView.isGameRuning) Statistic.addLose(); 
    		finish(); 
    		break; 
    	}
    	return true;
    }   
    @Override
    public void onResume(){
 	   super.onResume();
	   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	   boolean lowquality = prefs.getBoolean("nowpict", false);
	   if (lowquality!=LOWQUALITY){
		   LOWQUALITY = lowquality;
		   DrawMaster.onChangeQuality();
		   gameView.invalidate();
	   }
	   Statistic.load(prefs);
    }
    @Override
    public void onPause(){
    	super.onPause();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	Statistic.save(prefs);
    	
    }
	public void showStat(){
		if (statDlg==null){
			statDlg = new Dialog(this);
			statDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
			statDlg.setContentView(R.layout.stat_payout);

            Window window = statDlg.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            statDlg.setCancelable(true);
        	btStatReset = (Button)statDlg.findViewById(R.id.btStatReset);
        	btStatOk= (Button)statDlg.findViewById(R.id.btstatok);
        	btStatOk.setOnClickListener(onCancelStat);
        	btStatReset.setOnClickListener(onStatReset);
        	
        	tvCurLose=(TextView)statDlg.findViewById(R.id.tvcurlose);
        	tvCurWin =(TextView)statDlg.findViewById(R.id.tvcurwin);
        	tvTotalWin =(TextView)statDlg.findViewById(R.id.tvtotalwin); 
        	tvTotalLose =(TextView)statDlg.findViewById(R.id.tvtotallose); 
        	tvInRowWin =(TextView)statDlg.findViewById(R.id.tvinrowwin); 
        	tvInRowLose=(TextView)statDlg.findViewById(R.id.tvinrowlose);
		}
		Log.e("Statistic.T" , ""+Statistic.totalWin);
    	tvCurLose.setText(""+Statistic.currentLose);
    	tvCurWin.setText(""+Statistic.currentWin);
    	tvTotalWin.setText(""+Statistic.totalWin);
    	tvTotalLose.setText(""+Statistic.totalLose);
    	tvInRowWin.setText(""+Statistic.inRowWin);
    	tvInRowLose.setText(""+Statistic.inRowLose);

		statDlg.show();
	}
	OnClickListener onStatReset = new OnClickListener() {
		
		public void onClick(View v) {
			statDlg.dismiss();
			showAskResetDialog();
			
		}
	};
	OnClickListener onCancelStat  = new OnClickListener() {
		
		public void onClick(View v) {
			statDlg.dismiss();
		}
	};
	private void showAskResetDialog(){
		Builder showAsk  = new AlertDialog.Builder(this);
	   	showAsk.setIcon(android.R.drawable.ic_dialog_alert);
	   	showAsk.setMessage(R.string.statresetpromt);
	   	showAsk.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
	            	resetstat();
	            }
	        })
	        .setNegativeButton(R.string.no, null)
	        .show();
	}
	private void resetstat(){
    	
    	Statistic.reset();
    	Statistic.save(PreferenceManager.getDefaultSharedPreferences(this));
    	tvCurLose.setText(""+Statistic.currentLose);
    	tvCurWin.setText(""+Statistic.currentWin);
    	tvTotalWin.setText(""+Statistic.totalWin);
    	tvTotalLose.setText(""+Statistic.totalLose);
    	tvInRowWin.setText(""+Statistic.inRowWin);
    	tvInRowLose.setText(""+Statistic.inRowLose);
   	
	}
}