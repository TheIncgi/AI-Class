package square8;

import java.util.LinkedList;

import square8.GraphSearch.State;
import square8.Grid.Move;

public class GraphAgent {
	
	GraphSearch search;
	final int MAX_DEPTH = 20000;
	public GraphAgent(Grid grid) {
		
		search = new GraphSearch(new GridState(grid)) {
			
			@Override
			public boolean isGoal(State s) {
				return grid.isSolved();
			}
			
			@Override
			public LinkedList<State> getOptions(State prior) {
				LinkedList<State> options = new LinkedList<>();
				if (prior instanceof GridState) {
					GridState gs = (GridState) prior;
					Move[] moves = gs.getGrid().getValidMoves();
					for (Move move : moves) {
						options.add(new GridState(gs.getGrid().clone().doMove(move), move));
					}
				}
				return options;
			}
		};
	}
	
	public LinkedList<Move> trySolve(){
		
		State s = null;
		for(int i = 0; i<MAX_DEPTH; i++) {
			s = search.exploreStep();
			if(s!=null)break;
		}
		if(s==null) return null;
		LinkedList<Move> solution = new LinkedList<>();
		while(s!=null) {
			if (s instanceof GridState) {
				GridState gs = (GridState) s;
				solution.addFirst(gs.moveToGetHere);
				s = gs.prior;
			}else {
				throw new RuntimeException("Unexpected state type");
			}
			
		}
		return solution;
	}
	
	
	public static class GridState extends State{
		Grid g;
		Move moveToGetHere;
		@Override
		public double calcCost(int depth) {
			return g.stateValue() + depth;
		}
		public GridState(Grid inital) {
			g = inital;
		}
		public GridState(Grid g, Move moveToGetHere) {
			this.g = g;
			this.moveToGetHere = moveToGetHere;
		}
		public Grid getGrid() {
			return g;
		}
		
		@Override
		public int hashCode() {
			return g.hashCode();
		}
	}
}
