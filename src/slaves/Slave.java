package slaves;

public class Slave {
	SlaveMasterClient masterClient;
	SlaveServer server;
	HeartBeat heartBeatmsg;

	public static void main(String args[]){
		new Slave(args[0],Integer.parseInt(args[1]),args[2],args[3]);
	}
	public Slave(String host,int port,String path,String secpath){
		masterClient=new SlaveMasterClient();
		server=new SlaveServer(port,path,secpath);
		new Thread(server).start();
		masterClient.sendJoinRequest(host,port);
		heartBeatmsg = new HeartBeat(host,port);
		new Thread(heartBeatmsg).start();

	}
}
