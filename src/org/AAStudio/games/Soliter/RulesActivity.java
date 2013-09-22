package org.AAStudio.games.Soliter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;


public class RulesActivity extends Activity {	
	
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.rules);
	    Context context = getBaseContext();
	    String text = readRawTextFile(context, getResources().getIdentifier("help", "raw", "org.AAStudio.games.Soliter"));

	    WebView  myWebView = (WebView)findViewById(R.id.webview);
	    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	    String summary = "<html><body>" + text + "</body></html>";
	    myWebView.loadData(header+summary, "text/html", "utf-8");
	    
	    
	}
	  public String readRawTextFile(Context ctx, int resId) //читаем текст из raw - аргументы контекст и идентификатор ресурса
	  {
	     InputStream inputStream = ctx.getResources().openRawResource(resId);
	 
	      InputStreamReader inputreader = new InputStreamReader(inputStream);
	      BufferedReader buffreader = new BufferedReader(inputreader);
	       String line;
	       StringBuilder text = new StringBuilder();
	 
	       try {
	        while (( line = buffreader.readLine()) != null) {
	        Log.e("txt",line);
	          text.append(line);
	          text.append("<BR>");	          
	         }
	      } catch (IOException e) {
	        Log.e("rules","read data: ошибка!" + e.getMessage(), e);
	      }
	       return text.toString();
	  }
}
