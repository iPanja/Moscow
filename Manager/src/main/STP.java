package main;

import java.io.IOException;

public class STP extends Thread{
	private String name;
	private String ip;
	private int port;
	public ServerStatus status;
	private Process process;  //Move to thread class
	
	public STP(String name, String ip, int port) {
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.status = ServerStatus.Offline;
	}
	
	public void run() {
		//Everything in here is run asynchronously
		this.launch();
	}
	
	public void launch() {
		if(this.status != ServerStatus.Offline) return;
		
		this.status = ServerStatus.Running; //For testing purposes
		
		/*
		Runtime runtime = Runtime.getRuntime();
		try {
			this.process = runtime.exec("java -jar server.jar " + name + " " + ip + " " + port);
			this.isRunning = true;
		} catch (IOException e) {
			System.out.print("Failed to start server: " + this.name + " on " + ip + ":" + port);
			this.isRunning = false;
			e.printStackTrace();
		}
		*/
	}
	public ServerStatus getServerStatus() {
		return this.status;
	}
	public void close() { //Forcibly close process
		this.process.destroy();
	}
}
