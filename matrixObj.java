package calc;

public class matrixObj {
	private double[][] matrix;
	private int numRows;
	private int numCols;
	
	public matrixObj(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
		this.matrix = new double[numRows][numCols];
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}
	
	public double getVal(int row, int col) {
		return matrix[row][col];
	}
	
	public void setVal(int row, int col, double value) {
		matrix[row][col] = value;
	}
	
	public matrixObj add(matrixObj o) {
		matrixObj res = new matrixObj(numRows, numCols);
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				res.setVal(i, j, matrix[i][j] + o.getVal(i, j));
			}
		}
		return res;
	}
	
	public matrixObj subtract(matrixObj o) {
		matrixObj res = new matrixObj(numRows, numCols);
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				res.setVal(i, j, matrix[i][j] - o.getVal(i, j));
			}
		}
		return res;
	}
	
	public matrixObj multiply(matrixObj o) {
		matrixObj res = new matrixObj(numRows, o.getNumCols());
		
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < o.getNumCols(); j++) {
				double sum = 0.0;
				for(int k = 0; k < numCols; k++) {
					sum += matrix[i][k] * o.getVal(k, j);
				}
				res.setVal(i, j, sum);
			}
		}
		
		return res;
		
	}
	
	public String toString() {
		String temp = "";
		
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < numCols; j++) {
				if(j == 0)
					temp += "[\t";
				String thisVal = Double.toString(matrix[i][j]);
				temp += thisVal + "\t";
				if(j+1 == numCols)
					temp += "]\n";
			}
			
		}
		
		return temp;
	}

}
