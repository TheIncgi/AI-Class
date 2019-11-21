package yetAnotherConnect4.ai;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import yetAnotherConnect4.Player;

public class Record {
	Player startingPlayer, winner;
	LinkedList<Byte> moves = new LinkedList<>();
	
	public Record(Player startingPlayer) {
		this.startingPlayer = startingPlayer;
	}
	
	public void recordMove(byte m) {
		moves.add(m);
	}

	public void setWinner(Player p) {
		this.winner = p;
	}
	
	public static Record read(InputStream fis) throws IOException {
		Record r = new Record(Player.values()[fis.read()]);
		r.winner = Player.values()[fis.read()];
		int limit = Math.min(fis.read(), 6*7);
		for(int i = 0; i<limit; i++)
			r.moves.add((byte) fis.read());
		return r;
	}
	public void write(OutputStream fos) throws IOException {
		fos.write(startingPlayer.ordinal());
		fos.write(winner.ordinal());
		fos.write(moves.size()); //only 42 at most
		for(int i = 0; i<moves.size(); i++) 
			fos.write(moves.get(i));
	}
	
	public INDArray toSingleLiveINDArray(Player me) {
		INDArray out = Nd4j.zeros(new int[] {1, 9, moves.size()+1});
		//System.out.println(out.shape());
		for (int i = 1; i <= moves.size(); i++) {
			boolean a= startingPlayer.equals(me);
			out.putScalar(new int[] {1, 0, i}, a?1:0);
			out.putScalar(new int[] {1, 1, i}, a?0:1);
			out.putScalar(new int[] {1, 2+moves.get(i-1), i}, 1); //TODO checkme
		}
		return out;
	}
	
	public INDArray toInputsExampleINDArray() {
		//examples, timesteps, inputs
		INDArray out = Nd4j.zeros(2, 9,  moves.size()+1);
		Player x = startingPlayer;
		for(int i = 1; i<=moves.size(); i++) {
			boolean a= x.equals(startingPlayer);
			//self prespective
			out.putScalar(new int[]{0, 0, i},  a?1:0);
			out.putScalar(new int[]{0, 1, i}, !a?1:0);
			out.putScalar(new int[]{0, 2+moves.get(i-1), i}, 1);
			//not self
			out.putScalar(new int[]{1, 0, i}, !a?1:0);
			out.putScalar(new int[]{1, 1, i},  a?1:0);
			out.putScalar(new int[]{1, 2+moves.get(i-1), i}, 1);
			x = x.next();
		}
		return out;
	}
	public INDArray toLabelsINDArray() { //0 win, 1 draw, 2 loose
		INDArray out = Nd4j.zeros(2, 3,  moves.size()+1);
		for(int i = 1; i <= moves.size(); i++) {
			if(winner.isNone()) {
				out.putScalar(new int[]{0, 1, i}, 1);
				out.putScalar(new int[]{1, 1, i}, 1);
			}else {
				boolean me = winner.equals(startingPlayer);
				out.putScalar( new int[] {0, me?0:2, i}, 1);
				out.putScalar( new int[] {1, me?2:0, i}, 1);
			}
		}
		return out;
	}
	
	public int appendInputExamples(INDArray ind, int exNum) {
		Player x = startingPlayer;
		for(int i = 1; i<=moves.size(); i++) {
			boolean a= x.equals(startingPlayer);
			//self prespective
			ind.putScalar(new int[]{exNum, 0, i},  a?1:0);
			ind.putScalar(new int[]{exNum, 1, i}, !a?1:0);
			ind.putScalar(new int[]{exNum, 2+moves.get(i-1), i}, 1);
			//not self
			ind.putScalar(new int[]{exNum+1, 0, i}, !a?1:0);
			ind.putScalar(new int[]{exNum+1, 1, i},  a?1:0);
			ind.putScalar(new int[]{exNum+1, 2+moves.get(i-1), i}, 1);
			x = x.next();
		}
		return exNum+2;
	}
	public int appendLabelExamples(INDArray ind, int lbNum) {
		for(int i = 1; i <= moves.size(); i++) {
			if(winner.isNone()) {
				ind.putScalar(new int[]{lbNum, 1, i}, 1);
				ind.putScalar(new int[]{lbNum+1, 1, i}, 1);
			}else {
				boolean me = winner.equals(startingPlayer);
				ind.putScalar( new int[] {lbNum, me?0:2, i}, 1);
				ind.putScalar( new int[] {lbNum+1, me?2:0, i}, 1);
			}
		}
		return lbNum+2;
	}

	public Player getStartingPlayer() {
		return startingPlayer;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public LinkedList<Byte> getMoves() {
		return moves;
	}
}
