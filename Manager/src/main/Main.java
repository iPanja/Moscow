package main;
import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application{
	//Config
	public static String IP;
	public static int min_port = 1987;
	public static int max_port = 2000;
	public static int max_servers = 1;
	public static String SELoc = "E:\\Fletcher\\Downloads\\winserver";
	public static String SEName = "\\fnafmoba.exe";
	
	public static Boolean offlineMode = false; //-DEV
	public static int authkeySize = 32; //-DEV
	
	public static UIController _UIController = new UIController();
	public static Matchmaking _Matchmaking = new Matchmaking();
	
	
	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}
	
	@Override
	@FXML
	public void start(Stage primaryStage) {
		_UIController.setMain(this);
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
		
		startUpdateDaemonTask(this._UIController::refreshUI);
	}
	
	//https://stackoverflow.com/questions/44239194/javafx-updating-ui-from-a-thread-java-8
	public void startUpdateDaemonTask(Runnable runner) {
		Task task = new Task<Void>() {
			@Override
			protected Void call() throws Exception{
				while(true) {
					Platform.runLater(runner);
					Thread.sleep(1000);
				}
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}
	
	@Override
	public void stop() {
		for(Server s : _Matchmaking.servers){
			s.close();
		}
	}
	public void ErrorAlert(String title, String description) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText(title);
				a.setContentText(description);
				a.show();
			}
		});
		
	}
}
