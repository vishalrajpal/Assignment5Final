import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * class AudioProcessableFiles:
 * Description: 
 * This class converts a physical path to an instance of AudioProcessableFile.
 * The instance is only returned if and only if the file is of supported type.
 * 
 * CURRENT SUPPORTED TYPE/s: ["WAVE"]
 * 
 * WAVE: The audio file's header is validated against a specific format for
 * a WAVE file. If the header is valid below are the operations possible on an 
 * instance of AudioProcessableFile
 * 
 *  a) readSamples: Reads the audio samples of the audio file.
 *  b) compare: Compares another instance of AudioProcessableFile with this.
 *  c) getFileLength: Returns the file length of the audio file corresponding
 *                    to this.
 */

public abstract class AudioProcessableFiles
{
 /**
  * make : String -> AudioProcessableFile
  * @param filePath : The filePath for which to create a AudioProcessableFile
  * @return processableFile: If the value of 'filePath' is null throws 
  * NullPointerException, else returns an instance of type 
  * AudioProcessableFile
  */
  public static AudioProcessableFile make(String filePath)
  {
   if(filePath != null)
   {
    AudioProcessableFile processableFile = new Create(filePath);
    return processableFile;
   }
   else
   {
    String exString = "ERROR: Passed Invalid File Path : "+ filePath;
    NullPointerException ex = new NullPointerException(exString);
    throw ex;
   }
  }
	
 /** Implementation of AudioProcessableFile ADT */
  private static abstract class AudioProcessableBase 
  implements AudioProcessableFile
  {
   /* @see AudioProcessableFile#readSamples() */
    public abstract float[] readSamples();
      
   /* @see AudioProcessableFile#validateFile()*/
    public abstract void validateFile();
		
   /* @see AudioProcessableFile#compare(AudioProcessableFile) */
    public abstract void compare(AudioProcessableFile ap);
		
   /* @see AudioProcessableFile#getFileLength() */
    public abstract long getFileLength();
		
   /* @see AudioProcessableFile#getFileShortName() */
    public abstract String getFileShortName();
  }
	
 /** Create new AudioProcessableFile */
  private static class Create extends AudioProcessableBase
  {
   // Constants
   private final static int RIFF_HEXA_EQUIVALENT = 0x46464952;
   private final static int WAVE_HEXA_EQUIVALENT = 0x45564157;
   private final static int fmt_HEXA_EQUIVALENT = 0x20746D66;
   private final static int data_HEXA_EQUIVALENT = 0x61746164;
   private final static int AUDIO_FORMAT_EQUIVALENT = 1;
   private final static int STEREO_EQUIVALENT = 2;
   private final static int WAVE_SAMPLING_RATE = 44100;
   private final static int BITS_PER_SAMPLE = 16;
      
   // Instance Variables
   private long fileLength;
   private long noOfSamplesPerChannel;
   private int bitsPerSample;
   private int bytesPerSample;
   private int noOfChannels;
   private int bytesPerFrame;
   private int samplesPerFrame;
   private long frameLength;
   private FileInputStream audioFileInputStream;
   private File audioFile;
   private String filePath;
     
  /**
   * Constructor : String -> Create
   * @param filePath : The file path for which an AudioProcessableFile has 
   * to be created
   * @effect : The constructor implicitly returns an instance of type Create
   */
   Create(String filePath)
   {
    this.filePath = filePath;
    fetchFileIntoFileInputStream();
    validateFile();
   }
	
  /**
   * fetchFileIntoFileInputStream : -> void
   * @effect: Creates and loads a new FileInputStream with the file located 
   * at 'filePath'. If file not found throws FileNotFound Exception.
   */
   private void fetchFileIntoFileInputStream()
   {
    audioFile = new File(filePath);
    try 
    {
     audioFileInputStream = new FileInputStream(audioFile);
    } 
    catch (FileNotFoundException e)
    {
     AssertTests.assertTrue(filePath + ": File not found", false);
    }
   }
		
