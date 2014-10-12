
public interface ComplexNumber 
{
	/**
	 * add : ComplexNumber -> ComplexNumer
	 * @param complexToAdd : The ComplexNumber to add to this
	 * @return ComplexNumber : The ComplexNumber formed by adding this and 'complexToAdd'
	 */
	public ComplexNumber add(ComplexNumber complexToAdd);
	
	/**
	 * subtract : ComplexNumber -> ComplexNumer
	 * @param complexToSub : The ComplexNumber to subtract from this
	 * @return ComplexNumber : The ComplexNumber formed by subtracting 'complexToAdd' from this
	 */
	public ComplexNumber subtract(ComplexNumber complexToSub);
	
	/**
	 * multiply : ComplexNumber -> ComplexNumer
	 * @param complexToMul : The ComplexNumber to multiply to this
	 * @return ComplexNumber : The ComplexNumber formed by muliplying 'complexToAdd' and this
	 */
	public ComplexNumber multiply(ComplexNumber complexToMul);
	
	/**
	 * absolute : -> double
	 * @return double : The absolute value of this ComplexNumber
	 */
	public double absolute();
	
	/**
	 * getRealPart : -> double
	 * @return double : The real part of this ComplexNumber
	 */
	public double getRealPart();
	
	/**
	 * getImaginaryPart : -> double
	 * @return double : The imaginary part of this ComplexNumber
	 */
	public double getImaginaryPart();
}
