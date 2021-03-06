import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

  /**
   * class AudioProcessableFiles:Description: This class converts a physical 
   * path to an instance of AudioProcessableFile. The instance is only returned
   * if and only if the file is of supported type.
   *  
   * CURRENT SUPPORTED TYPE/s: ["WAVE"]
   * 
   * WAVE: The audio file's header is validated against a specific format for a
   * WAVE file. If the header is valid below are the operations possible on an
   * instance of AudioProcessableFile
   *   
   * a) readSamples: Reads the audio samples of the audio file. b) compare:
   * Compares another instance of AudioProcessableFile with this. c)
   * getFileLength: Returns the file length of the audio file corresponding to
   * this.
   */

public abstract class AudioProcessableFiles {
  /**
   * make : String -> AudioProcessableFile
   * 
   * @param filePath
   *            : The filePath for which to create a AudioProcessableFile
   * @return processableFile: If the value of 'filePath' is null throws
   *         NullPointerException, else returns an instance of type
   *         AudioProcessableFile
   */
  public static AudioProcessableFile make(String filePath) 
  {
     if (filePath != null) 
     {
        if (filePath.endsWith(".wav")) 
        {
           AudioProcessableFile processableFile = new WAVAudioProcessableFile(
        		   filePath);
           if (!processableFile.isValidFile()) return null;
           return processableFile;
        }
        else if (filePath.endsWith(".mp3"))
        {
           AudioProcessableFile mp3ProcessableFile = new 
        		   MP3AudioProcessableFile(filePath);
           if (!mp3ProcessableFile.isValidFile()) return null;
           filePath = getConvertedFilePath(filePath,
        		   mp3ProcessableFile.getFileShortName());
           AudioProcessableFile wavProcessableFile = null;
           if(filePath != null)
           {	
              wavProcessableFile = new WAVAudioProcessableFile(
              filePath, mp3ProcessableFile);
              if (!wavProcessableFile.isValidFile()) return null;
           }
           return wavProcessableFile;
        }
        else
        {
           String exString = "File Format not found : " + filePath;
           AssertTests.assertTrue(exString, false);
           return null;
        }
     }
     else
     {
        String exString = "ERROR: Passed Invalid File Path : " + filePath;
        AssertTests.assertTrue(exString, false);
        return null;
     }
  }
   

  /** Implementation of AudioProcessableFile ADT */
  private static abstract class AudioProcessableBase implements
  AudioProcessableFile
  {
     protected boolean isValidFile = true;
     protected float[] samples = null;
     protected FileInputStream audioFileInputStream;
     protected File audioFile;
     protected String filePath;
     /* @see AudioProcessableFile#readSamples() */
     public abstract float[] getSamples();

     /* @see AudioProcessableFile#validateFile() */
     public abstract boolean validateFile();

     /* @see AudioProcessableFile#compare(AudioProcessableFile) */
     public abstract void compare(AudioProcessableFile ap);

     /* @see AudioProcessableFile#getFileLength() */
     public abstract long getFileLength();

     /* @see AudioProcessableFiles.AudioProcessableBase#getFileShortName() */
     public abstract String getFileShortName();

     public boolean isValidFile() 
     {
        return isValidFile;
     }
     
        
  /**
   * fetchFileIntoFileInputStream : -> void
   * 
   * @effect: Creates and loads a new FileInputStream with the file
   *          located at 'filePath'. If file not found throws FileNotFound
   *          Exception.
   */
  protected void fetchFileIntoFileInputStream() 
  {
     audioFile = new File(filePath);
     try
     {
        audioFileInputStream = new FileInputStream(audioFile);
     }
     catch (FileNotFoundException e)
     {
        AssertTests.assertTrue(filePath + " File not found", false);
        isValidFile = false;
     }
  }
  }

