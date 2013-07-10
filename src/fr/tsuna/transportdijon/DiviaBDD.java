package fr.tsuna.transportdijon;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	
	public long insertLigne(Lignes lignes){
		ContentValues values = new ContentValues();

		values.put(MyDB.LIGNE_CODE, lignes.getCode());
		values.put(MyDB.LIGNE_COLOR, lignes.getCouleur());
		values.put(MyDB.LIGNE_NOM, lignes.getNom());
		values.put(MyDB.LIGNE_SENS, lignes.getSens());
		values.put(MyDB.LIGNE_VERS, lignes.getVers());
		
		return bdd.insert(MyDB.TABLE_LIGNE, null,values);
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
		boolean close_needed=false;
		if ( !isOpened){
			open();
			close_needed=true;
		}
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
		if (close_needed){
			close();
		}
		
	}
}
