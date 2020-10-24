package main;

public enum ServerStatus {
	Error, //Process had an error
	Running, //stp.launch() and subsequently the process has been called
	Matchmaking, //The process has registered itself with the API
	Ingame, //Players have been found and sent the authkey to join
	Finished //Game over/forcibly closed
}
