package fr.tsuna.transportdijon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class MyDB extends SQLiteOpenHelper {
	private static final int VERSION_BDD = 19;
	
	
	public static final String TABLE_LIGNE = "lignes";
	public static final String LIGNE_CODE = "code";
	public static final String LIGNE_NOM = "nom";
	public static final String LIGNE_SENS = "sens";
	public static final String LIGNE_VERS = "vers";
	public static final String LIGNE_COLOR = "couleur";
	
	public static final String TABLE_PARAM = "param";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_VALUE = "value";
	public static final String LAST_LINE_UPDATE = "line_update";
	public static final String REFRESH_LINE= "refresh_line"; 
	public static final String REFRESH_TOTEM= "refresh_totem"; 
	
	public static final String TABLE_FAV = "favoris";
	public static final String FAV_ID = "ID";
	public static final String FAV_CODE = "code";	// code de la ligne
	public static final String FAV_VERS = "vers";	// direction de la ligne
	public static final String FAV_NOM = "nom";		// Nom de l'arrêt
	public static final String FAV_REFS = "ref";	// Référense à transmettre à l'API divia
	public static final String FAV_POS = "position";	//position dans l'interface, sert de clef de tri 
	
	public static final String TABLE_STATION = "station";
	public static final String STATION_ID = "ID";
	public static final String STATION_CODE = "code";
	public static final String STATION_NOM = "nom";
	public static final String STATION_REFS = "refs";
	public static final String STATION_LIGNE_CODE = "ligne_code";
	public static final String STATION_LIGNE_SENS = "ligne_sens";
	
	
	private static final String CREATE_LINE = "CREATE TABLE " + TABLE_LIGNE + " ("
			+ LIGNE_CODE + " INTEGER,  " 
			+ LIGNE_NOM + " TEXT NOT NULL, "
			+ LIGNE_SENS + " TEXT NOT NULL, "
			+ LIGNE_VERS + " TEXT NOT NULL, "
			+ LIGNE_COLOR + " TEXT NOT NULL, "
			+ "PRIMARY KEY ("+LIGNE_CODE+","+LIGNE_SENS+"));";
	
	private static final String CREATE_PARAM = "CREATE TABLE "+ TABLE_PARAM+" ("
			+ PARAM_KEY+" TEXT NOT NULL, "
			+ PARAM_VALUE+" TEXT NOT NULL);";

	private static final String CREATE_FAV = "CREATE TABLE "+ TABLE_FAV + " ("
			+ FAV_ID + " INTEGER PRIMARY KEY, "
			+ FAV_CODE + " TEXT NOT NULL, "
			+ FAV_VERS + " TEXT NOT NULL, "
			+ FAV_NOM + " TEXT NOT NULL, "
			+ FAV_REFS + " TEXT NOT NULL,"
			+ FAV_POS + " INTEGER);";
	
	private static final String CREATE_STATION = "CREATE TABLE "+ TABLE_STATION+" ("
			+ STATION_ID+" INTEGER PRIMARY KEY, "
			+ STATION_CODE+" TEXT NOT NULL, "
			+ STATION_NOM+" TEXT NOT NULL, "
			+ STATION_REFS+" TEXT NOT NULL, "
			+ STATION_LIGNE_CODE+" TEXT NOT NULL, "
			+ STATION_LIGNE_SENS+" TEXT NOT NULL"
			+ ");";
	
	private static final String ADD_LAST_LINE_UPDATE= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ LAST_LINE_UPDATE+"','0');";
	private static final String ADD_REFRESH_LINE= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ REFRESH_LINE+"','72');";
	private static final String ADD_REFRESH_TOTEM= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ REFRESH_TOTEM+"','40');";
	
	public MyDB(Context context, String name, CursorFactory factory){
		super(context,name,factory,VERSION_BDD);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_LINE);
		db.execSQL(CREATE_PARAM);
		db.execSQL(CREATE_FAV);
		db.execSQL(CREATE_STATION);
		db.execSQL(ADD_LAST_LINE_UPDATE);
		db.execSQL(ADD_REFRESH_LINE);
		db.execSQL(ADD_REFRESH_TOTEM);
		insert_KeolisLine(db);
	}
	
	private void insert_KeoLine(SQLiteDatabase db,String code, String nom, String sens, String vers, String couleur){
		db.execSQL("insert into "+TABLE_LIGNE+" ('"+LIGNE_CODE+"','"+LIGNE_NOM+"','"+LIGNE_SENS+"','"+LIGNE_VERS+"','"+LIGNE_COLOR+"') VALUES ('"+code+"','"+nom+"','"+sens+"','"+vers+"','"+couleur+"');");
		
	}
	
	private void insert_KeolisLine(SQLiteDatabase db){
		insert_KeoLine(db,"T1","T1","A","QUETIGNY","13369548");
		insert_KeoLine(db,"T1","T1","R","Dijon Gare","13369548");
		insert_KeoLine(db,"T2","T2","A","Valmy","13369548");
		insert_KeoLine(db,"T2","T2","R","CHENOVE Centre","13369548");
		insert_KeoLine(db,"03","L3","A","Fontaine d''Ouche","13369548");
		insert_KeoLine(db,"03","L3","R","Epirey Cap Nord","13369548");
		insert_KeoLine(db,"04","L4","A","CHENOVE Centre Commercial","13369548");
		insert_KeoLine(db,"04","L4","R","Nation","13369548");
		insert_KeoLine(db,"05","L5","A","TALANT Dullin","13369548");
		insert_KeoLine(db,"05","L5","R","Université","13369548");
		insert_KeoLine(db,"06","L6","A","Toison d'Or / Zénith","13369548");
		insert_KeoLine(db,"06","L6","R","LONGVIC","13369548");
		insert_KeoLine(db,"07","L7","A","QUETIGNY Europe","13369548");
		insert_KeoLine(db,"07","L7","R","CHEVIGNY","13369548");
		insert_KeoLine(db,"DIV","City","A","République","13369395");
		insert_KeoLine(db,"DIV","City","R","Darcy","13369395");
		insert_KeoLine(db,"11","11","A","Parc de la Colombière","6710988");
		insert_KeoLine(db,"11","11","R","SAINT-APOLLINAIRE Val Sully","6710988");
		insert_KeoLine(db,"12","12","A","Chicago","6710988");
		insert_KeoLine(db,"12","12","R","PLOMBIERES","6710988");
		insert_KeoLine(db,"13","13","A","Fontaine Village","6710988");
		insert_KeoLine(db,"13","13","R","Motte Giron","6710988");
		insert_KeoLine(db,"14","14","A","MARSANNAY Charon","6710988");
		insert_KeoLine(db,"14","14","R","Sainte-Anne","6710988");
		insert_KeoLine(db,"15","15","A","Montagne de Larrey","6710988");
		insert_KeoLine(db,"15","15","R","PERRIGNY","6710988");
		insert_KeoLine(db,"16","16","A","QUETIGNY allées Cavalières","6710988");
		insert_KeoLine(db,"16","16","R","CRIMOLOIS","6710988");
		insert_KeoLine(db,"17","17","A","Collège Clos de Pouilly","6710988");
		insert_KeoLine(db,"17","17","R","AHUY","6710988");
		insert_KeoLine(db,"18","18","A","Longvic Carmélites","6710988");
		insert_KeoLine(db,"18","18","R","Square Darcy","6710988");
		insert_KeoLine(db,"19","19","A","St-Apollinaire Pré Thomas","6710988");
		insert_KeoLine(db,"19","19","R","Parc des Sports","6710988");
		insert_KeoLine(db,"20","20","A","Dubois","6710988");
		insert_KeoLine(db,"20","20","R","HAUTEVILLE","6710988");
		insert_KeoLine(db,"21","21","A","BRETENIERE","6710988");
		insert_KeoLine(db,"21","21","R","LONGVIC Centre","6710988");
		insert_KeoLine(db,"22","22","A","FENAY","6710988");
		insert_KeoLine(db,"22","22","R","LONGVIC Centre","6710988");
		insert_KeoLine(db,"23","23","A","Les Ateliers","6710988");
		insert_KeoLine(db,"23","23","R","Vieux Bourg","6710988");
		insert_KeoLine(db,"30","30","A","Bressey","6710988");
		insert_KeoLine(db,"30","30","R","Grand Marché","6710988");
		insert_KeoLine(db,"31","31","A","Grand Marché","6710988");
		insert_KeoLine(db,"31","31","R","Magny","6710988");
		insert_KeoLine(db,"32","32","A","Complexe Funéraire","6710988");
		insert_KeoLine(db,"32","32","R","Piscine Olympique","6710988");
		insert_KeoLine(db,"33","33","A","FLAVIGNEROT","6710988");
		insert_KeoLine(db,"33","33","R","Monge","6710988");
		insert_KeoLine(db,"","Express","A","BA 102","6710988");
		insert_KeoLine(db,"","Express","R","Gare SNCF","6710988");
		insert_KeoLine(db,"","Flexo 40","A","République","6710988");
		insert_KeoLine(db,"","Flexo 40","R","Toison d''Or","6710988");
		insert_KeoLine(db,"","Flexo 41","A","Chevigny ZI","6710988");
		insert_KeoLine(db,"","Flexo 41","R","Grand Marché","6710988");
		insert_KeoLine(db,"","Pleine Lune","A","Toison d''Or","6710988");
		insert_KeoLine(db,"","Pleine Lune","R","Université","6710988");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGNE + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAM + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAV + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION + ";");
		onCreate(db);
	}
}
