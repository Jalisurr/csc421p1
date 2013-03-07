/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * BoostingAlgorithm.java
 * Last modified: February 18, 2013
 * Authors: Torben Barsballe - V00696445
 *	    Joanna Stratton - V00702087
 *	    David Williams - V00701616			  
 * 
 * Implements a Boosting algorithm with decision stumps
 * 
 * Methods:
 * run_training - runs the boosting algorithm on the training data
 * classify - uses the trained algorithm to classify a data point
 * training_stats - runs the classifier against he training data, returns the training error
 * test_stats - runs the classifier against the test data (all data minus training data) and returns the error
 */

import java.util.Random;
import java.lang.Math;
import java.lang.Exception;

public class BoostingAlgorithm {
	DataRead data;
	//number of unique classes
	int classes;
	//number of features
	int features;
	//number of training iterations
	int iterations;
	//the size of the training set
	int setsize;
	
	//Objects for the boosting algorithm
	// |classes|*|features|*|iterations| classifiers (threshold value, lt or gt)
	DecisionStump[][][] classifiers;
	// |classes|*|features|*|iterations| classifier weights (alpha-t)
	double[][][] classifier_weights;
	// |classes|*|features|*|iterations| classifier error (epsilon-t)
	double[][][] classifier_error;
	// |setsize| integers for defining the training set as a subset of the input data set
	int[] training_indices;
	// |classes|*|features|*|iterations|*|setsize| data point weights
	double[][][][] training_weights;
	
	
	//initializes a BoostingAlgorithm object with a preset training set
	public BoostingAlgorithm(DataRead input, int[] training_set, int num_iterations) throws BoostingAlgorithmException {
		data = input;
		iterations = num_iterations;
		training_indices = training_set;
		
		for (int i = 0; i < training_set.length; i++) {
			if (i > 0) {
				if (training_set[i]<=training_set[i-1]) {
					throw new BoostingAlgorithmException("Indicies of Training Data set must be in ascending order");
				}
			}
			if (training_set[i] >= data.linecount || training_set[i] < 0) {
				throw new BoostingAlgorithmException("Training Data set must be a subset of the main data set");
			}
		}
		this.init();
	}
	
	//initializes a BoostingAlgorithm object with a randomized training set of training_size 
	public BoostingAlgorithm(DataRead input, int training_size, int num_iterations) throws BoostingAlgorithmException {
		
		data = input;
		iterations = num_iterations;
		Random r = new Random(training_size);
		int lowerbound;
		int upperbound;
		//verify training_size < data.linecount
		if (!(training_size < data.linecount)) {
			throw new BoostingAlgorithmException("Error: Training Data set must be smaller than main data set");
		}
		
		training_indices = new int[training_size];
		//Generate a training set chosen randomly from evenly spaced intervals of the source data 
		int interval = data.linecount/training_size;
		for (int i=0;i<training_indices.length;i++) {
			training_indices[i] = i*interval+r.nextInt(interval);
		}
		for (int i = 0; i < training_indices.length; i++) {
			if (i > 0) {
				if (training_indices[i]<=training_indices[i-1]) {
					throw new BoostingAlgorithmException("Indicies of Training Data set must be in ascending order");
				}
			}
			if (training_indices[i] >= data.linecount || training_indices[i] < 0) {
				throw new BoostingAlgorithmException("Training Data set must be a subset of the main data set");
			}
		}
		this.init();
	}
	
