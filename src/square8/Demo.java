package square8;

import java.util.LinkedList;

import square8.Grid.Move;

public class Demo {
	
	public static void main(String[] args) {
		Grid grid = new Grid();
		GraphAgent agent = new GraphAgent(grid.clone());
		LinkedList<Move> solution = agent.trySolve();
		System.out.println(solution);
	}
	
	public static void basicAgent() {
		Grid grid = new Grid();
		Agent agent = new  Agent(grid);
		
		grid.print();
		
		trySolve(grid, agent);
		if(grid.isSolved())
			System.out.println("SOLVED!");
		else {
			System.out.println("FLIPPED");
			grid.flipEvenOdd();
			trySolve(grid, agent);
			if(grid.isSolved())
				System.out.println("Solve flipped!");
			else
				System.out.println("Failure");
		}
	}
	
	public static void trySolve(Grid grid, Agent agent) {
		for(int i = 0; i<40000; i++) {
			System.out.println("Step: "+(1+i));
			agent.doStep();
			
			grid.print();
//			try {
//				Thread.sleep(25);
//			} catch (InterruptedException e) {
//			}
			if(grid.isSolved())
				break;
		}
	}
}
