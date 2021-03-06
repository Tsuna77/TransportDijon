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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DiviaBDD {
	private static final String TAG = "TransportDijon";

	private static final String NOM_BDD = "divia.db";
	private SQLiteDatabase bdd;
	private MyDB myDB;
	private boolean isOpened = false;
	
	
	public DiviaBDD(Context context){
		myDB = new MyDB(context, NOM_BDD, null);
	}
	
	public void open(){
		bdd = myDB.getWritableDatabase();
		isOpened=true;
	}
	
	public void close(){
		isOpened=false;
		bdd.close();
	}
	
	public SQLiteDatabase getBDD(){
		return bdd;
	}

	public void setParam(String key, String value){
		ContentValues values = new ContentValues();
		values.put(MyDB.PARAM_VALUE, value);
		myLog.write(TAG, "Le paramètre "+key+" prend pour valeur "+value);
		bdd.update(MyDB.TABLE_PARAM, values, MyDB.PARAM_KEY+"='"+key+"'", null);
		
	}
	
	public long insertLigne(Lignes lignes){
		ContentValues values = new ContentValues();

		values.put(MyDB.LIGNE_CODE, lignes.getCode());
		values.put(MyDB.LIGNE_COLOR, lignes.getCouleur());
		values.put(MyDB.LIGNE_NOM, lignes.getNom());
		values.put(MyDB.LIGNE_SENS, lignes.getSens());
		values.put(MyDB.LIGNE_VERS, lignes.getVers());
		
		myLog.write("BDD_test", "db.execSQL(\"" +
				"insert into \"+TABLE_LIGNE+\" " +
				"('\"+LIGNE_CODE+\"'," +
				"'\"+LIGNE_NOM+\"'," +
				"'\"+LIGNE_SENS+\"'," +
				"'\"+LIGNE_VERS+\"'," +
				"'\"+LIGNE_COLOR\")" +
				" VALUES ('"+lignes.getCode()+"',"+
				"'"+lignes.getNom()+"',"+
				"'"+lignes.getSens()+"',"+
				"'"+lignes.getVers()+"',"+
				"'"+lignes.getCouleur()+"')"+
				";\");");
		
		return bdd.insert(MyDB.TABLE_LIGNE, null,values);
	}

	public long insertStation(KeolisStation station, Lignes ligne){
		ContentValues values = new ContentValues();

		values.put(MyDB.STATION_CODE, station.getCode());
		values.put(MyDB.STATION_NOM, station.getNom());
		values.put(MyDB.STATION_REFS, station.getRef());
		values.put(MyDB.STATION_LIGNE_CODE, ligne.getCode());
		values.put(MyDB.STATION_LIGNE_SENS, ligne.getSens());
		
		myLog.write("BDD_test", "db.execSQL(\"" +
				"insert into \"+TABLE_STATION+\" " +
				"('\"+STATION_CODE+\"'," +
				"'\"+STATION_NOM+\"'," +
				"'\"+STATION_REFS+\"'," +
				"'\"+STATION_LIGNE_CODE+\"'," +
				"'\"+STATION_LIGNE_SENS+\"')" +
				" VALUES ('"+station.getCode()+"',"+
				"'"+station.getNom()+"',"+
				"'"+station.getRef()+"',"+
				"'"+ligne.getCode()+"',"+
				"'"+ligne.getSens()+"')"+
				";\");");
		
		return bdd.insert(MyDB.TABLE_STATION, null,values);
	}
	
	public int updateLigne(Lignes lignes){
		ContentValues values = new ContentValues();

		values.put(MyDB.LIGNE_CODE, lignes.getCode());
		values.put(MyDB.LIGNE_COLOR, lignes.getCouleur());
		values.put(MyDB.LIGNE_NOM, lignes.getNom());
		values.put(MyDB.LIGNE_SENS, lignes.getSens());
		values.put(MyDB.LIGNE_VERS, lignes.getVers());
		
		return bdd.update(MyDB.TABLE_LIGNE, values, MyDB.LIGNE_CODE+"="+lignes.getCode(),null);
		
	}
	
	public Lignes getLigne(String code){
		Cursor c = bdd.query(MyDB.TABLE_LIGNE, new String[] {MyDB.LIGNE_CODE, MyDB.LIGNE_COLOR, MyDB.LIGNE_NOM, MyDB.LIGNE_SENS, MyDB.LIGNE_VERS}, MyDB.LIGNE_CODE+"=\""+code+"\"",null,null,null,null);
		return cursorToLigne(c);
	}

	private Lignes cursorToLigne(Cursor c){
		if (c.getCount() == 0)
			return null;
 
		c.moveToFirst();
		Lignes ligne = new Lignes();
		ligne.setCode(c.getString(0));
		ligne.setCouleur(c.getString(1));
		ligne.setNom(c.getString(2));
		ligne.setSens(c.getString(3));
		ligne.setVers(c.getString(4));
		c.close();
		return ligne;
	}
	
	public String getParam(String Key){
		Cursor c = bdd.query(MyDB.TABLE_PARAM, new String[] {MyDB.PARAM_VALUE}, MyDB.PARAM_KEY+"='"+Key+"'", null, null, null, null);
		return cursorToString(c);
	}
	
	private String cursorToString(Cursor c){
		if (c.getCount() == 0)
			return null;
		c.moveToFirst();
		return c.getString(0);
	}
	
	public void fillinLineTable(List<Lignes> lignes){
		
		bdd.delete(MyDB.TABLE_LIGNE, "1=1", null);
		for (Lignes ligne : lignes){
			myLog.write(TAG, ligne.toString());
			insertLigne(ligne);
		}
		ContentValues values = new ContentValues();

		Long ts = System.currentTimeMillis()/1000;
		String sts = Long.toString(ts);
		values.put(MyDB.PARAM_VALUE,sts);
		
		bdd.update(MyDB.TABLE_PARAM, values, MyDB.PARAM_KEY+"='"+MyDB.LAST_LINE_UPDATE+"'", null);
	}

	public void fillinStationTable(List<KeolisStation> stations, Lignes ligne){
		boolean close_needed=false;
		if ( !isOpened){
			open();
			close_needed=true;
		}
		bdd.delete(MyDB.TABLE_STATION, MyDB.STATION_LIGNE_CODE+"='"+ligne.getCode()+"' AND "+MyDB.STATION_LIGNE_SENS+"='"+ligne.getSens()+"'", null);
		for (KeolisStation station : stations){
			myLog.write(TAG, station.toString());
			insertStation(station, ligne);
		}
		ContentValues values = new ContentValues();

		Long ts = System.currentTimeMillis()/1000;
		String sts = Long.toString(ts);
		values.put(MyDB.PARAM_VALUE,sts);
		
		bdd.update(MyDB.TABLE_PARAM, values, MyDB.PARAM_KEY+"='"+MyDB.LAST_LINE_UPDATE+"'", null);
		if (close_needed){
			close();
		}
		
	}

	public void insertFromFile(Context context, int resourceId) throws IOException {
	    // Open the resource
	    InputStream insertsStream = context.getResources().openRawResource(resourceId);
	    BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));
	
	    // Iterate through lines (assuming each insert has its own line and theres no other stuff)
	    while (insertReader.ready()) {
	        String insertStmt = insertReader.readLine();
	        bdd.execSQL(insertStmt);
	    }
	    insertReader.close();
	}
	
	public boolean asFavoris(){
		boolean result = false;
		Cursor c = bdd.rawQuery("SELECT COUNT(1) FROM "+MyDB.TABLE_FAV, null);
		c.moveToFirst();
		String val = c.getString(0);
		if (!val.equals("0"))
			result= true;
		
		return result;
			
		
	}
	
	public ArrayList<Lignes> getAllLines(){
		ArrayList<Lignes> lignes = new ArrayList<Lignes>();
		
		Cursor c = bdd.query(MyDB.TABLE_LIGNE, new String[] {MyDB.LIGNE_CODE, MyDB.LIGNE_COLOR, MyDB.LIGNE_NOM, MyDB.LIGNE_SENS, MyDB.LIGNE_VERS},null,null,null,null,null);
		if (c.getCount() == 0)
			return null;
		
		while(c.moveToNext()){
		    lignes.add(new Lignes(c.getString(0), c.getString(2), c.getString(3), c.getString(4), c.getString(1)));
		}
		return lignes;
		
	}
	
	public ArrayList<KeolisStation> getAllStations(Lignes ligne){
		ArrayList<KeolisStation> stations = new ArrayList<KeolisStation>();
		String code = codeToTwoDigit(ligne.getCode());
		String sens = ligne.getSens();
		Cursor c = bdd.query(MyDB.TABLE_STATION, new String[] {MyDB.STATION_CODE, MyDB.STATION_NOM, MyDB.STATION_REFS}, MyDB.STATION_LIGNE_CODE+"='"+code+"' AND "+MyDB.STATION_LIGNE_SENS+"='"+sens+"'", null, null, null, null, null);
		if (c.getCount() == 0)
			return null;
		
		while(c.moveToNext()){
			stations.add(new KeolisStation(c.getString(0), c.getString(1), c.getString(2)));
		}
		
		return stations;
	}
	
	private String codeToTwoDigit(String code){
		if (code.length() == 1){
			try{
				Integer.parseInt(code);
				code = '0'+code;
			}
			catch (Exception e){
				
			}
		}
		return code;
	}
	private boolean alreadyInFav(KeolisStation station, Lignes ligne){
		boolean result = true;
		String sql = "SELECT COUNT(*) FROM "+MyDB.TABLE_FAV+" WHERE "
				+ MyDB.FAV_CODE + "='"+protectSpecialChars(ligne.getNom())+"' AND "
				+ MyDB.FAV_NOM + "='"+protectSpecialChars(station.getNom())+"' AND "
				+ MyDB.FAV_REFS + "='"+station.getRef()+"' AND "
				+ MyDB.FAV_VERS + "='"+protectSpecialChars(ligne.getVers())+"'";
		Cursor c = bdd.rawQuery(sql, null);
		c.moveToFirst();
		if (c.getInt(0) == 0)
			result= false;
		return result;
	}
	private String protectSpecialChars(String input){
		String output;
		
		output = input.replaceAll("'","''");
		
		return output;
	}
	public int addFav(KeolisStation station, Lignes ligne){
		int result = 0;
		// mise en favori de l'arrêt
		if ( alreadyInFav(station, ligne)){
			result = 1;
		}
		else{
			ContentValues values = new ContentValues();
	
			values.put(MyDB.FAV_CODE,ligne.getNom());
			values.put(MyDB.FAV_NOM,station.getNom());
			values.put(MyDB.FAV_REFS,station.getRef());
			values.put(MyDB.FAV_VERS,ligne.getVers());
			values.put(MyDB.FAV_POS, getMaxPosFav() +1);
			try{
				bdd.insertOrThrow(MyDB.TABLE_FAV, null, values);
			}catch(Exception e){
				myLog.write(TAG, "Impossible de mettre l'arrêt en favori");
				result = 2;
			}
		}
		return result;
	}
	
	public int getMaxPosFav(){
		// renvoie le max de la position des favoris
		String sql = "SELECT MAX("+MyDB.FAV_POS+") FROM "+MyDB.TABLE_FAV;
		Cursor c = bdd.rawQuery(sql, null);
		
		c.moveToFirst();
		return c.getInt(0);
	}

	public List<diviaFav> getAllFav() {
		ArrayList<diviaFav> favoris = new ArrayList<diviaFav>();
		
		Cursor c = bdd.rawQuery("SELECT "+MyDB.FAV_CODE+","+ MyDB.FAV_NOM+","+MyDB.FAV_VERS+","+MyDB.FAV_REFS+" FROM "+MyDB.TABLE_FAV+" ORDER BY "+MyDB.FAV_POS, null);
		if (c.getCount() == 0)
			return null;
		while(c.moveToNext()){
			favoris.add(new diviaFav(c.getString(0), c.getString(2), c.getString(1), c.getString(3)));
		}
		
		return favoris;
	}

	public void SupressFav(String textView_text){
		// information sur le favoris contenu dans le champs text
		String[] list=textView_text.split("\n");
		myLog.write(TAG, "Suppresion du favori : "+ list[0]);
		String station = list[0].split(": ")[1];
		String code_vers = list[0].split(": ")[0];
		String code=code_vers.split(" \\(")[0];
		String vers=code_vers.split(" \\(")[1].replace(") ","");
		
		String sql="delete from "+MyDB.TABLE_FAV+" WHERE "+MyDB.FAV_CODE+"='"+protectSpecialChars(code)+"' AND "+MyDB.FAV_NOM+"='"+protectSpecialChars(station)+"' AND "+MyDB.FAV_VERS+"='"+protectSpecialChars(vers)+"'";
		Cursor rep=bdd.rawQuery(sql, null);
		
		rep.moveToFirst();
		
	}
}
