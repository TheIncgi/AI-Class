package yetAnotherConnect4.ui;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;

import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;

import yetAnotherConnect4.Board;
import yetAnotherConnect4.Board.Win;
import yetAnotherConnect4.GameManager;
import yetAnotherConnect4.Player;

public class ConsoleUI {
	static GameManager game;
	public static final String[] logo = {
			 " ╔════╗                                                    ╔════╗",
			 "╔╝████║                                                   ╔╝████║",
			 "║█████║                                                  ╔╝█████║",
			 "║███╔═╝                                           ╔══╗  ╔╝███░██║",
			 "║██╔╝  ╔═════╗╔═══════╗╔═══════╗  ╔════╗ ╔═════╗╔═╝██╚═╦╝███░░██║",
			 "║██║  ╔╝█████╚╣██░████╚╣██░████╚╗╔╝████╚╦╝█████╚╣██████║████████║",
			 "║██╚╗ ║███░███╠╗███░███╠╗███░███║║██░░██║███░░██╠═╗██╔═╣████████║",
			 "║███╚═╣██░░░██║║██╔═╗██║║██╔═╗██║║█████╔╣██░░░░░║ ║██║ ╚════╗███║",
			 "║█████║███░███║║██║ ║██║║██║ ║██║║██░░░╣║███░░██║ ║██╚╗     ║███║",
			 "╚╗████╠╗█████╔╝║██║ ║██║║██║ ║██║╚╗████║╚╗█████╔╝ ╚╗██║     ║███║",
			 " ╚════╝╚═════╝ ╚══╝ ╚══╝╚══╝ ╚══╝ ╚════╝ ╚═════╝   ╚══╝     ╚═══╝"
	}, colorMap = {
			 " aaaaaa                                                    aaaaaa",
			 "aa▓▓▓█a                                                   aa▓▓██a",
			 "a▓▓███a                                                  aa▓████a",
			 "a▓██aaa                                           aaaa  aa▓██b██a",
			 "a▓█aa  aaaaaaaaaaaaaaaaaaaaaaaaa  aaaaaa aaaaaaaaaa▓█aaaa▓██bb██a",
			 "a██a  aa▓▓▓▓█aa▓█b▓▓▓█aa▓█b▓▓▓█aaaa▓▓▓█aaa▓▓▓▓█aa▓▓████a▓███▓▓██a",
			 "a██aa a▓██b███aa▓██b███aa▓██b███aa▓█bb██a▓██bb██aaa▓█aaa▓███████a",
			 "a███aaa▓█bbb▓█aa▓█aaa██aa▓█aaa██aa▓██▓▓aa▓█bbbbba a▓█a aaaaaa▓██a",
			 "a████▓a███b▓██aa██a a██aa██a a██aa██bbbaa███bb▓█a a██aa     a▓██a",
			 "aa████aa█████aaa██a a██aa██a a██aaa████aaa███▓█aa aa██a     a▓██a",
			 " aaaaaaaaaaaaa aaaa aaaaaaaa aaaa aaaaaa aaaaaaa   aaaa     aaaaa"
	};

	
	
	public static void runGame() {
		
		System.out.println( ansi().eraseScreen().fg(RED).a("Hello").fg(GREEN).a(" World").reset() );
		

		game = new GameManager() ;
		game.init();
		drawBoardFromScratch(game, null);
		
		Scanner in = new Scanner(System.in);
		Win winner = null;
		do {
			try {
				game.doTurn(in.nextInt()-1);
			}catch (Exception e) {
			}
			drawBoardFromScratch(game, null);
			winner = game.getBoard().checkWinner();
		}while(winner == null);
		switch (winner.getPlayer()) {
		case BLUE:
			System.out.println(ansi().reset().bold().fgBrightGreen().a("Congratulations").boldOff().a(" to ")
					.fgBlue().a("BLUE ").fgBrightGreen().a(" player!"));
			break;
		case NONE:
			System.out.println(ansi().reset().fgBrightYellow().bold().a("Draw!").reset());
			break;
		case RED:
			System.out.println(ansi().reset().bold().fgBrightGreen().a("Congratulations").boldOff().a(" to ")
					.fgRed().a("RED ").fgBrightGreen().a(" player!"));
			break;
		}
	}


	public static void drawBoardFromScratch(GameManager gm, String[] sidebarRight) {
		Board c4 = gm.getBoard();
		Win win = c4.checkWinner(true);
		System.out.print( ansi().eraseScreen() );

		printLogo();
		
		Ansi fc = ansi().fg(WHITE);

		String d = ansi().fg(WHITE).a("║").toString();
		System.out.println(ansi().fgGreen().a(" 1 2 3 4 5 6 7").reset());
		System.out.println(ansi().fg(WHITE).a("╓┴╥┴╥┴╥┴╥┴╥┴╥┴╖").reset());
		for(int y = 5; y>=0; y--) {
			int x = 0;
			System.out.printf("%s%s%s%s%s%s%s%s%s%s%s%s%s%s%s  %s\n",
					d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					tile(c4.getSlot(x, y), h(win, x++, y)), d,
					sidebarRight!=null && sidebarRight.length > 6-y? sidebarRight[6-y] : ""
					);
			if(y>0)
				System.out.println(ansi().fg(WHITE).a("╟─╫─╫─╫─╫─╫─╫─╢").reset());
			else
				System.out.println(ansi().fg(WHITE).a("╚═╩═╩═╩═╩═╩═╩═╝").reset());
		}
	}

	/**Should highlight*/
	public static boolean h(Win win, int x, int y) {
		if(win==null) return false;
		for(int i = 0; i<4; i++) {
			if( win.getX() + win.getDx() * i == x &&
				win.getY() + win.getDy() * i == y) return true;
		}
		return false;
	}
	
	public static String tile(Player p, boolean highlight) {
		String prefix = highlight?ansi().boldOff().bgYellow().toString() : "";
		String suffix = highlight?ansi().bold().bg(Color.BLACK).toString() : "";
		String r = prefix + ansi().fg(RED).a("@").toString() + suffix;
		String b = prefix + ansi().fg(BLUE).a("@").toString() + suffix;
		switch(p) {
		case BLUE:
			return b;
		case NONE:
			return ansi().fgBright(Color.BLACK).a("░").toString();
		case RED:
			return r;
		default:
			return ansi().fgBright(WHITE).a("?").toString();
		}
	}
	
	
	public static void printLogo() {
		Ansi n = ansi();
		for (int l = 0; l < logo.length; l++) {
			for(int x = 0; x<logo[l].length(); x++) {
				switch (colorMap[l].charAt(x)) {
				case 'a':
					n.fgBrightGreen(); 
					break;
				case 'b':
					n.fgGreen(); 
					break;
				case '▓':
					if(colorMap[l].length() - 10 >= x)
						n.fgBrightCyan().bold();
					else
						n.fgBrightMagenta().bold(); 
					break;
				case '█':
					if(colorMap[l].length() - 10 >= x)
						n.fgCyan().boldOff();
					else
						n.fgMagenta().boldOff();
					break;
				default:
					n.fg(Color.WHITE);
				}
				n.a(logo[l].charAt(x));
			}
			n.a('\n');
		}
		System.out.println(n.toString());
	}
	
}
