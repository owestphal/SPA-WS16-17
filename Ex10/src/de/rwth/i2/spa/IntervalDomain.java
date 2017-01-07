package de.rwth.i2.spa;

import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import soot.Local;
import soot.Value;

/*
 * Representation of interval domain according to SPA lecture 7
 */
public class IntervalDomain {

	public TreeMap<String, Interval> delta;

	public IntervalDomain(Set<Value> variables, Interval initialInterval) {

		/*
		 * TODO create new domain element over the given set of variables. Hint:
		 * In Soot variables correspond to values of type Local.
		 */
		delta = new TreeMap<String, Interval>();
		for (Value var : variables) {
			if(var instanceof Local){
				Local varLocal = (Local) var;
				delta.put(varLocal.getName(), initialInterval);
			}
		}
	}

	public boolean equals(Object other) {

		/*
		 * TODO return true iff other and this represent the same domain
		 * element, i.e., are defined over the same set of variables and every
		 * variable is assigned the same interval.
		 */

		if (other instanceof IntervalDomain) {
			IntervalDomain otherID = (IntervalDomain) other;
			return this.delta.equals(otherID.delta);
		} else {
			return false;
		}
	}

	public String toString() {

		/*
		 * TODO returns a String representing this interval domain element in
		 * the following format: VARIABLE_NAME_1:INTERVAL_1
		 * VARIABLE_NAME_2:INTERVAL_2 etc. The list of variables has to be
		 * ordered lexicographically according to variable names
		 * (value.toString()). Further, all variable/interval pairs should be
		 * separated by exactly one whitespace with no trailing whitespace.
		 *
		 * Example: a:[-inf,inf] c:[8,19] z:[27:inf]
		 */
		String result = "";
		for (Entry<String, Interval> entry : delta.entrySet()) {
			result += " " + entry.getKey() + ":[" + entry.getValue().toString() + "]";
		}
		return result.trim();
	}

	public IntervalDomain lub(IntervalDomain other) {

		/*
		 * TODO return the least upper bound of interval domain elements this
		 * and other
		 */
		BiConsumer<String, Interval> computeLubWithOther = (k, v) -> {
			Interval i = other.delta.get(k);
			v = v.lub(i);
		};
		this.delta.forEach(computeLubWithOther);
		return this;
	}

	public IntervalDomain widen(IntervalDomain other) {

		/*
		 * TODO return the widening of interval domain elements this and other
		 */
		BiConsumer<String, Interval> computeWidenWithOther = (k, v) -> {
			Interval i = other.delta.get(k);
			v = v.widen(i);
		};
		this.delta.forEach(computeWidenWithOther);
		return this;
	}
}
