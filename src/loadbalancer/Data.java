package loadbalancer;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * this class holds server data
 * @author Milind
 *
 */
public class Data {
	static class Node{
		public String host;
		public int port;
		public Date date;
		Node(String host,int port){
			this.host=host;
			this.port=port;
			this.date=new Date();
		}
		@Override
		public String toString(){
			return this.host+" "+this.port;
		}
		@Override
		public boolean equals(Object o){
			Node n=(Node)o;
			if(n.host==this.host && n.port==this.port)
				return true;
			else
				return false;
		}
		/*@Override
		public int hashCode(){
			return port*13;
		}*/
	}
	private static int counter=1;
	private static Map<Integer,Node> IdHostMap =Collections.synchronizedMap(new HashMap<Integer,Node>());
	public static void addNode(String host,int port){
		Node n=new Node(host,port);
		IdHostMap.put(counter,n);
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
		Node n=IdHostMap.get(hash);
		Date currDate=new Date();
		System.out.println(hash);
		System.out.println(n);
		long diffinmillis=currDate.getTime()-n.date.getTime();
		if(diffinmillis> 16000){
			return false;
		}
		return true;
	}
	public static void setActive(String host,int port){
		for(Entry<Integer, Node> e: IdHostMap.entrySet()){
			Node n=e.getValue();
			if(n.host.equals(host) && n.port==port){
				Date now=new Date();
			    n.date=now;
			}
		}
	}
	public static boolean isActive(String host,int port){
		for(Entry<Integer, Node> e: IdHostMap.entrySet()){
			Node n=e.getValue();
			if(n.host==host && n.port==port){
				Date now=new Date();
				if((now.getTime()-n.date.getTime())<16000){
					return true;
				}
			}
		}
		return false;
	}
}
