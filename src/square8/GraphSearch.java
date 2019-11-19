package square8;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;

abstract public class GraphSearch {
	HashSet<State> closed;
	LinkedList<State> open;
	
	public int skips,states;
	
	private int depth = 0;
	public GraphSearch(State initalState) {
		closed = new HashSet<>();
		open = new LinkedList<>();
		open.add(initalState);
	}
	
	abstract public LinkedList<State> getOptions(State prior);
	
	
	/**
	 * Returns null or goal state node
	 * */
	public State exploreStep() {
		State toClose = open.removeFirst();
		System.out.printf("Exploring step: %d, [V %d, S: %d, T: %d]\n",depth, states, skips, states+skips);
	
		closed.add(toClose);
		if(isGoal(toClose))
			return toClose;
		addOptions(toClose, depth);
		
		depth++;
		return null;
	}
	
	abstract public boolean isGoal(State s);
	
	private void addOptions(State prior, int depth) {
		LinkedList<State> opts = getOptions( prior );
		ListIterator<State> it = open.listIterator();
		while( !opts.isEmpty() ) {
			State x = opts.removeFirst();
			x.prior = prior;
			x.cost = x.calcCost(depth);
			if(!closed.contains(x)) {
				open.add(x);
				states++;
			}else {
				skips++;
			}
		}
		open.sort(State.getComparator());
		
		
	}
	
	abstract public static class State{
		State prior;
		double cost;
		abstract public double calcCost(int depth);
		private final double getCost() {return cost;}
		private static Comparator<State> comparator = new Comparator<GraphSearch.State>() {
			@Override
			public int compare(State a, State b) { //+ <  |   0 =   |    - > 
				double c1 = a.getCost();
				double c2 = b.getCost();
				return c1==c2? 0:  (c1<c2?1:-1);
			}
		}; 
		public static Comparator<State> getComparator() {
			return comparator;
		}
		
	}
}
