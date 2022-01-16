package PLH512.monteCarlo;

import java.util.*;

import PLH512.server.Board;

public class Node {
private	 State state;
private	 double numOfVisits;
private	  double value;
private	 Node parent;
private	 ArrayList<Node> children;
	 
	 
	 public Node(Board board, String action, Node parent) {
			this.state = new State(board,action);
			this.numOfVisits=0;
			this.value=0;
//			if (this.state.getAction().contains("#CD1"))
//				this.value =100;
//			else if (this.state.getAction().contains("#TD"))
//				this.value=100;
			this.parent=parent;
			this.children = new ArrayList<Node>();
		}
	 
	 public Node(Board board, Node parent) {
			this.state = new State(board);
			this.numOfVisits=0;
			this.value=0;
			this.parent=parent;
			this.children = new ArrayList<Node>();
		}
	public Node(State s,Node parent) {
		this.state=s;
		
		this.numOfVisits=0;
		this.value=0;
		this.children = new ArrayList<Node>();
	}
	 
	 
	
	 
	 
	 public boolean isLeaf() {
		 
			return this.children.isEmpty();
		 }
	 
	 
	 
	 public void addChild(Node node) {
		 node.setParent(this);
		 this.children.add(node);
	 }
	 
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public ArrayList<Node> getChildren() {
		return children;
	}
 public void setChildren(ArrayList<Node> children) {
		this.children = children;
	}
 
	public Node getChild(int i) {
		
		return this.children.get(i);
	}
	public double getValue() {
		return value;
	}
	public void setValue(double v) {
		this.value = v;
	}
	 
	public double getNumOfVisits() {
		return numOfVisits;
	}
	public void setNumOfVisits(double nov) {
		this.numOfVisits = nov;
	} 
}
