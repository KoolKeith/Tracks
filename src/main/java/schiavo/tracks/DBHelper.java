package schiavo.tracks;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DBHelper extends SQLiteOpenHelper {
	//Nome del database
	public static final String NOME_DB = "DBTracks";
	//Versione del database
	public static final int VERSIONE_DB = 1;

	/**
     * Costruttore dell'helper
     * @param context
     */
    public DBHelper(Context context) {
        super(context, NOME_DB, null, VERSIONE_DB);
    }
    
	//creare la tabella posizioni
	private static final String CREATE_TABLE_POSIZIONI =
		"create table posizioni(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ Posizioni.IDs+ " INTEGER REFERENCES sessioni(_id) ON DELETE CASCADE, "
		+ Posizioni.LAT+ " DOUBLE, "
		+ Posizioni.LONGI+ " DOUBLE," 
		+ Posizioni.ALTITUDE + " DOUBLE, "
		+ Posizioni.SECONDS + " LONG, "
		+ Posizioni.SPEED + " FLOAT, "
		+ Posizioni.ACCURACY + " FLOAT, "
		+ Posizioni.BEARING + " FLOAT, "
		+ Posizioni.PROVIDER + " TEXT "+");";
		
	//creare la tabella sessioni
	private static final String CREATE_TABLE_SESSIONI =
		"create table sessioni (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ Sessioni.DATE + " DATETIME" + ");";
	
 
    /**
     * Metodo usato per creare il DB se non esiste
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_POSIZIONI);
        db.execSQL(CREATE_TABLE_SESSIONI);
    }
    /**
     * Metodo usato per fare upgrade del DB se il numero di versione nuovo e' maggiore del vecchio
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Posizioni.TABELLA);
        db.execSQL("DROP TABLE IF EXISTS "+ Sessioni.TABELLA);
        onCreate(db);
    }
}