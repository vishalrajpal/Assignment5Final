import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class AudioProcessableFiles 
{
	/**
	 * make : String -> AudioProcessableFile
	 * @param filePath : The filePath for which to create a AudioProcessableFile
	 * @return processableFile: If the value of 'filePath' is null throws NullPointerException
	 *         else returns an instance of type AudioProcessableFile
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
			NullPointerException ex = new NullPointerException("ERROR: Passed Invalid File Path.");
			throw ex;
		}
	}
	
	/** Implementation of AudioProcessableFile ADT */
	private static abstract class AudioProcessableBase implements AudioProcessableFile
	{
		/* @see AudioProcessableFile#readSamples() */
		public abstract float[] readSamples();
		
		/* @see AudioProcessableFile#validateFile()*/
		public abstract void validateFile();
		
		/* @see AudioProcessableFile#compare(AudioProcessableFile) */
		public abstract void compare(AudioProcessableFile ap);
		
		/* @see AudioProcessableFile#getFileLength() */
		public abstract long getFileLength();
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
		 * @param filePath : The file path for which an AudioProcessableFile has to be created
		 * @Effect : The constructor implicitly returns an instance of type Create
		 */
		Create(String filePath)
		{
			this.filePath = filePath;
			fetchFileIntoFileInputStream();
			validateFile();
		}
	
		/**
		 * fetchFileIntoFileInputStream : -> void
		 * @effect: Creates and loads a new FileInputStream with the file located at 'filePath'.
		 *          If file not found throws FileNotFound Exception.
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
				assertTrue("File not found", false);
				e.printStackTrace();
			}
		}
		
		/* @see AudioProcessableFiles.AudioProcessableBase#validateFile() */
		@Override
		public void validateFile() 
		{
			byte[] arrayFor2Bytes = new byte[2];
			byte[] arrayFor4Bytes = new byte[4];
			try 
			{
				// First 4 bytes are 'RIFF'
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The file should be of RIFF format", getLittleEndian(arrayFor4Bytes, 0, 4) == RIFF_HEXA_EQUIVALENT);
					
				// Skip the chunkSize
				audioFileInputStream.skip(4);
				
				// These 4 bytes should be WAVE'
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The file should be of WAVE format", getLittleEndian(arrayFor4Bytes, 0, 4) == WAVE_HEXA_EQUIVALENT);
			
				// These 4 bytes should be 'fmt '
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The chunk should be of type fmt", getLittleEndian(arrayFor4Bytes, 0, 4) == fmt_HEXA_EQUIVALENT);
				
				// Skip the chunkSize
				audioFileInputStream.skip(4);

				// The AudioFormat should be 1 i.e. PCM (Linear Quantization)
				audioFileInputStream.read(arrayFor2Bytes);
				assertTrue("The Audio Format should be of type PCM", getLittleEndian(arrayFor2Bytes, 0, 2) == AUDIO_FORMAT_EQUIVALENT);
							
				// These 2 bytes should mention number of channels and should be 2(Stereo)
				audioFileInputStream.read(arrayFor2Bytes);
				noOfChannels = (int)getLittleEndian(arrayFor2Bytes, 0, 2);
				assertTrue("The audio should be of type Stereo", noOfChannels == STEREO_EQUIVALENT);
				
				// The Sample rate should be 44.1 kHz
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The sampling rate should be 44.1 kHz", getLittleEndian(arrayFor4Bytes, 0, 4) == WAVE_SAMPLING_RATE);

				// Skip the ByteRate(4 Bytes) and BlockAlign(2 Bytes)
				audioFileInputStream.skip(6);
				
				// Bits per Sample should be 16 
				audioFileInputStream.read(arrayFor2Bytes);
				bitsPerSample = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
				assertTrue("There should be 16 bits/sample",bitsPerSample == BITS_PER_SAMPLE);
				bytesPerSample = bitsPerSample/8;
				
				// The data chunk gets started and should start with 'data' for 4 bytes
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("There should be a proper data chunk in the file", getLittleEndian(arrayFor4Bytes, 0, 4) == data_HEXA_EQUIVALENT);
				
				// The next 4 bytes determine the lenth of the data chunk
				audioFileInputStream.read(arrayFor4Bytes);
				fileLength = getLittleEndian(arrayFor4Bytes, 0, 4);
				
				bytesPerFrame = bytesPerSample * noOfChannels;
				samplesPerFrame = bytesPerFrame/bytesPerSample;
				frameLength = fileLength/bytesPerFrame;
				noOfSamplesPerChannel = (frameLength * samplesPerFrame)/noOfChannels;
								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				assertTrue("Invalid File Header", false);
				e.printStackTrace();
			}
		}
		
		/* @see AudioProcessableFiles.AudioProcessableBase#readSamples() */
		public float[] readSamples()
		{
			float[] readSamples = new float[(int) noOfSamplesPerChannel]; 
			byte[] twoByteArray = new byte[2];
			for(int i=0;i<noOfSamplesPerChannel;i++)
			{
				try 
				{
					audioFileInputStream.read(twoByteArray);
					readSamples[i] = (float) getLittleEndian(twoByteArray,0,2) / (float) (2<<15);
					audioFileInputStream.skip(2 * noOfChannels/bytesPerSample);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return readSamples;
		}
		
		public void compare(AudioProcessableFile ap)
		{
			
		}
		
		/* @see AudioProcessableFiles.AudioProcessableBase#getFileLength() */
		public long getFileLength()
		{
			return fileLength;
		}
	}
	
	/**
	 * getLittleEndian : byte[], int, int -> long
	 * @param arr : The array of type byte, the values of which will be converted to little endian
	 * @param offset : The offset from where to start in 'arr'
	 * @param numOfBytes : The number of indexes to convert
	 * @return val : The little endian value of the values in the arr starting from 'offset'
	 *              and ending at 'numOfBytes' from 'offset'
	 */
	private static long getLittleEndian(byte[] arr, int offset, int numOfBytes)
	{
		numOfBytes--;//3
		int endIndex = offset+numOfBytes;//3
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
	
	/**
	 * assertTrue : String, boolean -> void
	 * @param errorMsg : The errorMsg to print if 'isTrue' is false
	 * @param isTrue : if false the 'errorMsg' is printed through standard error
	 *                 and the program exits through status other than 0.
	 */
	private static void assertTrue(String errorMsg, boolean isTrue)
	{
		if(!isTrue)
		{
			System.err.println("ERROR: "+errorMsg);
			System.exit(1);
		}
	}
}
