package fr.tsuna.transportdijon;

import java.util.ArrayList;
import java.util.List;



import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "TransportDijon";
	private List<Lignes> line_name=new ArrayList<Lignes>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DiviaParser.setContext(this);
        
        

        myLog.clear();
        log_device_info();
        App.getContext();
		try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info;
			info = manager.getPackageInfo(this.getPackageName(), 0);
			Toast.makeText(getApplicationContext(), getString(R.string.version)+info.versionName, Toast.LENGTH_SHORT).show();

		} catch (Exception e) {
    		myLog.write(TAG,e.getMessage());
		}
        
        DiviaBDD diviabdd = new DiviaBDD(this);
        
       
        diviabdd.open();
        
        // chargement des parametres
        String last_line_update = diviabdd.getParam(MyDB.LAST_LINE_UPDATE);
        String refresh_line = diviabdd.getParam(MyDB.REFRESH_LINE);

        myLog.write(TAG, "last_line_update="+last_line_update);
        myLog.write(TAG, "refresh_line="+refresh_line);
        
        Long tsLong = System.currentTimeMillis()/1000;
        
        if (Integer.parseInt(last_line_update)+Integer.parseInt(refresh_line) < tsLong){
        	refresh_line_thread();
        }
        
        diviabdd.close();
        
    }
    private synchronized void refresh_line(){

		// récupération des lignes via l'api
    	line_name.clear();
    	try {
    		DiviaBDD diviabdd = new DiviaBDD(this);
            
    	       
            diviabdd.open();
    		line_name=new DiviaParser().parseLine();
    		diviabdd.fillinLineTable(line_name);
            diviabdd.close();

		}catch (Exception e) {
			myLog.write(TAG, e.getMessage());
		}
    }


    private synchronized void refresh_line_thread(){
    	if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD){
    		refresh_line();
    	}
    	else{
	    	new Thread(new Runnable() { 
	            public void run() {
	            	myLog.write(TAG,"Début du thread de mise à jour des lignes");
	                refresh_line();
	            } 
	       }).start(); 
    	}
    	Toast.makeText(getApplicationContext(), getString(R.string.line_loading), Toast.LENGTH_SHORT).show();
		
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
			 type_connexion="Non connectÃ©";
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.action_settings:
        		break;
                
        }
        return super.onOptionsItemSelected(item);
    }
    
}
