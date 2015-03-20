package loadbalancer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Worker threads which work on each thread
 * thread logic from http://tutorials.jenkov.com/java-multithreaded-servers/multithreaded-server.html
 * @author Milind
 *
 */
public class Worker implements Runnable{
	protected Socket clientSocket = null;
	protected String serverText   = null;

	public Worker(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText   = serverText;
	}

	@Override
	public void run() {
		try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            DataInputStream in =
                    new DataInputStream(input);
            String request=in.readUTF();
            processRequest(request);
            output.close();
            input.close();
           
        } catch (IOException e) {
          
            e.printStackTrace();
        }

	}
	public void processRequest(String request){
		
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
