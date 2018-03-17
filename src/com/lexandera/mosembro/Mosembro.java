package com.lexandera.mosembro;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lexandera.mosembro.dialogs.GoToDialog;
import com.lexandera.mosembro.dialogs.ManageActionsDialog;
import com.lexandera.mosembro.dialogs.SettingsDialog;
import com.lexandera.mosembro.dialogs.SiteSearchDialog;
import com.lexandera.mosembro.dialogs.SmartActionsDialog;
import com.lexandera.mosembro.jsinterfaces.ActionInterface;
import com.lexandera.mosembro.jsinterfaces.SiteSearchInterface;
import com.lexandera.mosembro.util.Reader;
import com.lexandera.raceg.R;

/**
 * Mosembro - Mobile semantic browser
 * 
 * The main parts are:
 * - JS interfaces which are used by injected JS code to pass data to the browser
 *   (each registered interface is then available to web pages as window.InterfaceName)
 * - JS scripts which are injected into loaded pages
 * - SmartActions which execute third party intents
 * 
 * A quick explanation of how it works:
 * 1. JS interfaces are registered in onCreate
 * 2. loadWebPage(...) is called at the end of onCreate
 * 3. When a page finishes loading, WebViewClient.onPageFinished() is called. 
 *    At this point JS files located in /res/raw/ are loaded and injected into the web page.
 * 4. JS code extracts microformats and passes the data to the browser using registered JS interfaces
 * 5. Interfaces create SmartActions which can then be executed by clicking on "smart links" (if enabled)
 *    or by going to "Menu > Smart actions"
 * */
public class Mosembro extends Activity implements SensorEventListener{
	
	
	
	private float mLastX, mLastY, mLastZ;
	private boolean mInitialized;

	private SensorManager mSensorManager;
	Timer myTimer;
	private Sensor mAccelerometer;

	private final float NOISE = (float) 2.0;
	
	Button button1;
	Button button2;
	 MediaPlayer MyAppTesting;
	 Calendar c8888;
    private WebView wv;
    private static final String PREFS_NAME = "smartBrowserPrefs";
    private boolean canSiteSearch = false;
    private HashMap<String, String> siteSearchConfig;
    private ArrayList<SmartAction> smartActions = new ArrayList<SmartAction>(10);
    private HashMap<String, ArrayList<SmartAction>> smartActionGroups = new HashMap<String, ArrayList<SmartAction>>(10);
    private ActionStore actionStore;
    private MenuItem searchMenuItem;
//    private MenuItem microformatsMenuItem;
    private boolean enableContentRewriting;
    private String lastEnteredURL = "";
    private String secretScriptKey = generateSecretScriptKey();
    
    static final int MENU_GO_TO = 1;
    static final int MENU_RELOAD = 2;
    static final int MENU_SITE_SEARCH = 3;
    static final int MENU_SMART_ACTIONS = 4;
    static final int MENU_SETTINGS = 5;
    static final int MENU_MANAGE_SCRIPTS = 6;
    public static int general_width = 0 ;
    public static int general_eigth = 0 ;
    
