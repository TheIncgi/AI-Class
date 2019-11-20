package yetAnotherConnect4.ui;

import static org.fusesource.jansi.Ansi.ansi;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.nd4j.linalg.api.ndarray.INDArray;

import scala.reflect.internal.Trees.Throw;

import org.fusesource.jansi.AnsiConsole;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.ExitCondition;
import yetAnotherConnect4.Board.BoardEventListener;
import yetAnotherConnect4.Board.Win;
import yetAnotherConnect4.ai.Agent;
import yetAnotherConnect4.ai.DNNAgent;
import yetAnotherConnect4.ai.GameHistory;
import yetAnotherConnect4.ai.HistoryAgent;
import yetAnotherConnect4.ai.RandomAgent;
import yetAnotherConnect4.ai.Record;
import yetAnotherConnect4.sounds.PlaySound;
import yetAnotherConnect4.GameManager;
import yetAnotherConnect4.Player;

public class Main {

	private static Agent agentA=null, agentB=null;
	private static ObserveMode observe;
	private static ExitCondition exitCondition;
	private static boolean exit = false;
	private static GameManager gm;
	private static DNNAgent theDNNAgent = new DNNAgent();
	private static RandomAgent theRandomAgent = new RandomAgent();

	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		ConsoleUI.printLogo();

		gm = new GameManager(recordKeeper);

		//		try {
		//			CudaEnviroment??
		//		}catch (Throwable e) {
		//			
		//		}

		makeMenus();
		activeMenu = mainMenu;
		while(!exit)
			activeMenu.showAndRun();

