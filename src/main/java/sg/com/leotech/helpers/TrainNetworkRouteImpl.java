package sg.com.leotech.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sg.com.leotech.components.Route;
import sg.com.leotech.components.Station;

public abstract class TrainNetworkRouteImpl implements TrainNetwork<Station, Route>
{
	private static final String COMMA = ",";

	private static final String SPACE = " ";

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(TrainNetworkRouteImpl.class);

	private WeightedGraph<Station, Route> trainNetwork;

	private Map<String, Station> stationsMap;

	/**
	 * Default constructor. Used for creating just the class and manually set the train
	 * network and stations map.
	 */
	public TrainNetworkRouteImpl()
	{
	}

	/**
	 * Constructor that takes a file name as an argument. Proceeds to create the train
	 * network and populate it using the file specified.
	 * 
	 * @param sourceFilePath
	 *            the file with the data/details of the train network
	 */
	public TrainNetworkRouteImpl(final String sourceFilePath)
	{
		createTrainNetwork(sourceFilePath);
	}

	/**
	 * Creates an empty graph for the train network and sets it to the class member
	 * <i>trainNetwork</i>. Populates the graph of the train network using the file to
	 * figure out the station and routes. Builds a hash map for the station names using
	 * their name as the key and sets it to the class member <i>stationsMap</i>. If the
	 * source file passed is an empty string then it just creates an empty train network
	 * with no nodes.
	 * 
	 * @param sourceFilePath
	 *            the file with the data/details of the train network
	 */
	private void createTrainNetwork(final String sourceFilePath)
	{
		// Creates an empty graph for Stations and Routes.
		this.trainNetwork = createTrainNetworkGraph();

		// Get file from resources folder and populate graph/map.
		if (!sourceFilePath.isEmpty())
		{
			ClassLoader classLoader = getClass().getClassLoader();
			File mapFile;
			try
			{
				URL sourceFileUrl = classLoader.getResource(sourceFilePath);
				if (sourceFileUrl == null)
				{
					throw new FileNotFoundException(
							"File [" + sourceFilePath + "] not found");
				}

				mapFile = new File(sourceFileUrl.toURI());
				// Create the train network map from a file in the resources.
				this.stationsMap = populateTrainNetwork(trainNetwork, mapFile);
			}
			catch (URISyntaxException e)
			{
				LOGGER.error(e.getMessage());
				this.stationsMap = null;
			}
			catch (FileNotFoundException e)
			{
				LOGGER.error(e.getMessage());
				this.stationsMap = null;
			}
		}
		else
		{
			this.stationsMap = Collections.emptyMap();
		}
	}

	/**
	 * Create a new empty directed weighted graph to use later to populate the network.
	 * 
	 * @return an empty directed weighted graph
	 */
	@Override
	public WeightedGraph<Station, Route> createTrainNetworkGraph()
	{
		return new DefaultDirectedWeightedGraph<Station, Route>(Route.class);
	}

	/**
	 * Reads a text file of a specific format and populates the empty graph passed as an
	 * argument.
	 * 
	 * @param map
	 *            an empty graph o be populated
	 * @param file
	 *            the URI to the file describing the network
	 * @return returns a hash map of all the nodes using their name as a key.
	 */
	@Override
	public Map<String, Station> populateTrainNetwork(WeightedGraph<Station, Route> map,
			File file)
	{
		Map<String, Station> stationsMap = new HashMap<String, Station>();

		BufferedReader bfr = null;
		Pattern pattern;

		try
		{
			bfr = new BufferedReader(new FileReader(file));
			// Where each line of the input file is stored.
			String line;

			// Separator for stations.
			pattern = Pattern.compile(COMMA);
			// Read stations.
			line = bfr.readLine();
			if (line == null)
			{
				throw new IOException("The file is empty.");
			}
			// Create station objects and add them to the hash map for easy lookup later.
			for (String stationName : pattern.split(line))
			{
				Station station = new Station(stationName.trim());
				stationsMap.put(stationName, station);
				map.addVertex(station);
			}

			// Separator for routes.
			pattern = Pattern.compile(SPACE);
			// Read rest of file to build routes.
			while ((line = bfr.readLine()) != null)
			{
				String routeDetails[] = pattern.split(line.trim());
				if (routeDetails.length != 3)
				{
					throw new IOException("Each route line must have three parts; "
							+ "origin, destination, trip duration. (i.e. A B 4)");
				}

				// Create route
				Station origin = stationsMap.get(routeDetails[0].trim());
				Station destination = stationsMap.get(routeDetails[1].trim());
				if (origin == null || destination == null)
				{
					throw new IOException("The specified stations don't exist. " + "["
							+ routeDetails[0] + ", " + routeDetails[1] + "]");
				}

				double tripDuration;
				try
				{
					tripDuration = Double.valueOf(routeDetails[2]);
				}
				catch (NumberFormatException e)
				{
					throw new IOException("The trip duration must be a valid number.");
				}

				map.setEdgeWeight(map.addEdge(origin, destination), tripDuration);
			}
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error(e.getMessage());
			return null;
		}
		catch (PatternSyntaxException e)
		{
			LOGGER.error(e.getMessage());
			return null;
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage());
			LOGGER.error("Invalid file content format.");
			return null;
		}
		finally
		{
			// Close stream.
			if (bfr != null)
			{
				try
				{
					bfr.close();
				}
				catch (IOException e)
				{
					// do nothing
					LOGGER.warn("Reader did not close correctly.");
				}
			}
		}

		return stationsMap;
	}

	/**
	 * It calculates the shortest trip between two node based on the graph/map passed as
	 * an argument.
	 * 
	 * @param map
	 *            the graph/map of the network
	 * @param origin
	 *            the node the route starts from
	 * @param destination
	 *            the node the route end on
	 * @return shortest trip duration from origin to destination.
	 */
	@Override
	public double getShortestTripDuration(WeightedGraph<Station, Route> map,
			Station origin, Station destination)
	{
		DijkstraShortestPath<Station, Route> dsp = new DijkstraShortestPath<Station, Route>(
				map, origin, destination);

		return dsp.getPathLength();
	}

	public WeightedGraph<Station, Route> getTrainNetwork()
	{
		return trainNetwork;
	}

	public Map<String, Station> getStationsMap()
	{
		return stationsMap;
	}

	public void setStationsMap(Map<String, Station> stationsMap)
	{
		this.stationsMap = stationsMap;
	}

}
