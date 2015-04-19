package slaves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SlaveReplicationClient {
	public void sendPutrequest(String req,String host,int port){
	
		
		serverHandler(host,port,req);

	}
	public String sendGetrquest(String req,String host,int port){
		return serverHandler(host,port,req);
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
