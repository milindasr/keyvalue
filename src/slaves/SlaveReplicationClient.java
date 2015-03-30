package slaves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SlaveReplicationClient {
	public void sendPutrequest(int key,String value,String host,int port){
	
		String putString="PUT "+key+" "+value;
		serverHandler(host,port,putString);

	}
	@SuppressWarnings("finally")
	private String serverHandler(String serverName, int port,String requestString) {
		String response="";
		try
		{
		   System.out.println("CLIENT Connecting to " + serverName
		                       + " on port " + port);
		   Socket client = new Socket(serverName, port);
		   System.out.println("CLIENT Just connected to "
		                + client.getRemoteSocketAddress());
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