	//initialize classification
	private void init() {
		//System.out.println("Initializing Boosting Algorithm");
		
		classes = data.numunique;
		features = data.argcount;
		setsize = training_indices.length;
		
		//Objects for the boosting algorithm
		// |classes|*|features|*|iterations| classifiers (threshold value, lt or gt)
		classifiers = new DecisionStump[classes][features][iterations];
		// |classes|*|features|*|iterations| classifier weights
		classifier_weights = new double[classes][features][iterations];
		classifier_error = new double[classes][features][iterations];

		// |classes|*|features|*|iterations|*|setsize| data point weights
		training_weights = new double[classes][features][iterations][setsize];
		
	}
	
	
	//run # iterations of boosting against training set, redefine weights and classifiers, print statistics
	public void run_training() {
		int c, f, i, s, j;
		double weight_sum, max;
		double[] weights_true;
		double[] weights_false;
		double[] intervals;
		//each class and each feature has its own set of classifiers. 
		//These will be combined upon completion of the algorithm
		for (c = 0; c < classes; c++) {
			for (f = 0; f < features; f++) {
					
				//define a sorted list of intervals for this feature
				intervals = new double[setsize+1];
				max = data.storage[training_indices[0]][f];
				for (s = 0; s < setsize; s++) {
					intervals[s] = data.storage[training_indices[s]][f];
					if (intervals[s]>max) {
						max = intervals[s];
					}
				}
				
				intervals[intervals.length-1] = max+1;
				intervals = sort_intervals(intervals);
				
				for (i = 0; i < iterations; i++) {
					
		/*Set the weights of the data points based on the previous iteration**********************************/
		//System.out.println("Set weigths");			
					weight_sum = 0;
					for (s = 0; s < setsize; s++) {
						if (i<=0) {
						//initialize the training weights for the first iteration to 1
							training_weights[c][f][i][s] = 1;
						} else {
							//if the previous iteration correctly classified the data point, multiply weight by exp(-classifier_weights[c][f][i-1])
							//otherwise, multiply weight by exp(classifier_weights[c][f][i-1])
							//
							if (classifiers[c][f][i-1].classify(data.storage[training_indices[s]][f])==(data.classes[training_indices[s]] == data.classlist[c])) {
								training_weights[c][f][i][s] = training_weights[c][f][i-1][s]*Math.exp(-classifier_weights[c][f][i-1]);
								//System.out.println("t: "+training_weights[c][f][i][s]+" = "+training_weights[c][f][i-1][s]+"*exp("+(-classifier_weights[c][f][i-1])+")");
							} else {
								training_weights[c][f][i][s] = training_weights[c][f][i-1][s]*Math.exp(classifier_weights[c][f][i-1]);
								//System.out.println("t: "+training_weights[c][f][i][s]+" = "+training_weights[c][f][i-1][s]+"*exp("+(classifier_weights[c][f][i-1])+")");
							}
						}
						weight_sum += training_weights[c][f][i][s];
					}
					//multiply by normalization factor (n/sum)
					for (s = 0; s < setsize; s++) {
						training_weights[c][f][i][s] = training_weights[c][f][i][s]/weight_sum;
					}
					
		/*Define the classifiers using decision stumps********************************************************/
		//System.out.println("Define classifiers");	
					//for each interval, compute sum of weights
					//weights_true[j] = sum training_weights[c][f][i][s] such that data.storage[training_indices[s]][f]>=intervals[j]
					weights_true = new double[intervals.length-1];
					weights_false = new double[intervals.length-1];
					for (j = 0; j < intervals.length-1; j++) {
						weights_true[j] = 0;
						weights_false[j] = 0;
						for (s = 0;s < setsize; s++) {
							//If the data point is in the interval
							if (data.storage[training_indices[s]][f]>=intervals[j] && data.storage[training_indices[s]][f] < intervals[j+1]) {
								//if the class matches
								if (data.classes[training_indices[s]].equals(data.classlist[c])) {
									weights_true[j] += training_weights[c][f][i][s];
								} else {
									weights_false[j] += training_weights[c][f][i][s];
								}
							}
						}
					}
					//then the classifier for the interval = true if weights_true[j]>weights_false[j] and false otherwise
					classifiers[c][f][i] = new DecisionStump(intervals);
					//System.out.print("[");
					for (j = 0; j < intervals.length-1; j++) {
						classifiers[c][f][i].value[j] = weights_true[j]>=weights_false[j];
						//System.out.print(classifiers[c][f][i].value[j]+"("+weights_true[j]+":"+weights_false[j]+"), ");
					}
					//System.out.println("]");
		/*Compute the weighted error**************************************************************************/
		//System.out.println("Compute Error");
					classifier_error[c][f][i] = 0;
					for (s = 0; s < setsize; s++) {
						//if hypothesis != classification
						//System.out.println("e: "+classifiers[c][f][i].classify(data.storage[training_indices[s]][f])+" ?!= ("+data.classes[training_indices[s]]+"?="+data.classlist[c]+")");
						if (classifiers[c][f][i].classify(data.storage[training_indices[s]][f]) != (data.classes[training_indices[s]].equals(data.classlist[c]))) {
							classifier_error[c][f][i] += training_weights[c][f][i][s];
						}
					}
		/*Compute the classifier weights**********************************************************************/
		//System.out.println("Compute classifier weights");	
					if (classifier_error[c][f][i] > 0) {
						classifier_weights[c][f][i] = (0.5)*Math.log((1-classifier_error[c][f][i])/classifier_error[c][f][i]);
					} else {
						//If there is no error, choose a neutral weight
						classifier_weights[c][f][i] = 1;
					}
					//System.out.println("c: "+classifier_weights[c][f][i]+" = 0.5*ln("+(1-classifier_error[c][f][i])+"/"+classifier_error[c][f][i]+")");
				}
			}
		}
	}
	//sorts the passed array in ascending order and removes any duplicates
	private double[] sort_intervals(double[] unsorted_intervals) {
		int i, length;
		double min = unsorted_intervals[0];
		double max = unsorted_intervals[0];
		double[] sorted_intervals = new double[unsorted_intervals.length];
		double[] intervals;
		for (i=0; i < unsorted_intervals.length; i++) {
			if (max<unsorted_intervals[i]) {
				max=unsorted_intervals[i];
			}
			if (min>unsorted_intervals[i]) {
				min=unsorted_intervals[i];
			}
		}
		
		length = 0;
		sorted_intervals[length] = min;
		length = 1;
		
		while (min != max) {
			for (i=0; i < unsorted_intervals.length; i++) {
				//remove all values equal to min by setting them to max
				if (unsorted_intervals[i] <= min) {
					unsorted_intervals[i]=max;
				}
			}
			//find the new min
			min = max;
			for (i=0; i < unsorted_intervals.length; i++) {
				if (min>unsorted_intervals[i]) {
					min=unsorted_intervals[i];
				}
			}
			
			sorted_intervals[length] = min;
			length = length + 1;
		}
			
		//create a new set of intervals with duplicates removed
		intervals = new double[length];
		for (i=0; i < intervals.length; i++) {
			intervals[i] = sorted_intervals[i];
		}
		
		return intervals;
	}
	//Attempts to classify the set at index datapoint based on the trained boosting hypothesis
	//returns a boolean array matching each class with a boolean value (is the class / is not the class)
	public boolean[] classify(int datapoint) {
		double hypothesis;
		boolean[] class_list = new boolean[classes];
		int c, f, i;
		
		//System.out.println("Classifying:");
		for (c = 0; c < classes; c++) {
			hypothesis = 0;
			//System.out.println("   "+data.classlist[c]+":");
			for (f = 0; f < features; f++) {
				//System.out.print("      "+data.storage[datapoint][f]+": [");
				for (i = 0; i < iterations; i++) {
					if (classifiers[c][f][i].classify(data.storage[datapoint][f])) {
				//System.out.print("true, ");
						hypothesis += classifier_weights[c][f][i];
					} else {
				//System.out.print("false, ");
						hypothesis -= classifier_weights[c][f][i];
					}
				}
				//System.out.println("]");
			}
			class_list[c] = hypothesis > 0;
		}
		return class_list;
	}
	
