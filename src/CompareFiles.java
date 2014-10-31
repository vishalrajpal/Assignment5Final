import java.io.File;




public class CompareFiles {

	CompareFiles(String[] args)
	{
		File[] firstPathFiles = null,secondPathFiles = null;
		if(args[0].equals("-d"))
		{
			firstPathFiles = getDirectoryFiles(args[1]); 
		}
		else 

		{
			try
			{
				File getFile = new File(args[1]);
				firstPathFiles[0] = getFile;
			}
			catch(Exception e)
			{
			  String exString = "ERROR: Invalid File Path : "+ args[1];
  	    	  System.out.println(exString);
			}
		}
		
		if(args[2].equals("-d"))
		{
			secondPathFiles = getDirectoryFiles(args[3]);
		}
		else
		{
			try
			{
				File getFile = new File(args[1]);
				secondPathFiles[0] = getFile;
			}
			catch(Exception e)
			{
			  String exString = "ERROR: Invalid File Path : "+ args[3];
  	    	  System.out.println(exString);
			}
		}
	compareAllFiles(firstPathFiles,secondPathFiles);	
	}
	
	File[] getDirectoryFiles(String path)
	{
		File[] filesToCompare = null;
		if(path != null)
		   {
		    File dirOfFiles = new File(path);
		    File[] listOfFiles = dirOfFiles.listFiles();
		    for(int i=0; i<listOfFiles.length;i++)
		    {
		    	if(listOfFiles[i].isFile())
		    	{
		    		 String fileName= listOfFiles[i].getName();
		    		 if(fileName.endsWith(".mp3")||(fileName.endsWith(".wav")))
		    		 {
		    			 filesToCompare[i] = listOfFiles[i];
		    		     
		    		 }
		    		 else break;
		    		 
		    	}
		    }
		   }
		   else
		   {
		    String exString = "ERROR: Passed Invalid File Path : "+ path;
		    NullPointerException ex = new NullPointerException(exString);
		    throw ex;
		   }
		return filesToCompare;
	}
	
	void compareAllFiles(File[] firstPathNameFiles,File[] secondPathNameFiles)
	{
		for(int x=0; x<firstPathNameFiles.length;x++)
		{
			for(int y=0; y<secondPathNameFiles.length;y++)
			{
				
			}
		}
	}

}
