package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class StorageClient {
	String masterhost="";
	int masterport;
	public void sendPutrequest(int key,String value){
		String hostString="GETHOST "+key;
		String putString="";
		String slavehost = null;
		int slaveport = 0;
		String slaveinfo=serverHandler(masterhost,masterport,hostString);
		slaveinfo=slaveinfo.substring(8);
		String[] slaves=slaveinfo.split("::");

		for(int i=0;i<3;i++){
			Scanner sc=new Scanner(slaves[i]);
			if(i==0){
				slavehost=sc.next();
				slaveport=sc.nextInt();
				String flag=sc.next();
				if(flag.equals("T")){
					putString=putString+"PUT "+key+" "+value;
				}
				else{
					String achost=sc.next();
					String acport=sc.next();
					putString=putString+"PUTS "+key+" "+value+" "+achost+" "+acport;
				}
			}
			else {
				putString=putString+ "::"+ slaves[i];
			}
			sc.close();
		}


		System.out.println(serverHandler(slavehost,slaveport,putString));

	}
	public void sendGetRequest(int key){
		String hostString="GETHOSG "+key;
		String getString="";
		String slavehost = null;
		int slaveport = 0;
		String slaveinfo=serverHandler(masterhost,masterport,hostString);
		System.out.println("************************"+slaveinfo);
		slaveinfo=slaveinfo.substring(8);
		String[] slaves=slaveinfo.split("::");
		for(int i=0;i<slaves.length;i++){
			Scanner sc=new Scanner(slaves[i]);
			if(i==0){
				slavehost=sc.next();
				slaveport=sc.nextInt();
				getString=getString+"GET "+key;
			}
			else {
				getString=getString+ "::"+ slaves[i];
			}
			sc.close();
		}

        
		System.out.println(serverHandler(slavehost,slaveport,getString));
	

	}
	
	@SuppressWarnings("finally")
	private String serverHandler(String serverName, int port,String requestString) {
		String response="";
		try
		{
		  // System.out.println("CLIENT Connecting to " + serverName
		  //                     + " on port " + port);
		   Socket client = new Socket(serverName, port);
		 //  System.out.println("CLIENT Just connected to "
		 //               + client.getRemoteSocketAddress());
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
	public static void main(String args[]) throws IOException{
		String input;
		StorageClient sc=new StorageClient();
		sc.masterhost=args[0];
		sc.masterport=Integer.parseInt(args[1]);
		
		while(true)
		{
			System.out.println("Enter request");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			input = br.readLine();
			if(input.startsWith("put")){
				String[] parts=input.split(" ");
				int key= Integer.parseInt(parts[1]);
				sc.sendPutrequest(key, parts[2]);
			}
			else if(input.startsWith("get")){
				String[] parts=input.split(" ");
				int key= Integer.parseInt(parts[1]);
				sc.sendGetRequest(key);
			}
			else{
				break;
			}
				
		}
		
	}

}
