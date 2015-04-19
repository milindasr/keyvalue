package slaves;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
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
    protected String[]     getValues=new String[3];
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
		
        String returnstring=processRequest(request);
        System.out.println("THIS IS IT"+returnstring);
        out.writeUTF(returnstring);
        output.close();
        input.close();
     
    }
    public String processRequest(String request){
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
    		return "OK";
    	}
    	else if(request.startsWith("GET")){
    		if(request.contains("::")){
    			String[] splits=request.split("::");
    			String get=splits[0];
    			Scanner sc=new Scanner(get);
    			sc.next();
    			sc.nextInt();
    			sc.close();
    			getValues[0]=readString(get);
    			for(int i=1;i<splits.length;i++){
    				Scanner sc1=new Scanner(splits[i]);
    				String host1=sc1.next();
        			int port1=sc1.nextInt();
        			sc1.close();
        			synchronized(this){
        			SlaveReplicationClient repclient1=new SlaveReplicationClient();
        			getValues[i]=repclient1.sendGetrquest(get, host1, port1);
        			}
    			}
    			String ret="";
    			for(int i=0;i<getValues.length;i++){
    				ret=ret+getValues[i];
    			}
    			return ret;
    		}
    		else{
    			return readString(request);
    		}
    	}
    	return "OK";
    }
    private String readString(String get){
    	Scanner sc=new Scanner(get);
		sc.next();
		int key=sc.nextInt();
		sc.close();
		return get(key);
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
    private String get(int key){
    	String getty="";
    	try {
    		File f = new File(path);
    		BufferedReader reader = new BufferedReader(new FileReader(f));
    		String currentLine;
			while((currentLine = reader.readLine()) != null ) {
				if(currentLine.isEmpty())
					continue;
				Scanner sc=new Scanner(currentLine);
	        	int filekey=sc.nextInt();
	        	String filevalue=sc.next();
	        	int version=sc.nextInt();
	        	sc.close();
	        	if(filekey==key){
	        		getty=getty+filevalue+" "+version;
	        		return getty;
	        	}
			}
			reader.close();
			return "-1 -1";
    } catch (IOException e) {
		
		e.printStackTrace();
	}
    	return "-1 -1";
    }
    private void put(int key,String value){
    	boolean status=false;
    	try {
    		File f = new File(path);
    		String tempString=path+serverPort;
			File temp=new File(tempString);
			if(!f.exists()){
				f.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(f));
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
			String currentLine;
			while((currentLine = reader.readLine()) != null ) {
				if(currentLine.isEmpty())
					continue;
				Scanner sc=new Scanner(currentLine);
	        	int filekey=sc.nextInt();
	        	String filevalue=sc.next();
	        	int version=sc.nextInt();
	        	sc.close();
	        	if(filekey==key){
	        		version++;
	        		writer.write("\n"+filekey+" "+filevalue+" "+version);
	        		status=true;
	        	}
	        	else{
	        		writer.write("\n"+filekey+" "+filevalue+" "+version);
	        	}
			}
			if(!status){
				writer.write("\n"+key+" "+value+" "+0);
			}
			reader.close();
			writer.close();
			f.delete();
        	temp.renameTo(f);
			System.out.println(key+" "+value+"\nwritten to primary database"+path);
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    private void put(int key,String value,String host,String port){
    	try {
			FileWriter file=new FileWriter(secpath,true);
			PrintWriter pw=new PrintWriter(file);
			pw.print("\n"+key+" "+value+" "+host+" "+port+"\n");
			System.out.println(key+" "+value+" "+host+" "+port+"\n written to secondary database"+secpath);
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