  /* @see AudioProcessableFiles.AudioProcessableBase#validateFile() */
   public void validateFile() 
   {
    byte[] arrayFor2Bytes = new byte[2];
    byte[] arrayFor4Bytes = new byte[4];
    try 
    {
     // First 4 bytes are 'RIFF'
     audioFileInputStream.read(arrayFor4Bytes);
     String riffError = filePath + ": The file should be in RIFF format";
     long riffLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
     AssertTests.assertTrue(riffError, riffLitEnd == RIFF_HEXA_EQUIVALENT);
 	
     // Skip the chunkSize
     audioFileInputStream.skip(4);
   
     // These 4 bytes should be WAVE'
     audioFileInputStream.read(arrayFor4Bytes);
     String waveError = filePath + ": The file should be in WAVE format";
     long waveLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
     AssertTests.assertTrue(waveError , waveLitEnd == WAVE_HEXA_EQUIVALENT);
		
     // These 4 bytes should be 'fmt '
     audioFileInputStream.read(arrayFor4Bytes);
     String fmtError = filePath + ": The chunk should be of type fmt";
     long fmtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
     AssertTests.assertTrue(fmtError, fmtLitEnd == fmt_HEXA_EQUIVALENT);
   
     // Skip the chunkSize
     audioFileInputStream.skip(4);
   
     // The AudioFormat should be 1 i.e. PCM (Linear Quantization)
     audioFileInputStream.read(arrayFor2Bytes);
     String pcmError = filePath + ": The Audio Format should be of type PCM";
     long pcmLitEnd = getLittleEndian(arrayFor2Bytes, 0, 2);
     AssertTests.assertTrue(pcmError, pcmLitEnd == AUDIO_FORMAT_EQUIVALENT);
   
     // These 2 bytes should mention number of channels and should be 
     // 2(Stereo)
     audioFileInputStream.read(arrayFor2Bytes);
     String noOfChanError = filePath + ": The audio should be of type Stereo";
     noOfChannels = (int)getLittleEndian(arrayFor2Bytes, 0, 2);
     AssertTests.assertTrue(noOfChanError, noOfChannels == STEREO_EQUIVALENT);
   
     // The Sample rate should be 44.1 kHz
     audioFileInputStream.read(arrayFor4Bytes);
     String samRtError = filePath + ": The sampling rate should be 44.1 kHz";
     long samRtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
     AssertTests.assertTrue(samRtError, samRtLitEnd == WAVE_SAMPLING_RATE);
   
     // Skip the ByteRate(4 Bytes) and BlockAlign(2 Bytes)
     audioFileInputStream.skip(6);
   
     // Bits per Sample should be 16 
     audioFileInputStream.read(arrayFor2Bytes);
     String bitError = filePath + ": There should be 16 bits/sample";
     bitsPerSample = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
     AssertTests.assertTrue(bitError, bitsPerSample == BITS_PER_SAMPLE);
 
     bytesPerSample = bitsPerSample/8;
   
     // The data chunk gets started and should start with 'data' for 4 bytes
     audioFileInputStream.read(arrayFor4Bytes);
     String dataError = filePath + ": There should be a proper data chunk";
     long dataLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
     AssertTests.assertTrue(dataError, dataLitEnd == data_HEXA_EQUIVALENT);
 
     // The next 4 bytes determine the lenth of the data chunk
     audioFileInputStream.read(arrayFor4Bytes);
     fileLength = getLittleEndian(arrayFor4Bytes, 0, 4);
 
     bytesPerFrame = bytesPerSample * noOfChannels;
     samplesPerFrame = bytesPerFrame/bytesPerSample;
     frameLength = fileLength/bytesPerFrame;
     noOfSamplesPerChannel = (frameLength * samplesPerFrame)/noOfChannels;
 
    } 
    catch (IOException e) 
    {
     AssertTests.assertTrue(filePath + ": Invalid File Header", false);
    }
   }
		
  /* @see AudioProcessableFiles.AudioProcessableBase#readSamples() */
   public float[] readSamples()
   {
    float[] readSamples = new float[(int) noOfSamplesPerChannel]; 
    byte[] twoByteArray = new byte[2];
    float toDivide = (float) (2<<15);
    for(int i=0;i<noOfSamplesPerChannel;i++)
    {
     try 
     {
      audioFileInputStream.read(twoByteArray);
      readSamples[i] = (float) getLittleEndian(twoByteArray,0,2) / toDivide;
      audioFileInputStream.skip(2 * noOfChannels/bytesPerSample);
     }
     catch (IOException e) 
     {
      AssertTests.assertTrue(filePath + ": I/O Error", false);
     }
    }
    return readSamples;
   }
		
  /* @see 
   * AudioProcessableFiles.AudioProcessableBase#compare(AudioProcessableFile) 
   */
   public void compare(AudioProcessableFile fileToCmp)
   {
    FFT thisFFT = new FFT(readSamples());
    FFT fileToCmpFFT = new FFT(fileToCmp.readSamples());
    double mse = thisFFT.calculateMSE(fileToCmpFFT.getTransformedSamples());
    if(mse == 0)
    {
     String fileToCmpShrtName = fileToCmp.getFileShortName();
     System.out.println("MATCH "+getFileShortName()+" "+ fileToCmpShrtName);
    }
    else
    {
     System.out.println("NO MATCH");
    }
    System.exit(0);
   }
   
  /* @see AudioProcessableFiles.AudioProcessableBase#getFileLength() */
   public long getFileLength()
   {
    return fileLength;
   }
		
  /* @see AudioProcessableFiles.AudioProcessableBase#getFileShortName() */
   public String getFileShortName()
   {
    return audioFile.getName();
   }
  }
	
  /**
   * getLittleEndian : byte[], int, int -> long
   * @param arr : The array of type byte, 
   *              the values of which will be converted to little endian
   * @param offset : The offset from where to start in 'arr'
   * @param numOfBytes : The number of indexes to convert
   * @return val : The little endian value of the values in the arr 
   *               starting from 'offset'
   *              and ending at 'numOfBytes' from 'offset'
   */
  private static long getLittleEndian(byte[] arr, int offset, int numOfBytes)
  {
   numOfBytes--;
   int endIndex = offset+numOfBytes;
   long val = 0;
   if(endIndex>arr.length)
    return val;
   
   val = arr[endIndex] & 0xFF;
   for(int i=0; i<numOfBytes; i++)
   {
    val = (val << 8) + (arr[--endIndex] & 0xFF);
   }
   return val;
  }
}