
public class dam 
{
 /**
 * static main : String[] -> void
 * @param args : The command line arguments
 * Effect: The program starts execution from this method.
 *         If the 'args' is a valid command compares the two files
 *         else prints message through standard error and exits with 
 *         status other than 0
 */
 public static void main(String[] args)
 {
  String pattern = "-f|-d <pathname> -f|-d <pathname>";
  CommandLineArgsParser.validateCommand(args, pattern);
  CompareFiles cm = new CompareFiles(args);
 }
}