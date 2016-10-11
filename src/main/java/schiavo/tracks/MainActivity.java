/*******************************************************************
 * Class Title: MainActivity
 * Developer Name: Schiavo Fabio
 * Production's Year: 2013
 * Copyright (c) 2013 SCHIAVO FABIO
 * Description: Contiene il main, da cui s'inizializza il programma
 ******************************************************************/
package schiavo.tracks;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity  extends Activity {
	public DBHelper databaseHelper;
	//variabile oggetto che permette di eseguire operazioni con il db
	private SQLiteDatabase db;
	//variabile cursore che permette di puntare a un determinato record di una interrogazione
	private SimpleCursorAdapter adapter = null;
	//Varibile interfaccia che permette un accesso casuale di lettura e scrittura al result set di un'interrogazione sql
	private Cursor cursor = null;
	public String[] opzioni = {"cancella", "visualizza"};
	private ListView listView;
	private int toViews[] = {R.id.ids, R.id.date};
	public GPSManager gps = null;
	private String idss;
	//variabile per contenere la barra menu situata nella parte alta dell'activity
	private ActionBar ab;

	/**
	 * Type: Procedure.
	 * Purpose: Inizializza le variabili e l'interfaccia dell'activity principale.
	 * Description: Inizializza le variabili globali e assegna gli eventi ai bottoni del dialog con if a cascata
	 */
	private void initControls(){
		ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				//ottengo l'id della sessione di registrazione visualizzato nella lista
				idss = ((TextView)view.findViewById(R.id.ids)).getText().toString();		
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("Seleziona l'azione per: "+idss)
				.setItems(opzioni, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//metodo invocato quando si preme un bottone del dialog
						if(which==0){
							//0: si cancella la sessione di registrazione
							Sessioni.deleteSessione(db, idss);
							//si riaggiona la lista delle sessioni presenti nel database
							loadSessioni();
						}else if(which==1){
							//visualizzare i dati
							Intent intent = new Intent(MainActivity.this, Tracker.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							//condividere dati tra activities
							 Bundle b = new Bundle();
							//inserisco nel bundle la chiave primaria della nuova sessione creata per poterna passare a parametro alla nuova activity
							 b.putString("Skey", idss);
							 //inserisco nel bulde un flag identificatvo necessario all'activity da richiamere per fare in modo di gestire una visualizzazione e non una registrazione
							 b.putChar("flag", 'v');
							 intent.putExtras(b);
							 startActivity(intent);
						}
					}
				});
			 	AlertDialog dialog = builder.create();
			 	//si visualizza il dialog per le opzioni sulla sessione premuta
			 	dialog.show();
			}
		});
	}
	
	/**
	 * Type: Routine.
	 * Purpose: Metodo richiamato per creare l'activity.
	 * Description: Inizializza il db, richiama la procedure per l'interfaccia grafica e infine popola la listview con le sessioni dal db.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//creo l'helper per aprire il DB
		try{
			databaseHelper = new DBHelper(this);
			//apro il DB sia in lettura che in scrittura
			db = databaseHelper.getWritableDatabase();
		}catch (Exception e){
			Log.w("errore ", e);
		}
		initControls();
		loadSessioni();
	}
	
	/**
	 * Type: Procedure.
	 * Purpose: Metodo richiamato quando l'activity viene riattivata.
	 * Description: ripopola la lista delle sessioni
	 */
	@Override
	public void onResume() {
	    super.onResume(); 
	    loadSessioni();
	}
	
	/**
	 * Type: Procedure.
	 * Purpose: Popolare la lista delle sessioni nella schermata principale.
	 * Description: Toglie le sessioni gia' presenti, aggiornandole a fronte di una interogazione del db
	 */
	public void loadSessioni(){
		listView.clearChoices();
		cursor = Sessioni.getAllSessioni(db);
		adapter = new SimpleCursorAdapter(MainActivity.this, R.layout.voce_sessione2, cursor, Sessioni.COLONNE, toViews, 0);
		if(adapter!=null){
			listView.setAdapter(adapter);
		}
	}
	
	/**
	 * Type: Function.
	 * Purpose: Funzione richiamata per creare la barra del menu: ActionBar.
	 * Description: incorpora il menu xml nella barra.
	 * @param menu
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mMenuInflater = getMenuInflater();
		mMenuInflater.inflate(R.menu.main_activity_holo_dark_action_bar, menu);
		mMenuInflater.inflate(R.menu.menu, menu);
		return true;
		
	}
	
	/**
	 * Type: Function.
	 * Purpose: Assegnare eventi ai bottoni su action bar e menu.
	 * Description: Il bottone per avviare la registrazione e quelli nascosti per le info appartengono alle "opzioni".
	 * @param item
	 * @return true
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//classe contenente le opzioni che sono sull'action bar
		if(item.getItemId()==R.id.abForwardHoloDark){
				//creo una nuova sessione di registrazione
				idss = Integer.toString(Sessioni.insertSessione(db));
				Intent intent = new Intent(this, Tracker.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				//condividere data tra activities
				Bundle b = new Bundle();
				//inserisco nel bundle il carattere identificativo della funzione che l'activity richiamata deve eseguire
				b.putString("Skey", idss);
				b.putChar("flag", 'r');
				//si inseriscono delle informazioni aggiuntive nell'intent attraverso il bundle
				intent.putExtras(b);
				//si avvia l'activity che e' stata incapsulata in intent
				startActivity(intent);
				return true;
		}else if(item.getItemId()==R.id.item1){
				String AboutInfo = "Applicazione realizzata da: Schiavo Fabio. Progetto di Tesi Esame di Stato 2012/2013";
				AlertDialog.Builder AboutDialog = new AlertDialog.Builder(MainActivity.this);
				AboutDialog.setTitle("About");
				AboutDialog.setMessage(AboutInfo);
				AboutDialog.show();
			return true;
		}else if (item.getItemId()==R.id.item2){
			//informazioni da visualizzare obbligatoriamente per contratto Google sottoscritto al momento del rilascio delle api per l'app
			String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
			AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MainActivity.this);
			LicenseDialog.setTitle("Legal Notices");
			LicenseDialog.setMessage(LicenseInfo);
			LicenseDialog.show();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		databaseHelper.close();
	}


}
