public class dam 
{
	
	/**
	 * static main : String[] -> void
	 * @param args : The command line arguments
	 * Effect: The program sarts execution from this method.
	 *         If the 'args' is a valid command compares the two files
	 *         else prints message through standard error and exits with 
	 *         status other than 0
	 */
	public static void main(String[] args)
	{
		String pattern = "-f <pathname> -f <pathname>";
    	CommandLineArgsParser.validateCommand(args, pattern);
		AudioProcessableFile ap = AudioProcessableFiles.make(args[1]);
		AudioProcessableFile ap1 = AudioProcessableFiles.make(args[3]);
		ap.compare(ap1);
	}
	
}
