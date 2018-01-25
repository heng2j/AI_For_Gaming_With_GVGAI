package controllers.adrienctxNew;

import java.util.ArrayList;
import java.util.Comparator;

import org.json.simple.JSONArray;

import cicero.seekwhence.model.Store;
import controllers.adrienctxNew.VizJSONData.TNode;
import core.VGDLRegistry;
import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;

public class VizJSONData {


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
	        return String.format("Name: " + this.name + "Parent: " + this.parent + "State Map: " +  this.stateMap + " ");
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
