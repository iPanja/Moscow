package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;

import com.google.gson.Gson;

import main.responses.Player;

public class Matchmaking extends Thread{
	private CloseableHttpClient httpClient;
	private Gson gson;
	private String host = "http://localhost/radiant-site/public/";
	public static Boolean online = true;
	
	private String ip = "localhost";
	private String serverSecret = "notMyActualSecret";
	
	public static HashSet<Integer> takenPorts = new HashSet<Integer>();
	public static ArrayList<Server> servers = new ArrayList<Server>();
	
	public Matchmaking() {
		this.httpClient = HttpClients.createDefault();
		this.gson = new Gson();
	}
	
	public void run() {
		//Run matchmaking unless turned off for some reason in the UI
		while(this.online) {
			try {
				System.out.println("Looking for players...");
				//Fetch players from API
				Player[] players = getPlayersInQueue();
				if(players == null) break; //Server issue
				
				Player[] party = Player.createFullParty(players);
				
				if(party == null) { //A full party can not be found, wait and check again
					System.out.println("No party can be created");
					Thread.sleep(5000);
					continue;
				}
				/*
				 * Party found
				 */
				
				//Generate authkey
				String authkey = Server.genKey(32);
				//Create server
				int port = findOpenPort();
				if(port == -1) { //No open ports available, or too many servers running, wait and check again
					System.out.println("No open ports/too many servers running");
					Thread.sleep(5000);
					continue;
				}
				Server temp = new Server("Server " + port, this.ip, port, authkey);
				this.servers.add(temp);
				//Send users ip:port and authkey
				Form formParams = Form.form();
				formParams.add("authkey", authkey);
				formParams.add("ip", ip);
				formParams.add("port", String.valueOf(port));
				try {
					postRequest("/mm/setup", formParams);
				} catch (IOException e) {
					System.out.println("API down, could not make POST request to /mm/setup");
					e.printStackTrace();
				}
			}catch(InterruptedException e) {
				System.out.println("Couldn't Thread.sleep() in Matchmaking");
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * Networking
	 */
	
	//https://www.vogella.com/tutorials/ApacheHttpClient/article.html
	//http://hc.apache.org/httpcomponents-client-ga/quickstart.html
	public String getRequest(String endpoint) throws IOException {
		Content content = Request.get(this.host + endpoint).execute().returnContent();
		return content.asString();
	}
	public String postRequest(String endpoint, Form formParams) throws IOException {
		Content content = Request.post(this.host + endpoint).bodyForm(formParams.build()).execute().returnContent();
		return content.asString();
	}
	public Player[] getPlayersInQueue(){
		Form formParams = Form.form();
		formParams.add("secret", this.serverSecret);
		try {
			String rawJson = postRequest("/mm/fetch", formParams);
			Player[] players = this.gson.fromJson(rawJson, Player[].class);
			return players;
		} catch (IOException e) {
			System.out.println("Something is wrong with the API");
			Main._UIController.ErrorAlert("Could not reach API", e.toString());
			this.online = false; //Turn matchmaking off
			e.printStackTrace();
		}
		return null;
	}
	private int findOpenPort() {
		if(servers.size() + 1 >= Main.max_servers) return -1;
		
		for(int i = Main.min_port; i < Main.max_port; i++) {
			if(!takenPorts.contains(i))
				return i;
		}
		return -1;
	}
	
	public static void onGameOver(Server server) {
		for(Server s : Matchmaking.servers) {
			if(s.port == server.port) {
				Matchmaking.takenPorts.remove(s.port);
				Matchmaking.servers.remove(server);
				break;
			}
		}
	}
}
