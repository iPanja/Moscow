package main.responses;

import com.google.gson.annotations.Expose;

public class ResponseTemplate<T> {
	
	@Expose
	private int code;
	@Expose
	private String status;
	@Expose
	private T message;
	
	//Getters
	public int getCode() {
		return this.code;
	}
	
	public String getStatus() {
		return this.status;
	}
	//Was response successful? (code == 200)
	public boolean isSuccess() {
		return this.code == 200;
	}
	//Get contents
	public T getMessage() {
		return this.message;
	}
}
