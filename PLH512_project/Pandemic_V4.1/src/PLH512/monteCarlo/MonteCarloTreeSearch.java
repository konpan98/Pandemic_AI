package PLH512.monteCarlo;

import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import PLH512.server.Board;

public class MonteCarloTreeSearch {
	
	private Tree tree;
	private int iterations;
	private int depth;
	
	public MonteCarloTreeSearch(Board board, int iterations, int depth ) {
		this.tree = new Tree(board);
		
		this.iterations = iterations;
		this.depth  = depth;
	}
	
	
	public void train() {
		for(int iter=0;iter<this.iterations;iter++) {
			
		
			System.out.println("iteration: "+iter);
			traverseAndExpand(tree.getRoot());
			
		}
	}
	// predicts the best action  
public ArrayList<Node> predict(int seqLength){
		
		ArrayList<Node> actions = new ArrayList<Node>();
		Node node = tree.getRoot();
		Node bestNode = tree.getRoot();
		for(int d=0;d<seqLength;d++) {
			double maxVal = Double.NEGATIVE_INFINITY;
			for(Node child:node.getChildren()) {
				double childVal = uct(child,0);
				if(childVal >=maxVal) {
					maxVal = childVal;
					bestNode=child;
				}
			}
			node=bestNode;
			actions.add(node);
			System.out.println("action: "+node.getState().getAction() );
		}
		return actions;
	}
	
	
	public void  rollout(Node node){
		PrintStream printStreamOriginal=System.out;
		System.setOut(new PrintStream(new OutputStream(){
				public void write(int b) {
				}
			}));
		
		State state = node.getState();
		if (state.isTerminal()) {
			tree.updateParents(node, state.evalState(state.getBoard()));
			return;
		}
		if (depth==-1){
			while(!state.isTerminal() ) {
				if(state.isTerminal())
					break;
				String act = state.chooseRandomAction();
				Board b =  PLH512.client.Agent.copyBoard(state.getBoard());  
				b= PLH512.server.Server.readActions(act, b);
				state = new State(b,act);
			}	
		}
			
		else {
			for(int d=0;d<depth;d++) {
				if(state.isTerminal())
					break;
				
				String act = state.chooseRandomAction();
				Board b =  PLH512.client.Agent.copyBoard(state.getBoard());  
				b= PLH512.server.Server.readActions(act, b);
				state = new State(b,act);
				
			}
		}
		
		
		tree.updateParents(node, state.evalState(state.getBoard()));
		System.setOut(printStreamOriginal);

		
	}
	
	public void traverseAndExpand(Node node) {
		
		
		
		State state = node.getState();
		
		while(!node.isLeaf()) {
//			System.out.println(!node.isLeaf());
			Node bestNode = findBestChild(node);
			node=bestNode;
			if (node.getState().isTerminal())
				break;
		}
//		System.out.println(!node.isLeaf());				
		if(node.getNumOfVisits()==0) {
			rollout(node);
		}
		else {
			tree.addChildren(node);
			assert !node.getChildren().isEmpty(): "No children";
			rollout(node.getChild(0));
			
		}
			
		
		
		
	
}
	

	public Node findBestChild(Node node) {
		Node bestNode=null;
		double minUCT = Double.NEGATIVE_INFINITY;
		
		for(Node child: node.getChildren() ) {
			double cUCT = uct(child);
//			System.out.println(cUCT+" "+child.getNumOfVisits()+" "+child.getValue());
			if(minUCT<= cUCT ) {
				minUCT= cUCT;
				bestNode= child;
//				System.out.println("Here");
			}
		}
		return bestNode;
	}
	
	
	public double  uct(Node node) {
		if (node.getNumOfVisits()==0) {
			return Double.POSITIVE_INFINITY;
		}
		return node.getValue()/node.getNumOfVisits()+2*Math.sqrt(Math.log(node.getParent().getNumOfVisits())/node.getNumOfVisits());
	}
	
	
	public double uct(Node node, double coeff) {
		if (node.getNumOfVisits()==0) {
			return Double.POSITIVE_INFINITY;
		}
		return node.getValue()/node.getNumOfVisits()+coeff*Math.sqrt(Math.log(node.getParent().getNumOfVisits())/node.getNumOfVisits());
	}
	public void setTree(Tree t) {
		this.tree =t;
	}
	public Tree getTree() {
		return tree;
	}


	public int getIterations() {
		return iterations;
	}


	public void setIterations(int iterations) {
		this.iterations = iterations;
	}


	public int getDepth() {
		return depth;
	}


	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	
	
	
}
