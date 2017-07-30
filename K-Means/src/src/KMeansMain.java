package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeansMain {
	Metadata metadata;//Class values and attributes and their corresponding domain of values.
	ArrayList<Instance> data;// All the instance needed to be clustered
	List<Cluster> clusterSystem;//List of K clusters based on K values provided by user or 4 by default.
	int k = 4;//No of clusters. Default = 4
	
	public static void main(String[] args){
		KMeansMain app = new KMeansMain();
		
		//Get the metadata from the car metadata file
		app.metadata = new Metadata("\\data\\car.metadata");		
		app.data = new ArrayList<Instance>();
		//get the instance/tuples of car data from file 
		app.fetchData(app.data, app.metadata,"\\data\\car.data");
		app.clusterSystem = new ArrayList<Cluster>();
		InputStreamReader r=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(r);
		boolean valid = false;
		
		
		while(!valid)
		{
			System.out.println("Please provide number of clusters");
			try{
				String choice=br.readLine();
				if(!choice.equalsIgnoreCase(""))
					{
						app.k = Integer.parseInt(choice);
						valid=true;
					}
				else
					{
						System.out.println("Invalid input provided. Please provide valid input");
					}
				}
			catch (Exception e)
			{
			System.out.println("Invalid input provided. Please provide valid input");
			}
		}
		/*try{
		//Read clusters from command line or run with default 4 clusters
			if(args == null)
				app.k = 4;//Integer.parseInt(args[1]);//No of clusters
			else
				app.k = Integer.parseInt(args[0]);
		}catch (ArrayIndexOutOfBoundsException a) {
			// TODO: handle exception
		}*/
		//randomly initialize cluster centers
		app.initCluster(app.k, app.data);
		//Run the K mean algo. on the data 
		app.KMean(app.k);
		//Print the misclassification error of each cluster
		app.printClusterDetail();
	}

	private void KMean(int k) {
		// TODO Auto-generated method stub
		int iterationCount = 0;
		double[] distance = new double[k];
		double minDist = 0;
		int clusterNo = -1;
		boolean stopClustering = false;
		Instance oldCenters[] = new Instance[k];
		double oldSSE[] = new double[k]; 
		double systemSSE = 0.0,
			   previousSSE = -1.0;
		
		do{
		//Clear the members before running clustering
		for(int i = 0; i<k; i++){
			this.clusterSystem.get(i).removeMembers();
			this.clusterSystem.get(i).setSSE(0.0);
		}
			
		//Distribute the points to clusters. The distance between each instance and cluster centers 
		//are calculated and the instance goes into the cluster having min distance with the cluster.
		for(int i = 0; i<this.data.size();i++){
			clusterNo = -1;
			//Find distance between the cluster centers and points
			for(int j = 0; j<k;j++){
				distance[j] = euclideanDistance(this.clusterSystem.get(j).getCenter(),this.data.get(i));
				//Find the cluster center which is closest to the point.
				if(j == 0){
					minDist = distance[j];
					clusterNo = j;
				}
				else if(distance[j]<minDist){
					minDist = distance[j];
					clusterNo = j;//store the cluster no to which the instance is going.
					}
				}
			this.clusterSystem.get(clusterNo).addMembers(this.data.get(i));//add instance into cluster
			this.clusterSystem.get(clusterNo).updateSSE(distance[clusterNo] * distance[clusterNo]);// Increment the SSE of the cluster
			}
		
		systemSSE = 0.0;//reset the SSE of the cluster system.
		//Calculate New cluster centers
		for(int i = 0; i<k;i++){
			oldSSE[i] = this.clusterSystem.get(i).getSSE();//Store the previous SSEs of individual clusters
			oldCenters[i] = this.clusterSystem.get(i).getCenter();//Store the old cluster centers
			this.clusterSystem.get(i).updateCenter(k);//based on the clusters, update the cluster center.
			System.out.println("Cluster"+Integer.toString(i)+": "+this.clusterSystem.get(i).getMembers().size()+" SSE/Cluster:"+this.clusterSystem.get(i).getSSE());
			systemSSE += this.clusterSystem.get(i).getSSE();//Calculate the Cluster System's SSE by adding all the SSE of all clusters. 
		}
		System.out.println("Total SSE: "+systemSSE);
		
		//if the centers are not moving stop the clustering process.
		stopClustering = this.checkStopCondition(this.clusterSystem,oldCenters);
		//After a 20 iteration, if the cluster center is not converging, choose the min SSE value to stop further K-Mean iteration.
		if(!stopClustering && previousSSE != -1.0 && previousSSE<=systemSSE && iterationCount>=20){
			stopClustering = true;
		}
		previousSSE = systemSSE;
		iterationCount++; // Keep the count of the no of iterations that have completed.
		System.out.println("------");
		} while(!stopClustering);
		
		//Set Label of all the clusters
		for(int i=0; i<this.k;i++){
			this.clusterSystem.get(i).updateLabel();
		}
	}

	//Check if center of clusters are moving or not
	private boolean checkStopCondition(List<Cluster> clusterSystem, Instance[] oldCenters) {
		// TODO Auto-generated method stub
		double dist = 0.0;
		for(int i = 0; i<clusterSystem.size();i++){
			dist = this.euclideanDistance(clusterSystem.get(i).getCenter(), oldCenters[1]);
			if(dist != 0.0){
				//no need check further clusters, return false
				return false;
			}
			dist = 0.0;//re-set it to 0
		}
		return true;
	}
	
	//Find distance of instance from given cluster centers
	private double euclideanDistance(Instance center, Instance instance) {
		// TODO Auto-generated method stub
		double dist = 0,
			   tempDist = 0;
		for(int i = 0; i<center.getTupleVector().length;i++){
			tempDist = center.getTupleVector()[i] - instance.getTupleVector()[i];
			dist += tempDist*tempDist;
		}
		if(dist!=0.0){
			dist = Math.round(Math.sqrt(dist)*100.0)/100.0;
		}
		return dist;
	}
	
	//Randomly initialize the cluster centers
	private void initCluster(int k, ArrayList<Instance> data) {
		// Random initialization of K centers
		int[] center = new Random().ints(0, data.size()).distinct().limit(k).toArray();
		for(int i = 0; i < center.length;i++){
			Cluster newC = new Cluster();
			newC.setCenter(data.get(center[i]));
//			System.out.println(center[i]);
			this.clusterSystem.add(newC);
		}
	}

	/*
	 * Fetch the data from the files and add it into the instances object
	 * 
	 */
	public void fetchData(List<Instance> data, Metadata meta, String pathData){
		String filePath = new File("").getAbsolutePath();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath + pathData));
			String line;
			while ((line = br.readLine()) != null) {
				Instance ins = new Instance();
				ins.setInstance(line, meta);
				data.add(ins);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	//Print the cluster details
	private void printClusterDetail() {
		String cName = "Cluster";
		// TODO Auto-generated method stub
		for(int i =0; i<this.k;i++){
			System.out.println(cName+ String.valueOf(i+1)+" Size:"+this.clusterSystem.get(i).getMembers().size()+" Label:"+this.clusterSystem.get(i).getLabel());
			this.clusterSystem.get(i).printClusterError();
		}
		
	}
}

