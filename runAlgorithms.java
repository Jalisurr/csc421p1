/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * DecisionStump.java
 * Last modified: February 21, 2013
 * Author:Torben Barsballe - V00696445
 * 
 * Runs DataRead and Classification algorithms on the Iris and Poker data sets
 */
public class runAlgorithms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataRead data;
		int i = 4; 		//Number of iterations
		int size = 4;	//Size of the training set = |Data|/size
		
		//Create a new BoostingAlgorithm for the iris_data set 
		//with a randomly selected training set 1/4 the size of the full data set
		
			
		//IRIS */
		//http://archive.ics.uci.edu/ml/datasets/Iris
		try {
			data = new DataRead("./src/Iris.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Iris Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Iris Data set...");
			runBoosting_p(data, 4, i);
			data = null;
			
			
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
			
		//DIABETES */
		//http://archive.ics.uci.edu/ml/datasets/Pima+Indians+Diabetes
		
		try {	
			data = new DataRead("./src/pima-indians-diabetes.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Diabetes Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Diabetes Data set...");
			runBoosting_p(data, 4, i);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
		//HEART */
		//http://archive.ics.uci.edu/ml/datasets/SPECTF+Heart
		try {
			data = new DataRead("./src/SPECTF.train", true);
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Heart Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Heart Data set...");
			runBoosting_p(data, 4, i);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		
		//CANCER SURVIVAL */
		//http://archive.ics.uci.edu/ml/datasets/Haberman%27s+Survival
		try {	
			data = new DataRead("./src/haberman.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Cancer Survival Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Cancer Survival Data set...");
			runBoosting_p(data, 4, i);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		//POKER */
		//http://archive.ics.uci.edu/ml/datasets/Poker+Hand
		/*
		try {
			data = new DataRead("./src/poker-hand-training-true.data");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Poker Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Poker Data set...");
			runBoosting_p(data, 4, i);
			data = null;
			
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		//LEAVES */
		//http://archive.ics.uci.edu/ml/datasets/One-hundred+plant+species+leaves+data+set#
		/*	
		try {	
			data = new DataRead("./src/100 leaves plant species/data_Sha_64.txt", true);
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Leaves Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Leaves Data set...");
			runBoosting_p(data, 4, i);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		// */
			
		
		
		
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
