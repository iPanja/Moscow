package main.responses;

import java.util.HashSet;

import com.google.gson.annotations.Expose;

public class Player {
	public int player_id;
	public GameCharacter gc;
	@Expose
	private int role;
	
	public Player(int player_id, GameCharacter gc) {
		this.player_id = player_id;
		this.gc = gc;
	}
	
	public static Boolean isFullParty(Player[] players) { //No real use right now
		HashSet<GameCharacter> taken = new HashSet<GameCharacter>();
		for(Player p : players) {
			if(taken.contains(p.gc))
				return false;
			taken.add(p.gc);
		}
		return taken.size() == GameCharacter.values().length;
	}
	//Creates a Player[] of size (Amount of in game characters - rn 5) if one can be made, otherwise returns null
	public static Player[] createFullParty(Player[] players) {
		Player[] party = new Player[GameCharacter.values().length];
		HashSet<GameCharacter> found = new HashSet<GameCharacter>();
		for(Player p : players) {
			if(!found.contains(p.gc))
				found.add(p.gc);
			if(found.size() == GameCharacter.values().length)
				break;
		}
		if(found.size() == GameCharacter.values().length)
			return party;
		return null;
	}
}
