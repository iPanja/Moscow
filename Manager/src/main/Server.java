package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.scene.paint.Color;

public class Server{
	public String name;
	public String ip;
	public int port;
	
	private STP stp;
	
	public Server(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		//Setup thread
		stp = new STP(name, ip, port);
		stp.start();
		
		Main._UIController.addServer(this);
	}
	
	//Methods
	public void reset() {} //Resets server instance, launching a new command prompt window and clearing the lobby
	
	//https://stackoverflow.com/questions/8496494/running-command-line-in-java
	public void launch() { //Launches a server instance
		this.stp.launch();
		Main._UIController.refreshServers();
	} 
	public void close() {
		this.stp.close(); //Forcibly closes server
	}
	
	public int getPlayerCount() {return 0;} //Gets amount of players in lobby
	public ServerStatus serverStatus() { return stp.getServerStatus(); }
	public String getError() { return stp.getError(); }
	
	public Color getStatusColor() {
		if(serverStatus() == ServerStatus.Online) return Color.LIMEGREEN;
		else if(serverStatus() == ServerStatus.Running) return Color.YELLOW;
		return Color.RED;
	}
}
