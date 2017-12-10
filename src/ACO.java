import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ACO
{
	static Random random;
	
	public ACO ()
	{
		random = new Random ();
	}
	
	public void acoAlg (int antCount, int iterations, double length, double alpha, double slope, double vMax, List<List<Double>> data) {
		List<ACOData> dataList = new ArrayList<ACOData> ();
		List<Ant> ants = new ArrayList<Ant> ();
		List<Integer> dataToPick = new ArrayList<Integer> ();
		int size = data.get (0).size ();
		for (int i = 0; i < data.size (); ++i) {
			ACOData newData = new ACOData ();
			newData.data = data.get (i);
			newData.x = randomInRange (-1 * size, size);
			newData.y = randomInRange (-1 * size, size);
			newData.pickedUp = false;
			newData.id = i + 1;
			newData.cluster = -1;
			dataList.add (newData);
			dataToPick.add (i);
		}
		for (int i = 0; i < antCount; ++i) {
			Ant ant = new Ant ();
			if (dataToPick.size () > 0) {
				int pick = (int) (Math.random () * dataToPick.size ());
				dataToPick.remove (new Integer (pick));
				
				ACOData pickedData = dataList.get (pick);
				ant.pickUp (pickedData);
			}
			else
				ant.loaded = false;
			ant.velocity = randomInRange (1, vMax);
			ant.maxDistance = Math.sqrt (Math.pow (size, 2) + Math.pow (size, 2));
			ant.id = i + 1;
			ants.add (ant);
		}
		for (int i = 0; i < iterations; ++i) {
			for (int j = 0; j < ants.size (); ++j) {
				Ant ant = ants.get (j);
				for (int k = 0; k < dataList.size (); ++k) {
					ACOData found = dataList.get (k);
					found.similarity = averageSimilarity (found, dataList, alpha, ant.velocity, vMax, length);
				}
				if (ant.loaded) {
					double drop = probDrop (ant.load.similarity, slope);
					double probability = randomInRange (0, 1);
					if (drop > probability) {
						ant.drop ();
						boolean picked = false;
						while (!picked) {
							int pick = (int) (Math.random () * dataList.size ());
							if (!dataList.get (pick).pickedUp) {
								ant.considerPickUp (dataList.get (pick));
								picked = true;
							}
						}
					}
					else {
						ant.MoveAnt ();
					}
				}
				else {
					double pickUp = probPickUp (ant.load.similarity, slope);
					double probability = randomInRange (0, 1);
					if (pickUp > probability && !ant.load.pickedUp) {
						ant.pickUp (ant.load);
						ant.MoveAnt ();
					}
					else {
						boolean picked = false;
						while (!picked) {
							int pick = (int) (Math.random () * dataList.size ());
							if (!dataList.get (pick).pickedUp) {
								ant.considerPickUp (dataList.get (pick));
								picked = true;
							}
						}
					}
				}
			}
		}
		int cluster = 0;
		int isolated = -2;
		boolean clusterFound = false;
		for (int i = 0; i < dataList.size (); ++i){
			clusterFound = false;
			ACOData current = dataList.get (i);
			if (current.cluster == -1) {				
				for (int j = 0; j < dataList.size (); ++j) {
					ACOData neighbor = dataList.get (j);
					if (neighbor.id != current.id && neighbor.cluster == -1 && distance (current, neighbor) < (length / 2)) {
						if (!clusterFound)
						{
							clusterFound = true;
							++cluster;
							current.cluster = cluster;
						}
						neighbor.cluster = cluster;
					}
				}
				if (!clusterFound)
				{
					current.cluster = isolated;
					--isolated;
				}
			}
		}
		for (int i = 0; i < dataList.size (); ++i) {
			System.out.println (dataList.get (i) + "\n");
		}
		
	}	
	
	private double averageSimilarity (ACOData found, List<ACOData> dataList, double alpha, double antVelocity, double vMax, double size) {
		double sum = 0;
		for (int i = 0; i < dataList.size (); ++i) {
			ACOData neighbor = dataList.get (i);
			if (neighbor.id != found.id && distance (found, neighbor) < (size / 2)) {
				double attDist = attributeDistance (found, neighbor);
				double quotient = 1 - (attDist / (alpha * (1 + (antVelocity - 1) / vMax)));
				sum += quotient;
			}
		}
		double total = (1 / Math.pow (size, 2)) * sum;
		return Math.max (0, total);
	}
	
	private double distance (ACOData first, ACOData second) {
		return Math.sqrt (Math.pow ((second.x - first.x), 2) + Math.pow ((second.y - first.y), 2));  
	}
	
	private double attributeDistance (ACOData first, ACOData second) {
		double sum = 0;
		for (int i = 0; i < first.data.size (); ++i) {
			double difference = first.data.get (i) - second.data.get (i);
			difference *= difference;
			sum += difference;
		}
		double distance = Math.sqrt (sum);
		return distance;
	}
	
	private double probPickUp (double similarity, double c) {
		return (1 - Sigmoid (similarity, c));
	}
	
	private double probDrop (double similarity, double c) {
		return (Sigmoid (similarity, c));
	}
	
	private double Sigmoid (double x, double c) {
		double e = Math.exp (-1 * c * x);
		return (1 - e) / (1 + e);
	}
	
	/**
	* randomInRange
	* $param min and max - doubles that define the range of the desired
	* 	random number
	* $return random number in the given range
	*/
    private static double randomInRange(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;
        return shifted; // == (rand.nextDouble() * (max-min)) + min;
    }
	
	public class ACOData {
		public List<Double> data;
		public double x;
		public double y;
		public boolean pickedUp;
		public int id;
		public double similarity;
		public int cluster;
		
		public String toString () {
			String output = "Data: \n";
			for (int i = 0; i < data.size (); ++i) {
				output += data.get (i) + " ";
			}
			output += "\npos: " + x + " " + y + " picked up? " + pickedUp + " similarity " + similarity + " cluster " + cluster;
			return output;
		}
	}
	
	public class Ant {
		public ACOData load;
		public boolean loaded;
		public Vector2D pos;
		public double velocity;
		private Random random;
		public double maxDistance;
		public int id;
		
		public Ant () {
			random = new Random ();
		}
		
		public void considerPickUp (ACOData data) {
			load = data;
			pos = new Vector2D (data.x, data.y);
		}
		
		public void pickUp (ACOData data) {
			data.pickedUp = true;
			
			load = data;
			pos = new Vector2D (data.x, data.y);
			loaded = true;
		}
		
		public void drop () {
			load.pickedUp = false;
			loaded = false;
			load.x = pos.x;
			load.y = pos.y;
			
			load = null;
		}
		
		public void MoveAnt () {
			if (distanceFromOrigin () < maxDistance) {
				double theta = randomInRange (0, 2 * Math.PI);
				Vector2D newDirection = new Vector2D (Math.cos (theta), Math.sin (theta));
				newDirection.x *= velocity;
				newDirection.y *= velocity;
				pos.x += newDirection.x;
				pos.y += newDirection.y;
			}
			else {
				Vector2D newDirection = new Vector2D (-1 * pos.x, -1 * pos.y);
				double mag = newDirection.Magnitude ();
				newDirection.x = newDirection.x / mag * velocity;
				newDirection.y = newDirection.y / mag * velocity;
				pos.x += newDirection.x;
				pos.y += newDirection.y;
			}
			load.x = pos.x;
			load.y = pos.y;
		}
		
		private double distanceFromOrigin () {
			return Math.sqrt (Math.pow ((0 - pos.x), 2) + Math.pow ((0 - pos.y), 2));  
		}
		
		/**
		* randomInRange
		* $param min and max - doubles that define the range of the desired
		* 	random number
		* $return random number in the given range
		*/
		private double randomInRange(double min, double max) {
			double range = max - min;
			double scaled = random.nextDouble() * range;
			double shifted = scaled + min;
			return shifted; // == (rand.nextDouble() * (max-min)) + min;
		}
		
		public String toString () {
			String output = "Ant: \n";
			output += load + "\n";
			output += "Loaded: " + loaded + "\n";
			output += "Pos: " + pos.x + " " + pos.y + " Velocity: " + velocity;
			
			return output;
		}
	}
	
	public class Vector2D {
		public double x;
		public double y;
		
		public Vector2D (double _x, double _y) {
			x = _x;
			y = _y;
		}
		
		public double Magnitude () {
			return Math.sqrt (x * x + y * y);
		}
	}
}