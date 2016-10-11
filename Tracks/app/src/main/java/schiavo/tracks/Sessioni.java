package schiavo.tracks;


import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
 
public class Sessioni {
    //I campi della tabella Sessioni
	
    public static final String IDs = "_id";
    public static final String DATE = "data";
    public static final String TABELLA = "sessioni";
    public static final String[] COLONNE = new String[]{ IDs, DATE};
    /**
     * Funzione statica usata per inserire la posizione nel DB con return l'id progressivo
     */
    public static int insertSessione(SQLiteDatabase db){
        db.execSQL("insert into sessioni (data) values (datetime(current_timestamp));");
        Cursor c = db.query(TABELLA, COLONNE, null, null, null, null, null);
        c.moveToLast();
        return c.getInt(0);
    }
    /**
     * Ritorna un cursore che punta a tutte le posizioni contenute nel DB
     * @return
     */
    public static Cursor getAllSessioni(SQLiteDatabase db){
        return db.query(TABELLA, COLONNE, null, null, null, null, null);
    }
    /**
     * Cancella la posizione che ha l'id passato come parametro
     * @return
     */
    public static boolean deleteSessione(SQLiteDatabase db, String ids) {
        return db.delete(TABELLA, IDs + "=" + ids, null) > 0;
    }
    /**
     * Ritorna un cursore che punta alla posizione che ha l'id passato come parametro
     * @return
     * @throws SQLException
     */
    public static Cursor getSessione(SQLiteDatabase db, long ids) throws SQLException {
        Cursor c = db.query(true, TABELLA, COLONNE, IDs + "=" + ids, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
}