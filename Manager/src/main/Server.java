package main;

import java.io.IOException;

import javafx.scene.paint.Color;

public class Server{
	public String name;
	public String ip;
	public int port;
	private Boolean isRunning;
	private Process process;  //Move to thread class
	private Thread thread;
	
	public Server(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.isRunning = false;
		Main._UIController.addServer(this);
	}
	
	//Methods
	public void reset() {} //Resets server instance, launching a new command prompt window and clearing the lobby
	
	//https://stackoverflow.com/questions/8496494/running-command-line-in-java
	public void launch() { //Launches a server instance
		if(this.isRunning) return;
		
		Runtime runtime = Runtime.getRuntime();
		try {
			this.process = runtime.exec("java -jar server.jar " + name + " " + ip + " " + port);
			this.isRunning = true;
		} catch (IOException e) {
			System.out.print("Failed to start server: " + this.name + " on " + ip + ":" + port);
			this.isRunning = false;
			e.printStackTrace();
		}
	} 
	public void close() {
		this.process.destroy(); //Forcibly closes server
	}
	
	public int getPlayerCount() {return 0;} //Gets amount of players in lobby
	public Boolean isRunning() {return false;} //Gets status of server
	
	public Color getStatusColor() {
		if(!this.isRunning) return Color.RED;
		return Color.GREEN;
	}

}
