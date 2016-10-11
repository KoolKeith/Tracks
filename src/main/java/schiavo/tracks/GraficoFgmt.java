package schiavo.tracks;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author Fabio
 * Classe che estende fragment e implementa l'interfaccia FragmentCommunicator, 
 * creata appositamente per ricevere le quote.
 */

public class GraficoFgmt extends Fragment implements  FragmentCommunicator {
	private  GraphViewSeries exampleSeries = null;
	private  GraphView graphView = null;
	static LinearLayout chartContainer;
	private View view;
	Context context;
	private static GraphViewData quote []; 
	private GraphViewData[] newValues;
	//contatore coordinate
	private int i=0;
	private boolean first = true;
	
	/**
	 * Type: Function
	 * Purpose: Settare il grafico al momento della creazione dell'interfaccia grafica del fragment
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		graphView = new LineGraphView(getActivity().getBaseContext(), "QUOTA");
		graphView.setBackgroundColor(-16777216);
		view = (LinearLayout) inflater.inflate(R.layout.grafico, container, false);
		chartContainer = (LinearLayout) view.findViewById(R.id.graph1);
		chartContainer.addView(graphView);
		return view;
	}
	
	/**
	 * implementato dell'interfaccia FragmentCommunicator.
	 * Metodo per visualizzare le coordinate in real time
	 */
	@Override
	public void setChartData(double x, double y, boolean realT){
		int j;
		//la variabile first identifica se la variabile ricevuta e' la prima della serie o una successiva
		if(first==true){
			quote = new GraphViewData[] {new GraphViewData(x, y)};
			exampleSeries = new GraphViewSeries(quote);
			first = false;
		}else if((i<100)&&(realT==true)){
			//se le coordinare sono minori di 100 e la visualizzazione e' in real time, duplica l'array estendendolo
			duplicArr(x,y);
			fineConf();
			i++;
		}else if((i>=100)&&(realT==true)){
			//se le coordinate sono maggiori di 100 e la visualizzazione e' in real time, trasla il contenuto dell'array
			for( j=1; j<quote.length; j++)
				quote[j-1] = quote[j];		
			quote[j-1]= new GraphViewData(x, y);
			fineConf();
		}else if(realT==false){
			//se la visualizzazione non e' in real time, visualizza tutto cio' che gli vene passato a parametro
			duplicArr(x,y);
			fineConf();
		}
	}
	/**
	 * Type: Routine
	 * Purpose: gestione dinamica della memoria centrale. Allungare dimensione vettore.
	 * @param x
	 * @param y
	 */
	private void duplicArr(double x, double y){
		//l'array viene inizializzato con la dimensione dell'array quote+1
		newValues = new GraphViewData[quote.length + 1];
		//copia quote il new Values			
		System.arraycopy(quote, 0, newValues, 0, quote.length);
		//all'ultima cella di newValues, si alloca un oggetto GraphViewData
		newValues[quote.length] = new GraphViewData(x, y);
		quote = newValues;
	}
	
	/**
	 * Type: Purpose
	 * Purpose: aggiungere al grafico la spezzata e aggiungerlo al container
	 */
	private void fineConf(){
		exampleSeries.resetData(quote);
		graphView.addSeries(exampleSeries);
		chartContainer.removeAllViews();
		chartContainer.addView(graphView);
	}
}