package sg.com.leotech.program;

public class RouteProgramEntry
{
	private static final String SOURCE_FILE = "trainMap.txt";
	
	public static void main(String[] args)
	{
		/*
		 * Creates program instance.
		 */
		RouteProgram program = new RouteProgram(SOURCE_FILE);
		
		/*
		 * Starts a loop of getting user input and processing it.
		 */
		program.processUserInput();

		/*
		 * Goodbye message.
		 */
		System.out.println("Program exiting...");
	}
}
