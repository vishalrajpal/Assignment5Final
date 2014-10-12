import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

public abstract class AudioProcessableFiles 
{
	public static AudioProcessable make(String filePath)
	{
		if(filePath != null)
		{
			AudioProcessable processableFile = new Create(filePath);
			return processableFile;
		}
		else
		{
			NullPointerException ex = new NullPointerException("ERROR: Passed Invalid File Path.");
			throw ex;
		}
	}
	
	private static abstract class AudioProcessableBase implements AudioProcessable
	{
		public abstract float[] readSamples();
		public abstract boolean isValidFile();
	}
	
	private static class Create extends AudioProcessableBase
	{
		private final static int RIFF_HEXA_EQUIVALENT = 0x46464952;
		private final static int WAVE_HEXA_EQUIVALENT = 0x45564157;
		private final static int fmt_HEXA_EQUIVALENT = 0x20746D66;
		private final static int data_HEXA_EQUIVALENT = 0x61746164;
		private final static int AUDIO_FORMAT_EQUIVALENT = 1;
		private final static int STEREO_EQUIVALENT = 2;
		private final static int WAVE_SAMPLING_RATE = 44100;
		private final static int BITS_PER_SAMPLE = 16;
		
		private String parentFileType;
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
		
		Create(String filePath)
		{
			this.filePath = filePath;
			fetchFileintoFileInputStream();
			isValidFile();
		}
	
		private void fetchFileintoFileInputStream()
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
		
		@Override
		public boolean isValidFile() 
		{
			byte[] arrayFor2Bytes = new byte[2];
			byte[] arrayFor4Bytes = new byte[4];
			try 
			{
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The file should be of RIFF format", getLittleEndian(arrayFor4Bytes, 0, 4) == RIFF_HEXA_EQUIVALENT);
					
				audioFileInputStream.skip(4);
				
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The file should be of WAVE format", getLittleEndian(arrayFor4Bytes, 0, 4) == WAVE_HEXA_EQUIVALENT);
			
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The chunk should be of type fmt", getLittleEndian(arrayFor4Bytes, 0, 4) == fmt_HEXA_EQUIVALENT);
				
				audioFileInputStream.skip(4);

				audioFileInputStream.read(arrayFor2Bytes);
				assertTrue("The Audio Format should be of type PCM", getLittleEndian(arrayFor2Bytes, 0, 2) == AUDIO_FORMAT_EQUIVALENT);
							
				audioFileInputStream.read(arrayFor2Bytes);
				noOfChannels = (int)getLittleEndian(arrayFor2Bytes, 0, 2);
				assertTrue("The audio should be of type Stereo", noOfChannels == STEREO_EQUIVALENT);
				
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("The sampling rate should be 44.1 kHz", getLittleEndian(arrayFor4Bytes, 0, 4) == WAVE_SAMPLING_RATE);

				audioFileInputStream.skip(6);
				
				audioFileInputStream.read(arrayFor2Bytes);
				bitsPerSample = (int) getLittleEndian(arrayFor2Bytes, 0, 2);
				assertTrue("There should be 16 bits/sample",bitsPerSample == BITS_PER_SAMPLE);
				bytesPerSample = bitsPerSample/8;
				
				audioFileInputStream.read(arrayFor4Bytes);
				assertTrue("There should be a proper data chunk in the file", getLittleEndian(arrayFor4Bytes, 0, 4) == data_HEXA_EQUIVALENT);
				
				audioFileInputStream.read(arrayFor4Bytes);
				fileLength = getLittleEndian(arrayFor4Bytes, 0, 4);
				bytesPerFrame = bytesPerSample * noOfChannels;
				samplesPerFrame = bytesPerFrame/bytesPerSample;
				frameLength = fileLength/bytesPerFrame;
				noOfSamplesPerChannel = (frameLength * samplesPerFrame)/noOfChannels;
								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return true;
		}
		
		public float[] readSamples()
		{
			float[] readSamples = new float[(int) noOfSamplesPerChannel]; 
			byte[] twoByteArray = new byte[2];
			for(int i=0;i<noOfSamplesPerChannel;i++)
			{
				try 
				{
					audioFileInputStream.read(twoByteArray);
					readSamples[i] = (float) getLittleEndian(twoByteArray,0,2)/ (float) (2<<15);
					audioFileInputStream.skip(2 * noOfChannels/bytesPerSample);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return readSamples;
		}
	}
	
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
	
	private static void assertTrue(String errorMsg, boolean isTrue)
	{
		if(!isTrue)
		{
			System.err.println("ERROR: "+errorMsg);
			System.exit(1);
		}
	}
}
