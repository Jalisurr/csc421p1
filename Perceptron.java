/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * DecisionStump.java
 * Last modified: February 18, 2013
 * Authors: Torben Barsballe - V00696445
 * 	    Joanna Stratton - V00702087
 * 	    David Williams - V00701616 
 * 
 * Classifies data points using the perceptron method.
 */
public class Perceptron {
  double[] weights;
	
	public Perceptron (int numfeatures) {
		weights = new double[numfeatures+1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = 0;
		}
	}
	
	//tests the array of data against weights. if > 0 returns true. 
	//if <= 0 adds data to weights and returns false
	public boolean update(double[] data, double weight, boolean classifier) {
		double sum = 0;
		
		//create a new data vector to encompass all data points plus classifier
		double[] vector = new double[data.length+1];
		double norm = 1;
		for (int i = 0; i < data.length; i++) {
			vector[i] = data[i];
			norm += data[i]*data[i];
		}
		//x_n+1 = -1
		
		vector[vector.length-1] = -1;
		
		norm = Math.sqrt(norm);
		//If the classifier is false, map x to -x.
		if (!classifier) {
			norm*=-1;
		}
		//normalize data vector
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i]/norm;
		}
		
		if (vector.length != weights.length) {
			System.err.println("Error: Perceptron must be the same lenghts as features + classifier");
			return false;
		}
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i]*vector[i];
		}
		//System.out.println("Sum: "+sum);
		if (sum > 0) {
			
			return true;
		}
		//update weights
		for (int i = 0; i < weights.length; i++) {
			weights[i]+=vector[i];
		}
		return false;
	}
	public void print_classifier() {
		for (int i = 0; i < weights.length; i++) {
			System.out.println(weights[i]+", ");
		}
	}
	
	public boolean classify(double[] data) {
		double sum = 0;
		
		//create a new data vector to encompass all data points plus classifier
		double[] vector = new double[data.length+1];
		double norm = 1;
		for (int i = 0; i < data.length; i++) {
			vector[i] = data[i];
			norm += data[i]*data[i];
		}
		//test for positive classifier
		vector[vector.length-1] = -1;
		norm = Math.sqrt(norm);
		//normalize data vector
		for (int i = 0; i < vector.length; i++) {
			vector[i] = vector[i]/norm;
		}
		
		if (vector.length != weights.length) {
			System.err.println("Error: Perceptron must be the same lenghts as features + classifier");
			return false;
		}
		for (int i = 0; i < weights.length; i++) {
			sum += vector[i]*weights[i];
		}
		if (sum > 0) {
			return true;
		}
		return false;
	}
}
