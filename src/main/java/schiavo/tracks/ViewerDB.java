package schiavo.tracks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * AsyncTask abilita un corretto e facile utilizzo del UI thread
 * Questa classe permette di eseguire in backgruond operazioni e pubblicarle sul UI thread
 * senza avere la necessita' di manipolare threads e/o handlers
 */
public class ViewerDB extends AsyncTask<Void, Void, Cursor>{
	private Drawer dr;

	/** 
	 * Metodo costruttore
	 */
	public ViewerDB(String idss, SQLiteDatabase db) {
		dr = new Drawer(idss, db);
	}
	@Override
	protected Cursor doInBackground(Void... params) {
		// TODO Auto-generated method stub
		return dr.interroga();
	}
	@Override
    protected void onPostExecute(Cursor c) {
		dr.traccia(c);
	}
}