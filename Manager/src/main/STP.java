package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;

public class STP extends Thread{
	private String name;
	private String ip;
	private int port;
	public ServerStatus status;
	private Process process;
	private String error;
	
	private Server _server;
	
	public STP(String name, String ip, int port, Server server) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.status = ServerStatus.Running;
		this._server = server;
	}
	
	public void run() {
		//Everything in here is run asynchronously
		this.launch();
		
		//Every 5 seconds check for error/closure
		while(this.isAlive()) {
			try {
				if(getError().length() != 0) {
					this.status = ServerStatus.Error;
				}
				Thread.sleep(5000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Game is over/process closed
		this.status = ServerStatus.Finished;
		Matchmaking.onGameOver(this._server);
	}
	
	public void launch() {
		if(this.status == ServerStatus.Running) return;
		System.out.println("[Opening] " + this.name + " on " + this.ip + ":" + this.port);
		
		if(Main.offlineMode) return; //--DEV
		
		Runtime runtime = Runtime.getRuntime();
		try { //Attempt to start server executable with specified port
			ProcessBuilder pb = new ProcessBuilder(Main.SELoc + Main.SEName, "name", "port");
			pb.redirectErrorStream(true);
			this.process = pb.start();
		}catch(Exception e) {
			System.out.println("Failed to start server: " + this.name + " on " + this.ip + ":" + this.port);
			this.status = ServerStatus.Error;
			e.printStackTrace();
			return;
		}
		this.error = this.getErrorStream();
		if(this.error != null) { //Error
			this.status = ServerStatus.Error;
		}
		
		this.status = ServerStatus.Matchmaking;
	}
	public ServerStatus getServerStatus() {
		return this.status;
	}
	public String getError() {
		if(this.error == null) return "";
		return this.error;
	}
	public void close() { //Forcibly close process
		System.out.println("[Closing] " + this.name + " on " + this.ip + ":" + this.port);
		this.status = ServerStatus.Finished;
		this.process.destroyForcibly();
	}
	
	public String getErrorStream() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
		StringBuilder builder = new StringBuilder();
		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		}catch(Exception e) { e.printStackTrace(); }
		
		return builder.toString();
	}
}
