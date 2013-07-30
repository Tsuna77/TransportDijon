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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchActivity extends Activity {
	private List<Lignes> list_line = new ArrayList<Lignes>();
	private List<KeolisStation> list_station = new ArrayList<KeolisStation>();
	private List<KeolisHoraire> list_horaire = new ArrayList<KeolisHoraire>();
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
       	KeolisStation current_station = (KeolisStation) station_list.getSelectedItem();
       	 
		int result = bdd.addFav(current_station, current_line);
		
		bdd.close();
		
		AlertDialog.Builder adb = new AlertDialog.Builder(SearchActivity.this);
    	adb.setTitle(R.string.popup_add_to_fav);
    	if (result == 0){
    		adb.setMessage(R.string.popup_add_text);
        	adb.setNegativeButton(R.string.stay,null);
        	adb.setPositiveButton(R.string.retour, new AlertDialog.OnClickListener(){
        		public void onClick(DialogInterface dialog, int arg1){
        			onBackPressed();
        		}
        	});
    	}
    	else if (result == 1){
    		adb.setMessage(R.string.already_in_fav);
	    	adb.setCancelable(false);
	    	adb.setPositiveButton(R.string.retour, null);
    	}
    	else{
    		adb.setMessage(R.string.adding_error);
	    	adb.setCancelable(false);
	    	adb.setPositiveButton(R.string.OK, null);
    	}
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

					horaire+=((KeolisHoraire)list_horaire.get(0)).getDest()+"\t";
					horaire+=((KeolisHoraire)list_horaire.get(0)).get_Left_Time()+"\n";
					try{
						horaire+=((KeolisHoraire)list_horaire.get(1)).getDest()+"\t";
						horaire+=((KeolisHoraire)list_horaire.get(1)).get_Left_Time()+"\n";
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
            	 KeolisStation current_station = (KeolisStation) station_list.getSelectedItem();
            	 myLog.write(TAG,"D�but du thread de mise � jour des horaires");
            	 clear_result();
            	 try {
            		if (current_line.getVers() != ""){
            			list_horaire = new KeolisParser().parser_horaire(current_station);
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
        myLog.write(TAG, "Ligne sélectionné : "+l.toString());
        list_station= diviabdd.getAllStations(l);
        if (list_station != null){
	        try{
	        	station_list = (Spinner) findViewById(R.id.spinner_station);
		    	ArrayAdapter<KeolisStation> adapter = new ArrayAdapter<KeolisStation>(this, android.R.layout.simple_list_item_1, list_station);
		    	
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

