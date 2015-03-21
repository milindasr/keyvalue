package slaves;

public class Slave {
	SlaveMasterClient masterClient;
	SlaveServer server;
	public static void main(String args[]){
		Slave slave=new Slave();
	}
	public Slave(){
		masterClient=new SlaveMasterClient();
		server=new SlaveServer(9001);
		new Thread(server).start();
		masterClient.sendJoinRequest();
	}
}
