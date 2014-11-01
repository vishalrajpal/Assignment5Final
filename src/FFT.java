/**
 * class FFT
 * Description: 
 * This class is used to determine Fast Fourier Transform of Audio Samples.
 */
public class FFT 
{
  private float[] originalSamples;
  private ComplexNumber[] hanningWindowSamples;
  private ComplexNumber[] FFTResult;
    
  /**
   * Constructor: float[] -> FFT
   * @param samples: The samples of an audio file
   * @Effect: The constructor implicitly returns an instance of FFT
   * Explanation: The Constructor calculates FFT of the samples provided
   */
  FFT(float[] samples)
  {
    originalSamples = samples;
    hanningWindowSamples = applyHanningWindow(originalSamples);
    int samplesLength = originalSamples.length;
    int nearestPowerOfTwo = getNearestPowerOfTwo(samplesLength);
    if(samplesLength != nearestPowerOfTwo)
    {
      ComplexNumber[] paddedWindowedSamples = 
    		padArrayWithZeros(hanningWindowSamples, nearestPowerOfTwo);
      int paddedSamplesLen = paddedWindowedSamples.length;
      FFTResult = performFFT(paddedWindowedSamples, paddedSamplesLen);
    }
    else
    {
      FFTResult = performFFT(hanningWindowSamples, samples.length);
    }
  }
	
  /**
   * calculateMSE : ComplexNumber[] -> int
   * @param samplesToCompare : The samples to compare with the FFT samples of
   *                           this
   * @return mse : The Mean Squared Error between the 'samplesToCompare' and
   *               FFT samples of this
   */
  public int calculateMSE(ComplexNumber[] samplesToCompare)
  {
    int samplesLen = FFTResult.length;
    int mse = 0;
    for(int i = 0; i<samplesLen; i++)
    {
	  if(i>=samplesLen || i>=samplesToCompare.length)
		 break;
      ComplexNumber currentSample = FFTResult[i];
      ComplexNumber sampleToCompare = samplesToCompare[i];
      ComplexNumber complexDiff = currentSample.subtract(sampleToCompare);
      mse += Math.pow(complexDiff.absolute(), 2);
    }
    return mse/samplesLen;
  }
    
  /**
   * getTransformedSamples : -> ComplexNumber[]
   * @return FFTResult: The private instance variable which has the samples
   *                    with FFT applied on them.
   */
  public ComplexNumber[] getTransformedSamples()
  {
    return FFTResult;
  }
	
  /**
   * static padArrayWithZeros : ComplexNumber[], int -> ComplexNumber[]
   * @param originalArray : The array of samples as ComplexNumber
   * @param nearestPowerOfTwo : The nearest power of 2 corresponding to the
   *                            length of 'originalArray'
   * @return paddedArray: The 'originalArray' with new ComplexNumber instances
   *                      added to it to make its length an exact power of 2
   */
  private static ComplexNumber[] padArrayWithZeros 
  (ComplexNumber[] originalArray, int nearestPowerOfTwo)
  {
    ComplexNumber[] paddedArray = new ComplexNumber[nearestPowerOfTwo];
    System.arraycopy(originalArray, 0, paddedArray, 0, originalArray.length);
    for(int i = originalArray.length; i<nearestPowerOfTwo; i++)
    {
      paddedArray[i] = ComplexNumbers.make(0, 0);
    }
    return paddedArray;
  }
	
  /**
   * static applyHanningWindow : float[] -> ComplexNumber[]
   * @param samples : The samples in little endian format
   * @return windowedValues : The samples with Hanning Window function 
   * 							applied to every sample
   */
  private static ComplexNumber[] applyHanningWindow(float[] samples)
  {
    int noOfSamples = samples.length;
    ComplexNumber[] windowedValues = new ComplexNumber[noOfSamples];
    for(int i = 0; i<noOfSamples; i++)
    {
      float piTimesIndex = (float) Math.PI * i;
      double windowReal = samples[i] * 
          (0.5f + 0.5f * (float) Math.cos(2.0f * piTimesIndex / noOfSamples));
      windowedValues[i] = ComplexNumbers.make(windowReal, 0);
    }
    return windowedValues;
  }
	
  /**
   * static performFFT : ComplexNumber[] , int
   * @param windowedSamples : The samples with Hanning Window Function
   *                          applied to every sample
   * @param samplesLen : The length of the array 'windowedSamples'
   * @return ComplexNumber[] : The array with FFT applied on every element
   *                           of 'windowedSamples'
   */
  private static ComplexNumber[] performFFT(ComplexNumber[] windowedSamples,
		  int samplesLen)
  {
    if(samplesLen == 1)
      return windowedSamples;
   
    int samplesLenBy2 = samplesLen / 2;
   
    // Even Samples
    ComplexNumber[] evenSamples = new ComplexNumber[samplesLen/2];
    for (int sampleCount = 0; sampleCount < samplesLenBy2; sampleCount++) 
    {
      evenSamples[sampleCount] = windowedSamples[2 * sampleCount];
    }
    ComplexNumber[] evenFFTSamples = performFFT(evenSamples, samplesLenBy2);
   
    // Odd Samples
    ComplexNumber[] oddSamples = new ComplexNumber[samplesLen/2];
    for (int sampleCount = 0; sampleCount < samplesLenBy2; sampleCount++) 
    {
      oddSamples[sampleCount] = windowedSamples[(2 * sampleCount) + 1];
    }
    ComplexNumber[] oddFFTSamples = performFFT(oddSamples, samplesLenBy2);
   
    //Combining the Even And Odd Samples
    ComplexNumber[] result = new ComplexNumber[samplesLen];
    for(int sampleCount = 0; sampleCount<samplesLenBy2; sampleCount++)
    {
      // 2*PI*i*k/n
      double nthRootOfUnity = (-2 * Math.PI * sampleCount) / samplesLen;
      ComplexNumber omega = ComplexNumbers.make(Math.cos(nthRootOfUnity),
    	     	Math.sin(nthRootOfUnity));
      ComplexNumber omegaMulOdd = omega.multiply(oddFFTSamples[sampleCount]);
      result[sampleCount] = evenFFTSamples[sampleCount].add(omegaMulOdd);
      int smpleOffsetN2 = sampleCount + samplesLenBy2;
      result[smpleOffsetN2] = evenFFTSamples[sampleCount].subtract(omegaMulOdd);
    }
    return result;
  }
	
  /**
   * static getNearestPowerOfTwo : int -> int
   * @param n : An int of which a nearest power of two greater than 'n'
   * 			  needs to be finded 
   * @return int : The nearest power of 2 greater than 'n'
   */
  private static int getNearestPowerOfTwo(int n)
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