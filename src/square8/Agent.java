package square8;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import square8.Grid.Move;

public class Agent {
	Grid grid;
	HashMap<Integer, Integer> hashes = new HashMap<>();
	double repeatCost = 50;
	
	public Agent(Grid grid) {
		this.grid = grid;
		hashes.put(grid.hashCode(), hashes.getOrDefault(grid.hashCode(), 0)+1);
	}
	
	private Move choose() {
		double v1 = grid.stateValue();
		Move[] moves = grid.getValidMoves();
		
		double bestV = v1;
		Move bestMove = null;
		for(int i = 0; i<moves.length; i++) {
			Grid g2 = grid.clone();
			double v2 = g2.doMove(moves[i]).stateValue();
			v2 += repeatCost * hashes.getOrDefault(g2.hashCode(), 0);
			if(bestMove==null || v2 < bestV) {
				bestMove = moves[i];
				bestV = v2;
			}
		}
		return bestMove;
	}
	
	public void doStep() {
		Move m = choose();
		grid.doMove(m);
		hashes.put(grid.hashCode(), hashes.getOrDefault(grid.hashCode(), 0)+1);
	}
	
	
}
