package yetAnotherConnect4.ai;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Player;

public interface Agent {

	public int decide(Board b, Record record, Player me);

	public void train(GameHistory gameHistory);
	
	public void save();
	
}
