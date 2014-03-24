package subgoalinference;

public class MathHelper {

	/**
	 * Adds/subtracts two numbers in log mode. 
	 * Converting back into normal scale loses some value
	 * @param logA
	 * @param logB
	 * @param subtract , always subtracts smaller from bigger, irrespective of order
	 * @return log (A + B) 
	 */
	public static double addTwoLogNumbers(double logA, double logB, boolean subtract)
	{
		double result = 0;

		if ( logA <= -Double.MAX_VALUE ) // log(0) = -Infinity
			return logB;
		else if ( logB == -Double.MAX_VALUE )
			return logA;

		if (subtract)
			result = Math.max(logA, logB) 
				+ Math.log(1 - Math.exp(Math.min(logA,logB) - Math.max(logA,logB))); // subtraction
		else
			result = Math.max(logA, logB) 
				+ Math.log(1 + Math.exp(Math.min(logA,logB) - Math.max(logA,logB)));	// default addition

		return result;

	}
	
	/**
	 * Adds two numbers in log mode. 
	 * Converting back into normal scale loses some value
	 * @param logA
	 * @param logB
	 * @return log (A + B) 
	 */
	public static double addTwoLogNumbers(double logA, double logB)
	{
		return addTwoLogNumbers(logA, logB, false);
	}
}
