package yetAnotherConnect4.ai;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Player;

public class HistoryAgent implements Agent{
	
	private Record r;

	public HistoryAgent(Record r) {
		this.r = r;
	}

	@Override
	public int decide(Board b, Record record, Player me) {
		int s = 0;
		for(int i = 0; i<b.slots.length; i++) {
			for(int j = 0; j<b.slots[i].length; j++)
				if(!b.getSlot(j, i).isNone())
					s++;
		}
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {}
		return r.moves.get(s);
	}

	@Override
	public void train(GameHistory gameHistory) {}

	@Override
	public void save() {}
	
	
}
