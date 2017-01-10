package de.rwth.i2.spa;

public class EmptySet extends Interval {
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public boolean isNonEmpty() {
		return false;
	}

	@Override
	public boolean satisfiableBounds() {
		return false;
	}

	@Override
	public Interval lub(Interval other) {

		return other;
	}

	@Override
	public Interval widen(Interval other) {

		return other;

	}

	@Override
	public boolean equals(Object other) {

		if(other instanceof EmptySet)
			return true;
		else
			return false;
	}

	public String toString() {

		/*
		 * TODO returns a String representing this interval. If the interval is
		 * empty, you should return []. Otherwise, the format should be "[x,y]"
		 * (no additional whitespace), where x,y are either integers, "-inf" for
		 * minus infinity, or "inf" for plus infinity
		 */
		return "";
	}
	public EmptySet(){
		
	}
}
