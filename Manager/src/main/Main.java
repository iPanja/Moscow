package main;
import java.io.IOException;

import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{
	//Config
	public static String IP;
	public static int min_port;
	public static int max_port;
	
	public static UIController _UIController = new UIController();
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
	
	@Override
	@FXML
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Moscow Server Manager");
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/Scene.fxml"));
			loader.setController(this._UIController);
			VBox box = loader.load();
			Scene scene = new Scene(box);
			primaryStage.setScene(scene);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		primaryStage.show();
	}
}