  /** Create new AudioProcessableFile */
  private static class WAVAudioProcessableFile extends AudioProcessableBase 
  {
     // Constants
     private final static int RIFF_HEXA_EQUIVALENT = 0x46464952;
     private final static int WAVE_HEXA_EQUIVALENT = 0x45564157;
     private final static int fmt_HEXA_EQUIVALENT = 0x20746D66;
     private final static int data_HEXA_EQUIVALENT = 0x61746164;
     private final static int AUDIO_FORMAT_EQUIVALENT = 1;
     private final static int STEREO_EQUIVALENT = 2;
     private final static int MONO_EQUIVALENT = 1;
     private final static int WAVE_SAMPLING_RATE_11025 = 11025;
     private final static int WAVE_SAMPLING_RATE_22050 = 22050;
     private final static int WAVE_SAMPLING_RATE_44100 = 44100;
     private final static int WAVE_SAMPLING_RATE_48000 = 48000;
     private final static int BITS_PER_SAMPLE_8 = 8;
     private final static int BITS_PER_SAMPLE_16 = 16;

     // Instance Variables
     private long fileLength;
     private int noOfSamplesPerChannel;
     private int bitsPerSample;
     private int bytesPerSample;
     private int noOfChannels;
     private int bytesPerFrame;
     private int samplesPerFrame;
     private int frameLength;
     private AudioProcessableFile mp3Processable = null;
     /**
      * Constructor : String -> WAVAudioProcessableFile
      * 
      * @param filePath
      *            : The file path for which an AudioProcessableFile has to
      *            be created
      * @effect : The constructor implicitly returns an instance of type
      *         Create
      */
      WAVAudioProcessableFile(String filePath) 
      {
         this.filePath = filePath;
         fetchFileIntoFileInputStream();
         validateFile();
      }
      WAVAudioProcessableFile(String filePath, AudioProcessableFile mp3File) {
      this.filePath = filePath;
      fetchFileIntoFileInputStream();
      validateFile();
      mp3Processable = mp3File;
      }
      /* @see AudioProcessableFiles.AudioProcessableBase#validateFile() */
      public boolean validateFile() 
      {
       	 if(!isValidFile()) return false;
         byte[] arrayFor2Bytes = new byte[2];
         byte[] arrayFor4Bytes = new byte[4];
         try
         {
            String notSupportedFormatError = " is not a supported format";
            // First 4 bytes are 'RIFF'
            audioFileInputStream.read(arrayFor4Bytes);
            String riffErr = filePath + notSupportedFormatError;
            long riffLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
            isValidFile = AssertTests.assertTrue(riffErr,
            		riffLitEnd == RIFF_HEXA_EQUIVALENT);
            if (!isValidFile) return isValidFile;
            // Skip the chunkSize
            audioFileInputStream.skip(4);

            // These 4 bytes should be WAVE'
            audioFileInputStream.read(arrayFor4Bytes);
            String waveErr = filePath + notSupportedFormatError;
            long waveLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
            isValidFile = AssertTests.assertTrue(waveErr,
            		waveLitEnd == WAVE_HEXA_EQUIVALENT);
            if (!isValidFile) return isValidFile;
            // These 4 bytes should be 'fmt '
            audioFileInputStream.read(arrayFor4Bytes);
            String fmtError = filePath + " The chunk should be type fmt";
            long fmtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
            isValidFile = AssertTests.assertTrue(fmtError,
            		fmtLitEnd == fmt_HEXA_EQUIVALENT);
            if (!isValidFile) return false;
            // Skip the chunkSize
            audioFileInputStream.skip(4);
            // The AudioFormat should be 1 i.e. PCM (Linear Quantization)
            audioFileInputStream.read(arrayFor2Bytes);
            String pcmError = filePath+ " The Audio Format should be of type"
            		+ " PCM";
            long pcmLitEnd = getLittleEndian(arrayFor2Bytes, 0, 2);
            isValidFile = AssertTests.assertTrue(pcmError,
            pcmLitEnd == AUDIO_FORMAT_EQUIVALENT);
            if (!isValidFile) return isValidFile;
            // These 2 bytes should mention number of channels & should be
            // 2(Stereo) or 1(Mono)
            audioFileInputStream.read(arrayFor2Bytes);
            String noOfChanError = filePath + " The audio should be of type"
            		+ " Stereo or Mono";
            noOfChannels = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
            isValidFile = AssertTests.assertTrue(noOfChanError,
            noOfChannels == STEREO_EQUIVALENT || noOfChannels == 
            MONO_EQUIVALENT);
            if (!isValidFile) return isValidFile;
            // The Sample rate should be 11.025kHz or 22.05kHz or 44.1kHz or
            // 48kHz
            audioFileInputStream.read(arrayFor4Bytes);
            String samRtError = filePath + "The sampling rate should be "
            		+ "11.025 kHz or 22.05 kHz or 44.1 kHz or 48kHz";
            long samRtLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
            isValidFile = AssertTests.assertTrue(samRtError,samRtLitEnd == 
            		WAVE_SAMPLING_RATE_11025 || samRtLitEnd == 
            		WAVE_SAMPLING_RATE_22050 || samRtLitEnd == 
            		WAVE_SAMPLING_RATE_44100 || samRtLitEnd == 
            		WAVE_SAMPLING_RATE_48000);
            if (!isValidFile) return isValidFile;
            // Skip the ByteRate(4 Bytes) and BlockAlign(2 Bytes)
            audioFileInputStream.skip(6);

            // Bits per Sample should be 8 or 16
            audioFileInputStream.read(arrayFor2Bytes);
            String bitError = filePath
            		+ " There should be 8 or 16 bits/sample";
            bitsPerSample = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
            isValidFile = AssertTests.assertTrue(bitError,
            		(bitsPerSample == BITS_PER_SAMPLE_8 || bitsPerSample == 
            		BITS_PER_SAMPLE_16));
            if (!isValidFile) return isValidFile;
            bytesPerSample = bitsPerSample / 8;

            // The data chunk gets started and should start with 'data' for
            // 4 bytes
            audioFileInputStream.read(arrayFor4Bytes);
            String dataError = filePath
            		+ " There should be a proper data chunk";
            long dataLitEnd = getLittleEndian(arrayFor4Bytes, 0, 4);
            isValidFile = AssertTests.assertTrue(dataError,
            		dataLitEnd == data_HEXA_EQUIVALENT);
            if (!isValidFile) return isValidFile;
            // The next 4 bytes determine the length of the data chunk
            audioFileInputStream.read(arrayFor4Bytes);
            fileLength = getLittleEndian(arrayFor4Bytes, 0, 4);

            bytesPerFrame = bytesPerSample * noOfChannels;
            samplesPerFrame = bytesPerFrame / bytesPerSample;
            frameLength = (int) fileLength / bytesPerFrame;
            noOfSamplesPerChannel = (frameLength * samplesPerFrame)
            		/ noOfChannels;
            }
         catch (IOException e)
         {
            AssertTests.assertTrue(filePath + " Invalid File Header", false);
            return false;
         }
         return true;
      }
      /* @see AudioProcessableFiles.AudioProcessableBase#getFileShortName() */
      public String getFileShortName() 
      {
    	 if(mp3Processable!=null)
         return mp3Processable.getFileShortName();
         return audioFile.getName();
      }
      
