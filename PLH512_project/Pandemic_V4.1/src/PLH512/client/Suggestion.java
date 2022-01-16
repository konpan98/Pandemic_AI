package PLH512.client;

import java.util.ArrayList;

public class Suggestion {
	private String action;
	private ArrayList<Double> eval;
	int size;
	private int points;
	
	
	public Suggestion(String action, Double eval) {
		super();
		this.action = action;
		this.eval = new ArrayList<Double>();
		this.eval.add(eval);
		this.points = 0;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public ArrayList<Double> getEval() {
		return eval;
	}
	public void setEval(ArrayList<Double> eval) {
		this.eval = eval;
	}
	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	
	
}
