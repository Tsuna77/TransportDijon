package fr.tsuna.transportdijon;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class MyDB extends SQLiteOpenHelper {
	private static final int VERSION_BDD = 15;
	
	
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
	public static final String FAV_NOM = "nom";		// Nom de l'arr�t
	public static final String FAV_REFS = "ref";	// R�f�rense � transmettre � l'API divia
	
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
			+ FAV_REFS + " TEXT NOT NULL);";
	
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
			+ REFRESH_LINE+"','24');";
	private static final String ADD_REFRESH_TOTEM= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ REFRESH_TOTEM+"','20');";
	
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
