package slaves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SlaveMasterClient {
	public void sendJoinRequest(String host,int port){
		String joinString="JOIN "+ host +" " + port;
		serverHandler("localhost",9000,joinString);
	}
	public boolean sendIsAlive(String host,int port){
		String joinString="ISALIVE "+ host +" " + port;
		String status=serverHandler("localhost",9000,joinString);
		if(status.equals("ALIVE"))
			return true;
		else{
			return false;
		}
	}
	@SuppressWarnings("finally")
	private String serverHandler(String serverName, int port,String requestString) {
		String response="";
		try
		{
		  
		   Socket client = new Socket(serverName, port);
		   System.out.println("slave client sent "+requestString+" at address "+ client.getRemoteSocketAddress());
		   OutputStream outToServer = client.getOutputStream();
		   DataOutputStream out =
		                 new DataOutputStream(outToServer);

		   out.writeUTF(requestString);
		   InputStream inFromServer = client.getInputStream();
		   DataInputStream in =
		                  new DataInputStream(inFromServer);
		   response=in.readUTF();
		   client.close();
		   return response;
		}catch(IOException e)
		{
		   e.printStackTrace();
		}
		finally{
			return response;
		}
	}
}
