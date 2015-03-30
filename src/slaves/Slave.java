package slaves;

public class Slave {
	SlaveMasterClient masterClient;
	SlaveServer server;
	public static void main(String args[]){
		new Slave(args[0],Integer.parseInt(args[1]),args[2]);
	}
	public Slave(String host,int port,String path){
		masterClient=new SlaveMasterClient();
	//	server=new SlaveServer(port,"C:\\Users\\Milind\\Desktop\\dbms code\\inteview\\db.txt");
		server=new SlaveServer(port,path);
		new Thread(server).start();
		masterClient.sendJoinRequest(host,port);
	}
}
