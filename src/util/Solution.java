package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Solution implements Comparable<Solution> {

	public static Collection<Solution> orgSec(List<Solution> ls, Category cat) {
		Map<Integer, Solution> ans = new TreeMap<Integer, Solution>();
		for (Solution s : ls) {
			int val = s.getID(cat);
			if (ans.containsKey(val)) {
				Solution sol = ans.get(val);
				sol.map.add(s);
				sol.value = Math.max(sol.value, s.value);
			} else {
				List<ECourse> lc = new ArrayList<ECourse>();
				for (ECourse c : s.courses)
					if (c.c != cat || cat.list.contains(c))
						lc.add(c);
				Solution n = new Solution(lc, s.value);
				n.map.add(s);
				ans.put(val, n);
			}
		}
		return ans.values();
	}

	public final ECourse[] courses;
	public final List<Solution> map = new ArrayList<Solution>();

	public int value;

	protected Solution(List<ECourse> cs, int val) {
		courses = cs.toArray(new ECourse[0]);
		value = val;
	}

	@Override
	public int compareTo(Solution o) {
		if (value > o.value)
			return -1;
		if (value < o.value)
			return 1;
		return 0;
	}

	public int getID(Category cat) {
		int ans = 0;
		for (ECourse c : courses)
			if (c.c != cat || cat.list.contains(c))
				ans += c.hashCode();
		return ans;
	}

	@Override
	public String toString() {
		String ans = "v=" + value;
		if (map.size() > 0)
			ans += " sec: " + map.size();
		return ans;
	}

}