		theDNNAgent.save();
		gameHistory.save();
		System.out.println(Ansi.ansi().bold().fgBrightGreen().a("Bye!").boldOff().fgBright(Color.WHITE));
	}

	private static TextMenu mainMenu, exitConditionMenu, observeModeMenu;
	private static void makeMenus() {
		mainMenu = new TextMenu("Choose a mode:");
		mainMenu.addOption("Player v Player", ()->{
			observeFull(); 
			gm.playGame(null, null);
		});
		mainMenu.addOption("Player v Random", ()->{
			agentB = new RandomAgent();
			observeFull();
			gm.playGame(null, agentB);
		});
		mainMenu.addOption("Random v Random", ()->{
			agentA = agentB = new RandomAgent();
			activeMenu = exitConditionMenu;
		});
		mainMenu.addOption("Player v DNN", ()->{
			agentB = theDNNAgent;
			observeFull();
			gm.playGame(null, agentB);
		});
		mainMenu.addOption("Random v DNN", ()->{
			agentA = theRandomAgent;
			agentB = theDNNAgent;
			activeMenu = exitConditionMenu;
		});
		mainMenu.addOption(Color.BLUE, false, "DNN v DNN", ()->{
			agentA = agentB = theDNNAgent;
			activeMenu = exitConditionMenu;
		});
		mainMenu.addOption(Color.GREEN, true, "Train network", ()->{
			int j = promptInt("How many epocs? ");
			for(int i = 0; i<j; i++) {
				System.out.println(ansi().fgBrightCyan().a("Epoch: ").fgBright(Color.WHITE).a((i+1)).fgBrightCyan().a(" of ").fgBright(Color.WHITE).a(j).reset());
				theDNNAgent.train(gameHistory);
				theDNNAgent.save();
			}
		});
		mainMenu.addOption(Color.YELLOW, true, "Playback a game", ()->{
			if(gameHistory.getSize()==0) {
				System.out.println("No games to replay!");
				return;
			}
			System.out.println("Choose a number between 1 and "+gameHistory.getSize());
			int gameNum = promptInt("Which game number? ");
			if(gameNum<0 || gameNum > gameHistory.getSize()) {
				System.out.println("Number out of range!");
				return;
			}
			observeFull();
			try {
				Record rec = gameHistory.get(gameNum-1);
				HistoryAgent historyAgent = new HistoryAgent(rec);
				agentA = agentB = historyAgent;
				gm.playGame(historyAgent, historyAgent, rec.getStartingPlayer());
			}catch (IOException e) {
				System.out.println("Sorry, IOException!");
			}
		});
		mainMenu.addOption(Color.MAGENTA, true, "Evaluate DNN Agent", ()->{theDNNAgent.evaluate();});
		mainMenu.addOption(Color.RED, true, "Wipe Game History", ()->{if(prompt(ansi().bold().fgBrightYellow().a("type ").fgBrightRed().a("delete history").fgBrightYellow().a(" to confirm.\n").toString()).equals("delete history"))gameHistory.wipe();});
		mainMenu.addOption(Color.RED, true, "Delete DNN Model", ()->{ if(prompt(ansi().bold().fgBrightYellow().a("type ").fgBrightRed().a("delete agent").fgBrightYellow().a(" to confirm.\n").toString()).equals("delete agent"))DNNAgent.DEFAULT_FILE.delete(); theDNNAgent = new DNNAgent();});
		mainMenu.addOption(Color.YELLOW, false, "Save and Exit", ()->{exit = true;});

		exitConditionMenu = new TextMenu("Choose timeout mode:")
				.addOption("# Games Played", ()->{
					exitCondition = new ExitCondition.GamesPlayed().fromInput(prompt("How many games?"));
					activeMenu = observeModeMenu;
				})
				.addOption("Time Elapsed", ()->{
					exitCondition = new ExitCondition.ForDurration().fromInput(prompt(Ansi.ansi().fgBrightCyan().a("Format: ").fgBrightMagenta().a("day:hour:min:sec:ms\n").fgBright(Color.WHITE).a("Choose a duration: ").toString()));
					activeMenu = observeModeMenu;
				})
				//.addOption("Until time", ()->{})
				.addOption("Back", ()->{activeMenu = mainMenu;})
				.addOption("Exit", ()->{exit = true;});

		observeModeMenu = new TextMenu("Observe Games?")
				.addOption("Yes", ()->{
					observeFull();
					playUntilExitCondition();
					activeMenu = mainMenu;
				})
				.addOption("End Only", ()->{
					observe = ObserveMode.END_ONLY; playUntilExitCondition();activeMenu = mainMenu;})
				.addOption("No", ()->{observe = ObserveMode.NONE; playUntilExitCondition();activeMenu = mainMenu;});


	}

	private static ObserveMode observeFull() {
		return observe = ObserveMode.FULL;
	}

	private static void playUntilExitCondition() {
		gm.getBoard().eventListeners.add(exitCondition.getBoardListener());
		while(!exitCondition.yet()) {
			gm.playGame(agentA, agentB);
		}
		gm.getBoard().eventListeners.remove(exitCondition);
	}

	private static TextMenu activeMenu;


	public static String prompt(String msg) {
		System.out.print(msg);
		return new Scanner(System.in).nextLine(); //not closing sys in
	}
	public static int promptInt(String msg) {
		Integer j = null;
		do{
			try{
				j = Integer.parseInt(prompt(msg));
			}catch (NumberFormatException nfe) {
			}
		}while(j==null);
		return j;
	}

	public static final GameHistory gameHistory = new GameHistory();
	private static Record currentRecord;

	private static long nextSave = System.currentTimeMillis()+5*60_000;
	public static final BoardEventListener recordKeeper = new BoardEventListener() {

		@Override
		public void onWinner(Board b, Player p, Win w) {
			currentRecord.setWinner(p);
			if(!(agentA instanceof HistoryAgent))
				gameHistory.record( currentRecord );
			if(nextSave <= System.currentTimeMillis()) {
				gameHistory.save();
				nextSave = System.currentTimeMillis()+5*60_000;
			}
			if(!observe.equals(ObserveMode.END_ONLY)) {
				switch (w.getPlayer()) {
				case BLUE:
					System.out.println(ansi().reset().bold().fgBrightGreen().a("Congradulations").boldOff().a(" to ")
							.fgBlue().a("BLUE ").fgBrightGreen().a(" player!"));
					break;
				case NONE:
					System.out.println(ansi().reset().fgBrightYellow().bold().a("Draw!").reset());
					break;
				case RED:
					System.out.println(ansi().reset().bold().fgBrightGreen().a("Congradulations").boldOff().a(" to ")
							.fgRed().a("RED ").fgBrightGreen().a(" player!"));
					break;
				}
			}
			if(observe.equals(ObserveMode.END_ONLY)) {
				ConsoleUI.drawBoardFromScratch(gm, null);
				sleep(250);
			}

		}

		@Override
		public void onTurnComplete(Board b, Player p, int x, int y) {
			currentRecord.recordMove((byte)x);
			if(observe.equals(ObserveMode.FULL)) {
				String[] predictions = new String[] {"","","",""};
				if( !(agentA instanceof HistoryAgent) ) {
					predictions[0] = Ansi.ansi().bold().fgBrightMagenta().a("Network predictions:").toString();
					INDArray forecast = theDNNAgent.evaluateBoardState(b, currentRecord, gm.getTurn());
					double fa, fb, fd;
					fa = forecast.getDouble(gm.getTurn().isBlue()?0:2);
					fb = forecast.getDouble(gm.getTurn().isBlue()?2:0);
					fd = forecast.getDouble(1);
					double fsum = fa+fb+fd;
					fa /= fsum;
					fb /= fsum;
					fd /= fsum;
					predictions[1] = field(Color.BLUE, "Blue wins: ",   fa);
					predictions[2] = field(Color.BLACK,"Draw       ", 	fd);
					predictions[3] = field(Color.RED,  "Red wins:  ",   fb);
				}
				ConsoleUI.drawBoardFromScratch(gm, predictions);
				sleep(500);
			}
		}
		private String field(Color c, String a, Serializable b) {
			return Ansi.ansi().fgBright(c).a(a).fg(Color.WHITE).a(b).reset().toString();
		}

		@Override
		public void onTurnFail(Board b, Player p, int x) {
			PlaySound.playDorp();
		}

		@Override
		public void onNewGame(Board b) {
			currentRecord = new Record(gm.getTurn());
			if(observe.equals(ObserveMode.FULL))
				ConsoleUI.drawBoardFromScratch(gm, null);
		}

	};

	public static void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static Record getRecord() {
		return currentRecord;
	}

	enum ObserveMode {
		FULL,
		END_ONLY,
		NONE;
	}
}
