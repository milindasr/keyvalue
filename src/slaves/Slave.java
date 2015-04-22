package slaves;

public class Slave {
	SlaveMasterClient masterClient;
	SlaveServer server;
	HeartBeat heartBeatmsg;
	SecondaryScanner secscanner;

	public static void main(String args[]){
		new Slave(args[0],Integer.parseInt(args[1]),args[2],args[3],args[4],Integer.parseInt(args[5]));
	}
	public Slave(String host,int port,String path,String secpath,String masterhost,int masterport){
		masterClient=new SlaveMasterClient(masterhost,masterport);
		server=new SlaveServer(port,path,secpath);
		new Thread(server).start();
		masterClient.sendJoinRequest(host,port);
		heartBeatmsg = new HeartBeat(host,port,masterhost,masterport);
		new Thread(heartBeatmsg).start();
		secscanner=new SecondaryScanner(secpath,masterhost,masterport);
		new Thread(secscanner).start();

	}
}
