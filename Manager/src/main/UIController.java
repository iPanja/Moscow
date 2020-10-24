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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class UIController {
	//Variables
	private Timer timer;
	private Main _main;
	
	public void setMain(Main main) {
		this._main = main;
	}
	
	//FXML Properties
	@FXML
	private VBox serversBox;
	@FXML
	private Label matchmakingLabel;
	@FXML
	private Label serversLabel;
	
	@FXML
	public void initialize() {	
		Main._Matchmaking.start();
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
	private void refreshServers() {
		this.serversBox.getChildren().clear();
		for(Server s : Matchmaking.servers) {
			addServer(s);
		}
	}
	
	@FXML
	private void refreshHeader() {
		if(_main._Matchmaking.online) {
			matchmakingLabel.setTextFill(Color.LIMEGREEN);
			matchmakingLabel.setText("Online");
		}else {
			matchmakingLabel.setTextFill(Color.RED);
			matchmakingLabel.setText("Offline");
		}
		serversLabel.setText(String.valueOf(_main._Matchmaking.servers.size()) + "/" + String.valueOf(_main.max_servers));
	}
	
	public void refreshUI() {
		this.refreshServers();
		this.refreshHeader();
	}
	
	public void ErrorAlert(String title, String description) {
		this._main.ErrorAlert(title, description);
	}
}
