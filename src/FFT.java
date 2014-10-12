public class FFT 
{
	private ComplexNumber[] samplesInComplex;
	private float[] originalSamples;
	private ComplexNumber[] hanningWindowSamples;
	private ComplexNumber[] FFTResult;
	
	FFT(float[] samples)
	{
		originalSamples = samples;
		hanningWindowSamples = applyHanningWindow(originalSamples);
		int samplesLength = originalSamples.length;
		int nearestPowerOfTwo = getNearestPowerOfTwoWithShifts(samplesLength);
		if(samplesLength != nearestPowerOfTwo)
		{
			ComplexNumber[] paddedWindowedSamples = padArrayWithZeros(hanningWindowSamples, nearestPowerOfTwo);
			FFTResult = performFFT(paddedWindowedSamples, paddedWindowedSamples.length);
		}else
		{
			FFTResult = performFFT(hanningWindowSamples, samples.length);
		}
		System.out.println(FFTResult.length);
	}
	
	private static ComplexNumber[] padArrayWithZeros(ComplexNumber[] originalArray, int nearestPowerOfTwo)
	{
		ComplexNumber[] paddedArray = new ComplexNumber[nearestPowerOfTwo];
		System.arraycopy(originalArray, 0, paddedArray, 0, originalArray.length);
		for(int i = originalArray.length; i<nearestPowerOfTwo; i++)
		{
			paddedArray[i] = ComplexNumbers.make(0, 0);
		}
		return paddedArray;
	}
	
	public double calculateMSE(ComplexNumber[] samplesToCompare)
	{
		int samplesLen = FFTResult.length;
		double mse = 0;
		for(int i = 0; i<samplesLen; i++)
		{
			ComplexNumber currentSample = FFTResult[i];
			ComplexNumber sampleToCompare = samplesToCompare[i];
			ComplexNumber complexDiff = currentSample.subtract(sampleToCompare);
			mse += Math.pow(complexDiff.absolute(), 2);
		}
		return mse/samplesLen;
	}
	
	public ComplexNumber[] getTransformedSamples()
	{
		return FFTResult;
	}
	
	private static ComplexNumber[] applyHanningWindow(float[] samples)
	{
		int noOfSamples = samples.length;
		int halfSamplesLength = noOfSamples/2;
		ComplexNumber[] windowedValues = new ComplexNumber[noOfSamples];
		for(int i = 0; i<noOfSamples; i++)
		{
			double windowReal = samples[i] * (0.5f + 0.5f * (float) Math.cos(2.0f * (float) Math.PI * i / noOfSamples));
			windowedValues[i] = ComplexNumbers.make(windowReal, 0);
		}
		return windowedValues;
	}
	
	private static ComplexNumber[] performFFT(ComplexNumber[] windowedSamples, int samplesLen)
	{
		ComplexNumber[] result = new ComplexNumber[samplesLen];
		if(samplesLen == 1)
			return windowedSamples;
		
		int samplesLenBy2 = samplesLen / 2;
		
		ComplexNumber[] evenSamples = new ComplexNumber[samplesLen/2];
		for (int sampleCount = 0; sampleCount < samplesLenBy2; sampleCount++) 
        {
			try{
				evenSamples[sampleCount] = windowedSamples[2 * sampleCount];
			}
			catch(ArrayIndexOutOfBoundsException ex)
			{
				ex.printStackTrace();
			}
        }
		ComplexNumber[] evenFFTSamples = performFFT(evenSamples, samplesLenBy2);
		
		ComplexNumber[] oddSamples = new ComplexNumber[samplesLen/2];
		for (int sampleCount = 0; sampleCount < samplesLenBy2; sampleCount++) 
        {
			oddSamples[sampleCount] = windowedSamples[(2 * sampleCount) + 1];
        }
		ComplexNumber[] oddFFTSamples = performFFT(oddSamples, samplesLenBy2);
		
		
		for(int sampleCount = 0; sampleCount<samplesLenBy2; sampleCount++)
		{
			// 2*PI*i*k/n
			double nthRootOfUnity = -2 * Math.PI * sampleCount / samplesLen;
			ComplexNumber omega = ComplexNumbers.make(Math.cos(nthRootOfUnity), Math.sin(nthRootOfUnity));
			ComplexNumber omegaMulOdd = omega.multiply(oddFFTSamples[sampleCount]);
			result[sampleCount] = evenFFTSamples[sampleCount].add(omegaMulOdd);
			result[sampleCount + samplesLenBy2] = evenFFTSamples[sampleCount].subtract(omegaMulOdd);
		}
		return result;
	}
	
	private static int getNearestPowerOfTwoWithShifts(int n)
	{
		if((n & (n-1)) == 0)
			return n;
		
		for(int i = 1; i<32; i*=2)
		{
			n |= (n >> i);
		}
		return n + 1;
	}
}
