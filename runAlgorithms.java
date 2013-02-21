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
		DataRead iris_data, poker_data, leaves_data;
		
		iris_data = new DataRead("./src/Data Sets/Iris/Iris.data");
		if (iris_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		
		poker_data = new DataRead("./src/Data Sets/Poker/poker-hand-training-true.data");
		if (poker_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		/*
		leaves_data = new DataRead("./src/Data Sets/Leaves/100 leaves plant species/data_Mar_64.txt");
		if (leaves_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		*/
		
		//Create a new BoostingAlgorithm for the iris_data set 
		//with a randomly selected training set 1/4 the size of the full data set
		try {
			System.out.println("Running Boosting Algorithm on Iris Data set...");
			runBoosting(iris_data);
			System.out.println("Running Boosting Algorithm on Poker Data set...");
			runBoosting(poker_data);
			//runBoosting(leaves_data);
		} catch(Exception e) {
			System.err.println("Error: "+ e.getMessage());
		}
	}
	
	//Create and run a  new BoostingAlgorithm for the passed data set 
	//with a randomly selected training set 1/4 the size of the full data set
	public static int runBoosting(DataRead data) throws Exception {
		BoostingAlgorithm boosting = new BoostingAlgorithm(data, data.linecount/4, 4);
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
	
	//Create and run a  new BoostingAlgorithm for the passed data set 
	//using the passed training set
	public static int runBoosting(DataRead data, int[] training_set) throws Exception {
		BoostingAlgorithm boosting = new BoostingAlgorithm(data, training_set, 4);
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

}