      /* (non-Javadoc)
       * @see AudioProcessableFiles.AudioProcessableBase#getSamples()
       */
      public float[] getSamples() 
      {
         if (samples == null)
         samples = readSamples();
         return samples;
      }

      /* @see AudioProcessableFiles.AudioProcessableBase#readSamples() */
      public float[] readSamples() {
      float[] readSamples = new float[noOfSamplesPerChannel];
      byte[] twoByteArray = new byte[2];
      float toDivide = (float) (2 << 15);
      for (int i = 0; i < noOfSamplesPerChannel; i++)
      {
         try
         {
            audioFileInputStream.read(twoByteArray);
            readSamples[i] = (float) getLittleEndian(twoByteArray, 0, 2)
            		/ toDivide;
            audioFileInputStream.skip(2 * noOfChannels / bytesPerSample);
         }
         catch (IOException e)
         {
            AssertTests.assertTrue(filePath + " I/O Error", false);
         }
      }
      return readSamples;
      }
      
      /*
       * @see AudioProcessableFiles.AudioProcessableBase#compare
       * (AudioProcessableFile)
       */
       public void compare(AudioProcessableFile fileToCmp) 
       {
          if (getFileLength() != fileToCmp.getFileLength()) 
          {
             // printNoMatchAndExit();
          }
          FFT thisFFT = new FFT(getSamples());
          FFT fileToCmpFFT = new FFT(fileToCmp.getSamples());
          int mse = thisFFT.calculateMSE(fileToCmpFFT.getTransformedSamples());
          if (mse == 0)
          {
             String fileToCmpShrtName = fileToCmp.getFileShortName();
             printMatchAndExit(getFileShortName(), fileToCmpShrtName);
          }
       }
       
