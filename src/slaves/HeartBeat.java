package slaves;


import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HeartBeat extends Thread{
	private int port;
	private String host;
	
	static private long TmHB = 15000;
	String masterhost="";
	int masterport;
	
	public HeartBeat (String host,int port,String masterhost,int masterport)
			{ 
				this.host=host;
				this.port = port;
				this.masterhost=masterhost;
				this.masterport=masterport;
			}
	@SuppressWarnings("resource")
	public void run()
	{
		String request="KEEP_ALIVE "+host+" "+port;
		Socket client = null;
		while(true)
		{
		 try {
			client = new Socket(masterhost, masterport);
			OutputStream outToServer = client.getOutputStream();
			DataOutputStream out =
			                 new DataOutputStream(outToServer);

			out.writeUTF(request);
			
			sleep(TmHB);
		}
		 catch (IOException e){System.err.println("Server can't send heartbeat");
         System.exit(-1);
		 }
		 catch (InterruptedException e){System.out.println(e);}
		}
	}// e
}