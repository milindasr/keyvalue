package slaves;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SecondaryScanner implements Runnable{
	
	private String secpath="";
	public SecondaryScanner(String secpath){
		this.secpath=secpath;
	}

	@Override
	public void run() {
		SlaveMasterClient masterclient=new SlaveMasterClient();
		while(true){
			try {
				Thread.sleep(10000);
				File f = new File(secpath);
				File temp=new File("C:\\Users\\Milind\\Desktop\\help\\temp.txt");
				if(!f.exists()){
					continue;
				}
				BufferedReader reader = new BufferedReader(new FileReader(f));
				BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
				String currentLine;
				while((currentLine = reader.readLine()) != null) {
					if(currentLine.isEmpty())
						continue;
					Scanner sc=new Scanner(currentLine);
		        	int key=sc.nextInt();
		        	String value=sc.next();
		        	String host=sc.next();
		        	int port=sc.nextInt();
		        	sc.close();
		        	if(masterclient.sendIsAlive(host, port)){
		        		String request="PUT "+key+" "+value;
		        		SlaveReplicationClient src=new SlaveReplicationClient();
		        		src.sendPutrequest(request, host, port);
		        		System.out.println("sent to the true owner");
		        		writer.write("");
		        	}
		        	else{
		        		writer.write(currentLine);
		        		System.out.println("still dead");
		        	}
		        	
		        }
				writer.close();
	        	reader.close();
	        	f.delete();
	        	temp.renameTo(f);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			}
			
		}
		
	}

}
