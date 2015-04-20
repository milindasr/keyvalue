package loadbalancer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
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
			try {
				DataOutputStream out =
						new DataOutputStream(output);
				out.writeUTF("OK");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		else if(type.equals("GETHOSG")){
			int key=sc.nextInt();
			String hostinfo="GETHOSG "+getanyHost(key);
			try {
				DataOutputStream out =
						new DataOutputStream(output);
				out.writeUTF(hostinfo);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("ISALIVE")){
			String host=sc.next();
			int port=sc.nextInt();
			String status="";
			if(Data.isActive(host, port)){
				status=status+"ALIVE";
			}
			else{
				status=status+"DEAD";
			}
			try {
				DataOutputStream out =
						new DataOutputStream(output);
				out.writeUTF(status);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(type.equals("KEEP_ALIVE")){
			String host=sc.next();
			int port=sc.nextInt();
			
			updatetime(host,port);
			sc.close();
			try {
				DataOutputStream out =
						new DataOutputStream(output);
				out.writeUTF("OK");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void updatetime(String host,int port){
		Data.setActive(host,port);
	}
	public void join(String host,int port){
		Data.addNode(host,port);
	}
	private int getHashValue(int key){
		return key;
	}
	public String getanyHost(int key){
		int hash=getHashValue(key);
		StringBuffer hostString=new StringBuffer();
		for(int i=0;i<3;i++){
			if(Data.isActive(hash)){
				hostString.append(Data.getNodeString(hash));
				hostString.append("::");
			}
			hash++;
		}
		return hostString.toString();
	}
	public String getHostName(int key){
		int hash=getHashValue(key);
		StringBuffer hostString=new StringBuffer();
		ArrayList<Integer> livehashes = new ArrayList<Integer>();
		ArrayList<Integer> truehashes = new ArrayList<Integer>();
		HashMap<Integer,Integer> hashhash=new HashMap<Integer,Integer>();
		int temphash=hash;
		for(int i=0;i<3;i++){
			truehashes.add(temphash++);
		}
		int count=0;
		temphash=hash;
		while(count<3){
			if(Data.isActive(temphash)){
				livehashes.add(temphash);
				count++;
			}
			temphash++;
		}
		for(Integer i:livehashes){
			if(truehashes.contains(i)){
				hashhash.put(i, -1);
				//livehashes.remove(i);
				//truehashes.remove(i);
			}
		}
		for(Entry<Integer,Integer> e: hashhash.entrySet()){
			if(e.getValue()==-1){
				livehashes.remove(e.getKey());
				truehashes.remove(e.getKey());
			}
		}
		for(int i=0;i<livehashes.size();i++){
			hashhash.put(livehashes.get(i),truehashes.get(i));
		}
		for(Entry<Integer,Integer> e: hashhash.entrySet()){
			hostString.append(Data.getNodeString(e.getKey()));
			if(e.getValue()==-1){
				hostString.append(" T ");
				hostString.append(Data.getNodeString(e.getKey()));
			}
			else{
				hostString.append(" F ");
				hostString.append(Data.getNodeString(e.getValue()));
			}
			hostString.append("::");
		}
	
		System.out.println(hostString);
		return hostString.toString();
		
	}
	
	
	
}
