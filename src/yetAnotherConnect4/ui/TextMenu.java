//Ian Charrett-Dykes
package yetAnotherConnect4.ui;

import java.util.LinkedList;
import java.util.Scanner;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

import yetAnotherConnect4.sounds.PlaySound;

public class TextMenu {
	private LinkedList<Option> options = new LinkedList<>();
	private int widestOption = 0;
	private final String title;
	private Scanner s = new Scanner(System.in);
	
	public TextMenu(String title) {
		this.title = title;
		widestOption = title.length();
	}
	
	
	public TextMenu addOption(Color color, boolean bright, String text, Runnable action) {
		options.add(new Option(text, action, color, bright));
		widestOption = Math.max(widestOption, text.length());
		return this;
	}
	
	public TextMenu addOption(String text, Runnable action) {
		return addOption(Color.WHITE, true, text, action);
	}
	
	
	
	public void showAndRun() {
		show();
		System.out.println("Choose an option and press ENTER...");
		System.out.print("> ");
		
		Option option = null;
		do {
			String line = s.nextLine();
			try {
				int selection = Integer.parseInt(line);
				if( selection <=0 || selection > options.size()) throw new NumberFormatException();
				option = options.get(selection-1);
			}catch (NumberFormatException e) {
				System.out.println("Invalid input");
				PlaySound.playDorp();
			}
		}while(option == null);
		PlaySound.playNextBell();
		option.r.run();
	}
	
	public static String r(String a, int i) {
		StringBuilder u = new StringBuilder();
		for(; i>=1; i--) u.append(a);
		return u.toString();
	}
	
	public void show() {
		int opWid = (options.size()+") ").length();
		StringBuilder sb = new StringBuilder();
		sb.append(Ansi.ansi().fgBrightGreen());
		sb.append('+');
		sb.append(r("=",opWid + widestOption));
		sb.append("+\n");
		
		sb.append('|');
		sb.append(Ansi.ansi().reset().bgBright(Color.BLACK).fgBrightYellow());
		sb.append(title);
		sb.append(r(" ",opWid + widestOption -title.length()));
		sb.append(Ansi.ansi().reset().bg(Color.BLACK).fgBrightGreen());
		sb.append("|\n");
		
		sb.append('+');
		sb.append(r("=",opWid + widestOption));
		sb.append("+\n");
		
		for (int i = 0; i<options.size(); i++) {
			sb.append('|');
			sb.append(Ansi.ansi().fgBrightCyan());
			String n = String.valueOf(i+1);
			sb.append(r(" ",opWid-2-n.length()));
			sb.append(n);
			sb.append(") ");
			//sb.append(Ansi.ansi().fgBright(Color.WHITE));
			sb.append(options.get(i).toString());
			sb.append(r(" ",widestOption-options.get(i).text.length()));
			sb.append(Ansi.ansi().fgBrightGreen());
			sb.append("|\n");
		}
		sb.append('+');
		sb.append(r("=",opWid + widestOption));
		sb.append("+");
		sb.append(Ansi.ansi().reset().fgBright(Color.WHITE));
		System.out.println(sb);
	}
	
	private class Option {
		final String text;
		Color color;
		boolean bright;
		final Runnable r;
		public Option(String text, Runnable r, Color color, boolean bright) {
			this.text = text;
			this.r = r;
			this.color = color;
			this.bright = bright;
		}
		@Override
		public String toString() {
			if(bright)
				return Ansi.ansi().bold().fgBright(color).a(text).reset().toString();
			else
				return Ansi.ansi().boldOff().fg(color).a(text).reset().toString();
		}
		
	}

	public void anyKey() {
		System.out.println("Press any key to continue...");
		while(!s.hasNextLine()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}


	
}
