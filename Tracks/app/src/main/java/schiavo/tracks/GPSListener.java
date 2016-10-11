package schiavo.tracks;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;
/**
 * 
 * @author Fabio
 * Classe che implementa l'interfaccia LocationListener. Si ottengono le coordinate dal sensore gps.
 * GPS gestito ad eventi.
 */
public class GPSListener implements LocationListener {
	
	private Drawer dr;
	private Activity activity;
	
	//inizializzo l'interfaccia
	public FragmentCommunicator fc = (FragmentCommunicator) Tracker.chartQ;
	//metodo costruttore: assegno alle variabili locali i parametri e inizializzo il flag f (prima registrazione) come true
	public GPSListener(Activity activity, LocationManager lm, int idss, SQLiteDatabase db) {
		dr = new Drawer(Integer.toString(idss), db);
		this.activity = activity;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(activity, "Gps Disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(activity, "Gps Enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLocationChanged(Location loc) {
		dr.inserisci(loc);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	

}