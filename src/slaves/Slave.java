package slaves;

public class Slave {
	SlaveMasterClient masterClient;
	SlaveServer server;
	public static void main(String args[]){
		new Slave(args[0],Integer.parseInt(args[1]),args[2],args[3]);
	}
	public Slave(String host,int port,String path,String secpath){
		masterClient=new SlaveMasterClient();
	//	server=new SlaveServer(port,"C:\\Users\\Milind\\Desktop\\dbms code\\inteview\\db.txt");
		server=new SlaveServer(port,path,secpath);
		new Thread(server).start();
		masterClient.sendJoinRequest(host,port);
	}
}
