package PLH512.QLearning;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import PLH512.server.Board;
import PLH512.server.City;

public class SARSCollection {
	Board board;
	ArrayList<SARS> collection;
	
	public SARSCollection(Board board) {
		this.board = board;
		}
	

	

	
	
	public ArrayList<SARS> generateSamples() {
		
		
		PrintStream printStreamOriginal=System.out;
		System.setOut(new PrintStream(new OutputStream(){
				public void write(int b) {
				}
			}));
		ArrayList<SARS> moves = new ArrayList<SARS>();
		Board testBoard = copyBoard(this.board);
		int whoIsPlaying = board.getWhoIsPlaying();
		
		ArrayList<String> hand = board.getHandOf(whoIsPlaying);
		String curCityName = board.getPawnsLocations(whoIsPlaying);
		City curCity = board.searchForCity(curCityName);
		
		int neighbourNum = curCity.getNeighboursNumber();
		
		String[] neighbours = new String[neighbourNum];
		
		// Drive - Ferry Action
		for (int i = 0; i< neighbourNum; i++)
		{
			neighbours[i] = curCity.getNeighbour(i);
			
			testBoard.driveTo(whoIsPlaying, neighbours[i]);
			
			City dst = testBoard.searchForCity(neighbours[i]);
			GameState gs = new GameState(testBoard, toTextDriveTo(whoIsPlaying, neighbours[i]), curCity,dst);
			moves.add(new SARS(gs));
			testBoard = copyBoard(board);
			
		}
		
		// Direct Flight Action
		int numofCards = hand.size();
		for (int i = 0; i< numofCards; i++)
		{
			if (curCityName != hand.get(i))
			{
			testBoard.directFlight(whoIsPlaying, hand.get(i));
			
			City dst = testBoard.searchForCity(hand.get(i));
			GameState gs = new GameState(testBoard, toTextDirectFlight(whoIsPlaying, hand.get(i)), curCity,dst);
			moves.add(new SARS(gs));
			testBoard = copyBoard(board);
			}
			
		}
		
		// Charter Flight Action
		if (hand.contains(curCityName))
		{
			for (int i = 0; i< this.board.getCitiesCount(); i++)
			{
				if (curCityName.contains(this.board.searchForCity(i).getName()))
				{
					testBoard.charterFlight(whoIsPlaying, this.board.searchForCity(i).getName());
					City dst = testBoard.searchForCity(this.board.searchForCity(i).getName());
					String a =  toTextCharterFlight(whoIsPlaying, this.board.searchForCity(i).getName());
					GameState gs = new GameState(testBoard, a, curCity,dst);
					moves.add(new SARS(gs));
					testBoard = copyBoard(board);
					
				}
			}
		}
		
		// Shuttle Flight Action
		ArrayList<String> researchLocations = board.getRSLocations();
		if (curCity.getHasReseachStation() == true)
		{
			
			for (int i =0; i< researchLocations.size(); i++)
			{
				if (curCityName != researchLocations.get(i))
				{
					testBoard.shuttleFlight(whoIsPlaying,  researchLocations.get(i));
					
					City dst = testBoard.searchForCity( researchLocations.get(i));
					String a =  toTextShuttleFlight(whoIsPlaying,  researchLocations.get(i));
					GameState gs = new GameState(testBoard, a, curCity,dst);
					moves.add(new SARS(gs));
					testBoard = copyBoard(board);
					
				}
			}
		}
		
		// Build an RS
		if (curCity.getHasReseachStation() == false && hand.contains(curCityName) == true && board.getRSLocations().size() < board.getResearchStationsLimit())
		{	
			testBoard.buildRS(whoIsPlaying, curCityName);
			
			String a =  toTextBuildRS(whoIsPlaying, curCityName);
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
			
		}
		else if (curCity.getHasReseachStation() == false &&  board.getRoleOf(whoIsPlaying) ==  "Operations Expert" && board.getRSLocations().size() < board.getResearchStationsLimit())
		{
			testBoard.buildRS(whoIsPlaying, curCityName);
			String a =  toTextBuildRS(whoIsPlaying, curCityName);
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
		}
		else if (curCity.getHasReseachStation() == false &&  board.getRoleOf(whoIsPlaying) ==  "Operations Expert" && board.getRSLocations().size() == board.getResearchStationsLimit())
		{
			for (int j = 0; j < board.getRSLocations().size(); j++)
			{
				testBoard.removeRS(whoIsPlaying, board.getRSLocations().get(j));
				testBoard.buildRS(whoIsPlaying, curCityName);
				String a =  toTextBuildRS(whoIsPlaying, curCityName);
				GameState gs = new GameState(testBoard, a, curCity,curCity);
				moves.add(new SARS(gs));
				testBoard =copyBoard(board);
			}
		}
		// Treat Disease
		if (curCity.getBlackCubes() > 0)
		{
			testBoard.treatDisease(whoIsPlaying, curCityName, "Black");
			String a =  toTextTreatDisease(whoIsPlaying, curCityName, "Black");
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
			
		}
		
		if ( curCity.getBlueCubes() > 0)
		{
			testBoard.treatDisease(whoIsPlaying, curCityName, "Blue");
			String a =  toTextTreatDisease(whoIsPlaying, curCityName, "Blue");
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
			
		}
		
		if (curCity.getYellowCubes() > 0 )
		{
			testBoard.treatDisease(whoIsPlaying, curCityName, "Yellow");
			String a =  toTextTreatDisease(whoIsPlaying, curCityName, "Yellow");
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
			
		}
		
		if (curCity.getRedCubes() > 0 )
		{
			testBoard.treatDisease(whoIsPlaying, curCityName, "Red");
			String a =  toTextTreatDisease(whoIsPlaying, curCityName, "Red");
			GameState gs = new GameState(testBoard, a, curCity,curCity);
			moves.add(new SARS(gs));
			testBoard =copyBoard(board);
		}
		
		
		int[] myColorCount = {0, 0, 0, 0};
		
		for (int i = 0 ; i < 4 ; i++)
    		myColorCount[i] =  cardsCounterOfColor(board, whoIsPlaying, board.getColors(i));
		
		// Research Station OK
		for (int i = 0 ; i < 4 ; i++)
		{
			if(myColorCount[i] == 4 && curCity.getHasReseachStation()) 
			{
				testBoard.cureDisease(i, curCityName);
				String a =  toTextCureDisease(i, curCityName);
				GameState gs = new GameState(testBoard, a, curCity,curCity);
				moves.add(new SARS(gs));
				testBoard =copyBoard(board);
			}
			else if (myColorCount[i] == 3 && curCity.getHasReseachStation() && board.getRoleOf(whoIsPlaying) == "Scientist")
			{
				testBoard.cureDisease(i, curCityName);
				String a =  toTextCureDisease(i, curCityName);
				GameState gs = new GameState(testBoard, a, curCity,curCity);
				moves.add(new SARS(gs));
				testBoard =copyBoard(board);
			}
		}
		
		if(board.getRoleOf(whoIsPlaying) == "Operations Expert")
		{
			for (int i = 0; i< this.board.getCitiesCount(); i++)
			{
				for (int j = 0; j < hand.size(); j++)
				{
					if (curCityName != this.board.searchForCity(i).getName()) 
					{
						testBoard.operationsExpertTravel(whoIsPlaying, this.board.searchForCity(i).getName(), hand.get(j));
						City dst = testBoard.searchForCity( this.board.searchForCity(i).getName());
						String a =   toTextOpExpTravel(whoIsPlaying, this.board.searchForCity(i).getName(), hand.get(j));
						GameState gs = new GameState(testBoard, a, curCity,dst);
						moves.add(new SARS(gs));
						testBoard =copyBoard(board);
						
					}
				}
			}
		}
		System.setOut(printStreamOriginal);
		Collections.shuffle(moves);
		return moves;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public static int cardsCounterOfColor(Board board, int  playerID, String color)
    {
    	int cardsCounter = 0;
    	
    	for (int i = 0 ; i < board.getHandOf(playerID).size() ; i++)
    		if (board.searchForCity(board.getHandOf(playerID).get(i)).getColour().equals(color))
    			cardsCounter++;
    	
    	return cardsCounter;
    }


	
	public Board copyBoard (Board boardToCopy) {
		return PLH512.client.Agent.copyBoard(boardToCopy);
	}
	public static String toTextDriveTo(int playerID, String destination)
    {
    	return "#DT,"+playerID+","+destination;
    }
    	
    public static String toTextDirectFlight(int playerID, String destination)
    {
    	return "#DF,"+playerID+","+destination;
    }
    
    public static String toTextCharterFlight(int playerID, String destination)
    {
    	return "#CF,"+playerID+","+destination;
    }
    
    public static String toTextShuttleFlight(int playerID, String destination)
    {
    	return "#SF,"+playerID+","+destination;
    }
    
    public static String toTextBuildRS(int playerID, String destination)
    {
    	return "#BRS,"+playerID+","+destination;
    }
    
    public static String toTextRemoveRS(int playerID, String destination)
    {
    	return "#RRS,"+playerID+","+destination;
    }
    
    public static String toTextTreatDisease(int playerID, String destination, String color)
    {
    	return "#TD,"+playerID+","+destination+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color)
    {
    	return "#CD1,"+playerID+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color, String card1, String card2, String card3, String card4)
    {
    	return "#CD2,"+playerID+","+color+","+card1+","+card2+","+card3+","+card4;
    }
    
    
    public static String toTextActionPass(int playerID)
    {
    	return "#AP,"+playerID;
    }
    
    public static String toTextChatMessage(int playerID, String messageToSend)
    {
    	return "#C,"+playerID+","+messageToSend;
    }
    
    public static String toTextPlayGG(int playerID, String cityToBuild)
    {
    	return "#PGG,"+playerID+","+cityToBuild;
    }
    
    public static String toTextPlayQN(int playerID)
    {
    	return "#PQN,"+playerID;
    }
    public static String toTextPlayA(int playerID, int playerToMove, String cityToMoveTo)
    {
    	return "#PA,"+playerID+","+playerToMove+","+cityToMoveTo;
    }
    public static String toTextPlayF(int playerID)
    {
    	return "#PF,"+playerID;
    }
    public static String toTextPlayRP(int playerID, String cityCardToRemove)
    {
    	return "#PRP,"+playerID+","+cityCardToRemove;
    }
    public static String toTextOpExpTravel(int playerID, String destination, String colorToThrow)
    {
    	return "#OET,"+playerID+","+destination+","+colorToThrow;
    }
    
	public ArrayList<SARS> getCollection() {
		return collection;
	}



	public Board getBoard() {
		return board;
	}



	public void setBoard(Board board) {
		this.board = board;
	}



	public void setCollection(ArrayList<SARS> collection) {
		this.collection = collection;
	}

	
}
