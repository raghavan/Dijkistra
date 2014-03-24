package subgoalinference;

import java.util.ArrayList;

public class ForwardBackward {

	/**
	 * likelihood and prior are 2D matrices of same sizes
	 * @param logLikelihood
	 * @param logPrior
	 * @param logAlpha initialized reference, forward estimation stored in it 
	 */
	public void computeLogAlpha(
			ArrayList<ArrayList<Double>> logLikelihood,
			ArrayList<ArrayList<Double>> logPrior,
			ArrayList<Double> logAlpha
			) {
		
		logAlpha.clear();
		logAlpha.add(new Double(0)); // log(1) = 0
		
		for (int i=0; i < logLikelihood.size(); i++){
			
			double val = -Double.MAX_VALUE; // log(0) = -Infinity
			for (int k=0; k<i; k++) {
				val = MathHelper.addTwoLogNumbers(val, 
						logLikelihood.get(k).get(i) + logPrior.get(k).get(i) 
						+ logAlpha.get(k));
			}
			logAlpha.add(val);
		}
		
	}	
	
	/**
	 * likelihood and prior are 2D matrices of same sizes
	 * @param logLikelihood
	 * @param logPrior
	 * @param logBeta initialized reference, forward estimation stored in it 
	 */
	public void computeLogBeta(
			ArrayList<ArrayList<Double>> logLikelihood,
			ArrayList<ArrayList<Double>> logPrior,
			ArrayList<Double> logBeta
			) {
		
		logBeta.clear();
		
		// initialize so that we can start from the end
		for (int i=0; i<logLikelihood.size(); i++)
			logBeta.add(-Double.MAX_VALUE); // log(0) = -Infinity

		// compute beta(i)
		logBeta.set(logLikelihood.size() - 1, 0.0); // log(1) = 0

		// beta(i), i < N-1
		for (int i=logLikelihood.size() - 1; i>=0; i--) {

			double val = logBeta.get(i);
			
			// beta(i, i) {= 0} // -Infinity
			// beta(i, i) = \sum_{j=i+1}^{T} beta(j) * P(traj_i,j|i,j) * P(g_j|g_i)
			for(int k=i+1; k<logLikelihood.size(); k++) {
					val = MathHelper.addTwoLogNumbers(val,
						logLikelihood.get(k).get(i) + logPrior.get(k).get(i)  
						+ logBeta.get(k)
					);
			}
			
			logBeta.set(i, val); // update
		
		}
	}
	
	/**
	 * Performs forward-backward 
	 * @param logLikelihood
	 * @param logPrior
	 * @param logAlpha
	 * @param logBeta
	 * @return logBeta(0) = logAlpha(N-1) = normalizing factor
	 */
	public double logAlphaBeta(
			ArrayList<ArrayList<Double>> logLikelihood,
			ArrayList<ArrayList<Double>> logPrior,
			ArrayList<Double> logAlpha,
			ArrayList<Double> logBeta
			) {
		
		computeLogAlpha(logLikelihood, logPrior, logAlpha);
		computeLogBeta(logLikelihood, logPrior, logBeta);
		
		return logBeta.get(0);
	}
	
	
	/**
	 * Computes the subgoal probabilities in log mode
	 * @param logLikelihood
	 * @param logPrior
	 * @return ArrayList of double as subgoal probabilities in log scale
	 */
	public ArrayList<Double> subgoalProbability(
			ArrayList<ArrayList<Double>> logLikelihood,
			ArrayList<ArrayList<Double>> logPrior
			)
	{
		ArrayList<Double> subgoalProbs = new ArrayList<Double>();
		
		ArrayList<Double> logAlpha = new ArrayList<Double>();
		ArrayList<Double> logBeta = new ArrayList<Double>();
		
		logAlphaBeta(logLikelihood, logPrior, logAlpha, logBeta);
		
		for (int i=0; i<logAlpha.size(); i++)
		{
			// beta(0) is the normalizing factor
			subgoalProbs.add(logAlpha.get(i) + logBeta.get(i) - logBeta.get(0));
		}
		
		return subgoalProbs;
	}
	
}
