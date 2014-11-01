public class AssertTests {
  /**
   * assertTrue : String, boolean -> void
   * 
   * @param errorMsg
   *            : The errorMsg to print if 'isTrue' is false
   * @param isTrue
   *            : if false the 'errorMsg' is printed through standard error
   *            and the program exits through status other than 0.
   */
  private static int exitStatus = 0;

  public static boolean assertTrue(String errorMsg, boolean isTrue) 
  {
    if (!isTrue) 
    {
      exitStatus = 1;
      System.err.println("ERROR: " + errorMsg);
      return false;
    }
    return isTrue;
  }

    public static boolean assertTrue(String errorMsg, boolean isTrue,
            boolean toExit) {
        assertTrue(errorMsg, isTrue);
        if (!isTrue && toExit)
            System.exit(1);
        return isTrue;
    }

    public static void exitWithValidStatus() {
        System.exit(exitStatus);
    }
}