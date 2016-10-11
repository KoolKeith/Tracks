package schiavo.tracks;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;


/**
 *
 * @author Fabio
 * Questa classe consente di gestire l'attivazione e disattivazione del listener GPS,
 * ovvero attivare e stoppare la ricezione/salvataggio delle coordinate gps sul db
 */
public class GPSManager {
    private Activity activity;
    //LocationManager consente di gestire il listener GPS
    private LocationManager mlocManager = null;
    //Il location listener permette di assegnare degli eventi agli stati di ricezione gps
    private LocationListener gpsListener = null;
    private int idss = 0;
    private SQLiteDatabase db;

    /**
     * Metodo costruttore
     * @param activity
     * @param idss
     * @param db
     */
    public GPSManager(Activity activity, int idss, SQLiteDatabase db) {
        this.activity = activity;
        this.idss = idss;
        this.db = db;
    }
    /**
     * Type: Procedure
     * Purpose: Interrompere la ricezione coordinate GPS
     */
	public void stop(){
		if((mlocManager != null)&&(gpsListener!=null))
			//rimuove tutti gli aggiornamenti della losizione del gpsListener
			//dopo questa istruzione non avverrano piu' ricezioni del segnale
            mlocManager.removeUpdates(gpsListener);
		gpsListener = null;
		mlocManager = null;
	
	}

	/**
	 * Type: Procedure
	 * Purpose: Consentire all'utente di attivare il sensore GPS sul device
	 * Description: Si inizializza il LocationManager, si richiede all'utente di attivare gps con alertDialog.
	 * Avvio ricezione con salvataggio su db
	 */
    public void start() {





        mlocManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //verifica se il provider che gestisce la ricezione del GPS e' abilitato
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            findLoc();
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    //si fa partire l'activity che permette di selezionare la fonte gps disponibile sul device. Activity OS
                                    activity.startActivity(callGPSSettingIntent);
                                    findLoc();
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            //si mostra il dialog appena configurato
            alert.show();
        }
    }
    /**
     * Type: Procedure
     * Purpose: configurare la ricezione delle coordinate gps
     */
    public void findLoc() {
    	gpsListener = new GPSListener(activity, mlocManager, idss, db);
    	//si configura la richiesta d'aggiornamento del segnale GPS
    	//con il provider del servizio, la distanza minima individuabile in metri, tempo minimo intervallare in millisecondi e la classe Listener


        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1,gpsListener);
    }
}