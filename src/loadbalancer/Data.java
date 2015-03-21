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
	class Node{
		public String host;
		public int port;
		Node(String host,int port){
			this.host=host;
			this.port=port;
		}
	}
	private static int counter=1;
	private static Map<Integer,Node> IdHostMap =Collections.synchronizedMap(new HashMap<Integer,Node>());
	private static Set<Integer> activehostIds =Collections.synchronizedSet(new HashSet<Integer>());
	public void addNode(String host,int port){
		Node n=new Node(host,port);
		IdHostMap.put(counter,n);
		activehostIds.add(counter);
		counter++;
	}
}
