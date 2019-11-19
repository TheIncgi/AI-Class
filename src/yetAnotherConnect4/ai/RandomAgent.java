package yetAnotherConnect4.ai;

import java.util.Stack;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Player;

public class RandomAgent implements Agent{

	@Override
	public int decide(Board b, Record r, Player me) {
		Stack<Integer> s = new Stack<>();
		for(int i = 0; i<7; i++)
			if(b.canDrop(i))
				s.push(i);
		
		return s.get((int) (Math.random()*s.size()));
	}

	@Override
	public void train(GameHistory gameHistory) {
	}
	
	@Override
	public void save() {
	}
	
}
