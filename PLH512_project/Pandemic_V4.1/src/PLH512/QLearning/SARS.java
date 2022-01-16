package PLH512.QLearning;

import PLH512.server.Board;
import PLH512.server.City;

public class SARS {
	GameState gs;
	String cState;
	String nState;
	String action;
	double reward;
	
	
	
	
	
	
	
	public SARS(GameState gs) {
		super();
		this.gs = gs;
		this.cState = cityToState(this.gs.getCityFrom());
		this.nState = cityToState(this.gs.getCityTo());
		this.action = encodeAction(this.gs.getAction());
		this.reward = calcReward();
	}
	
	
	
	String cityToState(City city) {
		boolean hasRS = city.getHasReseachStation();
		boolean hasCubes=false;
		if(city.getBlackCubes()>0||city.getBlackCubes()>0||city.getYellowCubes()>0||city.getRedCubes()>0)
			hasCubes=true;
		if(hasRS) {
			if(hasCubes) {
				return "s3";
			}
			return "s2";
		}
		else {
			if(hasCubes) {
				return "s1";
			}
			return "s0";
		}
		
	}
	
	
	String encodeAction(String a) {
		if(a.contains("DT") || a.contains("DF") || a.contains("CF") || a.contains("SF") || a.contains("OET"))
			return "a0";
		else if(a.contains("BRS"))
			return "a1";
		else if(a.contains("TD"))
			return "a2";
		else
			return "a3";
					
	
	}
	
	double calcReward() {
		
		String s = this.nState;
		String a = this.action;
		Board board = this.gs.getBoard();
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
		
		City dst=board.searchForCity(board.getPawnsLocations(board.getWhoIsPlaying()));
		String cc = dst.getColour();
		
		
		
		

		
		
		
		if(a.equals("a0")) {
			if(s.equals("s0")) {
				reward = WtrS0-handPenalty; 
			}
			else if(s.equals("s1")) {
				reward = WtrS1-handPenalty; 
			}
			else if(s.equals("s2")) {
				reward = WtrS2-handPenalty; 
			}
			else{
				reward = WtrS3-handPenalty; 
			}
			
		}
		
		else if(a.equals("a1")) {
			reward = Wbrs-handPenalty;
		}
		else if(a.equals("a2")) {
			reward = Wtd;
		}
		else if(a.equals("a3")){
			reward = Wcd;
		}
		else
		{
			reward=0;
		}
		return reward;
	}
	
	
	
	
	
	
	
	public GameState getGs() {
		return gs;
	}
	public void setGs(GameState gs) {
		this.gs = gs;
	}
	public String getcState() {
		return cState;
	}
	public void setcState(String cState) {
		this.cState = cState;
	}
	public String getnState() {
		return nState;
	}
	public void setnState(String nState) {
		this.nState = nState;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	
	
	
	
}
