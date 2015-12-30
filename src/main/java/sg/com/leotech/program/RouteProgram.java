package sg.com.leotech.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sg.com.leotech.components.Station;
import sg.com.leotech.helpers.TrainNetworkRouteImpl;

public class RouteProgram extends TrainNetworkRouteImpl
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RouteProgram.class);

	private static final String EXIT = "exit";

	/**
	 * Constructor with specified source file. Can be used for extending functionality...
	 * maybe.
	 * 
	 * @param sourceFilePath
	 *            the filename of the file to use to create the train network
	 */
	public RouteProgram(final String sourceFilePath)
	{
		super(sourceFilePath);
	}

	/**
	 * Default constructor for program. Used for tests.
	 */
	public RouteProgram()
	{
		super();
	}

	/**
	 * 
	 */
	protected void processUserInput()
	{
		if (!isNetworkSetUp())
		{
			System.out.println("The train network is not set up. Exiting...");
			return;
		}

		System.out.println("Welcome.\n"
				+ "Please put the origin and destination station in the format "
				+ "'A B' without the quotation marks.");

		// Reader for getting user input from console.
		BufferedReader consoleInput = new BufferedReader(
				new InputStreamReader(System.in));
		// The pattern to split the user input to tokens.
		Pattern pattern = Pattern.compile(" ");
		// Variable where the user input is stored and processed.
		String userInput;
		// Flag for the program to keep running.
		boolean exitProgram = false;

		do
		{
			try
			{
				System.out.print("Input> ");
				userInput = consoleInput.readLine();

				// If the user input is "exit", terminate program, otherwise process the
				// user input and get the time for the route.
				if (RouteProgram.EXIT.equalsIgnoreCase(userInput))
				{
					exitProgram = true;
				}
				else
				{
					processLine(userInput, pattern);
				}
			}
			catch (IOException e)
			{
				LOGGER.warn(e.getMessage());
				LOGGER.warn("Continuing with the next user input.");
			}
		}
		while (!exitProgram);

		try
		{
			if (consoleInput != null)
			{
				consoleInput.close();
			}
		}
		catch (IOException e)
		{
			LOGGER.warn(e.getMessage());
		}
	}

	private boolean isNetworkSetUp()
	{
		// This should never happen.
		if (getTrainNetwork() == null)
		{
			System.out.println(
					"There is not train network registered." + "\nSomething went wrong.");
			return false;
		}

		if (getStationsMap() == null || getStationsMap().isEmpty())
		{
			System.out
					.println("There are no stations registered." + "\nCheck input file.");
			return false;
		}

		return true;
	}

	private void processLine(String userInput, Pattern pattern)
	{
		String[] inputArgs = pattern.split(userInput);
		if (inputArgs.length != 2)
		{
			System.out.println("The input expected is two names of stations separated "
					+ "by space. (i.e. A B)");

			return;
		}

		Station origin = getStationsMap().get(inputArgs[0].trim());
		Station destination = getStationsMap().get(inputArgs[1].trim());
		if (origin == null || destination == null)
		{
			System.out.println("The specified route doesn't exist. " + "[" + inputArgs[0]
					+ ", " + inputArgs[1] + "]");
			return;
		}

		double shortestTripTime = getShortestTripDuration(getTrainNetwork(), origin,
				destination);

		if (Double.isInfinite(shortestTripTime))
		{
			System.out.println("There is no route between " + origin.getName() + " and "
					+ destination.getName());
		}
		else
		{
			System.out.println(origin.getName() + " to " + destination.getName()
					+ " takes " + shortestTripTime + " minutes.");
		}
	}
}