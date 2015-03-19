package loadbalancer;

import java.net.Socket;

public class Worker implements Runnable{
	protected Socket clientSocket = null;
	protected String serverText   = null;

	public Worker(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText   = serverText;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	public void join(String host){
		
	}
	private int getHashValue(int key){
		return 0;
	}
	public String getHostName(int key){
		return null;
	}
}
