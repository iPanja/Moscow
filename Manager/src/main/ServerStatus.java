package main;

public enum ServerStatus {
	Offline, //stp.launch() has yet to be called
	Running, //stp.launch() and subsequently the process has been called
	Online //The process has registered itself with the API
}
