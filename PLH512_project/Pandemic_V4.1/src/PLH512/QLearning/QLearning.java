package PLH512.QLearning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

import PLH512.server.Board;

public class QLearning {
	
//	private ArrayList<SARS> samples;
	private SARSCollection sarsc;
	private double [][] Q;
	private double lr;
	private double gamma;
	
	public QLearning(Board board) {
		this.sarsc=new SARSCollection(board);
//		this.samples = sarsc.generateSamples();
		this.Q = new double[4][4];
		for (int i = 0; i < this.Q.length; i++) { for (int j = 0; j < this.Q[i].length; j++) { this.Q[i][j] = 0; } }
		this.lr = 0.9;
		this.gamma=0.9;
	}
	
	public int fromStateToIndex(String state) {
		if(state.contains("0"))
				return 0;
		else if(state.contains("1"))
				return 1;
		else if(state.contains("2"))
				return 2;
		else 
			return 3;
	}
	
	public int fromActionToIndex(String action) {
		if(action.contains("0"))
				return 0;
		else if(action.contains("1"))
				return 1;
		else if(action.contains("2"))
				return 2;
		else 
			return 3;
	}
	
	public double max(double[][] q, int lineInd) {
		int cols = q[lineInd].length;
		double curMax = Double.NEGATIVE_INFINITY;
		for(int c=0;c<cols;c++){
			if(curMax<q[lineInd][c])
				curMax = q[lineInd][c];
		}
		return curMax;
	}
	
	public void train(int iterations, int simulationDepth) {
		
		Board initialBoard  = copyBoard(this.sarsc.getBoard());
		ArrayList<SARS> samples = null;
		for(int i=0;i<iterations;i++) {
			for(int d=0;d<simulationDepth;d++) {
				System.out.println(i+"."+d);
				samples=this.sarsc.generateSamples();
				int rnd  = new Random().nextInt(samples.size());
				SARS sample = samples.get(rnd);
				int stateInd = fromStateToIndex(sample.getcState());
				int actionInd = fromStateToIndex(sample.getcState());
				this.Q[stateInd][actionInd] = this.Q[stateInd][actionInd]+this.lr*(sample.getReward()+this.gamma*max(this.Q, stateInd)-this.Q[stateInd][actionInd]);
				this.sarsc.setBoard(sample.getGs().getBoard());
			}
			
			this.sarsc.setBoard(initialBoard);
		}
			
		
	}

	public int[] findPolicy() {
		int[] policy = new int[4]; 
		for (int i = 0; i < this.Q.length; i++) { 
			double maxP = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < this.Q[i].length; j++)
			{ 
				if(maxP<this.Q[i][j]) {
					maxP=this.Q[i][j];
					policy[i]=j;
				}
			}
			
			
		}
		return policy;
	}
	
	public static Board copyBoard (Board boardToCopy)
	{
		Board copyOfBoard;
		
		try {
		     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		     ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
		     outputStrm.writeObject(boardToCopy);
		     ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		     ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
		     copyOfBoard = (Board)objInputStream.readObject();
		     return copyOfBoard;
		   }
		   catch (Exception e) {
		     e.printStackTrace();
		     return null;
		   }
	}
	
	
	
	
	
}
