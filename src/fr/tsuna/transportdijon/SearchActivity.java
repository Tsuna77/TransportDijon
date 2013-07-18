package fr.tsuna.transportdijon;


import java.util.ArrayList;
import java.util.List;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchActivity extends Activity {
	private List<Lignes> list_line = new ArrayList<Lignes>();
	private List<diviaStation> list_station = new ArrayList<diviaStation>();
	private List<diviaHoraire> list_horaire = new ArrayList<diviaHoraire>();
	private String TAG = "transportdijon";
	private Spinner line_list;
	private Spinner station_list;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_totem, menu);
	    return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);
		
		// remplissage des lignes avec les informations de la base de donn�es
		

        DiviaBDD diviabdd = new DiviaBDD(this);
        
       
        diviabdd.open();

        list_line=diviabdd.getAllLines();
        if (list_line==null){
        	// pas de ligne disponible, retour sur la page d'accueil
        	this.onBackPressed();
        	return;
        }

        diviabdd.close();
        try{
	    	line_list = (Spinner) findViewById(R.id.spinner_line);
	    	ArrayAdapter<Lignes> adapter = new ArrayAdapter<Lignes>(this, android.R.layout.simple_list_item_1, list_line);
	    	line_list.setAdapter(adapter);
		}catch(Exception e){
			myLog.write(TAG, e.getMessage());
		}
       
        update_station();
		
        
        ((Spinner)findViewById(R.id.spinner_line)).setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	update_station();
	        }
	        
	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) {
	        }

        });
        
        ((Spinner)findViewById(R.id.spinner_station)).setOnItemSelectedListener(new OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	        	show_result();
	        }
	        
	        @Override
	        public void onNothingSelected(AdapterView<?> parentView) {
	        }

        });
	}
	
	private void save_fav(){
		DiviaBDD bdd = new DiviaBDD(getApplicationContext());
		bdd.open();
		

       	Lignes current_line = (Lignes) line_list.getSelectedItem();
       	diviaStation current_station = (diviaStation) station_list.getSelectedItem();
       	 
		bdd.addFav(current_station, current_line);
		
		bdd.close();
		
		AlertDialog.Builder adb = new AlertDialog.Builder(SearchActivity.this);
    	adb.setTitle(R.string.popup_add_to_fav);
    	adb.setMessage(R.string.popup_add_text);
    	adb.setNegativeButton(R.string.stay,null);
    	adb.setPositiveButton(R.string.retour, new AlertDialog.OnClickListener(){
    		public void onClick(DialogInterface dialog, int arg1){
    			onBackPressed();
    		}
    	});
    	adb.show();
		
	}
	private void show_result(){
		update_time();
	}
	private void clear_result(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				 TextView txt = (TextView)findViewById(R.id.search_result_info); 
			   	 String horaire="Prochain passage : \n";
			   	 txt.setText(horaire);
			}
		});
		
	}
	private void show_result_UI(){
		// affiche le contenu de la variable list_horaire � l'�cran
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (!list_horaire.isEmpty()){
					TextView txt = (TextView)findViewById(R.id.search_result_info); 
					String horaire="Prochain passage : \n";

					horaire+=((diviaHoraire)list_horaire.get(0)).getDest()+"\t";
					horaire+=((diviaHoraire)list_horaire.get(0)).get_Left_Time()+"\n";
					try{
						horaire+=((diviaHoraire)list_horaire.get(1)).getDest()+"\t";
						horaire+=((diviaHoraire)list_horaire.get(1)).get_Left_Time()+"\n";
					}
					catch(Exception e){
						myLog.write(TAG, "Il n'y a qu'un seul horaire de disponible", myLog.WARNING);
					}
					
					txt.setText(horaire);
				}
				
			}
		});
	}
	
	private void update_time(){
		 new Thread(new Runnable() { 
             public void run() { 
            	 Lignes current_line = (Lignes) line_list.getSelectedItem();
            	 diviaStation current_station = (diviaStation) station_list.getSelectedItem();
            	 myLog.write(TAG,"D�but du thread de mise � jour des horaires");
            	 clear_result();
            	 try {
            		if (current_line.getVers() != ""){
            			list_horaire = new DiviaParser().parser_horaire(current_station);
            			show_result_UI();
            		}
 				}catch (Exception e) {
 					myLog.write(TAG, "Erreur lors de la lecture du flux des horaires",myLog.ERROR);
 				}
             } 
        }).start(); 
	}
	
	private void update_station(){
		 // on r�cup�re la première ligne indiqué afin d'affichez ses arrêts
		DiviaBDD diviabdd = new DiviaBDD(this);
        
	       
        diviabdd.open();
        Lignes l = (Lignes) line_list.getSelectedItem();
        myLog.write(TAG, "Ligne s�lectionn� : "+l.toString());
        list_station= diviabdd.getAllStations(l);
        if (list_station != null){
	        try{
	        	station_list = (Spinner) findViewById(R.id.spinner_station);
		    	ArrayAdapter<diviaStation> adapter = new ArrayAdapter<diviaStation>(this, android.R.layout.simple_list_item_1, list_station);
		    	station_list.setAdapter(adapter);
			}catch(Exception e){
				myLog.write(TAG, e.getMessage());
			}
        }
        else{
        	myLog.write(TAG, "Aucune station trouvé pour la ligne : "+l.toString(), myLog.ERROR);
        }
        diviabdd.close();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.onBackPressed();
			break;
		case R.id.menu_save:
			save_fav();
			break;
		case R.id.menu_refresh:
			show_result();
			break;
		
		}
			
		return super.onOptionsItemSelected(item);
	}

}

