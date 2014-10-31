public interface AudioProcessableFile 
{
 /**
  * readSamples: -> float[]
  * @return : The samples of an audio file
  */
  float[] getSamples();
    
 /**
  * validateFile: -> void
  * @effect: Validates the file to check if it is one of the supported formats
  */
  boolean validateFile(); 
  
 /**
  * compare : AudioProcessableFile -> void
  * @param fileToCmp : The AudioProcessableFile to compare
  * @effect: Compares this and 'fileToCmp' in two steps
  * a) Compares the length of two files. If same proceeds to Step 2
  * b) Compares the FFT result of both files through Mean Squared Error
  *    If MSE is equal to 0 the files are same and a 'MATCH' message is printed
  *    through Standard output
  *    else a 'NO MATCH' message is printed
  *    In either case the program exits with a status of 0.
  */
  void compare(AudioProcessableFile fileToCmp);

 /**
  * getFileLength : -> long
  * @return long : The size of the data in the file
  */
  long getFileLength();
	
 /**
  * getFileShortName : -> String
  * @return String : The short name of the file corresponding to this
  */
    String getFileShortName();

    boolean isValidFile();
}