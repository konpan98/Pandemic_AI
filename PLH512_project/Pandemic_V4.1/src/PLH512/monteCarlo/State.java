  
package PLH512.monteCarlo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import javax.management.Descriptor;

import PLH512.client.citiesWithDistancesObj;
import PLH512.server.*;

public class State {
private	Board board;
private String action;
private	double stateEval;

	public State(Board board) {
		this.board = board;
		this.action = PLH512.client.Agent.toTextActionPass(this.board.getWhoIsPlaying());
	}
	public State(Board board,String action) {
		this.board = board;
		this.action = action;
//		this.stateEval = evalState(this.board);
	}
	 
	
	// all possible moves of the player
		
		public ArrayList<State> possibleStates(Board board) {
			
			final double cutOf=Double.NEGATIVE_INFINITY;
			
			PrintStream printStreamOriginal=System.out;
			System.setOut(new PrintStream(new OutputStream(){
					public void write(int b) {
					}
				}));
			ArrayList<State> moves = new ArrayList<State>();
			Board testBoard = copyBoard(board);
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
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextDriveTo(whoIsPlaying, neighbours[i])));
				testBoard = copyBoard(board);
				}
			}
			
			// Direct Flight Action
			int numofCards = hand.size();
			for (int i = 0; i< numofCards; i++)
			{
				if (curCityName != hand.get(i))
				{
				testBoard.directFlight(whoIsPlaying, hand.get(i));
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextDirectFlight(whoIsPlaying, hand.get(i))));
				testBoard = copyBoard(board);
				}
				}
			}
			
			// Charter Flight Action
			int citiesNum = board.getCitiesCount();
			ArrayList<String> citiesNames= new ArrayList<String>();
			
			for(int i =0; i <citiesNum; i++)
			{
				citiesNames.add(board.searchForCity(i).getName());
				
			}
			
			if (hand.contains(curCityName))
			{
				for (int i = 0; i< citiesNames.size(); i++)
				{
					if (curCityName != citiesNames.get(i))
					{
						testBoard.charterFlight(whoIsPlaying, citiesNames.get(i));
						if (evalState(testBoard) > cutOf )
						{
						moves.add(new State(testBoard, PLH512.client.Agent.toTextCharterFlight(whoIsPlaying, citiesNames.get(i))));
						testBoard = copyBoard(board);
						}
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
						if (evalState(testBoard) > cutOf )
						{
						moves.add(new State(testBoard, PLH512.client.Agent.toTextShuttleFlight(whoIsPlaying,  researchLocations.get(i))));
						testBoard = copyBoard(board);
						}
					}
				}
			}
			
			// Build an RS
			if (curCity.getHasReseachStation() == false && hand.contains(curCityName) == true && board.getRSLocations().size() < board.getResearchStationsLimit())
			{	
				testBoard.buildRS(whoIsPlaying, curCityName);
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextBuildRS(whoIsPlaying, curCityName)));
				testBoard =copyBoard(board);
				}
			}
			else if (curCity.getHasReseachStation() == false &&  board.getRoleOf(whoIsPlaying) ==  "Operations Expert" && board.getRSLocations().size() < board.getResearchStationsLimit())
			{
				testBoard.buildRS(whoIsPlaying, curCityName);
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextBuildRS(whoIsPlaying, curCityName)));
				testBoard = copyBoard(board);
				}
			}
			else if (curCity.getHasReseachStation() == false &&  board.getRoleOf(whoIsPlaying) ==  "Operations Expert" && board.getRSLocations().size() == board.getResearchStationsLimit())
			{
				for (int j = 0; j < board.getRSLocations().size(); j++)
				{
					testBoard.removeRS(whoIsPlaying, board.getRSLocations().get(j));
					testBoard.buildRS(whoIsPlaying, curCityName);
					if (evalState(testBoard) > cutOf )
					{
					moves.add(new State(testBoard, PLH512.client.Agent.toTextBuildRS(whoIsPlaying, curCityName)));
					testBoard = copyBoard(board);
					}
				}
			}
			// Treat Disease
			if (curCity.getBlackCubes() > 0)
			{
				testBoard.treatDisease(whoIsPlaying, curCityName, "Black");
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextTreatDisease(whoIsPlaying, curCityName, "Black")));
				testBoard =copyBoard(board);
				}
			}
			
			if ( curCity.getBlueCubes() > 0)
			{
				testBoard.treatDisease(whoIsPlaying, curCityName, "Blue");
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextTreatDisease(whoIsPlaying, curCityName,  "Blue")));
				testBoard = copyBoard(board);
				}
			}
			
			if (curCity.getYellowCubes() > 0 )
			{
				testBoard.treatDisease(whoIsPlaying, curCityName, "Yellow");
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextTreatDisease(whoIsPlaying, curCityName, "Yellow")));
				testBoard = copyBoard(board);
				}
			}
			
			if (curCity.getRedCubes() > 0 )
			{
				testBoard.treatDisease(whoIsPlaying, curCityName, "Red");
				if (evalState(testBoard) > cutOf )
				{
				moves.add(new State(testBoard, PLH512.client.Agent.toTextTreatDisease(whoIsPlaying, curCityName, "Red")));
				testBoard = copyBoard(board);
				}
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
					if (evalState(testBoard) > cutOf )
					{
					moves.add(new State(testBoard, PLH512.client.Agent.toTextCureDisease(i, curCityName)));
					testBoard =copyBoard(board);
					}
				}
				else if (myColorCount[i] == 3 && curCity.getHasReseachStation() && board.getRoleOf(whoIsPlaying) == "Scientist")
				{
					testBoard.cureDisease(i, curCityName);
					if (evalState(testBoard) > cutOf )
					{
					moves.add(new State(testBoard, PLH512.client.Agent.toTextCureDisease(i, curCityName)));
					testBoard = copyBoard(board);
					}
				}
			}
			
			if(board.getRoleOf(whoIsPlaying) == "Operations Expert")
			{
				for (int i = 0; i< citiesNames.size(); i++)
				{
					for (int j = 0; j < hand.size(); j++)
					{
						if (curCityName != citiesNames.get(i)) 
						{
							testBoard.operationsExpertTravel(whoIsPlaying, citiesNames.get(i), hand.get(j));
							if (evalState(testBoard) > cutOf )
							{
							moves.add(new State(testBoard, PLH512.client.Agent.toTextOpExpTravel(whoIsPlaying, citiesNames.get(i), hand.get(j))));
							testBoard =copyBoard(board);
							}
						}
					}
				}
			}
			System.setOut(printStreamOriginal);
			Collections.shuffle(moves);
			return moves;
		}
		
	
	public boolean isTerminal() {
		 
		return this.board.getGameEnded();
	 }
	 // Count how many card of the color X player X has
    public static int cardsCounterOfColor(Board board, int  playerID, String color)
    {
    	int cardsCounter = 0;
    	
    	for (int i = 0 ; i < board.getHandOf(playerID).size() ; i++)
    		if (board.searchForCity(board.getHandOf(playerID).get(i)).getColour().equals(color))
    			cardsCounter++;
    	
    	return cardsCounter;
    }
    

    
   
    
    
    
    public static int infectionInCity(City city) {
    	return city.getBlackCubes()+city.getBlueCubes()+city.getYellowCubes()+city.getRedCubes();
    }
    
    
    public static double roundFactor(Board board) {
    	int cardsToTake;
		
		if (board.getNumberOfPlayers() == 2)
			cardsToTake = 4;
		else if (board.getNumberOfPlayers() == 3)
			cardsToTake = 3;
		else
			cardsToTake = 2;
		
		int roundsMax = board.getPlayersDeck().size()+board.getDiscardedPile().size();
		for(int h=0;h<board.getNumberOfPlayers();h++) {
			
			roundsMax = roundsMax+board.getHandOf(h).size();
		}
		
		double remainingRounds = board.getPlayersDeck().size()/(board.getNumberOfPlayers()*cardsToTake);
		
		return remainingRounds/roundsMax;
    }
    
