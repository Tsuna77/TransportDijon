/*
 * Copyright (C) 2013 Ricordeau Raphaël
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package fr.tsuna.transportdijon;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
	private static final String TAG = "TransportDijon";
	boolean force_refresh=false;
	boolean refresh=false;
	String console_msg = "";
	boolean first_launch=true;
	boolean locked=false;
	List<diviaFav> fav_list = new ArrayList<diviaFav>();
	List<TextView> fav_text = new ArrayList<TextView>();
	boolean updating = false;
	boolean UI_reload = false;
	int totem_refresh=20000;
	boolean starting=true;

	private Handler handler_horaire = new Handler();
	private Runnable run_horaire = new Runnable() {public void run() {update_time();}}; 
	private final int EDIT_POS=1;
	private TextView selected_fav;
	
	
	private synchronized void update_time(){ 
    	if(updating)
    		return;
    	updating = true;
    	handler_horaire.removeCallbacks(run_horaire);
    	// on recharge la liste des favoris en cas de mise à jour
    	DiviaBDD diviadb = new DiviaBDD(MainActivity.this);
    	diviadb.open();
    	try{
    		totem_refresh = Integer.parseInt(diviadb.getParam(MyDB.REFRESH_TOTEM))*1000;	// valeur en seconde dans la DB mais besoin en milliseconde
    	}
    	catch(Exception e){
    		myLog.write(TAG, e.getMessage(), myLog.ERROR);
    	}
    	fav_list=diviadb.getAllFav();
    	if (fav_list == null & starting){
    		// il n'y a plus de favoris, redirection vers la page de recherche
    		startSearch();
    		return;
    	}
    	diviadb.close();
    	if ( fav_list != null && !fav_list.isEmpty()){
            new Thread(new Runnable() {
                 public void run() {
                	 List<KeolisHoraire> list_horaire;
                	 fav_text.clear();
           			 TextToConsole(getString(R.string.reload));
           			try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {

					}
           			fav_text.clear();
                	 myLog.write(TAG,"Début du thread de mise à jour des horaires");
          			String horaire="";
          			if (!new KeolisParser().isConnected()){
          				TextView txt = new TextView(MainActivity.this);

                		txt.setText(getString(R.string.no_network));
                		txt.setTextColor(getResources().getColor(R.color.white));
     					fav_text.add(txt);
              			update_time_UI();
              			updating=false;
              			TextToConsole("");
     					return;
          			}


                	 for( diviaFav fav: fav_list){

                		 TextView txt = new TextView(MainActivity.this);
                		 registerForContextMenu(txt);
                		 try {
                     		if (fav.getVers() != ""){

                     			list_horaire = new KeolisParser().parser_horaire(fav.getRefs());

                 				horaire=getString(R.string.line_title);
                 				horaire=horaire.replace("%1$s", fav.getCode());
                 				horaire=horaire.replace("%2$s", fav.getNom());
                 				horaire=horaire.replace("%3$s", fav.getVers());

            					horaire+=((KeolisHoraire)list_horaire.get(0)).getDest()+"\t";
            					horaire+=((KeolisHoraire)list_horaire.get(0)).get_Left_Time(getApplicationContext())+"\n";
            					try{
            						horaire+=((KeolisHoraire)list_horaire.get(1)).getDest()+"\t";
            						horaire+=((KeolisHoraire)list_horaire.get(1)).get_Left_Time(getApplicationContext())+"\n";
            					}
            					catch(Exception e){
            						horaire+="\n";
            						myLog.write(TAG, "Il n'y a qu'un seul horaire de disponible", myLog.WARNING);
            					}


                     		}
          				}catch (Exception e) {
          					horaire+=getString(R.string.no_time_available);
    						horaire+="\n";
          				}
                		txt.setText(horaire);
                		txt.setTextColor(getResources().getColor(R.color.white));
                		// enregistrement pour le menu contextuel
                		registerForContextMenu(txt);
     					fav_text.add(txt);
                	 }
          			update_time_UI();
          	    	handler_horaire.postDelayed(run_horaire, totem_refresh);
          			updating=false;
          			TextToConsole("");
                 }


            }).start();

            ((TextView)findViewById(R.id.textView_refresh_tim)).setText(getString(R.string.refresh_info_test, totem_refresh/1000));
    	}
    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu,View v,ContextMenuInfo info)
    {
		menu.setHeaderTitle(getString(R.string.context_menu_title));
		menu.add(0, EDIT_POS,0,R.string.context_menu_supress);
		selected_fav = (TextView)v;
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item)
    {
		switch (item.getItemId()){
			case EDIT_POS:
				//myLog.write(TAG, "Vous avez choisi de supprimer le favoris : "+selected_fav.getText());
				DiviaBDD db = new DiviaBDD(MainActivity.this);
				db.open();
				db.SupressFav(selected_fav.getText().toString());
				db.close();
				update_time();
		}
		return false;
    }
	
	public void onPause(){
		super.onPause();
		handler_horaire.removeCallbacks(run_horaire);
	}
	private void update_time_UI() {
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (UI_reload) return;
				UI_reload=true;
				LinearLayout ly = ((LinearLayout)findViewById(R.id.fav_scroll));
		    	ly.removeAllViews(); // on supprime toute les vue en cours
		    	
		    	
		    	for (TextView txt: fav_text){
		    		ly.addView(txt);
		    	}
		    	UI_reload=false;
			}
		});
		
		
	} 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
		}
        KeolisParser.setContext(this);
		handler_horaire.removeCallbacks(run_horaire);
        
        myLog.clear();
        log_device_info();
		try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info;
			info = manager.getPackageInfo(this.getPackageName(), 0);
			myLog.write(TAG,  getString(R.string.version,info.versionName));

		} catch (Exception e) {
    		myLog.write(TAG,e.getMessage(),myLog.ERROR);
    		finish();
		}
        
        DiviaBDD diviabdd = new DiviaBDD(this);
        
       
        diviabdd.open();
        
        // chargement des parametres
        String last_line_update = diviabdd.getParam(MyDB.LAST_LINE_UPDATE);
        String totem_refresh_db = diviabdd.getParam(MyDB.REFRESH_TOTEM);
        try{
        	totem_refresh = Integer.parseInt(diviabdd.getParam(MyDB.REFRESH_TOTEM))*1000;	// valeur en seconde dans la DB mais besoin en milliseconde
        }
        catch(Exception e){
        	myLog.write(TAG, e.getMessage(), myLog.ERROR);
        }

        if (!last_line_update.equals("0")){
        	first_launch = false;
        }
        else{
        	myLog.write(TAG, "Premier démarrage");
        }
        
        myLog.write(TAG, "last_line_update="+last_line_update);

        //*
        // sauvegarde des paramètres
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor editor = SP.edit();
		editor.putString(MyDB.REFRESH_TOTEM, totem_refresh_db);
		editor.apply();
        //*/
        // affichage des favoris 
        if (diviabdd.asFavoris()){
        	// des favoris sont présents. On les affiches ainsi que leur infos
        	handler_horaire.postDelayed(run_horaire, 10000);
        }else{
        	LinearLayout linearLayout =  (LinearLayout) findViewById(R.id.fav_scroll);
        	linearLayout.removeAllViews();
        	TextView msg = new TextView(this);
        	msg.setText(getString(R.string.no_fav_saved));
        	linearLayout.addView(msg);
        	// redirection vers la page des totems
        	if (starting){
        		// il n'y a plus de favoris, redirection vers la page de recherche
        		startSearch();
        		return;
        	}
        	
        }
        diviabdd.close();

    }
    
    public void onResume(){
    	super.onResume();
    	DiviaBDD diviadb = new DiviaBDD(MainActivity.this);
    	diviadb.open();
    	LinearLayout ly = ((LinearLayout)findViewById(R.id.fav_scroll));
    	ly.removeAllViews();
    	if (diviadb.asFavoris()){
    		update_time();
    	}
    	else{
    		TextView msg = new TextView(this);
    		msg.setText(getString(R.string.no_fav_saved));
    		ly.addView(msg);
    	}
    	
    	diviadb.close();
    }
    
    private String leftTimeToString(int leftTime){
    	String mtime="";
    	
    	if (leftTime >= 86400){
    		int nbJour=leftTime/86400;
    		mtime=nbJour+" jour";
    		if (nbJour > 1){
    			mtime+='s';
    		}
    		mtime+=" ";
    		leftTime=leftTime-(nbJour*86400);
    	}
    	
    	if (leftTime >= 3600){
    		int nbH=leftTime/3600;
    		mtime+=nbH+"h ";
    		leftTime=leftTime-(nbH*3600);
    	}
    	if (leftTime >= 60){
    		int nbM=leftTime/60;
    		mtime+=nbM+"m ";
    		leftTime=leftTime-(nbM*60);
    	}
    	if (leftTime >0){
    		mtime+=leftTime+"s";
    	}
    	
    	return mtime;
    	
    }
    
    
    private void TextToConsole(String message){
    	console_msg=message;
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TextView console = (TextView)findViewById(R.id.console);
				console.setText("");
				console.setText(console_msg);
				
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void log_device_info(){
		 NetworkInfo info = ((ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		 
		 String type_connexion="";
		 if (info != null && info.isConnected()){
			 switch (info.getType()){
			 	default:
			 		type_connexion="unknown";
			 		break;
			 	case ConnectivityManager.TYPE_WIFI:
			 		type_connexion="WIFI";
			 		break;
			 	case ConnectivityManager.TYPE_BLUETOOTH:
			 		type_connexion="BLUETOOTH";
			 		break;
			 	case ConnectivityManager.TYPE_MOBILE:
			 		type_connexion="Mobile";
			 		break;
			 	case ConnectivityManager.TYPE_ETHERNET:
			 		type_connexion="Mobile";
			 		break;
			 }
		 }
		 else{
			 type_connexion="Non connecté";
		 }
		 String s="Debug-infos:";
		 s += "\n OS Kernel Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		 s += "\n OS Version: "+android.os.Build.VERSION.RELEASE;
		 s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
		 s += "\n Device: " + android.os.Build.DEVICE;
		 s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
		 s += "\n Network type: "+type_connexion;
		 
		 
		 
		 myLog.write(TAG, s);

    }

    public void startSettings(){
    		Intent intent = new Intent(this, SettingsActivity.class );
    		startActivityForResult(intent,0);
    }

    public void startAbout(){
    		Intent intent = new Intent(this, AboutActivity.class );
    		startActivityForResult(intent,0);
    }
    public void startSearch(){
    	if (starting){
    		// il n'y a plus de favoris, redirection vers la page de recherche
    		starting=false;
        	Intent intent = new Intent(this, SearchActivity.class );
        	startActivityForResult(intent,0);
    	}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	if (!locked){
	        switch (item.getItemId()) {
	        	case R.id.action_settings:
	        		startSettings();
	        		break;
	        	case R.id.acton_refresh:
	        		update_time();
	        		break;
	        	case R.id.action_addTotem:
	        		starting=true;
	        		startSearch();
	        		break;
	        	case R.id.action_about:
	        		startAbout();
	        		break;
	        }
    	}
        return super.onOptionsItemSelected(item);
    }
    
}

