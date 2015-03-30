package slaves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Single threaded server to process get and put requests
 * thread logic from http://tutorials.jenkov.com/java-multithreaded-servers/singlethreaded-server.html
 * @author Milind
 *
 */
public class SlaveServer implements Runnable{
	protected int          serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected String       path="";

    public SlaveServer(int port,String path){
        this.serverPort = port;
        this.path=path;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            try {
                processClientRequest(clientSocket);
            } catch (IOException e) {
               
            }
        }
        
        System.out.println("Server Stopped.");
    }

    private void processClientRequest(Socket clientSocket)
    throws IOException {
        InputStream  input  = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();
        DataInputStream in =
                new DataInputStream(input);
        String request=in.readUTF();
        DataOutputStream out =
				new DataOutputStream(output);
		out.writeUTF("OK");
		
        processRequest(request);
        output.close();
        input.close();
     
    }
    public void processRequest(String request){
    	if(request.startsWith("PUT")){
    		if(request.contains("::")){
    			String[] splits=request.split("::");
    			String put=splits[0];
    			Scanner sc=new Scanner(put);
    			sc.next();
    			int key=sc.nextInt();
    			String value=sc.next();
    			sc.close();
    			put(key,value);
    			String nextNode=splits[1];
    			Scanner sc1=new Scanner(nextNode);
    			String host1=sc1.next();
    			int port1=sc1.nextInt();
    			String nextNode1=splits[2];
    			sc1.close();
    			Scanner sc2=new Scanner(nextNode1);
    			String host2=sc2.next();
    			int port2=sc2.nextInt();
    			sc2.close();
    			SlaveReplicationClient repclient1=new SlaveReplicationClient();
    			repclient1.sendPutrequest(key, value, host1, port1);
    			SlaveReplicationClient repclient2=new SlaveReplicationClient();
    			repclient2.sendPutrequest(key, value, host2, port2);
    			
    			
    		}
    		else{
    			Scanner sc=new Scanner(request);
    			sc.next();
    			int key=sc.nextInt();
    			String value=sc.next();
    			sc.close();
    			put(key,value);
    		}
    	}
    }
    private void put(int key,String value){
    	try {
			FileWriter file=new FileWriter(path,true);
			PrintWriter pw=new PrintWriter(file);
			pw.print(key+" "+value+"\n");
			pw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
