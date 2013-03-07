/*****************************************************************************************
* CSC 421 Artificial Intelligence - Project 1
* University of Victoria
*
* KernelPerceptron.java
* Authors: Joanna Stratton - V00702087
*  		  David Williams - V00701616
*			  Torben Barsballe - V00696445
*****************************************************************************************/
import java.lang.Math.*;
import java.lang.Exception;
import java.lang.Double;
public class KernelPerceptron
{

	// Data members.
	DataRead data;		// Variable to store the data.
	int d;			// The degree in the kernel equation.
	int trainingSetSize;	// The size of the training set.
	int testSetSize;	// The size of the test set.
	double[] c;		// The implicit classifier.
	int[] trainingIndices;	// Holds the indices of the training set.
	int[] testIndices;	// Hold the indices of the test set.		
	
	
	/*************************************************************************
	* Default constructor.
	*************************************************************************/
	KernelPerceptron()
	{
		data = null;
		d = 0;
		trainingSetSize = 0;
		testSetSize = 0;
		c = new double[0];
		trainingIndices = new int[0];
		testIndices = new int[0];
	}
	
	/*************************************************************************
	* Constructor that takes the following parameters:
	*	1) the name of the data file to use
	*	2) the degree of the polynomials
	*	3) the percentage of cases to use in training set, expressed as a decimal
	* Note: class attribute is assumed to be last attribute in each line of file.
	*************************************************************************/
	KernelPerceptron(String fileName, int degree, double trainingPercentage)
	{
		data = new DataRead(fileName);
		d = degree;
		trainingSetSize = (int)Math.floor(((double)data.linecount)*trainingPercentage);
		testSetSize = data.linecount - trainingSetSize;
		c = new double[trainingSetSize];
		
		trainingIndices = new int[trainingSetSize];
		testIndices = new int[testSetSize];
		// Set training indices to be an even space apart in data file.
		int index = 0;
		int step = (int)Math.floor(((double)data.linecount)/((double)trainingSetSize));
		for(int i = 0; i < trainingSetSize; i++)
		{
			trainingIndices[i] = index;
			index += step;
		}
		// Set test indices to be every index that is not a training index.
		int j = 0;
		for (int i = 0; i < data.linecount; i++)
		{
			if (containsInt(trainingIndices, i))
			{
				continue;
			}
			else
			{
				testIndices[j] = i;
				j++;
			}
		}	
	}
	
