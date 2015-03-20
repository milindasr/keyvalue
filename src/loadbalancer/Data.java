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
	private static Map<Integer,String> IdHostMap =Collections.synchronizedMap(new HashMap<Integer,String>());
	private static Set<Integer> activehostIds =Collections.synchronizedSet(new HashSet<Integer>());
	
}