public static int drawFromCustomDistr(int[] numsToGenerate, double [] probSums)
{
	int rnd = new Random().nextInt(100);
	int bound = numsToGenerate.length;
	for(int i=0;i<bound;i++) {
		if(i==0 && rnd<=probSums[0]) {
			rnd=0;
			break;
		}
		else if(rnd<=probSums[i] && rnd>probSums[i-1]) {
			rnd=i;
			break;
		}
	}
	return rnd;
}

// finds and returns a random Action
public String chooseRandomAction() {
	
	PrintStream printStreamOriginal=System.out;
	System.setOut(new PrintStream(new OutputStream(){
			public void write(int b) {
			}
		}));
	int[] myColorCount = {0, 0, 0, 0};
	int whoIsPlaying = board.getWhoIsPlaying();
	Board tmpBoard = copyBoard(board);
	ArrayList<String> hand = board.getHandOf(whoIsPlaying);
	String curCityName = board.getPawnsLocations(whoIsPlaying);
	City curCity = board.searchForCity(curCityName);
	String cityColour = curCity.getColour();
	
	boolean canTreat = false, canCure=false, canDF = false, canCF=false, canSF = false,canOET =false, canBRS=false;
	
	tmpBoard =copyBoard(this.board);
	if (curCity.getMaxCube() > 0)
	{
		canTreat=true;
		
	}
	
	for (int i = 0 ; i < 4 ; i++)
		myColorCount[i] =  cardsCounterOfColor(board, whoIsPlaying, board.getColors(i));
	
	// Research Station OK
	for (int i = 0 ; i < 4 ; i++)
	{
		if((((myColorCount[i] == 3 && this.board.getRoleOf(whoIsPlaying).equals("Scientist"))) || myColorCount[i] == 4) && curCity.getHasReseachStation()) 
		{
			canCure=true;
			
		}
	}
		
	if(!hand.isEmpty()) {
		canDF = true;
		
		
		if((!curCity.getHasReseachStation()) && (tmpBoard.getRoleOf(whoIsPlaying).equals("Operations Expert"))) {
			canBRS=true;
		}
		
		
		if(hand.contains(curCityName)) {
			canCF = true;
			if(!curCity.getHasReseachStation()) {
				canBRS=true;
			}
		}
		if(curCity.getHasReseachStation()) {
			canSF = true;
			if(tmpBoard.getRoleOf(whoIsPlaying).equals("Operations Expert")) {
				canOET = true;
			}
		}
		
	}
	
	int rndActionType = new Random().nextInt(8);
	while((rndActionType==1 &&(!canDF)) || (rndActionType==2 && (!canCF)) || (rndActionType==3 &&(!canSF)) || (rndActionType==4 &&(!canOET)) || (rndActionType==5 &&(!canBRS)) || (rndActionType==6 &&(!canTreat)) || (rndActionType==7 &&(!canCure))){
		rndActionType = new Random().nextInt(8);
		
	}
	String a =null;
	
	
	
	if(rndActionType==1) {
		int nn = hand.size();
		int dInd = new Random().nextInt(nn);
		String  dst = hand.get(dInd);
		a = PLH512.client.Agent.toTextDirectFlight(whoIsPlaying, dst);
		
	}
	
	else if(rndActionType==2) {
		int nn = tmpBoard.getCitiesCount();
		int dInd = new Random().nextInt(nn);
		String  dst = tmpBoard.searchForCity(dInd).getName();
		tmpBoard.charterFlight(whoIsPlaying, dst);
		a = PLH512.client.Agent.toTextCharterFlight(whoIsPlaying, dst);
	}
	
	else if(rndActionType==3) {
		int nn = tmpBoard.getCitiesCount();
		int dInd = new Random().nextInt(nn);
		City  dst = tmpBoard.searchForCity(dInd);
		while(!dst.getHasReseachStation()) {
			dInd = new Random().nextInt(nn);
			dst = tmpBoard.searchForCity(dInd);
		}
		
		a = PLH512.client.Agent.toTextShuttleFlight(whoIsPlaying, dst.getName());
		
	}
	
	else if(rndActionType==5) {
		a = PLH512.client.Agent.toTextBuildRS(whoIsPlaying, curCityName);
		
	}
	
	else if(rndActionType==4) {
		int nn = tmpBoard.getCitiesCount();
		int handSize = hand.size();
		int tInd = new Random().nextInt(handSize);
		int dInd = new Random().nextInt(nn);
		String  dst = tmpBoard.searchForCity(dInd).getName();
		String ht = hand.get(tInd);
		a=PLH512.client.Agent.toTextOpExpTravel(whoIsPlaying, dst, ht);
		
	}
	
	else if(rndActionType==6) {
		String cityCol = curCity.getMaxCubeColor();
		a=PLH512.client.Agent.toTextTreatDisease(whoIsPlaying, curCityName, cityCol);
		
	}
	
	
	else if(rndActionType==7) {
		
		for (int i = 0 ; i < 4 ; i++)
		{	
			if((((myColorCount[i] == 3 && this.board.getRoleOf(whoIsPlaying).equals("Scientist"))) || myColorCount[i] == 4) && curCity.getHasReseachStation()) 
			{	
				a=PLH512.client.Agent.toTextCureDisease(whoIsPlaying, cityColour);
				
				
			}
		}
	}
	
	
	else {
		int nn = curCity.getNeighboursNumber();
		int dInd = new Random().nextInt(nn);
		String  dst = curCity.getNeighbour(dInd);
		a=PLH512.client.Agent.toTextDriveTo(whoIsPlaying, dst);
		
	}
	System.setOut(printStreamOriginal);
		assert a !=null:"RandomState is problematic";
	return a;
}	


