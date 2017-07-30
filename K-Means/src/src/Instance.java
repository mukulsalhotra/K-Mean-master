package src;

public class Instance {
	String label;
	double[] tupleVector = new double[21];
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double[] getTupleVector() {
		return tupleVector;
	}

	public void setTupleVector(double[] tupleVector) {
		this.tupleVector = tupleVector;
	}

	public void setInstance(String line, Metadata meta){
		String[] temp = line.split(","); // divide the CSV data into individual cell
		this.label = temp[temp.length - 1].trim(); // last entry in a tuple is the class of the tuple
		int vectorCounter = 0;
		
		//Decompose the instance attribute-value into individual attribute.
		for(int i = 0; i < meta.getAttrWithValue().size(); i++){
			for(int j = 1; j < meta.getAttrWithValue().get(i).size(); j++){
				if(temp[i].equals(meta.getAttrWithValue().get(i).get(j))){
					this.tupleVector[vectorCounter] = 1;
				}else{
					this.tupleVector[vectorCounter] = 0;
				}
				
				vectorCounter++;
			}
		}
		
	}
	
}
