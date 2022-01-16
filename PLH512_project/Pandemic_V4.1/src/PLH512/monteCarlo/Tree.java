package PLH512.monteCarlo;

import java.util.ArrayList;

import PLH512.server.Board;

public class Tree {
	Node root;

	
	public Tree(Board board) {
		this.root =  new Node(board,null);
		addChildren(root);
		root.setNumOfVisits(1);
		
	}
	
	public Tree(Node node) {
		this.root = node;
		this.root.setParent(null);
	}
	// finds and adds the children of a Node
	 public void addChildren(Node pNode) {
		 Board cb = pNode.getState().getBoard();
		 ArrayList<State> children =  pNode.getState().possibleStates(cb); 
		 for(State child: children) {
//			  System.out.println("Here");
			  boolean isLegalMove = true;
			  Node childNode =new Node(child,pNode);
			 // Checks if OpExp move is legal
			  while(child.getAction().contains("#OET") &&  childNode!=null) {
				  Node parent  = childNode.getParent();
				  if(parent.getState().getAction().contains("#OET")) {
					  childNode=null;
				  }
				  childNode=parent;
				  
			  }
			  if(isLegalMove) {
				  childNode =  new Node(child, pNode);
				  pNode.addChild(childNode);
			  
			  }
			  
			  
			  
			  
		  }
		 
	 }
	
	public void updateParents(Node node, double stateValue) {
		node.setNumOfVisits(node.getNumOfVisits()+1);
		node.setValue(node.getValue()+stateValue);
		while(node.getParent()!=null) {
			Node parent = node.getParent();
			parent.setNumOfVisits(parent.getNumOfVisits()+1);
			parent.setValue(parent.getValue()+stateValue);
			node=parent;
				
		}
	}
	
	public Node getRoot() {
		return root;
	}
	public void setRoot(Node n) {
		this.root=n;
		this.root.setParent(null);

	}
	
	
}
