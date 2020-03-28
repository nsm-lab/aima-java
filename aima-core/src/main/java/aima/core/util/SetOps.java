package aima.core.util;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Note: This code is based on - <a href=
 * "http://download.oracle.com/javase/tutorial/collections/interfaces/set.html"
 * >Java Tutorial: The Set Interface</a> <br>
 * 
 * Using LinkedHashSet, even though slightly slower than HashSet, in order to
 * ensure order is always respected (i.e. if called with TreeSet or
 * LinkedHashSet implementations).
 * 
 * @author Ciaran O'Reilly
 * @author Ravi Mohan
 */
public class SetOps {

	/**
	 * 
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the union of s1 and s2. (The union of two sets is the set
	 *         containing all of the elements contained in either set.)
	 */
	public static <T> Set<T> union(Set<T> s1, Set<T> s2) {
		if (s1 == s2) {
			return s1;
		}
		Set<T> union = new LinkedHashSet<T>(s1);
		union.addAll(s2);
		return union;
	}

	/**
	 * 
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the intersection of s1 and s2. (The intersection of two sets is
	 *         the set containing only the elements common to both sets.)
	 */
	public static <T> Set<T> intersection(Set<T> s1, Set<T> s2) {
		if (s1 == s2) {
			return s1;
		}
		Set<T> intersection = new LinkedHashSet<T>(s1);
		intersection.retainAll(s2);
		return intersection;
	}

	/**
	 * 
	 * @param <T>
	 * @param s1
	 * @param s2
	 * @return the (asymmetric) set difference of s1 and s2. (For example, the
	 *         set difference of s1 minus s2 is the set containing all of the
	 *         elements found in s1 but not in s2.)
	 */
	public static <T> Set<T> difference(Set<T> s1, Set<T> s2) {
		if (s1 == s2) {
			return new LinkedHashSet<T>();
		}
		Set<T> difference = new LinkedHashSet<T>(s1);
		difference.removeAll(s2);
		return difference;
	}
}
