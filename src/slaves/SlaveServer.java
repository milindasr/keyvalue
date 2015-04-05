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
    protected String 	   secpath="";

    public SlaveServer(int port,String path,String secpath){
        this.serverPort = port;
        this.path=path;
        this.secpath=secpath;
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
    			writetoPrimRSec(put);
    			sc.close();
    			String nextNode=splits[1];
    			Scanner sc1=new Scanner(nextNode);
    			String host1=sc1.next();
    			int port1=sc1.nextInt();
    			sc1.close();
    			String req1=createPutrequestString(key,value, nextNode);
    			String nextNode1=splits[2];
    			Scanner sc2=new Scanner(nextNode1);
    			String host2=sc2.next();
    			int port2=sc2.nextInt();
    			sc2.close();
    			String req2=createPutrequestString(key,value, nextNode1);
    			SlaveReplicationClient repclient1=new SlaveReplicationClient();
    			repclient1.sendPutrequest(req1, host1, port1);
    			SlaveReplicationClient repclient2=new SlaveReplicationClient();
    			repclient2.sendPutrequest(req2, host2, port2);
    			
    			
    		}
    		else{
    			writetoPrimRSec(request);
    			
    		}
    	}
    }
    private void writetoPrimRSec(String put){
    	Scanner sc=new Scanner(put);
		sc.next();
		int key=sc.nextInt();
		String value=sc.next();
		
		if(put.startsWith("PUTS")){
			String host=sc.next();
			String port=sc.next();
			put(key,value,host,port);
		}
		else{
			put(key,value);
		}
		sc.close();
    }
    private String createPutrequestString(int key,String value,String nextNode){
    	Scanner sc1=new Scanner(nextNode);
		sc1.next();
		sc1.nextInt();
		String flag=sc1.next();
		String returns="";
		if(flag.equals("T")){
			returns=returns+"PUT "+key+" "+value;
		}
		else{
			returns=returns+"PUTS "+key+" "+value+" "+sc1.next()+" "+sc1.next();
		}
		sc1.close();
		return returns;
    }
    private void put(int key,String value){
    	try {
			FileWriter file=new FileWriter(path,true);
			PrintWriter pw=new PrintWriter(file);
			pw.print(key+" "+value+"\n");
			System.out.println(key+" "+value+"\nwritten to primary database");
			pw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    private void put(int key,String value,String host,String port){
    	try {
			FileWriter file=new FileWriter(secpath,true);
			PrintWriter pw=new PrintWriter(file);
			pw.print(key+" "+value+" "+host+" "+port+"\n");
			System.out.println(key+" "+value+" "+host+" "+port+"\n written to secondary database");
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
            System.out.println("Slave running and accepting requests at port "+this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
