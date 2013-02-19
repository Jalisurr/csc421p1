
public class runAlgorithms {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataRead iris_data, poker_data, leaves_data;
		
		iris_data = new DataRead("./Data Sets/Iris/Iris.data");
		if (iris_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		/*
		poker_data = new DataRead("./Data Sets/Poker/poker-hand-training-true.data");
		if (poker_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		leaves_data = new DataRead("./Data Sets/Leaves/100 leaves plant species/data_Mar_64.txt");
		if (leaves_data == null) {
			//Error
			System.err.println("Error: DataRead object not initialized");
			System.exit(0);
		}
		*/
		
		//Create a new BoostingAlgorithm for the iris_data set 
		//with a randomly selected training set 1/4 the size of the full data set
		try {
			runBoosting(iris_data);
			//runBoosting(poker_data);
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
		boosting.training_stats();
		boosting.test_stats();
		return 0;
	}

}
