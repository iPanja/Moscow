package main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.sun.media.jfxmediaimpl.platform.Platform;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class UIController {
	//Variables
	public ArrayList<Server> servers;
	private Timer timer;
	private CloseableHttpClient httpClient;
	
	//FXML Properties
	@FXML
	private VBox serversBox;
	
	@FXML
	public void initialize() {
		this.httpClient = HttpClients.createDefault();	
		
		//Creates servers between the ports Main.min_port and Main.max_port, not exceeding Main.max_servers
		this.servers = new ArrayList<Server>();
		for(int i = Main.min_port; i <= Main.max_port && (i-Main.min_port) < Main.max_servers; i++) {
			Server temp = new Server("Server " + i, "localhost", i);
			servers.add(temp);
		}
	}
	
	public void quit() {
		//Main.primaryStage.close();
	}
	
	//Methods
	@FXML
	public void addServer(String serverName, String ip, String port, Color color, String error) {
		Pane newServerPane;
		try {
			newServerPane = FXMLLoader.load(getClass().getResource("/resources/ServerPane.fxml"));
			((Circle) newServerPane.getChildren().get(0)).setFill(color);
			((Label) newServerPane.getChildren().get(1)).setText(serverName); //Server name label
			((Label) newServerPane.getChildren().get(2)).setText(ip + ":" + port);
			((Label) newServerPane.getChildren().get(3)).setText("0/5");
			if(error.length() == 0) {
				newServerPane.getChildren().get(4).setOpacity(0.0);
			}else {
				Button errButton = (Button) newServerPane.getChildren().get(4);
				errButton.setOpacity(1.0);
				errButton.setOnAction(new EventHandler<ActionEvent>(){
					@Override
					public void handle(ActionEvent event) {
						try {
							PrintStream out = new PrintStream(new FileOutputStream("error.txt"));
							out.print(error);
							out.flush();
							out.close();
							Desktop.getDesktop().edit(new File("error.txt"));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				});
			}
			this.serversBox.getChildren().add(newServerPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void addServer(Server server) {
		this.addServer(server.name, server.ip, String.valueOf(server.port), server.getStatusColor(), server.getError());
	}
	
	@FXML
	public void refreshServers() {
		this.serversBox.getChildren().clear();
		for(Server s : this.servers) {
			addServer(s);
		}
	}
	
	public void refreshUI() {
		this.refreshServers();
	}
	
	//Networking
	
	//https://www.vogella.com/tutorials/ApacheHttpClient/article.html
	//http://hc.apache.org/httpcomponents-client-ga/quickstart.html
	public String getRequest(String address) {
		HttpGet request = new HttpGet("http://localhost/radiant-site/public/");
		CloseableHttpResponse response;
		String all = "";
		try {
			response = this.httpClient.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while((line = rd.readLine()) != null) {
				all += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return all;
	}
}
