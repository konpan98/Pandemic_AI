package PLH512.client;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Node;

import PLH512.monteCarlo.MonteCarloTreeSearch;
import PLH512.monteCarlo.State;
import PLH512.server.Board;
import PLH512.server.City;

public class Agent  
{
    final static int ServerPort = 64240;
    final static String username = "myName";
    
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException  
    { 
    	int numberOfPlayers;
    	int myPlayerID;
    	String myUsername;
    	String myRole;
    	ArrayList<Suggestion> suggArray = new ArrayList<Suggestion>();
        
        // Getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // Establish the connection 
        Socket s = new Socket(ip, ServerPort); 
        System.out.println("\nConnected to server!");
        
        // Obtaining input and out streams 
        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());  
        
        // Receiving the playerID from the Server
        myPlayerID = (int)dis.readObject();
        myUsername = "User_" + myPlayerID;
        System.out.println("\nHey! My username is " + myUsername);
        
        // Receiving number of players to initialize the board
        numberOfPlayers = (int)dis.readObject();
        
        // Receiving my role for this game
        myRole = (String)dis.readObject();
        System.out.println("\nHey! My role is " + myRole);
        
        // Sending the username to the Server
        dos.reset();
        dos.writeObject(myUsername);
        
        // Setting up the board
        Board[] currentBoard = {new Board(numberOfPlayers)};
        
        // Creating sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() {
            	
            	boolean timeToTalk = false;
            	