	public void print_classifier() {
		int c, f, i, j;
		
		for (c = 0; c < classes; c++) {
			
			System.out.println(data.classlist[c]+":");
			for (f = 0; f < features; f++) {
				System.out.print("   "+f+"{");
				for (i = 0; i < iterations; i++) {
					System.out.print(i+"[");
					for (j = 0; j < classifiers[c][f][i].value.length; j++) {
						System.out.print(classifiers[c][f][i].interval[j]+"-"+classifiers[c][f][i].interval[j+1]+":"+classifiers[c][f][i].value[j]+", ");
					}
					System.out.print("] ");
				}
				System.out.println("}");
			}
		}
		
	}
	
	//compares the boolean class_list returned from classify() with the actual class of the datapoint
	//returns:
	//	0 for correct classification
	//	1 for correct classification but also some incorrect classifications
	//	2 for incorrect classifications
	//	3 for no classification (class_list[i] = false for all i)
	public int compare(boolean[] class_list, int datapoint) throws BoostingAlgorithmException{
		int c;
		int class_index = -1;
		int classification = -1;
		//determine what class in the classlist the datapoint has
		for (c = 0; c < classes; c++) {
			if (data.classes[datapoint].equals(data.classlist[c])) {
				class_index = c;
			}
		}
		if (class_index == -1) {
			throw new BoostingAlgorithmException("Class \"" + data.classes[datapoint] + "\" not found in class list");
		}
		//Check if the correct class is indicated
		if (class_list[class_index]) {
			//the correct class is selected
			classification = 0;
		} else {
			//the correct class is not selected
			classification = 3;
		}
		for (c = 0; c < classes; c++) {
			//check if any other classes are indicated
			if (c != class_index && class_list[c]) {
				if (classification == 0) {
					//the correct class is selected, but so are other classes
					classification = 1;
				} else if (classification == 3) {
					//the correct class is not selected, but other classes are selected
					classification = 2;
				}
			}
		}
		
		return classification;
	}
	
	
	//runs the classifier against the training set and prints stats
	public void training_stats() throws BoostingAlgorithmException {
		boolean class_list[];
		int result;
		int[] statistics = {0, 0, 0, 0};
		
		for (int s = 0; s < setsize; s++) {
			class_list = this.classify(training_indices[s]);
			result = this.compare(class_list, training_indices[s]);
			statistics[result] += 1;
		}
		System.out.println("Training Data results:");
		this.print_statistics(statistics);
	}
	//runs the classifier against the data set and prints stats
	public void test_stats() throws BoostingAlgorithmException {
		boolean class_list[];
		int result;
		int[] statistics = {0, 0, 0, 0};
		int s = 0;
		//System.out.println(data.classes[150]);
		for (int i = 0; i < data.linecount-1; i++) {
			//only test against data not in the training set
			if (s<training_indices.length && i == training_indices[s]) {
				s++;
			} else {
				class_list = this.classify(i);
				result = this.compare(class_list, i);
				//System.out.println(s+", "+i+": "+result);
				statistics[result] += 1;
			}
		}
		System.out.println("General Test Data results:");
		this.print_statistics(statistics);
	}
	
	private void print_statistics(int[] statistics) {
		int sum = 0;
		int error = 0;
		double error_ratio;
		for (int i = 0; i < 4; i++) {
			sum += statistics[i];
		}
		error = sum - statistics[0];
		error_ratio = ((double)(error))/((double)(sum));
		
		
		System.out.println("Error Ratio: "+error_ratio);
		System.out.println("Total data points: "+sum);
		System.out.println("Correctly classified points: "+statistics[0]);
		System.out.println("Overclassified points: "+statistics[1]);
		System.out.println("Incorectly classified points: "+statistics[2]);
		System.out.println("Unclassified points: "+statistics[3]);
	}
	public class BoostingAlgorithmException extends Exception {
		public BoostingAlgorithmException(String message) {
			super(message);
		}
	}
	
}
