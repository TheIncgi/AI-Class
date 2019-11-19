package yetAnotherConnect4;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.WHITE;

import java.util.Scanner;

import org.fusesource.jansi.Ansi;

import yetAnotherConnect4.Board.BoardEventListener;
import yetAnotherConnect4.Board.Win;
import yetAnotherConnect4.ai.Agent;
import yetAnotherConnect4.ai.RandomAgent;
import yetAnotherConnect4.ai.Record;
import yetAnotherConnect4.ui.Main;

public class GameManager {
	Board b;
	Player current;
	
	public GameManager(BoardEventListener...boardEventListeners) {
		b = new Board(boardEventListeners);
	}
	
	public void init() {
		b.clear();
	}
	
	public void playGame(Agent p1, Agent p2) {
		current = Math.random() > .5 ? Player.BLUE : Player.RED ;
		playGame(p1, p2, current);
	}
	
	public void playGame(Agent p1, Agent p2, Player pfirst) {
		Win winner = null;
		init();
		current = pfirst;
		do {
			Agent agent = current.isBlue()? p1 : p2;
			if(agent==null) {
				System.out.print(ansi().fgBrightYellow().a("Turn: "));
				switch (getTurn()) {
				case BLUE: System.out.println(ansi().fgBlue().a("BLUE")); break;
				case NONE: System.out.println(ansi().fgBrightBlack().a("NONE")); break;
				case RED:  System.out.println(ansi().fgRed().a("RED")); break;
				default:
					break;
				}
				System.out.println(ansi().reset().a("Choose a slot: ").fgBright(WHITE).reset());
				int ans = -1;
				do {
					try {
						ans = Integer.parseInt(new Scanner(System.in).nextLine())-1;
					}catch (NumberFormatException e) {
						//invalid
					}
				}while(!b.drop(current, ans));
				nextTurn();
			}else {
				if(!b.drop(current, agent.decide(b, Main.getRecord(), current))) throw new RuntimeException("Agent chose a slot that's already full!");
				else {
					nextTurn();
				}
			}
			winner = b.checkWinner();
			
		}while(winner == null);
	}
	
	public void nextTurn() {
		switch (current) {
		case BLUE:
			current = Player.RED;
			break;
		case RED:
			current = Player.BLUE;
			break;
		default:
		}
	}
	
	public Player getTurn() {
		return current;
	}
	
	public Board getBoard() {
		return b;
	}

	public void doTurn(int x) {
		if(b.drop(current, x))
			nextTurn();
	}

}
