package sg.com.leotech.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.WeightedGraph;
import org.junit.Test;

import sg.com.leotech.components.Route;
import sg.com.leotech.components.Station;
import sg.com.leotech.helpers.TrainNetworkRouteImpl;
import sg.com.leotech.program.RouteProgram;

public class TrainNetworkTest
{
	private TrainNetworkRouteImpl testInstance;
	
	/**
	 * Create an instance with an empty network and no stations.
	 */
	@Test
	public void createTrainNetworkGraphEmptyTest()
	{
		testInstance = new RouteProgram("");
		
		assertNotNull(testInstance.getTrainNetwork());
		assertNotNull(testInstance.getStationsMap());
		assertTrue(testInstance.getStationsMap().isEmpty());
	}
	
	/**
	 * Create an instance with a non existing file.
	 */
	@Test
	public void createTrainNetworkGraphNoFileTest()
	{
		testInstance = new RouteProgram("nofile");
		assertNotNull(testInstance.getTrainNetwork());
		assertNull(testInstance.getStationsMap());
	}
	
	/**
	 * Create an instance with an invalid file.
	 * Invalid route line format.
	 */
	@Test
	public void createTrainNetworkGraphInvalidFileTest()
	{
		testInstance = new RouteProgram("trainMapInvalid1.txt");
		assertNotNull(testInstance.getTrainNetwork());
		assertNull(testInstance.getStationsMap());
	}
	
	/**
	 * Create an instance with an invalid file.
	 * Invalid route line format.
	 */
	@Test
	public void createTrainNetworkGraphInvalidFileTest2()
	{
		testInstance = new RouteProgram("trainMapInvalid3.txt");
		assertNotNull(testInstance.getTrainNetwork());
		assertNull(testInstance.getStationsMap());
	}
	
	/**
	 * Create an instance with an invalid file.
	 * Invalid route line format.
	 */
	@Test
	public void createTrainNetworkGraphInvalidFileTest3()
	{
		testInstance = new RouteProgram("trainMapInvalid4.txt");
		assertNotNull(testInstance.getTrainNetwork());
		assertNull(testInstance.getStationsMap());
	}
	
	/**
	 * Create an instance with an invalid file.
	 * Empty file.
	 */
	@Test
	public void createTrainNetworkGraphEmptyFileTest()
	{
		testInstance = new RouteProgram("trainMapInvalid2.txt");
		assertNotNull(testInstance.getTrainNetwork());
		assertNull(testInstance.getStationsMap());
	}
	
	/**
	 * Create an instance with an empty network and no stations.
	 */
	@Test
	public void getShortestTripDurationTest()
	{
		testInstance = new RouteProgram("");
		WeightedGraph<Station, Route> map = testInstance.getTrainNetwork();
		testInstance.setStationsMap(populateTrainNetwork(map));
		Map<String, Station> stations = testInstance.getStationsMap();
		
		double tripTime;
		tripTime = testInstance.getShortestTripDuration(map, 
									stations.get("A"), stations.get("D"));
		assertEquals(1.0d, tripTime, 0.0d);
		
		tripTime = testInstance.getShortestTripDuration(map, 
									stations.get("C"), stations.get("D"));
		assertEquals(9.0d, tripTime, 0.0d);
		
		tripTime = testInstance.getShortestTripDuration(map, 
									stations.get("A"), stations.get("C"));
		assertEquals(Double.POSITIVE_INFINITY, tripTime, 0.0d);
		
	}

	private Map<String, Station> populateTrainNetwork(WeightedGraph<Station, Route> map)
	{
		Map<String, Station> stationsMap = new HashMap<String, Station>();

		// Add stations.
		Station A = new Station("A");
		map.addVertex(A);
		stationsMap.put("A", A);

		Station B = new Station("B");
		map.addVertex(B);
		stationsMap.put("B", B);

		Station C = new Station("C");
		map.addVertex(C);
		stationsMap.put("C", C);
		
		Station D = new Station("D");
		map.addVertex(D);
		stationsMap.put("D", D);

		// Add routes.
		/*
		 *         7
		 *      |----C
		 *  3   |     
		 * -----|     ----|
		 * |    v    3|   v
		 * A    B-----|   D
		 * |<---|         ^
		 * |  1           |1
		 * |--------------|
		 * 
		 */
		map.setEdgeWeight(map.addEdge(A, B), 3);
		map.setEdgeWeight(map.addEdge(A, D), 1);
		map.setEdgeWeight(map.addEdge(B, A), 1);
		map.setEdgeWeight(map.addEdge(B, D), 3);
		map.setEdgeWeight(map.addEdge(C, B), 7);

		return stationsMap;
	}	
}
