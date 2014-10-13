public class AssertTests 
{
 /**
 * assertTrue : String, boolean -> void
 * @param errorMsg : The errorMsg to print if 'isTrue' is false
 * @param isTrue : if false the 'errorMsg' is printed through standard error
 *                 and the program exits through status other than 0.
 */
 public static void assertTrue(String errorMsg, boolean isTrue)
 {
  if(!isTrue)
  {
   System.err.println("ERROR "+errorMsg);
   System.exit(1);
  }
 }
}