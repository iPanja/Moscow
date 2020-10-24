package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javafx.scene.paint.Color;

public class Server{
	public String name;
	public String ip;
	public int port;
	
	private STP stp;
	private String authkey;
	
	public Server(String name, String ip, int port, String authkey) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.authkey = authkey;
		//Setup thread
		stp = new STP(name, ip, port, this);
		launch();
		
		Main._UIController.addServer(this);
	}
	
	//Methods
	
	//https://stackoverflow.com/questions/8496494/running-command-line-in-java
	public void launch() { //Launches a server instance
		this.stp.launch();
	} 
	public void close() {
		this.stp.close(); //Forcibly closes server
	}
	
	public int getPlayerCount() {return 0;} //Gets amount of players in lobby
	public ServerStatus serverStatus() { return stp.getServerStatus(); }
	public String getError() { return stp.getError(); }
	
	public Color getStatusColor() {
		ServerStatus ss = serverStatus();
		if(ss == ServerStatus.Error) return Color.RED;
		else if(ss == ServerStatus.Running) return Color.ORANGE;
		else if(ss == ServerStatus.Matchmaking) return Color.CYAN;
		else if(ss == ServerStatus.Ingame) return Color.LIMEGREEN;
		
		return Color.BLACK;
	}
	
	public static String genKey(int length) {
		String key = "";
		String dict = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		for(int i = 0; i < length; i++) {
			int index = random.nextInt(dict.length());
			key += dict.charAt(index);
		}
		
		return key;
	}
}
