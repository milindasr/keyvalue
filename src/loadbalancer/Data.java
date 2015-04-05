package loadbalancer;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * this class holds server data
 * @author Milind
 *
 */
public class Data {
	static class Node{
		public String host;
		public int port;
		Node(String host,int port){
			this.host=host;
			this.port=port;
		}
		@Override
		public String toString(){
			return this.host+" "+this.port;
		}
	}
	private static int counter=1;
	private static Map<Integer,Node> IdHostMap =Collections.synchronizedMap(new HashMap<Integer,Node>());
	private static Set<Integer> activehostIds =Collections.synchronizedSet(new HashSet<Integer>());
	public static void addNode(String host,int port){
		Node n=new Node(host,port);
		IdHostMap.put(counter,n);
		if(counter!=2)
		activehostIds.add(counter);
		counter++;
	}
	public static String getNodeString(int hash){
		Node n=IdHostMap.get(hash);
		return n.toString();
	}
	public static String getNodeip(int hash){
		Node n=IdHostMap.get(hash);
		return n.host;
	}
	public static String getNodeport(int hash){
		Node n=IdHostMap.get(hash);
		return Integer.toString(n.port);
	}
	public static boolean isActive(int hash){
		if(activehostIds.contains(hash)){
			return true;
		}
		return false;
	}
}
