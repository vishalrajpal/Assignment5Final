public abstract class ComplexNumbers 
{
	/**
	 * static make : double, double -> ComplexNumber
	 * @param real : The real part of to be created ComplexNumber
	 * @param imag : The imaginary part of to be created ComplexNumber
	 * @return cmplexNum : The ComplexNumber formed by the 'real' and 'imag'
	 */
	public static ComplexNumber make(double real, double imag)
	{
		ComplexNumber cmplexNum = new Create(real, imag);
		return cmplexNum;
	}
	
	/**
	 * Implementation of ComplexNumber ADT
	 */
	private abstract static class ComplexNumberBase implements ComplexNumber
	{
		/* @see ComplexNumber#add(ComplexNumber) */
		public abstract ComplexNumber add(ComplexNumber complexToAdd);
		
		/* @see ComplexNumber#subtract(ComplexNumber) */
		public abstract ComplexNumber subtract(ComplexNumber complexToAdd);
		
		/* @see ComplexNumber#multiply(ComplexNumber) */
		public abstract ComplexNumber multiply(ComplexNumber complexToAdd);
		
		/* @see ComplexNumber#absolute() */
		public abstract double absolute();
		
		/* @see ComplexNumber#getRealPart() */
		public abstract double getRealPart();
		
		/* @see ComplexNumber#getImaginaryPart() */
		public abstract double getImaginaryPart();
	}
	
	/**
	 * Create new ComplexNumber
	 */
	private static class Create extends ComplexNumberBase
	{
		// Instance Variables
		public final double realPart;
		public final double imaginaryPart;

		/**
		 * Constructor
		 * Create : double, double -> Create
		 * @param real : The real part of this
		 * @param imag : The imaginary part of this
		 * @Effect: The constructor implicitly returns an instance of type Create
		 */
		Create(double real, double imag)
		{
			realPart = real;
			imaginaryPart = imag;
		}
		
		/* @see ComplexNumbers.ComplexNumberBase#add(ComplexNumber) */
		public ComplexNumber add(ComplexNumber complexToAdd) 
		{
			double addedReal = this.realPart + complexToAdd.getRealPart();
			double addedImag = this.imaginaryPart + complexToAdd.getImaginaryPart();
			ComplexNumber addedComplex = new Create(addedReal, addedImag);
			return addedComplex;
		}

		/* @see ComplexNumbers.ComplexNumberBase#subtract(ComplexNumber) */
		public ComplexNumber subtract(ComplexNumber complexToSub) 
		{
			double subedReal = this.realPart - complexToSub.getRealPart();
			double subedImag = this.imaginaryPart - complexToSub.getImaginaryPart();
			ComplexNumber subedComplex = new Create(subedReal, subedImag);
			return subedComplex;
		}

		/* @see ComplexNumbers.ComplexNumberBase#multiply(ComplexNumber) */
		public ComplexNumber multiply(ComplexNumber complexToMul) 
		{
			double mulReal = (this.realPart * complexToMul.getRealPart()) 
					- (this.imaginaryPart - complexToMul.getImaginaryPart());
			double mulImag = (this.realPart * complexToMul.getImaginaryPart()) + 
					(complexToMul.getRealPart() * this.imaginaryPart);
			ComplexNumber mulComplex = new Create(mulReal, mulImag);
			return mulComplex;
		}
		
		/* @see ComplexNumbers.ComplexNumberBase#absolute() */
		public double absolute() 
		{
			return Math.hypot(realPart, imaginaryPart);
		}

		/* @see ComplexNumbers.ComplexNumberBase#getRealPart() */
		public double getRealPart() 
		{
			return realPart;
		}

		/* @see ComplexNumbers.ComplexNumberBase#getImaginaryPart() */
		public double getImaginaryPart() 
		{
			return imaginaryPart;
		}
	}
}
