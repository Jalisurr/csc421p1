/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * BoostingAlgorithm_p.java
 * Last modified: March 2, 2013
 * Author:Torben Barsballe - V00696445
 * 
 * Implements a Boosting algorithm with perceptron
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

public class BoostingAlgorithm_p {
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
	// |classes|*|iterations| classifiers (threshold value, lt or gt)
	Perceptron[][] classifiers;
	// |classes|*|iterations| classifier weights (alpha-t)
	double[][] classifier_weights;
	// |classes|*|iterations| classifier error (epsilon-t)
	double[][] classifier_error;
	// |setsize| integers for defining the training set as a subset of the input data set
	int[] training_indices;
	// |classes|*|iterations|*|setsize| data point weights
	double[][][] training_weights;
	
	
	//initializes a BoostingAlgorithm object with a preset training set
	public BoostingAlgorithm_p(DataRead input, int[] training_set, int num_iterations) throws BoostingAlgorithmException {
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
	public BoostingAlgorithm_p(DataRead input, int training_size, int num_iterations) throws BoostingAlgorithmException {
		
		data = input;
		iterations = num_iterations;
		Random r = new Random(training_size);
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
		classifiers = new Perceptron[classes][iterations];
		// |classes|*|features|*|iterations| classifier weights
		classifier_weights = new double[classes][iterations];
		classifier_error = new double[classes][iterations];

		// |classes|*|features|*|iterations|*|setsize| data point weights
		training_weights = new double[classes][iterations][setsize];
		
	}
	
	
	//run # iterations of boosting against training set, redefine weights and classifiers, print statistics
	public void run_training() throws BoostingAlgorithmException {
		int c, i, s, b;
		int rand_s;
		double weight_sum;
		Random r;
		//each class and each feature has its own set of classifiers. 
		//These will be combined upon completion of the algorithm
		for (c = 0; c < classes; c++) {
			for (i = 0; i < iterations; i++) {
				
		/*Set the weights of the data points based on the previous iteration**********************************/
		//System.out.println("Set weigths");			
				weight_sum = 0;
				for (s = 0; s < setsize; s++) {
					if (i<=0) {
					//initialize the training weights for the first iteration to 1
						training_weights[c][i][s] = 1;
					} else {
						//if the previous iteration correctly classified the data point, multiply weight by exp(-classifier_weights[c][f][i-1])
						//otherwise, multiply weight by exp(classifier_weights[c][f][i-1])
						//
						if (classifiers[c][i-1].classify(data.storage[training_indices[s]])==(data.classes[training_indices[s]] == data.classlist[c])) {
							training_weights[c][i][s] = training_weights[c][i-1][s]*Math.exp(-classifier_weights[c][i-1]);
							//System.out.println("t: "+training_weights[c][i][s]+" = "+training_weights[c][i-1][s]+"*exp("+(-classifier_weights[c][i-1])+")");
						} else {
							training_weights[c][i][s] = training_weights[c][i-1][s]*Math.exp(classifier_weights[c][i-1]);
							//System.out.println("t: "+training_weights[c][i][s]+" = "+training_weights[c][i-1][s]+"*exp("+(classifier_weights[c][i-1])+")");
						}
					}
					weight_sum += training_weights[c][i][s];
				}
				
				//multiply by normalization factor (n/sum)
				for (s = 0; s < setsize; s++) {
					training_weights[c][i][s] = training_weights[c][i][s]/weight_sum;
				}
				
	/*Define the classifiers using perceptron********************************************************/
	//System.out.println("Define classifiers");
				//Train the perceptron classifier
				classifiers[c][i] = new Perceptron(data.argcount);
				//System.out.print("[");
				boolean result = false;
				b = 0;
				r = new Random();
				while (!result) {
					result = false;
					for (s = 0; s < setsize/2; s++) {
						//Select a subset of training data based on weighted distribution
						rand_s = getRandomData(r.nextDouble(),training_weights[c][i]);
						result = classifiers[c][i].update(data.storage[training_indices[rand_s]], training_weights[c][i][rand_s], data.classes[training_indices[rand_s]].equals(data.classlist[c]));
						if (!result) {
							break;
						}
					}
					b++;
					if (b>1000*data.argcount*setsize) {
						//throw new BoostingAlgorithmException("Perceptron does not converge!");
						System.err.println("Warning: Perceptron failed to converge!");
						break;
					}
					//classifiers[c][i].print_classifier();
				}
				//System.out.println("]");
	/*Compute the weighted error**************************************************************************/
	//System.out.println("Compute Error");
				classifier_error[c][i] = 0;
				for (s = 0; s < setsize; s++) {
					//if hypothesis != classification
					//System.out.println("e: "+classifiers[c][i].classify(data.storage[training_indices[s]])+" ?!= ("+data.classes[training_indices[s]]+"?="+data.classlist[c]+")");
					if (classifiers[c][i].classify(data.storage[training_indices[s]]) != (data.classes[training_indices[s]].equals(data.classlist[c]))) {
						classifier_error[c][i] += training_weights[c][i][s];
					}
				}
	/*Compute the classifier weights**********************************************************************/
	//System.out.println("Compute classifier weights");	
				if (classifier_error[c][i] > 0) {
					classifier_weights[c][i] = (0.5)*Math.log((1-classifier_error[c][i])/classifier_error[c][i]);
				} else {
					//If there is no error, choose a neutral weight
					classifier_weights[c][i] = 1;
				}
				//System.out.println("c: "+classifier_weights[c][i]+" = 0.5*ln("+(1-classifier_error[c][i])+"/"+classifier_error[c][i]+")");
				//classifiers[c][i].print_classifier();
			}
		}
	}
	//Determines for which index i of weights that Sum[0:i-1] weights < r <= Sum[0:i]
	//Note 0 <= r <= Sum[0:n] weights
	int getRandomData(double r, double[] weights) {
		double sum;
		if (r <= weights[0]) {
			return 0;
		}
		sum = 0;
		for (int s = 1; s < weights.length; s++) {
			if (r > sum && r <= sum + weights[s]) {
				return s;
			}
			sum += weights[s];
		}
		return weights.length-1;
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
				//System.out.print("      "+data.storage[datapoint]+": [");
				for (i = 0; i < iterations; i++) {
					if (classifiers[c][i].classify(data.storage[datapoint])) {
				//System.out.print("true, ");
						hypothesis += classifier_weights[c][i];
					} else {
				//System.out.print("false, ");
						hypothesis -= classifier_weights[c][i];
					}
				}
				//System.out.println("]");
			}
			class_list[c] = hypothesis > 0;
		}
		return class_list;
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
		
		
		System.out.println("  Error Ratio: "+error_ratio);
		System.out.println("  Total data points: "+sum);
		System.out.println("  Correctly classified points: "+statistics[0]);
		System.out.println("  Overclassified points: "+statistics[1]);
		System.out.println("  Incorectly classified points: "+statistics[2]);
		System.out.println("  Unclassified points: "+statistics[3]);
	}
	public class BoostingAlgorithmException extends Exception {
		public BoostingAlgorithmException(String message) {
			super(message);
		}
	}
	
}

