import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CompareFiles 
{
	Map<String, AudioProcessableFile> filesProcessed;
	CompareFiles(String[] args)
	{
		File[] firstPathFiles;
		File[] secondPathFiles;
		firstPathFiles = parseArgAndPath(args[0], args[1]);
		secondPathFiles = parseArgAndPath(args[2], args[3]);
		filesProcessed = new HashMap<String, AudioProcessableFile>();
		compareAllFiles(firstPathFiles,secondPathFiles);	
	}
	
	File[] parseArgAndPath(String arg, String path)
	{
		File[] initArr = new File[1];
		if(arg.equals("-d"))
		{
			initArr = validateDirAndGetFiles(path); 
		}
		else 
		{
			try
			{
				File getFile = new File(path);
				initArr[0] = getFile;
			}
			catch(Exception e)
			{
			  String exString = "ERROR: Invalid File Path : "+ arg;
  	    	  System.out.println(exString);
			}
		}
		return initArr;
	}
	
	File[] validateDirAndGetFiles(String path)
	{
		File dirOfFiles;
		File[] listOfFiles;
		if(path != null)
		{
		    dirOfFiles = new File(path);
		    if(!dirOfFiles.isDirectory())
		    {
		    	AssertTests.assertTrue(path+":Invalid Directory", false, true);
		    	return null;
		    }
		    listOfFiles = dirOfFiles.listFiles();
		}
		   else
		   {
		    String exString = "ERROR: Passed Invalid File Path : "+ path;
		    NullPointerException ex = new NullPointerException(exString);
		    throw ex;
		   }
		return listOfFiles;
	}
	
	void compareAllFiles(File[] firstPathNameFiles,File[] secondPathNameFiles)
	{
		int NoOfDirInPath1 = firstPathNameFiles.length;
		int NoOfDirInPath2 = secondPathNameFiles.length;
		for(int path1Count=0; path1Count<NoOfDirInPath1; path1Count++)
		{
			String file1Path = firstPathNameFiles[path1Count].getPath();
			AudioProcessableFile path1File = getProcessableFile(file1Path);
			if(path1File==null)
				continue;
			for(int path2Count=0; path2Count<NoOfDirInPath2;path2Count++)
			{
				String file2Path = secondPathNameFiles[path2Count].getPath();
				AudioProcessableFile path2File = getProcessableFile(file2Path);
				if(path2File==null)
					continue;
				path1File.compare(path2File);
			}
		}
		
		AssertTests.exitWithValidStatus();
	}
	
	AudioProcessableFile getProcessableFile(String filePath)
	{
		AudioProcessableFile f = filesProcessed.get(filePath);
		if(f == null)
		{
			f = AudioProcessableFiles.make(filePath);
			filesProcessed.put(filePath, f);
		}
		return f;
	}
	
}
