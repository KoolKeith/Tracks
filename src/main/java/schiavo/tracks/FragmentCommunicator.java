package schiavo.tracks;

// FragmentCommunicator e' un'interfaccia usata per passare dati da un'Activity al Fragment
public interface FragmentCommunicator {
	public void setChartData(double x, double y, boolean realT);
}
