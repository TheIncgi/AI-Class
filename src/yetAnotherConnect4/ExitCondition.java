package yetAnotherConnect4;

import java.util.Scanner;

import yetAnotherConnect4.Board.BoardEventListener;
import yetAnotherConnect4.Board.Win;

abstract public class ExitCondition {
	private static final BoardEventListener NO_ACTION = new BoardEventListener() {

		@Override
		public void onWinner(Board b, Player p, Win w) {
		}

		@Override
		public void onTurnComplete(Board b, Player p, int x, int y) {
		}

		@Override
		public void onTurnFail(Board b, Player p, int x) {
		}

		@Override
		public void onNewGame(Board b) {
		}
		
	};
	abstract public boolean yet();
	abstract public BoardEventListener getBoardListener();
	abstract public ExitCondition fromInput(String line);
	
	
	public static class ForDurration extends ExitCondition {
		long dur;
		Long start = null;
		@Override
		public boolean yet() {
			if(start == null) return false;
			return System.currentTimeMillis() >= (start+dur);
		}

		@Override
		public BoardEventListener getBoardListener() {
			return bel;
		}

		@Override
		public ExitCondition fromInput(String line) {
			String[] x = line.split(":");
			long out = 0;
			int[] units  = {1, 1000, 60, 60, 24};
			for (int i = 0; i < x.length; i++) {
				int m = Integer.parseInt(x[i]);
				out *= units[i];
				out += m;
			}
			dur = out;
			return this;
		}
		
		BoardEventListener bel = new BoardEventListener() {
			@Override
			public void onWinner(Board b, Player p, Win w) {
			}

			@Override
			public void onTurnComplete(Board b, Player p, int x, int y) {
			}

			@Override
			public void onTurnFail(Board b, Player p, int x) {
			}

			@Override
			public void onNewGame(Board b) {
				if(start==null)
					start = System.currentTimeMillis();
			}
		};
		
	}
	
	public static class GamesPlayed extends ExitCondition {
		long gamesPlayed = 0;
		long minGames;
		
		public GamesPlayed fromInput(String line) {
			this.minGames = Long.parseLong(line);
			return this;
		}


		@Override
		public boolean yet() {
			return gamesPlayed >= minGames;
		}
		
		
		@Override
		public BoardEventListener getBoardListener() {
			return bel;
		}
		
		BoardEventListener bel = new BoardEventListener() {
			@Override
			public void onWinner(Board b, Player p, Win w) {
				gamesPlayed++;
			}

			@Override
			public void onTurnComplete(Board b, Player p, int x, int y) {
			}

			@Override
			public void onTurnFail(Board b, Player p, int x) {
			}

			@Override
			public void onNewGame(Board b) {
			}
		};
		
	}
}
