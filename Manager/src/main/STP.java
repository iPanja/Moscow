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
	
	
	
	public STP(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.status = ServerStatus.Offline;
	}
	
	public void run() {
		//Everything in here is run asynchronously
		this.launch();
		
		//Tell API this server is up and running
		
		//Every 5 seconds check API for players
		while(this.status != ServerStatus.Offline) {
			//Code
			System.out.println("[Server] " + this.name + " pinging...");
			//Wait 5 seconds
			try {
				Thread.sleep(5000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void launch() {
		if(this.status != ServerStatus.Offline) return;
		System.out.println("[Opening] " + this.name + " on " + this.ip + ":" + this.port);
		Runtime runtime = Runtime.getRuntime();
		try { //Attempt to start server executable with specified port
			ProcessBuilder pb = new ProcessBuilder(Main.SELoc + Main.SEName, "name", "port");
			pb.redirectErrorStream(true);
			this.process = pb.start();
		}catch(Exception e) {
			System.out.println("Failed to start server: " + this.name + " on " + this.ip + ":" + this.port);
			this.status = ServerStatus.Offline;
			e.printStackTrace();
			return;
		}
		this.error = this.getErrorStream();
		if(this.error != null) { //Error
			this.status = ServerStatus.Offline;
		}
		this.status = ServerStatus.Running;
		//Check if port is open, then set status to Online
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
		this.status = ServerStatus.Offline;
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
