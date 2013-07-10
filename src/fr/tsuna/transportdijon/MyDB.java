package fr.tsuna.transportdijon;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;


public class MyDB extends SQLiteOpenHelper {
	private static final int VERSION_BDD = 5;
	
	
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

	private static final String ADD_LAST_LINE_UPDATE= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ LAST_LINE_UPDATE+"','0');";
	private static final String ADD_REFRESH_LINE= "INSERT INTO "+TABLE_PARAM+" VALUES('"
			+ REFRESH_LINE+"','3600');";
	
	public MyDB(Context context, String name, CursorFactory factory){
		super(context,name,factory,VERSION_BDD);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_LINE);
		db.execSQL(CREATE_PARAM);
		db.execSQL(ADD_LAST_LINE_UPDATE);
		db.execSQL(ADD_REFRESH_LINE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIGNE + ";");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARAM + ";");
		onCreate(db);
	}
}
