package square8;

import java.util.Arrays;
import java.util.Stack;

public class Grid {
	int size = 3;
	private byte[][] theGrid = new byte[size][size];
	
	private Grid(byte[][] toClone) {
		for (int line = 0; line < theGrid.length; line++) 
			for (int elem = 0; elem < theGrid[line].length; elem++) 
				theGrid[line][elem] = toClone[line][elem];
	}
	public Grid() {
		Stack<Byte> tiles = new Stack<>();
		for (byte i = 0; i < (size*size); i++)
			tiles.add(i);
		for (int line = 0; line < theGrid.length; line++) 
			for (int elem = 0; elem < theGrid[line].length; elem++) 
				theGrid[line][elem] = tiles.remove((int)(Math.random()*tiles.size()));
	}
	
	public Move[] getValidMoves() {
		int x = -1, y = -1;
		for (int line = 0; line < theGrid.length; line++) {
			for (int elem = 0; elem < theGrid[line].length; elem++) {
				if(theGrid[line][elem] == 0) {
					x = elem;
					y = line;
					break;
				}
			}
		}
		Move[] moves = new Move[ (x!=0?1:0) + (x!=size-1?1:0) + (y!=0?1:0) + (y!=size-1?1:0) ];
		int n = 0;
		if(x!=0)
			moves[n++] = Move.LEFT;
		if(x!=size-1)
			moves[n++] = Move.RIGHT;
		if(y!=0)
			moves[n++] = Move.UP;
		if(y!=size-1)
			moves[n++] = Move.DOWN;
		return moves;
	}
	
	/**sum of all distances for tiles*/
	public double stateValue() {
		double total = 0;
		for(int i = 0; i<size*size; i++) {
			int tx = i%size;
			int ty = i/size;
			int x = 0, y = 0;
			for (int line = 0; line < theGrid.length; line++) {
				for (int elem = 0; elem < theGrid[line].length; elem++) {
					if(theGrid[line][elem] == i) {
						x = elem;
						y = line;
						break;
					}
				}
			}
			double dist = Math.abs(tx - x) + Math.abs(ty - y);
			total += dist * (i); 
		}
		return total;
	}
	
	
	public final Grid clone(){
		return new Grid(theGrid);
	}
	
	/**returns this*/
	public Grid doMove(Move m) {
		int x = -1, y = -1;
		for (int line = 0; line < theGrid.length; line++) {
			for (int elem = 0; elem < theGrid[line].length; elem++) {
				if(theGrid[line][elem] == 0) {
					x = elem;
					y = line;
					break;
				}
			}
		}
		if(x==-1 || y==-1) throw new RuntimeException("empty not found");
		int tx = 0, ty = 0;
		if(m.equals(Move.UP)) 
			ty = -1;
		else if(m.equals(Move.DOWN)) 
			ty = 1;
		else if(m.equals(Move.LEFT)) 
			tx = -1;
		else if(m.equals(Move.RIGHT))
			tx = 1;
		swap(x, y, x+tx, y+ty);
		return this;
	}
	
	private void swap(int a, int b, int x, int y) {
		byte tmp = theGrid[b][a];
		theGrid[b][a] = theGrid[y][x];
		theGrid[y][x] = tmp;
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(theGrid);
		return result;
	}
	
	public boolean isSolved() {
		for (int line = 0; line < theGrid.length; line++) 
			for (int elem = 0; elem < theGrid[line].length; elem++) 
				if(theGrid[line][elem] != (line*size+elem)) 
					return false;
		return true;
	}
	
	enum Move{
		LEFT, UP, RIGHT, DOWN;
	}
	
	public String rep(String x, int n) {
		StringBuilder b = new StringBuilder();
		for(int i = 0; i<n; i++) b.append(x);
		return b.toString();
	}
	
	public void print() {
		int cwid = ((size*size)+"").length();
		System.out.println("+"+rep("=",size*(cwid+1)-1)+"+");
		for (int i = 0; i < theGrid.length; i++) {
			System.out.print("|");
			for (int j = 0; j < theGrid[i].length; j++) {
				System.out.print(theGrid[i][j]==0?rep(" ",cwid):String.format("%"+cwid+"d",theGrid[i][j]));
				System.out.print("|");
			}
			System.out.println();
		}
		System.out.println("+"+rep("=",size*(cwid+1)-1)+"+");
	}
	
	public void flipEvenOdd() {
		int oddX = 0, oddY = 0;
		int evenX = 0, evenY = 0;
		for (int line = 0; line < theGrid.length; line++) 
			for (int elem = 0; elem < theGrid[line].length; elem++) {
				if(theGrid[line][elem]%2==1) {
					oddX = elem;
					oddY = line;
				}else {
					evenX = elem;
					evenY = line;
				}					
			}
		swap(oddX, oddY, evenX, evenY);
	}
}
