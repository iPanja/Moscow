package main;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class UIController {
	//Variables
	public ArrayList<Server> servers;
	
	//FXML Properties
	@FXML
	private VBox serversBox;
	
	
	@FXML
	public void initialize() {
		//Do whatever
		this.servers = new ArrayList<Server>();
		for(int i = 1; i <= 5; i++) {
			Server temp = new Server("Server " + i, "localhost", 8080+i);
			servers.add(temp);
		}
	}
	
	public void quit() {
		//Main.primaryStage.close();
	}
	
	//Methods
	@FXML
	public void addServer(String serverName, String ip, String port, Color color) {
		Pane newServerPane;
		try {
			newServerPane = FXMLLoader.load(getClass().getResource("/resources/ServerPane.fxml"));
			((Circle) newServerPane.getChildren().get(0)).setFill(color);
			((Label) newServerPane.getChildren().get(1)).setText(serverName); //Server name label
			((Label) newServerPane.getChildren().get(2)).setText(ip + ":" + port);
			((Label) newServerPane.getChildren().get(3)).setText("0/5");
			this.serversBox.getChildren().add(newServerPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@FXML
	public void addServer(Server server) {
		this.addServer(server.name, server.ip, String.valueOf(server.port), server.getStatusColor());
	}
	
	@FXML
	public void refreshServers() {
		this.serversBox.getChildren().clear();
		for(Server s : this.servers) {
			addServer(s);
		}
	}

}
