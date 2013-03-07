/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * DecisionStump.java
 * Last modified: February 21, 2013
 * Authors: Torben Barsballe - V00696445
 * 	    Joanna Stratton - V00702087
 * 	    David Williams - V00701616 
 * 
 * Runs DataRead and Classification algorithms on the Iris and Poker data sets
 */
public class runAlgorithms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataRead data;
		int iterations = 4; 		//Number of iterations
		int size = 4;	//Size of the training set = |Data|/size
		
		//Create a new BoostingAlgorithm for the iris_data set 
		//with a randomly selected training set 1/4 the size of the full data set
		
			
		//IRIS */
		//http://archive.ics.uci.edu/ml/datasets/Iris
		try {
			data = new DataRead("./Iris.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Iris Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Iris Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
			
			
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
			
		//DIABETES */
		//http://archive.ics.uci.edu/ml/datasets/Pima+Indians+Diabetes
		
		try {	
			data = new DataRead("./pima-indians-diabetes.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Diabetes Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Diabetes Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
		//HEART */
		//http://archive.ics.uci.edu/ml/datasets/SPECTF+Heart
		try {
			data = new DataRead("./SPECTF.train", true);
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Heart Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Heart Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
		//CANCER SURVIVAL */
		//http://archive.ics.uci.edu/ml/datasets/Haberman%27s+Survival
		try {	
			data = new DataRead("./haberman.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Cancer Survival Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Cancer Survival Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		//POKER */
		//http://archive.ics.uci.edu/ml/datasets/Poker+Hand
		/*
		try {
			data = new DataRead("./poker-hand-training-true.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Poker Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Poker Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
			
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		//LEAVES */
		//http://archive.ics.uci.edu/ml/datasets/One-hundred+plant+species+leaves+data+set#
		/*	
		try {	
			data = new DataRead("./data_Sha_64.txt", true);
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Leaves Data set...");
			runBoosting(data, 4, iterations);
			System.out.println("Running Boosting with Perceptron Algorithm on Leaves Data set...");
			runBoosting_p(data, 4, iterations);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		// */
			
		// TESTING KERNELIZED PERCEPTRON
		System.out.println("Testing the Kernelized Perceptron Algorithm:");
		System.out.println("   -tested with 1, 2, 3, 4, 5, 10, 100, and 1000 iterations");
		System.out.println("   -tested with 10/90, 25/75, and 40/60 splits of training/test data");
		System.out.println();
		System.out.println("Below are the most efficient cases with low test errors and a low number of iterations:");
		System.out.println();
	
		// TEST CASE #1: The Pima Indian Diabetes Data Set
		KernelPerceptron kp1 = new KernelPerceptron("pima-indians-diabetes.data", 3, 0.25);
		kp1.convertClassifiers();									// Convert the classifiers to -1 and +1.
		for (int i = 0; i < kp1.data.storage.length; i++)	// Normalize the data.
		{
			kp1.data.storage[i] = kp1.normalizeVector(kp1.data.storage[i]);
		}
		kp1.kernalPerceptron(2);		// Run kernelized perceptron for a maximum of 2 iterations.
		
		System.out.println("Case #1: The Pima Indian Diabetes Data Set");
		System.out.println("     Degree: 3");
		System.out.println("     Training Data: 25% - " + kp1.trainingSetSize + " instances");
		System.out.println("     Test Data: 75% - " + kp1.testSetSize + " instances");
		System.out.println("     Maximum Iterations: 2");
		kp1.generateStatistics();		// Calculate and print the training and test errors.
	
		
		
		// TEST CASE #2: The Haberman Cancer Survival Rate
		KernelPerceptron kp2 = new KernelPerceptron("haberman.data", 3, 0.10);
		kp2.convertClassifiers();									// Convert the classifiers to -1 and +1.
		for (int i = 0; i < kp2.data.storage.length; i++)	// Normalize the data.
		{
			kp2.data.storage[i] = kp2.normalizeVector(kp2.data.storage[i]);
		}
		kp2.kernalPerceptron(2);		// Run kernelized perceptron for a maximum of 2 iterations.
		
		System.out.println("Case #2: The Haberman Cancer Survival Rate");
		System.out.println("     Degree: 3");
		System.out.println("     Training Data: 10% - " + kp2.trainingSetSize + " instances");
		System.out.println("     Test Data: 90% - " + kp2.testSetSize + " instances");
		System.out.println("     Maximum Iterations: 2");
		kp2.generateStatistics();		// Calculate and print the training and test errors.
		
		
		// The section below can be uncommented and edited to run your own perceptron tests
		
/*
		// These four variables can be modified as desired to run tests on perceptron.
		String fileName = "pima-indians-diabetes.data";
		int degree = 3;
		double trainingPercentage = 0.25;
		int maximumIterations = 10;
		
		KernelPerceptron kp3 = new KernelPerceptron(fileName, degree, trainingPercentage);
		kp3.convertClassifiers();									// Convert the classifiers to -1 and +1.
		for (int i = 0; i < kp3.data.storage.length; i++)	// Normalize the data.
		{
			kp3.data.storage[i] = kp3.normalizeVector(kp3.data.storage[i]);
		}
		kp3.kernalPerceptron(2);		// Run kernel perceptron.
		
		System.out.println("Running a customized kernelized perceptron test:");
		System.out.println("     Degree: " + degree);
		System.out.println("     Training Data: " + kp3.trainingSetSize + " instances");
		System.out.println("     Test Data: " + kp3.testSetSize + " instances");
		System.out.println("     Maximum Iterations: " + maximumIterations);
		kp3.generateStatistics();		// Calculate and print the training and test errors.
*/	
	}
	
	//Create and run a  new BoostingAlgorithm for the passed data set 
	//with a randomly selected training set 1/size the size of the full data set
	public static int runBoosting(DataRead data, double size, int iterations) throws Exception {
		BoostingAlgorithm boosting = new BoostingAlgorithm(data, (int)(data.linecount/(size)), iterations);
		if (boosting == null) {
			//Error
			System.err.println("Error: BoostingAlgorithm object not initialized");
			return -1;
		}
		boosting.run_training();
		//boosting.print_classifier();
		boosting.training_stats();
		boosting.test_stats();
		return 0;
	}
	//Create and run a  new BoostingAlgorithm_p for the passed data set 
	//with a randomly selected training set 1/size the size of the full data set
	public static int runBoosting_p(DataRead data, double size, int iterations) throws Exception {
		BoostingAlgorithm_p boosting = new BoostingAlgorithm_p(data, (int)(data.linecount/(size)), iterations);
		if (boosting == null) {
			//Error
			System.err.println("Error: BoostingAlgorithm object not initialized");
			return -1;
		}
		boosting.run_training();
		//boosting.print_classifier();
		boosting.training_stats();
		boosting.test_stats();
		return 0;
	}
	
	/*
	//Create and run a  new BoostingAlgorithm for the passed data set 
	//using the passed training set
	public static int runBoosting(DataRead data, int[] training_set, int iterations) throws Exception {
		BoostingAlgorithm boosting = new BoostingAlgorithm(data, training_set, iterations);
		if (boosting == null) {
			//Error
			System.err.println("Error: BoostingAlgorithm object not initialized");
			return -1;
		}
		boosting.run_training();
		//boosting.print_classifier();
		boosting.training_stats();
		boosting.test_stats();
		return 0;
	}
	 */
	 
	 
}
