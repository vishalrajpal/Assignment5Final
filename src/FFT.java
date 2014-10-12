
public class FFT 
{
	Complex[] samplesInComplex;
	float[] originalSamples;
	Complex[] hanningWindowSamples;
	Complex[] FFTResult;
	FFT(float[] samples)
	{
		originalSamples = samples;
		hanningWindowSamples = applyHanningWindow(originalSamples);
		int samplesLength = originalSamples.length;
		int nearestPowerOfTwo = getNearestPowerOfTwoWithShifts(samplesLength);
		if(samplesLength != nearestPowerOfTwo)
		{
			Complex[] paddedWindowedSamples = padArrayWithZeros(hanningWindowSamples, nearestPowerOfTwo);
			FFTResult = performFFT(paddedWindowedSamples, paddedWindowedSamples.length);
		}else
		{
			FFTResult = performFFT(hanningWindowSamples, samples.length);
		}
		System.out.println(FFTResult.length);
	}
	
	private static Complex[] padArrayWithZeros(Complex[] originalArray, int nearestPowerOfTwo)
	{
		Complex[] paddedArray = new Complex[nearestPowerOfTwo];
		System.arraycopy(originalArray, 0, paddedArray, 0, originalArray.length);
		for(int i = originalArray.length; i<nearestPowerOfTwo; i++)
		{
			paddedArray[i] = new Complex(0);
		}
		return paddedArray;
	}
	
	private static class Complex
	{
		private final double realPart;
		private final double imaginaryPart;
		
		Complex(double realPart)
		{
			this.realPart = realPart;
			this.imaginaryPart = 0;
		}
		
		Complex(double realPart, double imagPart)
		{
			this.realPart = realPart;
			this.imaginaryPart = imagPart;
		}
		
		private Complex add(Complex complexToAdd)
		{
			double addedReal = this.realPart + complexToAdd.realPart;
			double addedImag = this.imaginaryPart + this.imaginaryPart;
			Complex addedComplex = new Complex(addedReal, addedImag);
			return addedComplex;
		}
		
		private Complex subtract(Complex complexToAdd)
		{
			double subedReal = this.realPart - complexToAdd.realPart;
			double subedImag = this.imaginaryPart - this.imaginaryPart;
			Complex subedComplex = new Complex(subedReal, subedImag);
			return subedComplex;
		}
		
		private Complex multiply(Complex complexToMul)
		{
			double mulReal = (this.realPart * complexToMul.realPart) - (this.imaginaryPart - complexToMul.imaginaryPart);
			double mulImag = (this.realPart * complexToMul.imaginaryPart) + (complexToMul.realPart * this.imaginaryPart);
			Complex mulComplex = new Complex(mulReal, mulImag);
			return mulComplex;
		}
	}
	
	private static Complex[] applyHanningWindow(float[] samples)
	{
		int noOfSamples = samples.length;
		int halfSamplesLength = noOfSamples/2;
		Complex[] windowedValues = new Complex[noOfSamples];
		for(int i = -halfSamplesLength; i<halfSamplesLength; i++)
		{
			int j = i + halfSamplesLength;
			windowedValues[j] = new Complex(samples[j] * (0.5f + 0.5f * (float) Math.cos(2.0f * (float) Math.PI * j / noOfSamples)));
		}
		return windowedValues;
	}
	
	private static Complex[] performFFT(Complex[] windowedSamples, int samplesLen)
	{
		Complex[] result = new Complex[samplesLen];
		if(samplesLen == 1)
			return windowedSamples;
		
		int samplesLenBy2 = samplesLen / 2;
		
		Complex[] evenSamples = new Complex[samplesLen/2];
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
		Complex[] evenFFTSamples = performFFT(evenSamples, samplesLenBy2);
		
		Complex[] oddSamples = new Complex[samplesLen/2];
		for (int sampleCount = 0; sampleCount < samplesLenBy2; sampleCount++) 
        {
			oddSamples[sampleCount] = windowedSamples[(2 * sampleCount) + 1];
        }
		Complex[] oddFFTSamples = performFFT(oddSamples, samplesLenBy2);
		
		
		for(int sampleCount = 0; sampleCount<samplesLenBy2; sampleCount++)
		{
			// 2*PI*i*k/n
			double nthRootOfUnity = -2 * Math.PI * sampleCount / samplesLen;
			Complex omega = new Complex(Math.cos(nthRootOfUnity), Math.sin(nthRootOfUnity));
			Complex omegaMulOdd = omega.multiply(oddFFTSamples[sampleCount]);
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
//	private static float[] padSamplesWithZaros(float[] windowedSamples)
//	{
//		int samplesLen = windowedSamples.length;
//		
//	}
}
