/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * DecisionStump.java
 * Last modified: February 18, 2013
 * Authors: Torben Barsballe - V00696445
 * 	    Joanna Stratton - V00702087
 * 	    David Williams - V00701616 
 * 
 * Classifies data points using the decision stumps method.
 */
public class DecisionStump {
  double[] interval;
	boolean[] value;
	
	//Constructors
	public DecisionStump(int num_intervals) {
		interval = new double[num_intervals];
		value = new boolean[num_intervals];
	}
	//Constructors
	public DecisionStump(double[] intervals) {
		interval = intervals;
		value = new boolean[intervals.length-1];
	}

	
	/* If point >= value, returns result_gt
	 * If point < value, returns result_lt
	 */
	public boolean classify(double point) {
		for (int i =0; i < interval.length-1; i++) {
			if (point >= interval[i] && point < interval[i+1]) {
				return value[i];
			} 
		}
		return false;
	}
}
