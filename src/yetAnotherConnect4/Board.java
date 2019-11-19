package yetAnotherConnect4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Board {
	/**y,x*/
	public Player[][] slots = new Player[6][7];
	public ArrayList<BoardEventListener> eventListeners = new ArrayList<>();
	
	public Board(BoardEventListener... eventListeners) {
		Collections.addAll(this.eventListeners, eventListeners);
	}
	
	/**Call before starting*/
	public void clear() {
		for (int y = 0; y < slots.length; y++) {
			for (int x = 0; x < slots[y].length; x++) {
				setSlot(Player.NONE, x, y);
			}
		}
		notifyNewGame();
	}
	
	public Player getSlot(int x, int y) {
		if(y < 0 || slots.length <= y) return Player.NONE;
		if(x < 0 || slots [y].length <= x) return Player.NONE;
		return slots[y][x];
	}
	
	public void setSlot(Player p, int x, int  y) {
		if(p==null) throw new NullPointerException("Player must not be null");
		if(y < 0 || slots.length <= y ) return;
		if(x < 0 || slots [y].length <= x ) return;
		slots[y][x] = p;
	}
	
	public boolean canDrop(int x) {
		return getSlot(x, 5).isNone();
	}
	public boolean drop(Player p, int x) {
		if(!canDrop(x)) {
			notifyTurnFail(p, x);
			return false;
		}
		for(int y = 0; y<slots.length; y++) {
			if (getSlot(x, y).isNone()) {
				setSlot(p, x, y);
				notifyTurnComplete(p, x, y);
				return true;
			}
		}
		//should be unreachable
		notifyTurnFail(p, x);
		return false;
	}
	
	public boolean canPlaceAny() {
		for(int i = 0; i<7; i++)
			if(canDrop(i)) return true;
		return false;
	}
	
	public Win checkWinner() {return checkWinner(false);}
	public Win checkWinner(boolean quiet) {
		for(int y = 0; y < slots.length; y++) {
			for(int x = 0; x < slots[y].length; x++) {
				for(int dx = -1; dx<=1; dx++) {
					for(int dy = -1; dy<=1; dy++) {
						if(dx==0&&dy==0) continue;
						Win w = checkWin(x, y, dx, dy);
						if(w!=null) {
							if(!quiet)
							notifyWinner(getSlot(x, y), w);
							return w;
						}
					}
				}
			}
		}
		return canPlaceAny()?null:new Win(Player.NONE, -1, -1, -1, -1);
	}
	private Win checkWin(int x, int y, int dx, int dy) {
		Player p = getSlot(x, y);
		if(p.isNone()) return null;
		
		for(int i = 1; i<4; i++) {
			if(!p.equals(getSlot(x + dx*i, y + dy*i))) return null;
		}
		return new Win(p, x, y, dx, dy);
	}
	
	private void notifyWinner(Player p, Win w) {
		for (BoardEventListener boardEventListener : eventListeners) {
			boardEventListener.onWinner(this, p, w);
		}
	}
	private void notifyTurnComplete(Player p, int x, int y) {
		for (BoardEventListener boardEventListener : eventListeners) {
			boardEventListener.onTurnComplete(this, p, x, y);
		}
	}
	private void notifyTurnFail(Player p, int x) {
		for (BoardEventListener boardEventListener : eventListeners) {
			boardEventListener.onTurnFail(this, p, x);
		}
	}
	private void notifyNewGame() {
		for (BoardEventListener boardEventListener : eventListeners) {
			boardEventListener.onNewGame(this);
		}
	}
	
	public static class Win {
		int x, y;
		int dx, dy;
		Player p;
		public Win(Player p, int x, int y, int dx, int dy) {
			this.p = p;
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
		}

		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public int getDx() {
			return dx;
		}
		public int getDy() {
			return dy;
		}
		public Player getPlayer() {
			return p;
		}
		
	}
	
	public static class BoardEventListener {
		public void onWinner(Board b, Player p, Win w) {}
		public void onTurnComplete(Board b, Player p, int x, int y) {}
		public void onTurnFail(Board b, Player p, int x) {}
		public void onNewGame(Board b) {}
	}
}
