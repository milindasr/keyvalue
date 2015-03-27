package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class StorageClient {
	public void sendPutrequest(int key,String value){
		String hostString="GETHOST "+key;
		String slaveinfo=serverHandler("localhost",9000,hostString);
		Scanner sc=new Scanner(slaveinfo);
		sc.next();
		String slavehost=sc.next();
		int slaveport=sc.nextInt();
		sc.close();
		String putString="PUT "+key+" "+value;
		serverHandler(slavehost,slaveport,putString);
		
	}
	public void sendGetRequest(int key){
		String hostString="GETHOST "+key;
		String slaveinfo=serverHandler("localhost",9000,hostString);
		Scanner sc=new Scanner(slaveinfo);
		sc.next();
		String slavehost=sc.next();
		int slaveport=sc.nextInt();
		sc.close();
		String putString="GET "+key;
		serverHandler(slavehost,slaveport,putString);
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
	public static void main(String args[]){
		StorageClient sc=new StorageClient();
		sc.sendPutrequest(1, "choooooo");
	}
}
