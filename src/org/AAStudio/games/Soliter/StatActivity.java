package org.AAStudio.games.Soliter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class StatActivity extends Activity {
	TextView lb2pl,lb2pl1 , lb2pl2, lb2perc1 , lb2perc2 ; 
	TextView lb3pl,lb3pl1 , lb3pl2,lb3pl3,lb3perc1 , lb3perc2, lb3perc3 ;
	TextView lb4pl,lb4pl1 , lb4pl2,lb4pl3 , lb4pl4,lb4perc1,lb4perc2,lb4perc3 , lb4perc4 ;
	TextView lbTitle , lbTitle2 , lbTitle3 , lbTitle4; 
	TextView tv1 ,tv2 ,tv3 ,tv4 ,tv5 ,tv6 ,tv7 ,tv8 ,tv9 ,tv10 ,tv11 ,tv12 ; 
	Typeface mTypeface;
	
	int perc21,perc22;
	int perc31,perc32,perc33;
	int perc41,perc42,perc43,perc44;
	Button btReset;
	boolean wasReset = false;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
       	requestWindowFeature(Window.FEATURE_NO_TITLE); 
      //  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/dsgoose.ttf");
        
	}
	OnClickListener onResetClick = new OnClickListener() {
		
		//@Override
		public void onClick(View v) {
			showAskDialog();
		}
	};
	
	private void showAskDialog(){
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
	@Override
	public void onBackPressed() {
		Log.e("onBackPressed"," "+wasReset);
		if (wasReset)
			setResult(RESULT_OK);
		else 	
			setResult(RESULT_CANCELED);
		finish();
	}
	private void resetstat() {
		// TODO Auto-generated method stub
		
	}
}