            	//MPOREI NA GINEI WHILE  TRUE ME BREAK GIA SINTHIKI??
                while (currentBoard[0].getGameEnded() == false) 
                { 	
                	timeToTalk = ((currentBoard[0].getWhoIsTalking() == myPlayerID)  && !currentBoard[0].getTalkedForThisTurn(myPlayerID));
                	
                	try {
						TimeUnit.MILLISECONDS.sleep(15);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	
                    try { 
                        // Executing this part of the code once per round
                        if (timeToTalk)
                        {
                        	
                        	// Initializing variables for current round
                        	
                        	Board myBoard = currentBoard[0];
                        	
                        	String myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                        	City myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        	
                        	ArrayList<String> myHand = myBoard.getHandOf(myPlayerID);
                        	
                        	int[] myColorCount = {0, 0, 0, 0};
                        	
                        	for (int i = 0 ; i < 4 ; i++)
                        		myColorCount[i] =  cardsCounterOfColor(myBoard, myPlayerID, myBoard.getColors(i));
                        	
                        	ArrayList<citiesWithDistancesObj> distanceMap = new ArrayList<citiesWithDistancesObj>();
                        	distanceMap = buildDistanceMap(myBoard, myCurrentCity, distanceMap);
                        	
                        	
                        	String myAction = "";
                        	String mySuggestion = "";
                        	
                        	int myActionCounter = 0;
                        	
                        	// Printing out my current hand
                        	System.out.println("\nMy current hand...");
                        	printHand(myHand);
                        	
                        	// Printing out current color count
                        	System.out.println("\nMy hand's color count...");
                        	for (int i = 0 ; i < 4 ; i++)
                        		System.out.println(myBoard.getColors(i) + " cards count: " + myColorCount[i]);
                        	
                        	// Printing out distance map from current city
                        	//System.out.println("\nDistance map from " + myCurrentCity);
                        	//printDistanceMap(distanceMap);
                        	
                        	// ADD YOUR CODE FROM HERE AND ON!! 
/*======================================================================================================================================================================================================================*/                       	
                        	ArrayList<String> actions  = new ArrayList<String>();
                        	State actionsEval = new State(myBoard);
                        	if (myBoard.getWhoIsPlaying() == myPlayerID)
                        	{
                            	// gets the suggestions of the other players 

                        		for (int i = 0; i < myBoard.getNumberOfPlayers(); i++)
                        		{
                       
                        			actions.add(myBoard.getActions(i));
                        			
                        			
                        		}
                        		actions.set(myPlayerID," ");
                        		
                        		Board suggBoard = copyBoard(myBoard);
                            	String[] lastAct;
                            	int j=0;
                            	for (int i = 0; i < actions.size(); i++)
                        		{	
                            		// assigns each action to a suggestion and finds its evaluation
                            		lastAct = actions.get(i).split("#");
	                            	if (suggArray.size() < (myBoard.getNumberOfPlayers() -1) && actions.get(i).equals(" ") == false)
	                            	{
	                            		actionsEval.setAction(lastAct[3]);
	                            		suggArray.add(new Suggestion(actions.get(i), actionsEval.evalState(readActions(actions.get(i), suggBoard))));
	                            		suggBoard = copyBoard(myBoard);
	                            	}
	                            	else if (actions.get(i).equals(" ") == false)
	                            	{
	                            		suggArray.get(j).setAction(actions.get(i));
	                            		actionsEval.setAction(lastAct[3]);
 	                            		suggArray.get(j).getEval().add(actionsEval.evalState(readActions(actions.get(i), suggBoard)));
	                            		j = j+1;
	                            	}
                        		}
                        	}
                    		MonteCarloTreeSearch mcts = new MonteCarloTreeSearch(myBoard, 1000, 100);
                        	while(myActionCounter<4) {
                            	mcts.train();
                            	ArrayList<PLH512.monteCarlo.Node> nodes=mcts.predict(1);
                        		PLH512.monteCarlo.Node currNode= nodes.get(0); 
                        		myAction = myAction+currNode.getState().getAction();
                        		System.out.println(currNode.getState().getAction());
                        		myBoard = copyBoard(currNode.getState().getBoard());
                            	System.out.println("Player "+myPlayerID+" evaluation for this state is "+ nodes.get(0).getValue()/nodes.get(0).getNumOfVisits());
                            	mcts.getTree().setRoot(currNode);
                            	myActionCounter = myActionCounter+1;
                        	}
                        	
                        	Double myEval;
                        	
                        	if (myBoard.getWhoIsPlaying() == myPlayerID)
                        	{
                        		String myLastAct[];
                        		myLastAct = myAction.split("#");
                        		actionsEval.setAction(myLastAct[3]);
                        		// compares our action with the actions the other players suggested
                        		myEval = actionsEval.evalState(readActions(myAction, copyBoard(myBoard)));
                        		for (int i=0; i< suggArray.size(); i++)
                        		{
                        			if (myEval < suggArray.get(i).getEval().get(suggArray.get(i).getEval().size()-1))
                        			{
                        				suggArray.get(i).setPoints(suggArray.get(i).getPoints() +1);
                        			}         
                        		}
                        		for (int i=0; i< suggArray.size(); i++)
                        		{
                        			// if a player is better than as for 3 times, it plays the action he suggested
                        			if(suggArray.get(i).getPoints() >= myBoard.getNumberOfPlayers()-1 && myEval < suggArray.get(i).getEval().get(suggArray.get(i).getEval().size()-1))
                        			{
                        				myAction = suggArray.get(i).getAction();
                        				myEval = suggArray.get(i).getEval().get(suggArray.get(i).getEval().size()-1);
                        				suggArray.get(i).setPoints(2);
                        				System.out.println("CoOp");
                        			}
                       
                        		}
                        		
                        	}
                        	// UP TO HERE!! DON'T FORGET TO EDIT THE "msgToSend"
                        	
                        	// Message type 
                        	// toTextShuttleFlight(0,Atlanta)+"#"+etc
                        	String msgToSend;
                        	if (myBoard.getWhoIsPlaying() == myPlayerID)
                        		msgToSend = myAction;
                        		
                        		//msgToSend = "AP,"+myPlayerID+"#AP,"+myPlayerID+"#AP,"+myPlayerID+"#C,"+myPlayerID+",This was my action#AP,"+myPlayerID+"#C,"+myPlayerID+",This should not be printed..";//"Action";
                            else 
                        		msgToSend = myAction; //"Recommendation"
                        	
                        	
 /*======================================================================================================================================================================================================================*/                       	
                        	// NO EDIT FROM HERE AND ON (EXEPT FUNCTIONS OUTSIDE OF MAIN() OF COURSE)
                        	
                        	// Writing to Server
                        	dos.flush();
                        	dos.reset();
                        	if (msgToSend != "")
                        		msgToSend = msgToSend.substring(1); // Removing the initial delimeter
                        	dos.writeObject(msgToSend);
                        	System.out.println(myUsername + " : I've just sent my " + msgToSend);
                        	currentBoard[0].setTalkedForThisTurn(true, myPlayerID);
                        }
                    } catch (IOException e) { 
                        e.printStackTrace(); 
					}
                } 
            } 
        }); 
          
        // Creating readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
            	
            	
                while (currentBoard[0].getGameEnded() == false) { 
                    try { 
                        
                    	// Reading the current board
                    	//System.out.println("READING!!!");
                    	currentBoard[0] = (Board)dis.readObject();
                    	//System.out.println("READ!!!");
                    	
                    	// Read and print Message to all clients
                    	String prtToScreen = currentBoard[0].getMessageToAllClients();
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    	// Read and print Message this client
                    	prtToScreen = currentBoard[0].getMessageToClient(myPlayerID);
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } catch (ClassNotFoundException e) {
						e.printStackTrace();
					} 
                } 
            } 
        }); 
        
        // Starting the threads
        readMessage.start();
        sendMessage.start(); 
        
        // Checking if the game has ended
        while (true) 
        {
        	if (currentBoard[0].getGameEnded() == true) {
        		System.out.println("\nGame has finished. Closing resources.. \n");
        		//scn.close();
            	s.close();
            	System.out.println("Recources closed succesfully. Goodbye!");
            	System.exit(0);
            	break;
        }
        
        }
    } 
    
    // --> Useful functions <--
    
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
    
    public static String getDirectionToMove (String startingCity, String goalCity, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	City startingCityObj = myBoard.searchForCity(startingCity);
    	
    	int minDistance = distanceFrom(goalCity, distanceMap);
    	int testDistance = 999;
    	
    	String directionToDrive = null;
    	String testCity = null;
    	
    	for (int i = 0 ; i < startingCityObj.getNeighboursNumber() ; i++)
    	{
    		ArrayList<citiesWithDistancesObj> testDistanceMap = new ArrayList<citiesWithDistancesObj>();
    		testDistanceMap.clear();
    		
    		testCity = startingCityObj.getNeighbour(i);
    		testDistanceMap = buildDistanceMap(myBoard, testCity, testDistanceMap);
    		testDistance = distanceFrom(goalCity, testDistanceMap);
    		
    		if (testDistance < minDistance)
    		{
    			minDistance = testDistance;
    			directionToDrive = testCity;
    		}
    	}
    	return directionToDrive;
    }
    
    
    public static String getMostInfectedInRadius(int radius, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	int maxCubes = -1;
    	String mostInfected = null;
    	
    	for (int i = 0 ; i < distanceMap.size() ; i++)
    	{
    		if (distanceMap.get(i).getDistance() <= radius)
    		{
    			City cityToCheck = myBoard.searchForCity(distanceMap.get(i).getName());
    			
    			if (cityToCheck.getMaxCube() > maxCubes)
    			{
    				mostInfected = cityToCheck.getName();
    				maxCubes = cityToCheck.getMaxCube();
    			}
    		}
    	}
    	
    	return mostInfected;
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
    
    public static void printHand(ArrayList<String> handToPrint)
    {
    	for (int i = 0 ; i < handToPrint.size() ; i++)
    		System.out.println(handToPrint.get(i));
    }
    
    public static boolean alredyInDistanceMap(ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	
    	return false;
    }
    
    public static boolean isInDistanceMap (ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    	{
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	}
    	return false;
    }
    
    public static void printDistanceMap(ArrayList<citiesWithDistancesObj> currentMap)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		System.out.println("Distance from " + currentMap.get(i).getName() + ": " + currentMap.get(i).getDistance());
    }
    
    public static int distanceFrom(String cityToFind, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int result = -1;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getName().equals(cityToFind))
    			result = currentDistanceMap.get(i).getDistance();
    	
    	return result;
    }
    
    public static int numberOfCitiesWithDistance(int distance, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int count = 0;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getDistance() == distance)
    			count++;
    	
    	return count;
    }
    
    public static ArrayList<citiesWithDistancesObj> buildDistanceMap(Board myBoard, String currentCityName, ArrayList<citiesWithDistancesObj> currentMap)
    {
    	currentMap.clear();
    	currentMap.add(new citiesWithDistancesObj(currentCityName, myBoard.searchForCity(currentCityName), 0));

    	for (int n = 0 ; n < 15 ; n++)
    	{
        	for (int i = 0 ; i < currentMap.size() ; i++)
        	{
        		if (currentMap.get(i).getDistance() == (n-1))
        		{
        			for (int j = 0 ; j < currentMap.get(i).getCityObj().getNeighboursNumber() ; j++)
        			{
        				String nameOfNeighbor = currentMap.get(i).getCityObj().getNeighbour(j);
        				
        				if (!(alredyInDistanceMap(currentMap, nameOfNeighbor)))
        					currentMap.add(new citiesWithDistancesObj(nameOfNeighbor, myBoard.searchForCity(nameOfNeighbor), n));
        			}
        		}
        	}
    	}
    	
    	return currentMap;
    }
    
    
    // --> Actions <--
    
   
    // --> Coding functions <--
    
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
	public static Board readActions(String toRead, Board board)
	{	
		PrintStream printStreamOriginal=System.out;
		System.setOut(new PrintStream(new OutputStream(){
				public void write(int b) {
				}
			}));
		
		String delimiterActions = "#";
		String delimiterVariables = ",";
		
		String[] actions;
		String[] variables;
		
		int actionCounter = 0;
		
		actions = toRead.split(delimiterActions);
		
		for (int i = 0 ; i < actions.length; i++)
		{
			variables = actions[i].split(delimiterVariables);
			
			if (variables[0].equals("DT"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " drives to " + variables[2]);
				board.driveTo(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("DF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a direct flight to " + variables[2]);
				board.directFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("CF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a charter flight to " + variables[2]);
				board.charterFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("SF"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " takes a shuttle flight to " + variables[2]);
				board.shuttleFlight(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
				
			else if (variables[0].equals("BRS"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is building a Research Station to " + variables[2]);
				board.buildRS(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
			else if (variables[0].equals("RRS"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is removing a Reseaerch Station from " + variables[2]);
				board.removeRS(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("TD"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is treating the " + variables[3] + " disease from " + variables[2]);
				board.treatDisease(Integer.parseInt(variables[1]), variables[2], variables[3]);
				actionCounter++;
			}
			else if (variables[0].equals("CD1"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease");
				board.cureDisease(Integer.parseInt(variables[1]), variables[2]);
				actionCounter++;
			}
			else if (variables[0].equals("CD2"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " is curing the " + variables[2] + " disease and throws " + variables[3] + variables[4] + variables[5] + variables[6] );
				board.cureDisease(Integer.parseInt(variables[1]), variables[2], variables[3], variables[4], variables[5], variables[6]);
				actionCounter++;
			}
			else if (variables[0].equals("AP"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " decided to pass this action");
				board.actionPass(Integer.parseInt(variables[1]));
				actionCounter++;
			}
			else if (variables[0].equals("C"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " sends the following message: " + variables[2]);
				board.chatMessage(Integer.parseInt(variables[1]), variables[2]);
			}
			else if (variables[0].equals("PA"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " plays Airlift. Moving player " + Integer.parseInt(variables[2]) + " to " + variables[3]);
				
			}
			else if (variables[0].equals("OET"))
			{
				//System.out.println("Player " + Integer.parseInt(variables[1]) + " travels to " + variables[2] + " as the Operations Expert");
				board.operationsExpertTravel(Integer.parseInt(variables[1]), variables[2], variables[3]);
				actionCounter++;
			}
			
			
			if (actionCounter >= 4)
			{
				System.out.println("\nYou reached the maximum actions for this turn..");
				break;
			}
		}
		System.setOut(printStreamOriginal);
		return board;
	}

    
    

} 