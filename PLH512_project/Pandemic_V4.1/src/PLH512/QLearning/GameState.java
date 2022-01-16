package PLH512.QLearning;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import PLH512.client.citiesWithDistancesObj;
import PLH512.monteCarlo.State;
import PLH512.server.Board;
import PLH512.server.City;

public class GameState {

	private	Board board;
	private String action;
	private City cityFrom;
	private City cityTo;
	
		
		public GameState(Board board, String action, City cityFrom, City cityTo) {
		super();
		this.board = board;
		this.action = action;
		this.cityFrom = cityFrom;
		this.cityTo = cityTo;
	}
		
		public boolean isTerminal() {
			if(this.cityTo==null)
					return true;
			return false;
		}
		

		
		
	    public Board getBoard() {
			return board;
		}
		public void setBoard(Board board) {
			this.board = board;
		}
		public City getCityFrom() {
			return cityFrom;
		}
		public void setCityFrom(City cityFrom) {
			this.cityFrom = cityFrom;
		}
		public City getCityTo() {
			return cityTo;
		}
		public void setCityTo(City cityTo) {
			this.cityTo = cityTo;
		}
		


		public String getAction() {
			return action;
		}


		public void setAction(String action) {
			this.action = action;
		}

	
	
}
