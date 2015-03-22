package loadbalancer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

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
           // System.out.println(request+"***************");
            processRequest(request,output);
            output.close();
            input.close();
           
        } catch (IOException e) {
          
            e.printStackTrace();
        }

	}
	public void processRequest(String request,OutputStream output){
		Scanner sc=new Scanner(request.trim());
		String type=sc.next();
		if(type.equals("JOIN")){
			String host=sc.next();
			int port=sc.nextInt();
			join(host,port);
			sc.close();
		}
		else if(type.equals("GETHOST")){
			int key=sc.nextInt();
			String hostinfo="GETHOST "+getHostName(key);
			try {
				DataOutputStream out =
						new DataOutputStream(output);
				out.writeUTF(hostinfo);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void join(String host,int port){
		Data.addNode(host,port);
	}
	private int getHashValue(int key){
		return 1;
	}
	public String getHostName(int key){
		int hash=getHashValue(key);
		return Data.getNodeString(hash);
	}
}
