package mdp.solver.exploration;
import mdp.Map;
public class MapViewer{
	
	private Map map;
	private int detectRange;  //default to 3
	
	
	public void initMap(){
		map = new Map();
	}
	
	//take RPI data from Solver , update what I saw
	public Map updateMap(String data){
	
		return map;
	}
	
}