       /* @see AudioProcessableFiles.AudioProcessableBase#getFileLength() */
       public long getFileLength()
       {
          return fileLength;
       }
  }
  /**
   * getLittleEndian : byte[], int, int -> long
   * 
   * @param arr
   *            : The array of type byte, the values of which will be
   *            converted to little endian
   * @param offset
   *            : The offset from where to start in 'arr'
   * @param numOfBytes
   *            : The number of indexes to convert
   * @return val : The little endian value of the values in the arr starting
   *         from 'offset' and ending at 'numOfBytes' from 'offset'
   */
   private static long getLittleEndian(byte[] arr, int offset, int numOfBytes) 
   {
      numOfBytes--;
      int endIndex = offset + numOfBytes;
      long val = 0;
      if (endIndex > arr.length) return val;
      val = arr[endIndex] & 0xFF;
      for (int i = 0; i < numOfBytes; i++) 
      {
         val = (val << 8) + (arr[--endIndex] & 0xFF);
      }
      return val;
   }
   private static void printMatchAndExit(String fileName1, String fileName2) 
   {
      System.out.println("MATCH " + fileName1 + " " + fileName2);
   }

   private static String getConvertedFilePath(String filePath, 
		   String shortName)
   {
      String newFilePath = "/tmp/" + shortName + ".wav";
      //String lamePath = "/course/cs5500f14/bin/lame";
      String lamePath = "/usr/local/bin/lame";
      ProcessBuilder pb = new ProcessBuilder(lamePath,"--decode", filePath,
    		  newFilePath);
      try
      {
         Process p = pb.start();
         return newFilePath;
      }
      catch (IOException e) 
      {
         AssertTests.assertTrue("Invalid File", false);
         return null;
      }
   }   
   
  private static class MP3AudioProcessableFile extends AudioProcessableBase 
  {
     // First 16bits of the header that contain the frame sync,
     // MPEG Version, Layer and Protection (CRC) bit.
     private static final int first16bitsWithCRC = 0xFFFA;
     private static final int first16bitsWithoutCRC = 0xFFFB;

     private static final HashMap<Integer, Integer> samplingRateMap = 
    		 new HashMap<Integer, Integer>();
     private static final HashMap<Integer, Integer> bitRateMap = 
    		 new HashMap<Integer, Integer>();

     static
     {
        samplingRateMap.put(0, 44100);
        samplingRateMap.put(1, 48000);
        samplingRateMap.put(2, 32000);

        bitRateMap.put(0, 0);
        bitRateMap.put(1, 32000);
        bitRateMap.put(2, 40000);
        bitRateMap.put(3, 48000);
        bitRateMap.put(4, 56000);
        bitRateMap.put(5, 64000);
        bitRateMap.put(6, 80000);
        bitRateMap.put(7, 96000);
        bitRateMap.put(8, 112000);
        bitRateMap.put(9, 128000);
        bitRateMap.put(10, 160000);
        bitRateMap.put(11, 192000);
        bitRateMap.put(12, 224000);
        bitRateMap.put(13, 256000);
        bitRateMap.put(14, 320000);
     }
     
     private boolean isCRC = false;
     
     /**
      * Constructor : String -> MP3AudioProcessableFile
      * 
      * @param filePath
      *            : The file path for which an AudioProcessableFile has to
      *            be created
      * @effect : The constructor implicitly returns an instance of type
      *         MP3AudioProcessableFile
      */
      private MP3AudioProcessableFile(String filePath) 
      {
         this.filePath = filePath;
         fetchFileIntoFileInputStream();
         validateFile();
      }
      @Override
      public float[] getSamples() 
      {
         return null;
      }
      
