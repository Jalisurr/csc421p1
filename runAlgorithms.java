/* CSC 421 Artificial Intelligence - Project 1
 * University of Victoria
 * 
 * DecisionStump.java
 * Last modified: March 2, 2013
 * Author:Torben Barsballe - V00696445
 * 
 * Runs DataRead and Boosting (both with Decision Stumps and Perceptron) algorithms on the Iris, Diabeters, Heart and Cancer data sets
 */
public class runAlgorithms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataRead data;
		int i = 3; 		//Number of iterations
		int size = 4;	//Size of the training set = |Data|/size
		
		//Create a new BoostingAlgorithm for the iris_data set 
		//with a randomly selected training set 1/4 the size of the full data set
		
			
		//IRIS */
		
		try {
			data = new DataRead("./src/Data Sets/Iris/Iris.data");
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
		
		try {	
			data = new DataRead("./src/Data Sets/pima-indians-diabetes.data");
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
		
		try {
			data = new DataRead("./src/Data Sets/SPECTF.train", true);
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
		
		try {	
			data = new DataRead("./src/Data Sets/haberman.data");
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
		
		//SKIN */
		/*
		try {
			data = new DataRead("./src/Data Sets/Skin_NonSkin.txt");
			if (data == null) {
				//Error
				System.err.println("Error: DataRead object not initialized");
				System.exit(0);
			}
			System.out.println("Running Boosting with Decision Stumps Algorithm on Skin Data set...");
			runBoosting(data, 4, i);
			System.out.println("Running Boosting with Perceptron Algorithm on Skin Data set...");
			runBoosting_p(data, 4, i);
			data = null;
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
		
		//POKER */
		/*
		try {
			data = new DataRead("./src/Data Sets/Poker/poker-hand-training-true.data");
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
		/*	
		try {	
			data = new DataRead("./src/Data Sets/Leaves/100 leaves plant species/data_Sha_64.txt", true);
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
