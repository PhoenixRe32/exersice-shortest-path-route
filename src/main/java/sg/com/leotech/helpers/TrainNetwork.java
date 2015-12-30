package sg.com.leotech.helpers;

import java.io.File;
import java.util.Map;

import org.jgrapht.WeightedGraph;

import sg.com.leotech.components.Station;

public interface TrainNetwork<V, E>
{
	/**
	 * Create a new empty directed weighted graph to use later to populate the network.
	 * 
	 * @return an empty directed weighted graph
	 */
	public WeightedGraph<V, E> createTrainNetworkGraph();

	/**
	 * Reads a text file of a specific format and populates the empty graph passed as an
	 * argument.
	 * 
	 * @param map
	 *            an empty graph to be populated
	 * @param file
	 *            the URI to the file describing the network
	 * @return returns a hash map of all the nodes using their name as a key.
	 */
	public Map<String, Station> populateTrainNetwork(WeightedGraph<V, E> map, File file);
	
	/**
	 * It calculates the shortest trip between two node based on the graph/map passed as
	 * an argument.
	 * @param map			the graph/map of the netwrok
	 * @param origin		the node the route starts from
	 * @param destination	the node the route end on
	 * @return				shortest trip duration from origin to destination.
	 */
	double getShortestTripDuration(WeightedGraph<V, E> map, V origin, V destination);
}