      @Override
      public boolean validateFile() 
      {
         // Reference file for validation MP3 format:
         // http://www.codeproject.com/Articles/8295/MPEG-Audio-Frame-Header
         /*
          * Verifying if it is Layer3 with/without CRC
          */
         if (!isValidFile()) return isValidFile;
         try
         {
            /**
             * Reference file for validation MP3 format:
             * http://www.codeproject
             * .com/Articles/8295/MPEG-Audio-Frame-Header Verifying if it is
             * MPEG Version 1 Layer3 with a valid Frame Sync and
             * with/without CRC
             */
             byte[] first16bits = new byte[2];
             audioFileInputStream.read(first16bits);
             short first2bytes = byteArrToShort(first16bits);
             if (first2bytes == first16bitsWithCRC) 
             {
                isCRC = true;
             }
             else if (first2bytes != first16bitsWithoutCRC)
             {
                isCRC = false;
             }
             else
             {
                isValidFile = AssertTests.assertTrue("Invalid first 16 bits "
                		+ "in the MP3 header", false);
                return isValidFile;
             }
             
             // Reads the next byte which contains the bit rate, frequency
             // and padding bits.
             byte[] bitRateFreqPad = new byte[1];
             audioFileInputStream.read(bitRateFreqPad);
             // After reading we get the byte from the byte array.
             final byte byteRateFreqPad = bitRateFreqPad[0];

             // Obtaining the bits required for bitRate in header.
             int intBitRate = byteRateFreqPad & 0x000000F0;
             // Obtaining the integer value.
             int bitRateIndex = intBitRate >>> 4;
             // Bit Rate 15 is reserved; hence invalid
             isValidFile = AssertTests.assertTrue("Bit Rate is invalid",
            		 bitRateIndex != 15);
             
             // Obtaining the bit rate value from the Bit Rate Map.
             
             if (!isValidFile) return isValidFile;
             int bitRate = bitRateMap.get(bitRateIndex);

             // Obtaining the bits required for Sampling rate in header.
             int intSamplingRate = byteRateFreqPad & 0x0000000C;
             // Obtaining the integer value.
             int samplingRateIndex = intSamplingRate >>> 2;

             // Sampling Rate 3 is reserved; hence invalid.
             AssertTests.assertTrue("Sampling Rate is invalid",
            		 samplingRateIndex != 3);
             // Obtaining the sampling rate value from the Sampling Rate Map.

             // Sampling Rate 3 is reserved; hence invalid
             isValidFile = AssertTests.assertTrue(filePath
            		 + ": Sampling Rate is invalid", samplingRateIndex != 3);
             if (!isValidFile) return false;
             
             int samplingRate = samplingRateMap.get(samplingRateIndex);

             // Obtaining bit required for padding in header.
             int intPadding = byteRateFreqPad & 0x00000002;
             // Obtaining the integer value.
             int padding = intPadding >>> 1;

             byte[] lastHeaderByte = new byte[1];
             audioFileInputStream.read(lastHeaderByte);
             byte lastbyte = lastHeaderByte[0];
             int intChannel = lastbyte & 0x000000C0;
             // Based on Piazza post mono or stereo can be given.
             // Not needed unless we want to find which channel is given.
             int channel = intChannel >>> 6;
             int FrameLengthInBytes = 144 * bitRate / samplingRate + padding;

             final int CRCBytes = 2;
             final int AudioDataInBytes;
             if (isCRC == true)
             {
                // After the CRC comes the audio data.
                AudioDataInBytes = FrameLengthInBytes - CRCBytes;
                audioFileInputStream.skip(2);
             }
             else
             {
                // No CRC
                AudioDataInBytes = FrameLengthInBytes;
             }
             
             // Skipping the audio data bytes to reach the next header.
             audioFileInputStream.skip(AudioDataInBytes);
         }
         catch (IOException e)
         {
            AssertTests.assertTrue(filePath + " Invalid File Header", false);
            return false;
         }
         return true;
      }
      
      @Override
      public void compare(AudioProcessableFile fileToCmp) 
      {
         // Not needed file will e converted to WAV before comparison
      }
      
      @Override
      public long getFileLength() 
      {
         // Returns 0 as this method will not be used
         // as a valid MP3 will be converted to WAV
         return 0;
      }
      
      @Override
      public String getFileShortName() 
      {
         return audioFile.getName();
      }
      
      private static short byteArrToShort(byte[] x) 
      {
         ByteBuffer wrapped = ByteBuffer.wrap(x);
         short num = wrapped.getShort();
         return num;
      }
  }
}