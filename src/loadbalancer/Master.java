package loadbalancer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Master {
	private Map<Integer,String> IdHostMap =new HashMap<Integer,String>();
	private Set<Integer> activehostIds =new HashSet<Integer>();
	public void join(String host){
		
	}
	private int getHashValue(int key){
		return 0;
	}
	public String getHostName(int key){
		return null;
	}
}
