package src;

import java.util.ArrayList;

public class Cluster {
	ArrayList<Instance> members = new ArrayList<Instance>();//All instances belonging to a clusters
	String label;//Class having max no of elements
	int[] clusterClass = new int[4];//amount of members belonging to a individual class
	Instance center = new Instance();//The centroid/center of a cluster
	double SSE = 0.0; //Sum of Squared Error
	
	public int[] getClusterClass() {
		return clusterClass;
	}

	public void setClusterClass(int[] clusterClass) {
		this.clusterClass = clusterClass;
	}
	
	public double getSSE() {
		return SSE;
	}

	public void setSSE(double sse) {
		SSE = sse;
	}
	
	public void updateSSE(double sse_increment){
		this.SSE = this.SSE + Math.round(sse_increment*100.0)/100.0;//increment the SSE per 
	}

	//Use the mean of the values of instances to find the center of the cluster.
	public void updateCenter(int k){
		Instance clusCenter = new Instance();// To hold the new updated cluster centers
		
		for(int i = 0; i<this.members.size();i++){
			for(int j = 0; j<this.members.get(0).getTupleVector().length; j++){
				clusCenter.tupleVector[j] = clusCenter.tupleVector[j] + this.members.get(j).getTupleVector()[j];
			}
		}
		
		for(int i = 0; i<this.members.get(0).getTupleVector().length;i++){
			clusCenter.tupleVector[i] = clusCenter.tupleVector[i] / this.members.size();
			clusCenter.tupleVector[i] = Math.round(clusCenter.tupleVector[i]*100.0)/100.0;//round it to 2 digits in decimal
		}
		
		this.setCenter(clusCenter);
	}
	
	public void addMembers(Instance member) {
		this.members.add(member);
	}
	
	public ArrayList<Instance> getMembers() {
		return members;
	}

	public void setMembers(ArrayList<Instance> members) {
		this.members = members;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;		
	}

	public void updateLabel() {
		int maxCluster = 0;
		int index = -1;
		
		for(int i = 0; i< this.members.size(); i++){
			switch(this.members.get(i).label){
				case "unacc":
					this.clusterClass[0]++;
					break;
				case "acc":
					this.clusterClass[1]++;
					break;
				case "good":
					this.clusterClass[2]++;
					break;
				case "vgood":
					this.clusterClass[3]++;
					break;
			}
		}
		
		for(int i = 0; i<4; i++){
			if(maxCluster<this.clusterClass[i]){
				maxCluster = clusterClass[i];
				index = i;
			}	
		}
		
		//Set Cluster Label
		switch(index){
		case 0:
			this.setLabel("unacc");
			break;
		case 1:
			this.setLabel("acc");
			break;
		case 2:
			this.setLabel("good");
			break;
		case 3:
			this.setLabel("vgood");
			break;
		}
	}

	public Instance getCenter() {
		return center;
	}

	public void setCenter(Instance center) {
		this.center = center;
	}

	public void removeMembers() {
		// TODO Auto-generated method stub
		this.members.removeAll(getMembers());		
	}
	
	//Print the Misclassification error of each cluster
	public void printClusterError(){
		String label = this.getLabel();
		int error=0;
		switch (label) {
		case "unacc":
			error = (this.getMembers().size() - this.getClusterClass()[0]);
			System.out.println("Error: "+error);
			break;
		case "acc":
			error = (this.getMembers().size() - this.getClusterClass()[1]);
			System.out.println("Error: "+error);
			break;
		case "good":
			error = (this.getMembers().size() - this.getClusterClass()[2]);	
			System.out.println("Error: "+error);
			break;
		case "vgood":
			error = (this.getMembers().size() - this.getClusterClass()[1]);	
			System.out.println("Error: "+error);
			break;
		}
	}

}
