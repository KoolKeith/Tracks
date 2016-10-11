package schiavo.tracks;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
 
public class Posizioni {
    //I campi della tabella Posizioni
    public static final String IDp = "_id";
    public static final String IDs = "ids";
    public static final String LAT = "lat";
    public static final String LONGI = "longi";
    public static final String ALTITUDE = "altitude";
    public static final String SECONDS = "seconds";
    public static final String SPEED = "speed";
    public static final String ACCURACY = "accuracy";
    public static final String BEARING = "bearing";
    public static final String PROVIDER = "provider";
    public static final String TABELLA = "posizioni";
    public static final String[] COLONNE = new String[]{IDp, IDs, LAT, LONGI, ALTITUDE, SECONDS, SPEED, ACCURACY, BEARING, PROVIDER};
    /**
     * Funzione statica usata per inserire la posizione nel DB
     */
    public static void insertPosizione(SQLiteDatabase db, long ids, double lat, double longi, double altitude, long seconds, float speed, float accuracy, float bearing, String provider){
        ContentValues v = new ContentValues();
        v.put(IDs, ids);
        v.put(LAT, lat);
        v.put(LONGI, longi);
        v.put(ALTITUDE, altitude);
        v.put(SECONDS, seconds);
        v.put(SPEED, speed);
        v.put(ACCURACY, accuracy);
        v.put(BEARING, bearing);
        v.put(PROVIDER, provider);
        db.insert(TABELLA, null, v);
    }
    /**
     * Ritorna un cursore che punta a tutte le posizioni contenute nel DB della sessione passata a parametro
     */
    public static Cursor getPosizioni(SQLiteDatabase db, long idss){
        return db.query(TABELLA, COLONNE, IDs+" = "+idss, null, null, null, null);
    }
    public static long getCountPos(SQLiteDatabase db, String idss){
    	String sql = "SELECT COUNT(*) FROM POSIZIONI WHERE IDs = "+idss;
    	db.rawQuery(sql, null);
        SQLiteStatement statement = db.compileStatement(sql);
        return statement.simpleQueryForLong();
    }
    /**
     * Ritorna un cursore che punta a tutte le posizioni contenute nel DB
     */
    public static Cursor getAllPosizioni(SQLiteDatabase db){
        return db.query(TABELLA, COLONNE, null, null, null, null, null);
    }
    /**
     * Cancella la posizione che ha l'id passato come parametro
     */
    public static boolean deletePosizione(SQLiteDatabase db, long id) {
        return db.delete(TABELLA, IDp + "=" + id, null) > 0;
    }
    /**
     * Ritorna un cursore che punta alla posizione che ha l'id passato come parametro
     * @throws SQLException
     */
    public static Cursor getPosizione(SQLiteDatabase db, long id) throws SQLException {
        Cursor c = db.query(true, TABELLA, COLONNE, IDp + "=" + id, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    /**
     * Modifica i valori della posizione il cui id e' uguale a quello passato come parametro
     */
    public static boolean updatePosizioni(SQLiteDatabase db, long idp, long ids, double lat, double longi, double altitude, long seconds, float speed, float accuracy, float bearing, String provider){
        ContentValues v = new ContentValues();
        v.put(LAT, lat);
        v.put(IDs, ids);
        v.put(LONGI, longi);
        v.put(ALTITUDE, altitude);
        v.put(SECONDS, seconds);
        v.put(SPEED, speed);
        v.put(ACCURACY, accuracy);
        v.put(BEARING, bearing);
        v.put(PROVIDER, provider);
        return db.update(TABELLA, v, IDp + "=" + idp, null) >0; 
    }
 
}