    public void runafter() 
    {
   	 try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

   	 runOnUiThread(new Runnable() {
		    public void run() {
		    	
		    	 wv.loadUrl("javascript:todroit()"); 
		   
		    }
		});

    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

     
        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        getWindow().requestFeature(Window.FEATURE_RIGHT_ICON);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        general_width = getWindowManager().getDefaultDisplay().getWidth(); 
        general_eigth = getWindowManager().getDefaultDisplay().getHeight();

        setContentView(R.layout.main);
        updateTitleIcons();
        
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        enableContentRewriting = settings.getBoolean("enableContentRewriting", true);
       
        actionStore = new ActionStore(this);
        actionStore.updateBuiltInActions();
        
        
        
        myTimer = new Timer();
        button1 = (Button) findViewById(R.id.button1);
        button1.setBackgroundColor(Color.GRAY);
        button1.setBackgroundResource(R.drawable.left1);
        button1.setOnTouchListener(new OnTouchListener() {
        	  @Override
        	  public boolean onTouch(View v, MotionEvent event) {
        		   c8888 = Calendar.getInstance(); 
        		  int seconds = c8888.get(Calendar.SECOND);
        		  Log.v("hello","here" + seconds);
        		  

            	  runOnUiThread(new Runnable() {
          		    public void run() {
          		 	 wv.loadUrl("javascript:plusvite();");
          		 	 /*
            	        myTimer.schedule(new TimerTask() {          
            	            @Override
            	            public void run() {
            	               
                 		    	 wv.loadUrl("javascript:plusvite();");
            	            }

            	        }, 0, 1000);*/
          		    	 
          		    }
          		});
        		  
        	    return true;
        	  }
        	});
       button1.setOnClickListener(new Button.OnClickListener() {  
            public void onClick(View v)
                {
     		   c8888 = Calendar.getInstance(); 
     		  int seconds = c8888.get(Calendar.SECOND);
     		  Log.v("hello","here" + seconds);
            	  runOnUiThread(new Runnable() {
            		    public void run() {
            		    	 wv.loadUrl("javascript:moinsvite();");
            		    	// myTimer.cancel();
            		    	 
            		    }
            		});
            	 // runafter(); 
                }
             });
        

        
        button1.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
            
     		   c8888 = Calendar.getInstance(); 
     		  int seconds = c8888.get(Calendar.SECOND);
     		  Log.v("hello","here" + seconds);
          	  runOnUiThread(new Runnable() {
      		    public void run() {
      		    	 wv.loadUrl("javascript:setleft()");
      		    	 
      		    }
      		});
               
                return true;
            }
        });
        
        button2 = (Button) findViewById(R.id.button2);
        button2.setBackgroundResource(R.drawable.right1);
        button2.setOnTouchListener(new OnTouchListener() {
      	  @Override
      	  public boolean onTouch(View v, MotionEvent event) {
   		   c8888 = Calendar.getInstance(); 
   		  int seconds = c8888.get(Calendar.SECOND);
   		  Log.v("hello","here" + seconds);
          	  runOnUiThread(new Runnable() {
      		    public void run() {
      		    	 wv.loadUrl("javascript:setrigth()");
      		    	 
      		    }
      		});
      	    return true;
      	  }
      	});
        
      button2.setOnClickListener(new Button.OnClickListener() {  
            public void onClick(View v)
                {
     		   c8888 = Calendar.getInstance(); 
     		  int seconds = c8888.get(Calendar.SECOND);
     		  Log.v("hello","here" + seconds);
            	  runOnUiThread(new Runnable() {
            		    public void run() {
            		    	 wv.loadUrl("javascript:setrigth()");
            		    	 
            		    }
            		});
            	 // runafter(); 
                   
                }
             });

        button2.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
            	
     		   c8888 = Calendar.getInstance(); 
     		  int seconds = c8888.get(Calendar.SECOND);
     		  Log.v("hello","here" + seconds);
          	  runOnUiThread(new Runnable() {
      		    public void run() {
      		    	 wv.loadUrl("javascript:setrigth()");
      		    	 
      		    }
      		});
               
                return true;
            }
        });
        
         MyAppTesting = MediaPlayer.create ( getApplicationContext ( ) , R.raw.racer ) ;
        MyAppTesting.setLooping ( true ) ;
        MyAppTesting.start ( ) ;
        wv = (WebView)findViewById(R.id.browser);
        
        WebSettings websettings = wv.getSettings();
        websettings.setJavaScriptEnabled(true);
       

        /* Enable zooming */
     //   websettings.setSupportZoom(true);
      //  websettings.setBuiltInZoomControls(true); 

        /* Register JS interfaces used by action scripts */
        wv.addJavascriptInterface(new ActionInterface(this), "ActionInterface");
        wv.addJavascriptInterface(new SiteSearchInterface(this), "SiteSearchInterface");
        wv.addJavascriptInterface(new JavaScriptInterface(), "MyAndroid"); 
        
        wv.setWebViewClient(new WebViewClient()
        {
            /** 
             * This method is called after a page finishes loading.
             * 
             * It reads all the JS microformat parsers and injects them into the web page which has just 
             * finished loading. This is achieved by calling loadUrl("javascript:<js-code-here>"),
             * which is the exact same method used by bookmarklets.
             */
            @Override
            public void onPageFinished(WebView view, String url)
            {
                String commonJS = getScript(R.raw.common);
                String[] scripts = {getScript(R.raw.search_form), getScript(R.raw.parser_adr), getScript(R.raw.parser_vevent)};
                
                for (String script : scripts) {
                    getWebView().loadUrl("javascript:(function(scriptSecretKey){ " + 
                                         commonJS + " " +
                                         script + " })('" + Mosembro.this.secretScriptKey + "')");
                }

                super.onPageFinished(view, url);
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                setSiteSearchOptions(false, null);
                resetSmartActions();
                
                super.onPageStarted(view, url, favicon);
            }
            
            public boolean shouldOverrideUrlLoading(WebView view, final String url)
            {
                if (looksLikeActionScript(url)) {
                    installActionScript(url);
                    return true;
                }
                return false;
            }
        });
        
        wv.setDownloadListener(new DownloadListener() 
        {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                installActionScript(url);
            }
            
        });
        
        wv.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) 
            {
                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_alert)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();
                
                return true;
            };
            
            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) 
            {
                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_confirm)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, 
                            new DialogInterface.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel, 
                            new DialogInterface.OnClickListener() 
                            {
                                public void onClick(DialogInterface dialog, int which) 
                                {
                                    result.cancel();
                                }
                            })
                .create()
                .show();
            
                return true;
            };
            
            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) 
            {
                final LayoutInflater factory = LayoutInflater.from(Mosembro.this);

                new AlertDialog.Builder(Mosembro.this)
                    .setTitle(R.string.title_dialog_prompt)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    result.cancel();
                                }
                            })
                    .setOnCancelListener(
                            new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {
                                    result.cancel();
                                }
                            })
                    .show();
                
                return true;
            };
            
            
            @Override
            public void onProgressChanged(WebView view, int newProgress)
            {
             //   updateProgress(newProgress);
              //  super.onProgressChanged(view, newProgress);
            }
            
            @Override
            public void onReceivedTitle(WebView view, String title)
            {
              //  setTitle(title);
              //  super.onReceivedTitle(view, title);
            }
        });
        
        
        //loadWebPage("http://10.0.2.2/");
        //david
        loadWebPage("file:///android_asset/index.html");
 
    
    
    
    }
   
    
    public class JavaScriptInterface {
        public void runafter() 
        {
       	 try {
    			Thread.sleep(500);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

       	 runOnUiThread(new Runnable() {
    		    public void run() {
    		    	
    		    	 wv.loadUrl("javascript:todroit()"); 
    		   
    		    }
    		});

        }

        //add other interface methods to be called from JavaScript
   }
    @Override 
    public boolean dispatchTouchEvent(MotionEvent me){ 
    
     return super.dispatchTouchEvent(me); 
    }
    
    


 

    
    public void loadWebPage(String targetURL)
    {
        if (targetURL == null) {
            return;
        }
        
        /* Fix URL if it doesn't begin with 'http' or 'file:'. 
         * WebView will not load URLs which do not specify protocol. */
        if (targetURL.indexOf("http") != 0 && targetURL.indexOf("file:") != 0) {
            targetURL = "http://" + targetURL;
        }
        
        lastEnteredURL = targetURL;
      //  setTitle("Loading "+targetURL);
        
        getWebView().loadUrl(targetURL);
    }
    
    public WebView getWebView()
    {
        return wv;
    }

    public void setSiteSearchOptions(boolean canSiteSearch, HashMap<String, String> config)
    {
        this.canSiteSearch = canSiteSearch;
        this.siteSearchConfig = config;
    }
    
    public int addSmartAction(SmartAction sa, int groupId)
    {
        String groupKey = "actionGroup" + Integer.toString(groupId);
        
        /* create group if it doesn't exist yet */
        if (!smartActionGroups.containsKey(groupKey)) {
            smartActionGroups.put(groupKey, new ArrayList<SmartAction>(5));
        }
        
        smartActions.add(sa);
        smartActionGroups.get(groupKey).add(sa);
        
        return smartActions.size() -1;
    }
    
    public void resetSmartActions()
    {
        smartActions = new ArrayList<SmartAction>(10);
        smartActionGroups = new HashMap<String, ArrayList<SmartAction>>(10);
        updateTitleIcons();
    }
    
    public ArrayList<SmartAction> getSmartActions()
    {
        return smartActions;
    }
    
    public ArrayList<SmartAction> getSmartActionsForGroup(int groupId)
    {
        String groupKey = "actionGroup" + Integer.toString(groupId);

        if (smartActionGroups.containsKey(groupKey)) {
            return smartActionGroups.get(groupKey);
        }
        
        return null;
    }
    
    public boolean getEnableContentRewriting()
    {
        return enableContentRewriting;
    }
    
    public void setEnableContentRewriting(boolean enable)
    {
        enableContentRewriting = enable;
    }
    
    public String getLastEnteredUrl()
    {
        return lastEnteredURL;
    }
    
    public void updateProgress(int progress)
    {
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, progress * 100);
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        MyAppTesting.release();
        savePreferences();
    }
    
    public void savePreferences()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("enableContentRewriting", enableContentRewriting);
        editor.commit();
    }
    
    /**
     * Updates active/inactive state of icons in the title bar 
     */
    public void updateTitleIcons()
    {
        if (this.smartActions.size() > 0) {
            getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.title_mf_ico);
        }
        else {
            getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.title_mf_ico_disabled);
           // wv.loadUrl("javascript:document.getElementById('keyLeft').innerHTML = 'true;'");
        }
        
    }
    
    /** 
     * Reads a script form a javascript file located in /res/raw/ 
     * and retuns it as a String.
     */
    public String getScript(int resourceId)
    {
        return Reader.readRawString(getResources(), resourceId);
    }
    
    public ActionStore getActionStore()
    {
        return actionStore;
    }
    
    private String generateSecretScriptKey()
    {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Checks if the given string is a valid secret key
     * @param scriptSecretKey A generated key which is available only to installed JS scripts. Prevents other scripts from calling this function.
     * @return true if valid, false otherwise
     */
    public boolean isValidScriptKey(String scriptSecretKey)
    {
        return scriptSecretKey.equals(this.secretScriptKey);
    }
    
    /**
     * Checks if a given file URL looks like it is pointing to an action script file
     * @param url URL of file
     * @return true if URL ends with ".action.js", false otherwise
     */
    boolean looksLikeActionScript(String url)
    {
        if (url.endsWith(".action.js")) {
            return true;
        }
        return false;
    }
    
    /**
     * Called when the browser detects an action script file
     * @param url Location of the script
     */
    void installActionScript(final String url)
    {
        if (!looksLikeActionScript(url)) {
            return;
        }
        
        new AlertDialog.Builder(this)
            .setTitle(R.string.action_install_dialog_title)
            .setMessage(R.string.action_install_dialog_msg)
            .setPositiveButton(android.R.string.yes, 
                    new AlertDialog.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (getActionStore().installFromUrl(url)) {
                                Toast.makeText(Mosembro.this, R.string.action_install_ok, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Mosembro.this, R.string.action_install_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
            .setNegativeButton(android.R.string.no, null)
            .create()
            .show();
    }
    
    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public boolean isIntentAvailable(String action, String value) 
    {
        try {
            String intentAction = (String)Intent.class.getField(action).get(null);
            Intent i = new Intent(intentAction, Uri.parse(value));
            List<ResolveInfo> list = 
                getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        
     //   menu.add(Menu.NONE, MENU_GO_TO, Menu.NONE, R.string.menu_go_to)
     //       .setIcon(R.drawable.menu_go_to);
        
        menu.add(Menu.NONE, MENU_RELOAD, Menu.NONE, R.string.menu_reload)
        .setIcon(R.drawable.menu_refresh);
        MyAppTesting.release();
        MyAppTesting = MediaPlayer.create ( getApplicationContext ( ) , R.raw.racer ) ;
       MyAppTesting.setLooping ( true ) ;
       MyAppTesting.start ( ) ;

        
  //      microformatsMenuItem = menu.add(Menu.NONE, MENU_SMART_ACTIONS, Menu.NONE, R.string.menu_smart_actions);
  //      microformatsMenuItem.setIcon(R.drawable.menu_microformats3_disabled);
        
     //   searchMenuItem = menu.add(Menu.NONE, MENU_SITE_SEARCH, Menu.NONE, R.string.menu_search_site);
     //   searchMenuItem.setIcon(R.drawable.menu_site_search2_disabled);
        
     //   menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, R.string.menu_settings)
     //       .setIcon(R.drawable.menu_microformats_settings);
        
     //   menu.add(Menu.NONE, MENU_MANAGE_SCRIPTS, Menu.NONE, R.string.menu_manage_scripts)
     //       .setIcon(R.drawable.menu_manage_scripts);
        
        return true;
    }
    
    /**
     * Changes enabled/disabled state of menu items
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {/*
        if (canSiteSearch) {
            searchMenuItem.setIcon(R.drawable.menu_site_search2);
            searchMenuItem.setEnabled(true);
        }
        else {
            searchMenuItem.setIcon(R.drawable.menu_site_search2_disabled);
            searchMenuItem.setEnabled(false);
        }*/
        
   /*     if (smartActions.size() > 0) {
            microformatsMenuItem.setIcon(R.drawable.menu_microformats3);
            microformatsMenuItem.setEnabled(true);
        }
        else {
            microformatsMenuItem.setIcon(R.drawable.menu_microformats3_disabled);
            microformatsMenuItem.setEnabled(false);
        }*/
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case MENU_GO_TO:
                /* open URL dialog */
                new GoToDialog(this).show();
                return true;
                
            case MENU_RELOAD:
                /* reload */
                wv.reload();
                return true;
                
            case MENU_SITE_SEARCH:
                /* site search */
                if (canSiteSearch) {
                    new SiteSearchDialog(this, siteSearchConfig).show();
                }
                return true;
                
            case MENU_SMART_ACTIONS:
                /* microformats */
                if (smartActions.size() > 0) {
                    new SmartActionsDialog(this).show();
                }
                return true;
                
            case MENU_SETTINGS:
                new SettingsDialog(this).show();
                return true;
                
            case MENU_MANAGE_SCRIPTS:
                new ManageActionsDialog(this).show();
                return true;
        }
                
        return false;
    }
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void onSensorChanged(SensorEvent event) {
	String tvX ;
	String tvY ;
	String tvZ ;
	
	float x = event.values[0];
	float y = event.values[1];
	float z = event.values[2];
	if (!mInitialized) {
	mLastX = x;
	mLastY = y;
	mLastZ = z;
	tvX = "0.0";
	tvY = "0.0";
	tvZ = "0.0";
	mInitialized = true;
	} else {
	float deltaX = Math.abs(mLastX - x);
	float deltaY = Math.abs(mLastY - y);
	float deltaZ = Math.abs(mLastZ - z);
	if (deltaX < NOISE) deltaX = (float)0.0;
	if (deltaY < NOISE) deltaY = (float)0.0;
	if (deltaZ < NOISE) deltaZ = (float)0.0;
	mLastX = x;
	mLastY = y;
	mLastZ = z;
	tvX = Float.toString(deltaX) ;
	tvY =  (Float.toString(deltaY));
	tvZ =  (Float.toString(deltaZ));
		// Log.v("hello","111111111111111111    "+mLastX);
		 //Log.v("hello","222222222222222222   "+mLastY);
		if(mLastY < -1)
		{
			Log.v("hello","droite   "+mLastY);
		   	  runOnUiThread(new Runnable() {
				    public void run() {
				    	 wv.loadUrl("javascript:setleft()");
				    	 
				    }
				});

		
		}
		if(mLastY > 1)
		{
			Log.v("hello","gauche   "+mLastY);
		
		 	  runOnUiThread(new Runnable() {
				    public void run() {
				    	 wv.loadUrl("javascript:setrigth()");
				    	 
				    }
				});
		
		}
		//	 Log.v("hello","33333333333333333    "+mLastZ);
		
	}
	}






    
}