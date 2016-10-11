package schiavo.tracks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
/**
 * Classe che contiene i metodi che tracciano direttamente i dati (segmenti) sulla mappa e il grafico.
 * @author Fabio
 *
 */
public class Drawer{
	//valore che rappresenta la sensibilita' della distanza in metri delle misurazioni
	private final int sensMeter = 0;
	//variabile usata per identificare se il parametro ricevuto e' il primo della serie
	private boolean f = true;
	private GoogleMap mMap = Tracker.firstMap.getMap();;
	private Polyline polyline;
	private String idss;
	private SQLiteDatabase db;
	//0: partenza; 1: arrivo;
	private Marker marker0, marker1;
	private PolylineOptions rectOptions;
	private double quotaI;
	//variabile per salvataggio temporane del luogo tra i richiami del metodo segmento
	private Location loco;
	private double distTot=0;
	/**
	 * Metodo costruttore
	 * @param idss
	 * @param db
	 */
	public Drawer(String idss, SQLiteDatabase db) {
		this.idss = idss;
		this.db = db;
		f = true;
	}
	
	/**
	 * Type: Function
	 * Purpose: atto all'inizializzazione della variabile mMap e all'interrogazione delle posizioni della sessione ottenuta da parametro
	 * @return [cursor]
	 */
	public Cursor interroga(){
		mMap = Tracker.firstMap.getMap();
		return Posizioni.getPosizioni(db, Long.valueOf(idss));
	}
	
	/**
	 * Type: Procedure
	 * Purpose: Gestire la visualizzazione sul grafico dell'andamento delle quote memorizzate sul db. Usato in modalita' visualizza
	 * @param c
	 */
	public void traccia(Cursor c){
		LatLng ll = null;
		int incrP =0;
		mMap = Tracker.firstMap.getMap();
		//inizializzo il vettore quote
		//conto il numero totale di coordinare registrate
		int tot = c.getCount();
		//ottengo il passo incrementale per la rappresentazione dell'andamento della quota
		int step = tot/100;
		if(step<=1)
			//se lo step e' <= 1, significa che i valori registrati sono inferiori a 100. Si rappresentano tutti
			while(c.moveToNext()){
				ll = new LatLng(c.getDouble(2), c.getDouble(3));
				segmento(getLoc(c), false);
			}
		else{
			//se lo step e' >1, si rappresenta l'andamento della quota sul grafico
			while(c.moveToPosition(incrP)){
				segmento(getLoc(c), false);
				ll = new LatLng(c.getDouble(2), c.getDouble(3));
				incrP+=step;
			}
		}
		//a differenza dell'inserici, in questo metodo la rappresentazione su mappa la si carica completamente in un solo
		//istante al fine di velocizzare la rappresentazione.
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 17));
		marker1.setPosition(ll);
		polyline = mMap.addPolyline(rectOptions);
		c.close();
		db.close();
	}
	/**
	 * Type: Function
	 * Purpose: Creare un oggetto Location, usando i valori ottenuti dall'interrogazione del db
	 * @param c
	 * @return loc
	 */
	private Location getLoc(Cursor c){
		Location loc = new Location(c.getString(9));
		loc.setAccuracy(c.getFloat(7));
		loc.setAltitude(c.getDouble(4));
		loc.setBearing(c.getFloat(8));
		loc.setLatitude(c.getDouble(2));
		loc.setLongitude(c.getDouble(3));
		loc.setSpeed(c.getFloat(6));
		loc.setTime(c.getLong(5));
		return loc;
	}
	/**
	 * Type: Procedure
	 * Purpose: Tracciare i segmenti
	 * Description: La variabile realTime e' usata per identificare se questo metodo viene usato per tracciare le coordinate in tempo reale o in modalita' visualizza.
	 * Se e' in tempo reale, le quote sono effettive e traslano sul grafico, inoltre la traccia precisa, ogni singolo punto rappresentato.
	 * In visualizza si osserva l'andamento delle quote e non quelle effettive. (campionate).
	 * @param loc
	 * @param realTime
	 */
	private void segmento(Location loc, boolean realTime) {
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
		//inizializzo l'interfaccia
		FragmentCommunicator fc = (FragmentCommunicator) Tracker.chartQ;
		LatLng ll = new LatLng(loc.getLatitude(), loc.getLongitude());
		mMap = Tracker.firstMap.getMap();
		//la variabile f e' usata per verificare se la posizione rilevata e' la prima della serie o una delle successive
		if(f==true){
			quotaI = loc.getAltitude();
			//marker arrivo
			marker1 = mMap.addMarker(new MarkerOptions().position(ll));
			//marker partenza
			marker0 = mMap.addMarker(new MarkerOptions().position(ll).icon(bitmapDescriptor));
			rectOptions = new PolylineOptions().add(ll);
			if(realTime==true){
				polyline = mMap.addPolyline(rectOptions);
				polyline.setColor(-16776961);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 17));
			}
			loco = loc;
			f=false;
		}else if(loco.distanceTo(loc)>sensMeter){
			if(realTime==true){
				//si pulisce la mappa per non incorrere in problemi di over heap size
				mMap.clear();
				//partenza
				mMap.addMarker(new MarkerOptions().position(marker0.getPosition()).icon(bitmapDescriptor));
				//arrivo
				marker1 = mMap.addMarker(new MarkerOptions().position(ll));
			}
			rectOptions.add(ll);
			polyline = mMap.addPolyline(rectOptions);
			distTot += loco.distanceTo(loc)/1000;
			fc.setChartData(distTot, loc.getAltitude()-quotaI, realTime);
			loco = loc;
		}
	}
	
	/**
	 * Type: Procedure
	 * Purpose: Inserire i dati nel db e tracciarli.
	 * @param loc
	 */
	public void inserisci(Location loc){	
		if(loc != null) {
			Posizioni.insertPosizione(
				db,
				Long.valueOf(idss),
				loc.getLatitude(),
				loc.getLongitude(),
				loc.getAltitude(),
				loc.getTime(),
				loc.getSpeed(),
				loc.getAccuracy(),
				loc.getBearing(),
				loc.getProvider()
			);
			segmento(loc, true);
		}
	}
}