private int colourToInd(String colour) {
	if(colour.equals("Black"))
		return 0;
	else if (colour.equals("Yellow"))
		return 1;
	else if (colour.equals("Blue"))
		return 2;
	else
		return 3;
	
}
    
    
public  double evalState(Board board) {
    	
		
    	double  hdcure=0, hcards=0, hdisc=0, hinf=0, hdist=0, hcures=0,totalInfection=0, hstateMax=200;
    	double reward=0;
    	double WtrS0=-100;
		double WtrS1=20;
		double WtrS2=30;
		double WtrS3=50;
		double Wbrs=150;
		double infeas = -100;
		double Wtd = 200;
		double Wcd = 300;
		double handPenalty = (7-board.getHandOf(board.getWhoIsPlaying()).size())/7;
		
		if(board.getRoleOf(board.getWhoIsPlaying()).equals("Medic")) {
			WtrS0=-100;
			WtrS1=100;
			WtrS2=20;
			WtrS3=150;
			
		}
		if(board.getRoleOf(board.getWhoIsPlaying()).equals("Scientist")) {
			WtrS0=-300;
			WtrS1=20;
			WtrS2=100;
			WtrS3=150;
			
		}
		if(board.getRoleOf(board.getWhoIsPlaying()).equals("Operations Expert")) {
			WtrS0=50;
			WtrS1=100;
			WtrS2=50;
			WtrS3=100;
			
		}
		else {
			WtrS0=-100;
			WtrS1=100;
			WtrS2=20;
			WtrS3=150;
		}
		
		
		City dst=board.searchForCity(board.getPawnsLocations(board.getWhoIsPlaying()));
		String cc = dst.getColour();
		
		boolean hasRS = false, hasCubes = false;
		hasRS = dst.getHasReseachStation();
		for( int i=0; i<4; i++) {
			if(dst.getCubes(board.getColors(i))>0)
				hasCubes=true;
				break;
		}
		
		

		
		int regionRisk = 24-board.getCubesLeft(colourToInd(cc));
		
		
		if(this.action.contains("DT") || this.action.contains("SF")|| this.action.contains("DF")||this.action.contains("CF")||this.action.contains("OET")) {
			if((!hasCubes)&&(!hasRS)) {
				reward = WtrS0-handPenalty; 
			}
			else if((hasCubes)&&(!hasRS)) {
				reward = WtrS1-handPenalty; 
			}
			else if((!hasCubes)&&(hasRS)) {
				reward = WtrS2-handPenalty; 
			}
			else{
				reward = WtrS3-handPenalty; 
			}
			
		}
		
		else if(this.action.contains("BRS")) {
			reward = Wbrs-handPenalty;
		}
		else if(this.action.contains("TD")) {
			reward = Wtd;
		}
		else if(this.action.contains("CD")){
			reward = Wcd;
		}
		else
		{
			reward=0;
		}
		
		
		

    	
    	for (int i=0;i<board.getNumberOfPlayers();i++){
    		
    		String playerLocation = board.getPawnsLocations(i);
    		ArrayList<citiesWithDistancesObj> distanceMap = new ArrayList<citiesWithDistancesObj>();
        	distanceMap = PLH512.client.Agent.buildDistanceMap(board, playerLocation, distanceMap);
        	for (int j = 0 ; j < distanceMap.size() ; j++)
        	{
        		City cityToCheck = board.searchForCity(distanceMap.get(j).getName());
        		totalInfection=totalInfection+infectionInCity(cityToCheck);
        		
        		if((i==board.getWhoIsPlaying() && board.getRSLocations().contains(cityToCheck.getName())) && (hdcure>distanceMap.get(j).getDistance())){
        			
        			hdcure = distanceMap.get(j).getDistance();	
        		}
        	}
    		
    	}
    	totalInfection=totalInfection/board.getNumberOfPlayers();
    	hinf = totalInfection; 
    	
    	
    	
    	int minCardsForCure;
    	for (int i=0;i< 4;i++){
    		int active = board.getCured(i) ? 1 : 0;
    		int discardColor = 0;
    		for(String card: board.getDiscardedPile()) {
    			
    			City cityToCheck =board.searchForCity(card);
				String colorToCheck=cityToCheck.getColour();
				if (colorToCheck.equals(board.getColors(i))) {
					
					discardColor = discardColor+1;
				}
    			
    		}
    		hdist=hdist+discardColor;
    		hcures=hcures+active;
    		minCardsForCure=10000;
    		for (int j=0;j<board.getNumberOfPlayers();j++){
    			
    			int playerCardsOfColor = cardsCounterOfColor(board,j,board.getColors(i));
    			int playerCardsForCure=0;
    			String playerRole = board.getRoleOf(j);
    			if(playerRole.equals("Scientist")) {
    				playerCardsForCure=3-playerCardsOfColor;
    			}
    			else {
    				playerCardsForCure=4-playerCardsOfColor;
    			}
    			minCardsForCure = playerCardsForCure<minCardsForCure?playerCardsForCure : minCardsForCure;
    			
    		}
    		hcards=hcards+active*minCardsForCure;	
    	}
    	int citiesNum = board.getCitiesCount();
		ArrayList<City> cities= new ArrayList<City>();
		
		for(int i =0; i <citiesNum; i++)
		{
			cities.add(board.searchForCity(i));
			
		} 
    	for(City city: cities) {
    		
    		ArrayList<citiesWithDistancesObj> distanceMap = new ArrayList<citiesWithDistancesObj>();
        	distanceMap =  PLH512.client.Agent.buildDistanceMap(board, city.getName(), distanceMap);
        	
        	for(int c=0;c<distanceMap.size();c++) {
        		hdist=hdist+distanceMap.get(c).getDistance();
        	}
    		
    	}
    	
    	hdist=hdist/(48*47)*roundFactor(board);
    			
    	double hstate = 0.5*(hdcure+hdisc)+0.6*(hinf+hdist)+hcards+24*hcures;
    	if(this.isTerminal() && this.board.checkIfWon() ) {
    		return 200;
    		
    	}
    	if(this.isTerminal() && (!this.board.checkIfWon()) ) {
    		return 0;
    		
    	}
    	
    	hstate= hstateMax-hstate;
//    	System.out.println(hstate);
    	int round=board.getRound();
    	reward=0.05*(reward+regionRisk)+0.95*hstate;
		return	reward*round;

    }
    
    



    public Board copyBoard(Board b) {
    	return PLH512.client.Agent.copyBoard(b);
    }

    
    
    public void setBoard(Board boardToSet) {
		this.board = boardToSet;
	}
    
    public Board getBoard() {
		return board;
	}
    
    


	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}



    
    
}
    
    
   
























 