
public class dam {

	public static void main(String[] args)
	{
		AudioProcessableFile ap = AudioProcessableFiles.make("src/bad0616.wav");
		AudioProcessableFile ap1 = AudioProcessableFiles.make("src/Sor3508.mp3");
		ap.compare(ap1);
//		AudioProcessableFile ap = AudioProcessableFiles.make("src/bad0616.wav");
//		float[] apSamples = ap.readSamples();
//		FFT apFFT = new FFT(apSamples);
//		
//		AudioProcessableFile ap1 = AudioProcessableFiles.make("src/bad0616.wav");
//		float[] apSamples1 = ap1.readSamples();
//		FFT apFFT1 = new FFT(apSamples1);
//		
//		AudioProcessableFile ap2 = AudioProcessableFiles.make("src/bad2131.wav");
//		float[] apSamples2 = ap2.readSamples();
//		FFT apFFT2 = new FFT(apSamples2);
//		
//		double mse = apFFT.calculateMSE(apFFT1.getTransformedSamples());
//		System.out.println(mse);
//		if(mse == 0)
//		{
//			System.out.println("Match");
//		}
//		else
//		{
//			System.out.println("No Match");
//		}
//
//		double mse1 = apFFT.calculateMSE(apFFT2.getTransformedSamples());
//		System.out.println(mse1);
//		if(mse1 == 0)
//		{
//			System.out.println("Match");
//		}
//		else
//		{
//			System.out.println("No Match");
//		}

	}
}
