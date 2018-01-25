package controllers.aStar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


import cicero.constants.Folder;
//import cicero.seekwhence.model.PairIdentifierSymbol; //This class was not public before
//import cicero.seekwhence.model.PairIdentifierSymbol;
import cicero.seekwhence.model.Store;
import cicero.utils.Util;
import cicero.utils.VGDLTreeFormation;
import core.Node;
import core.VGDLParser;
import core.VGDLRegistry;
import core.VGDLSprite;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import tools.IO;
import tools.Vector2d;

@SuppressWarnings("unused")
public class Agent_backUp extends AbstractPlayer{

	public ArrayList<Vector2d> points = new ArrayList<Vector2d>();
	public int colorIntensity = 10;
	public static final ArrayList<Store> storeList = new ArrayList<Store>();
	public int gridXlength, gridYlength;
	


	public Agent_backUp(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){

	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

		//SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);
		float avgTime = 10;
		float worstTime = 10;
		float totalTime = 0;
		int numberOfIterations = 0;
		Comparator<TNode> comparator = new NodeCostFunction();
		PriorityQueue<TNode> queue = new PriorityQueue<TNode>(comparator);
		PriorityQueue<TNode> closed = new PriorityQueue<TNode>(comparator);
		TNode currentNode = null;
		
		int stateNum = 0;
		String stateStr = "state";
//		
//		Map<String, ArrayList<JSONObject>> stateMapDict = new HashMap<String, ArrayList<JSONObject>>();
//		

		queue.add(new TNode(null, stateStr + stateNum , Types.ACTIONS.ACTION_NIL, stateObs, Double.POSITIVE_INFINITY));
		queue.add(new TNode(null, stateStr + stateNum , Types.ACTIONS.ACTION_NIL, stateObs, Double.POSITIVE_INFINITY));
		closed.add(new TNode(null, stateStr + stateNum , Types.ACTIONS.ACTION_NIL,stateObs, Double.POSITIVE_INFINITY));
		ArrayList<Types.ACTIONS> possibleActions = stateObs.getAvailableActions();
		
		int blockSize = stateObs.getBlockSize();
		
		gridXlength = (int) stateObs.getWorldDimension().getWidth() / blockSize;
		gridYlength = (int) stateObs.getWorldDimension().getHeight() / blockSize;
		
		
		
		//Initiated a JSON Object to be store 
		//The name should match to the frame reference
		JSONObject rootNodeMap = new JSONObject();


		JSONArray rootNodeChildrenList = new JSONArray();
		
	
	
		while(!queue.isEmpty() && elapsedTimer.remainingTimeMillis() > 2 * avgTime && elapsedTimer.remainingTimeMillis() > worstTime){
			//while(!queue.isEmpty()){
			ElapsedCpuTimer methodTime = new ElapsedCpuTimer();

			currentNode = queue.remove();
//			
//		   JSONObject pNodeMap = new JSONObject();
		    
			rootNodeMap.put("name", currentNode.name);
		   
		   if (currentNode.parent == null) {
			   
			   rootNodeMap.put("parent", null);
			   
		   }
		   else {
			   
			   rootNodeMap.put("parent", currentNode.parent.name);
			   
		   }
		   
//		   rootNodeMap.put("parent", currentNode);
		   
		   JSONArray rootStateMap = getData(currentNode.state,  blockSize);
		   
		   	    
		   ArrayList<JSONObject> childrenArrayList = new ArrayList<JSONObject>();
//		   stateMapDict.put(currentNode.name, childrenArrayList);
//			
//		   
		   JSONArray childrenList = new JSONArray();
			
			
			possibleActions = currentNode.state.getAvailableActions();

			for(int i=0;i<possibleActions.size();i++){
				
				StateObservation newState = currentNode.state.copy();
				
				stateNum += 1;
				
				//getData(newState,)
				
				JSONArray thisStateMap = getData(newState,  blockSize);
				System.out.println(" "); 
								
				newState.advance(possibleActions.get(i));		
				TNode newNode = new TNode(currentNode, stateStr + stateNum, possibleActions.get(i), newState, Double.POSITIVE_INFINITY );
				newNode.stateMap = thisStateMap;
				
				
				
				newNode.getCost();
				if(newNode.state.getGameWinner() == WINNER.PLAYER_WINS){
					break;
				}
				if(newNode.state.getGameWinner() == WINNER.PLAYER_LOSES){
					continue;
				}
				if (!queue.isEmpty()){
					if (newNode.cost < queue.peek().cost)
						queue.add(newNode);
					else if (newNode.cost < closed.peek().cost )
						queue.add(newNode);
					
					
					currentNode.children.add(newNode);
					
				    JSONObject cNodeMap = new JSONObject();
				    
				    cNodeMap.put("name", newNode.name);
				    cNodeMap.put("parent", newNode.parent.name);
				    cNodeMap.put("stateMap", newNode.stateMap);
//				    
//					stateMapDict.get(currentNode.name).add(cNodeMap);
//					

				    childrenList.add(cNodeMap);
				    
				    
				}	
			}
			

			
//			pNodeMap.put("children", childrenList);
//			rootNodeChildrenList.add(pNodeMap);
			
			closed.remove();
			closed.add(currentNode);

			numberOfIterations += 1;
			totalTime += methodTime.elapsedMillis();
			avgTime = totalTime / numberOfIterations;

		}
		

		
		
		
		ArrayList<Observation>[][] grid = currentNode.state.getObservationGrid();
		
		for (int j = 0; j < gridXlength; j++) 
		{
			for (int j2 = 0; j2 < gridYlength; j2++) 
			{
				for (int k = 0; k < grid[j][j2].size(); k++) 
				{
					String spriteIdentifier = VGDLRegistry.GetInstance().getRegisteredSpriteKey(grid[j][j2].get(k).itype);
					int spriteCode = grid[j][j2].get(k).obsID;
					if(spriteIdentifier != null)
					{
						storeList.add(new Store(currentNode.state.getGameTick(),
								(int) grid[j][j2].get(k).position.x / blockSize,
								(int) grid[j][j2].get(k).position.y / blockSize,
								spriteIdentifier, spriteCode)); 
					}	
				}
			}
		}
		
		rootNodeMap.put("children", rootNodeChildrenList);
		
		
//	    System.out.print( "stateMapDict: " + stateMapDict.size());
//	    
//	    stateMapDict.forEach((key, value) -> {
//	        System.out.println("I am the key! \"" + key.toString() + "\"");
//	        System.out.println("I am the value! \"" + value + "\"");
//	    });
	    
		
		
		//Export JSON File
		 try (FileWriter file = new FileWriter("/Users/zhonghengli/Temp/test" + elapsedTimer.toString() +".json")) {

	            file.write(rootNodeMap.toJSONString());
	            file.flush();

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

		 

		return currentNode.getAction();
	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub
		super.draw(g);

		if(this.isAlive)
		{	
			Color c = new Color(255, 0, 0, colorIntensity);
			for (int i = 0; i < points.size(); i++) {
				g.setColor(c);
				g.drawRect((int)points.get(i).x, (int)points.get(i).y, 20, 20);
			}
		}
		else
		{
			//g.drawOval((int)this.position.x, (int)this.position.y, 20, 20);
		}
	}
	
	public PairIdentifierSymbol[] getGameSpriteMapValues(String gameName)
	{
		String[] desc_lines = new IO().readFile(Folder.FILES_FOLDER + gameName);
		VGDLParser vgdlParser = new VGDLParser();
		Node root = vgdlParser.indentTreeParser(desc_lines);
		root.correctSetValues();		
		int size = root.children.get(1).children.size();
		PairIdentifierSymbol [] pairs = new PairIdentifierSymbol[size+1];//1 means the wall symbol (w)
		for (int i = 0; i < size; i++) 
		{
			pairs[i] = new PairIdentifierSymbol(
					Util.getNameFromLevelMappingLine(root.children.get(1).children.get(i).content.line),
					root.children.get(1).children.get(i).content.line.charAt(0));
		}
		PairIdentifierSymbol pair = new PairIdentifierSymbol("wall", 'w');//including the pair (wall, w)
		pairs[pairs.length-1] = pair;
		return pairs;
	}

	public char getSymbolByIdentifier(String identifier, PairIdentifierSymbol[] pairs)
	{
		char symbolToReturn = 0;
		for (int i = 0; i < pairs.length; i++) 
		{
			if(pairs[i].identifier.equals(identifier))
			{
				symbolToReturn = pairs[i].symbol;
			}
		}
		return symbolToReturn;
	}

	
	
	public JSONArray getData(StateObservation newState, int blockSize)
    {
    	ArrayList<Observation>[][] grid = newState.getObservationGrid();

    	int gridXlength = (int)newState.getWorldDimension().getWidth()/blockSize;
    	int gridYlength = (int)newState.getWorldDimension().getHeight()/blockSize;

    	
//    	Need to create a Json Array    	
    	JSONArray dataMap = new JSONArray();

    		
    	for (int j = 0; j < gridXlength ; j++) 
    	{
    		
    	    JSONArray stateMap_row = new JSONArray();
//    		System.out.println(" "); //LineBreak
    		for (int j2 = 0; j2 < gridYlength; j2++) 
    		{
    			
    			if (grid[j][j2].isEmpty()) {
    				
    			    stateMap_row.add("empty" );
//    				
//    				System.out.print("empty");
//				System.out.print("'");

    			}

    			
    			for (int k = 0; k < grid[j][j2].size(); k++) 
    			{
    				String spriteIdentifier = VGDLRegistry.GetInstance().getRegisteredSpriteKey(grid[j][j2].get(k).itype);
    				int spriteCode = grid[j][j2].get(k).obsID;
    				


    				if(spriteIdentifier != null)
    				{
    					cicero.controllers.RT.adrienctx.Agent.
    					frameList.add(new Store(newState.getGameTick(),
    							(int) grid[j][j2].get(k).position.x / blockSize,
    							(int) grid[j][j2].get(k).position.y / blockSize,
    							spriteIdentifier, spriteCode)); 
    					
    					
    					
    					
//    					if(ViszCollection.viszCollection.get(spriteIdentifier) != null)
//    					{
//    						Track t_ = new Track(this.getObservation().getGameTick(), grid[j][j2].get(k).position, new Color(0.0f, 1.0f, 0.0f, 0.0f), spriteIdentifier, false);
//    						System.out.println("-- " + t_.identifier);
//    						ViszCollection.viszCollection.get(spriteIdentifier).addTrack(t_);
//    					}
    					

//    					System.out.print(VGDLRegistry.GetInstance().getRegisteredSpriteKey(grid[j][j2].get(0).itype));
//    					
//  					
//    					System.out.print("'");
//    					
        			    stateMap_row.add(VGDLRegistry.GetInstance().getRegisteredSpriteKey(grid[j][j2].get(0).itype) );
    					
    					
    				}
	
    			}
    		}
    		
    		dataMap.add(stateMap_row);
    		
    	}
    	
    	   
    	   return dataMap;
    	
    	
    }
	
	public class PairIdentifierSymbol
	{
		String identifier;
		char symbol;

		public PairIdentifierSymbol(String identifier, char symbol)
		{
			this.identifier = identifier;
			this.symbol = symbol;
		}
	}
	

	public class NodeCostFunction implements Comparator<TNode>
	{
		@Override
		public int compare(TNode x, TNode y)
		{
			if (x.cost < y.cost)
			{
				return -1;
			}
			else
				return 0;
		}
	}

	public class TNode {
		public TNode parent;
		public String name;
		public Types.ACTIONS action;
		public StateObservation state;
		public double cost;
		public JSONArray stateMap;
		public ArrayList children;

		public TNode(TNode parent, String name, Types.ACTIONS action, StateObservation state, double cost){
			this.parent = parent;
			this.name = name;
			this.action = action;
			this.state = state;
			this.cost = cost;
		}

		public Types.ACTIONS getAction(){
			if(this.parent == null){
				return action;
			}
			if(this.parent.parent == null){
				return action;
			}
			return parent.action;
		}

		public void getCost(){
			double g = deepFromRoot(this);
			if (g==0) g=0.1;
			double h = 10/g;
			this.cost = h+g;
		}
		
	   @Override
	    public String toString() {
	        return String.format("Name: " + this.name + "Parent: " + this.parent + "State Map: " +  this.stateMap + " Children:" + this.children  + " ");
	    }
	}
	public int deepFromRoot(TNode finalNode){
		int i = 0;
		while(finalNode.parent!=null) {
			finalNode = finalNode.parent;
			i++;
		}
		return i;
	}	
}