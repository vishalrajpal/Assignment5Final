
public class dam {

	public static void main(String[] args)
	{
		AudioProcessable ap = AudioProcessableFiles.make("src/bad0616.wav");
		float[] apSamples = ap.readSamples();
		FFT apFFT = new FFT(apSamples);
		
	}
}
