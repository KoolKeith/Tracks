package schiavo.tracks;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import com.google.android.gms.maps.MapFragment;

/**
 * Questa classe si occupa di gestire l'interfaccia grafica nel momento in cui viene 
 * avviata la registrazione oppure quando si visualizza un percorso gi� registrato.
 * A questa Activity deve essere passato a parametro un flag per sapere se deve gestire una registrazione o un visualizzazione
 * */
public class Tracker extends Activity implements ActionBar.TabListener {
	private SQLiteDatabase db;
	private DBHelper databaseHelper;
	//si dichiara e innizializza il fragment per la mappa
	public static MapFragment firstMap = MapFragment.newInstance();
	//variabile per il fragment grafico
	public static Fragment chartQ;
	private String Skey;
	private char flag=' ';
	private GPSManager gps;
	FrameLayout container = null;
	
	@Override
	public void onAttachedToWindow(){
		super.onAttachedToWindow();
		if(flag=='v'){
			//si dichiara e inizializza la variabile/thread per poter eseguire in modo asincrono le istruzioni della classe Drawer
			ViewerDB vdb = new ViewerDB(Skey, db);
			vdb.execute();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = this.getIntent().getExtras();	
		Skey = extras.getString("Skey");
		flag = extras.getChar("flag"); 
		container = new FrameLayout(this);
		container.setLayoutParams(new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT));
		//si impone un id in modo arbitrario al container
		//noinspection ResourceType
		container.setId(1245666912);
		//nella visualizzazione dell'activity inposto un determinato layout (container)
		setContentView(container);
		//inizializzo l'oggetto chartQ
		chartQ = new GraficoFgmt();
		//aggiungo al Manager di fragment la mappa e il Fragment chart
		getFragmentManager().beginTransaction()
			.add(1245666912, firstMap).hide(firstMap)
			.add(1245666912, chartQ).hide(chartQ)
			.commit();
		// Set up l'action bar per mostrare i tabs.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//si aggiunge un tab per ogni fragment d'aggiungere
		actionBar.addTab(actionBar.newTab().setText("First Map").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("ChartQ").setTabListener(this));
		try{
			databaseHelper = new DBHelper(this);
			//apro il DB sia in lettura che in scrittura
			db = databaseHelper.getWritableDatabase(); 
		}catch (Exception e){
			Log.w("errore ", e);
		}
	
		
	}
	@Override
	public void onStart(){
		super.onStart();
		 if(flag=='r'){

			 //queste due istruzioni devono essere eseguite solo se si vuole registrare un nuovo percorso
			 gps = new GPSManager(Tracker.this, Integer.parseInt(Skey), db);
			 gps.start();
		 }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getMenuInflater();
		mMenuInflater.inflate(R.menu.tracker_activity_holo_dark_action_bar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//interrompe la registrazione
		if(item.getItemId()==R.id.abStopHoloDark){
		if(gps!=null){
			gps.stop();
			//se alla nuova sessione creata, non � referenziata neanche una posizione, viene eliminata
			if(Posizioni.getCountPos(db, Skey)<=0)
				Sessioni.deleteSessione(db, Skey);
		}
			this.finish();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		//permette uno swith dei fragment sui tab quando vengono selezionati
		if(tab.getPosition() == 0){
			getFragmentManager().beginTransaction().hide(chartQ).commit();
			getFragmentManager().beginTransaction().show(firstMap).commit();
			fragmentTransaction.attach(firstMap);
		}else{
			getFragmentManager().beginTransaction().hide(firstMap).commit();
			getFragmentManager().beginTransaction().show(chartQ).commit();
			fragmentTransaction.attach(chartQ);
		}
	}
	
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		//operazione inversa del metodo onTabSelected. l'uso di questi metodi permettono di switch correttamente le visualizzazioni dei fragment
		if(tab.getPosition() == 0){
			//commit permette di preservare lo stato del fragment durante la transizione di stato
			getFragmentManager().beginTransaction().hide(firstMap).commit();
			getFragmentManager().beginTransaction().show(chartQ).commit();
			//re-attach un fragment. lo ancora.
			fragmentTransaction.attach(chartQ);
		}else{ 
			getFragmentManager().beginTransaction().hide(chartQ).commit();
			getFragmentManager().beginTransaction().show(firstMap).commit();
			fragmentTransaction.attach(firstMap);
		}
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		//metodo chiamato per ottenere uno stato preistanziato da un'activity prima di essere killed, cos� da poter essere memorizzato
		super.onSaveInstanceState(outState);
		getFragmentManager().putFragment(outState, "firstMap", firstMap);
		getFragmentManager().putFragment(outState, "chartQ", chartQ);
	}
	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		//metodo chiamato dopo onStart() quando l'activity e stata re-inizializzata da uno stato salvato precedentemente
		super.onRestoreInstanceState(savedInstanceState);
		getFragmentManager().findFragmentByTag("chartQ");
		getFragmentManager().findFragmentByTag("firstMap");
		getFragmentManager().beginTransaction()
		.add(1245666912, firstMap).hide(firstMap)
		.add(1245666912, chartQ).hide(chartQ)
		.commit();

	}
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	//questo metodo deve essere necessariamente inizializzato.
	}
}