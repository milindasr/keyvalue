package slaves;

import java.io.DataInputStream;
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

    public SlaveServer(int port){
        this.serverPort = port;
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
        
        processRequest(request);
        output.close();
        input.close();
     
    }
    public void processRequest(String request){
    	Scanner sc=new Scanner(request.trim());
		String type=sc.next();
		if(type.equals("PUT")){
			int key=sc.nextInt();
			String value=sc.next();
			put(key,value);
			sc.close();
		}
	}
    private void put(int key,String value){
    	try {
			FileWriter file=new FileWriter("C:\\Users\\Milind\\Desktop\\dbms code\\inteview\\db.txt",true);
			PrintWriter pw=new PrintWriter(file);
			pw.print(key+" "+value+"\n");
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