	/*************************************************************************
	* Constructor that takes the following parameters:
	*	1) the name of the data file to be read
	*	2) the degree of the polynomials
	*	3) the percentage of cases to use in training set, expressed as a decimal
	*	4) boolean variable describing whether or not class attribute is first
	*		attribute in the data set.
	*************************************************************************/
	KernelPerceptron(String fileName, int degree, double trainingPercentage, boolean classFirst)
	{
		data = new DataRead(fileName, classFirst);
		d = degree;
		trainingSetSize = (int)Math.floor(((double)data.linecount)*trainingPercentage);
		testSetSize = data.linecount - trainingSetSize;
		c = new double[trainingSetSize];
		
		trainingIndices = new int[trainingSetSize];
		testIndices = new int[testSetSize];
		// Set training indices to be an even space apart in data file.
		int index = 0;
		int step = (int)Math.floor(((double)data.linecount)/((double)trainingSetSize));
		for(int i = 0; i < trainingSetSize; i++)
		{
			trainingIndices[i] = index;
			index += step;
		}
		// Set test indices to be every index that is not a training index.
		int j = 0;
		for (int i = 0; i < data.linecount; i++)
		{
			if (containsInt(trainingIndices, i))
			{
				continue;
			}
			else
			{
				testIndices[j] = i;
				j++;
			}
		}
	}
	
	
	/*************************************************************************
	* Method Name: containsInt()
	* Purpose: helper method to determine whether an int is an element in an array
	* Parameters: an int and an array of ints
	* Return Type: boolean true if int parameter is in the array, false otherwise
	*************************************************************************/
	boolean containsInt(int[] array, int num)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == num)
			{
				return true;
			}
		}
		return false;
	}			
				
				
	/*************************************************************************
	* Method Name: convertClassifiers()
	* Purpose: to map the classifiers in the data set to +1 and -1
	* Parameters: none
	* Return Type: void
	*************************************************************************/
	void convertClassifiers()
	{		
		String class1;
		String class2;
		
		// Get the first class of the data set.
		class1 = data.classes[0];								
		// Find the second distinct class of the data set.
		for (int i = 1; i < data.classes.length; i++)	
		{
			if (class1.compareTo(data.classes[i]) != 0)
			{
				class2 = data.classes[i];
				break;
			}
		}
		// Map the class of every data entry to +1 or -1 accordingly.
		for (int i = 0; i < data.classes.length; i++)
		{
			if (class1.compareTo(data.classes[i]) == 0)	// Map the first distinct class to 1.0
			{
				data.classes[i] = "1.0";
			}
			else														          // Map the second distinct class to -1.0
			{
				data.classes[i] = "-1.0";
			}
 		}
	}
	
	/*************************************************************************
	* Method Name: kernalPerceptron()
	* Purpose: runs kernalized perceptron on the test data and updates the 
	*			  implicit classifier to represent a hyperplane separating the 
	*			  data by their labels
	* Parameters: an int representing the maximum iterations to allow
	* Return Type: void
	* NOTE: kernelPerceptron() is meant to run on normalized data
	*************************************************************************/
	void kernalPerceptron(int maxIterations)
	{
		// Set all elements in c, the implicit classifier, to 0
		for (int j = 0; j < trainingSetSize; j++)
		{
			c[j] = 0.0;
		}
		// While maximum iterations is not exceeded.													
		for (int iterations = 1; iterations <= maxIterations; iterations++)
		{
			// Count the number of incorrectly classified data entries in training set.
			int incorrectCount = 0;
			for (int a = 0; a < trainingSetSize; a++) {
				if (correctClassifier(data.storage[trainingIndices[a]], data.classes[trainingIndices[a]]) == false){
					incorrectCount++;					
				}
			}
			// If every training example if properly classified, then classifier does not need to be further modified.
			if (incorrectCount == 0)	
			{
				break;
			}
			
			for (int j = 0; j < trainingSetSize; j++)		
			{	
				// Determine whether or not each entry in training set is misclassified.
				double temp = 0.0;	// Temp variable to hold the classification.					
				for (int k = 0; k < trainingSetSize; k++)
				{
					// Apply the kernel function.
					temp += c[k]*(Math.pow((1 + dotProduct(data.storage[trainingIndices[k]], data.storage[trainingIndices[j]])), d));
				}
				temp *= Double.parseDouble(data.classes[trainingIndices[j]]);
				// If jth entry is misclassified.
				if (temp <= 0)											
				{
					// Update the implicit classifier.
					c[j] = c[j] + Double.parseDouble(data.classes[trainingIndices[j]]);
				}
			}	
		} 
	} // end method

	
	/*************************************************************************
	* Method Name: correctClassifier()
	* Purpose: to determine whether kernalized perceptron has correctly 
	*			  classified a data point
	* Parameters: double[] - an element of the data set
	*				  String - the corresponding classifier to the element
	* Return Type: boolean - true if classified correctly, false otherwise
	* NOTE: assumes method will be called with data.storage[i] and data.classes[i]
	*************************************************************************/
	boolean correctClassifier(double[] x, String y)
	{
		double classifier = 0.0;	// A temporary variable to store the classifier label.
		//  Use implicit classifier to calculate (dot(w, phi(x)) - theta)
		for (int j = 0; j < trainingSetSize; j++)
		{
			classifier += c[j]*(Math.pow((1 + dotProduct(x, data.storage[trainingIndices[j]])), d));
		}

		// Return true if sign(classifier) == y, return false if sign(classifier) != y;
		if (classifier < 0)	{
			if (y.equals("-1.0")) {
				return true;
			}
			else {
				return false;
			}
		}
		else
		{
			if (y.equals("1.0")) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	
	/*************************************************************************
	* Method Name: generateStatistics()
	* Purpose: to calculate and print the training and test error of 
	*          kernalized perceptron
	* Parameters: none
	* Return Type: void
	*************************************************************************/
	void generateStatistics()
	{
		int trainingErrorCount;
		int testErrorCount;
		double trainingErrorPercentage;
		double testErrorPercentage;
	
		// Calculate Training Error
		trainingErrorCount = 0;
		for (int i = 0; i < trainingSetSize; i++)
		{
			if (correctClassifier(data.storage[trainingIndices[i]], data.classes[trainingIndices[i]]) == false)
			{
				trainingErrorCount++;
			}
		}
		trainingErrorPercentage = ((double)trainingErrorCount)/((double)trainingSetSize)*100.0;
		
		// Calculate Test Error
		testErrorCount = 0;
		for (int i = 0; i < testSetSize; i++)
		{
			if (correctClassifier(data.storage[testIndices[i]], data.classes[testIndices[i]]) == false)
			{
				testErrorCount++;
			}
		}
		testErrorPercentage = ((double)testErrorCount)/((double)testSetSize)*100.0;
		
		// Print statistics
		System.out.println("     Training Error: " + trainingErrorPercentage);
		System.out.println("     Test Error: " + testErrorPercentage);
	}



	/*************************************************************************
	* Method Name: dotProduct()
	* Purpose: helper function to calculate the dot (inner) product between two vectors
	* Parameters: two double arrays to store the two vectors
	* Return Type: a double representing the dot product
	*************************************************************************/
	double dotProduct(double[] vec1, double[] vec2)
	{
		double result = 0.0;
		if (vec1.length == vec2.length)
		{
			for (int i = 0; i < vec1.length; i++) 
			{
				result += vec1[i]*vec2[i];
			}
		}
		return result;
	}


	/*************************************************************************
	* Method Name: normalizeVector()
	* Purpose: helper method to create a unit vector (vector with length = 1) 
	*          in the same direction as the original vector
	* Parameters: a double array storing the vector to be normalized
	* Return Type: a double array storing the normalized vector
	*************************************************************************/
	double[] normalizeVector(double[] vec)
	{
		double[] normalizedVec = new double[vec.length];
		double length = 0.0;
		// Calculate the length of the vector.
		for (int i = 0; i < vec.length; i++)
		{
			length += Math.pow(vec[i],2.0);
		}
		length = Math.sqrt(length);
		// Divide each element in the vector by its length.
		for (int j = 0; j < normalizedVec.length; j++)
		{
			normalizedVec[j] = vec[j]/length;
		}
		return normalizedVec;
	}	

} // end class.
