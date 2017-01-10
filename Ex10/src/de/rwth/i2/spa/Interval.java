package de.rwth.i2.spa;

/*
 * Representation of single intervals
 */
public class Interval {

	public boolean isEmpty() {
		boolean empty = false;
		if (this instanceof EmptyInterval) {
			empty = true;
		} else {
			empty = false;
		}
		return empty;
	}

	public boolean isNonEmpty() {
		boolean nonEmpty = false;
		if (this instanceof NonEmptyInterval) {
			nonEmpty = true;
		} else {
			nonEmpty = false;
		}
		return nonEmpty;
	}

	public boolean satisfiableBounds() {
		boolean result = false;
		if (this.isEmpty()) {
			result = true;
		} else {
			Bound lower = ((NonEmptyInterval) this).getLowerBound();
			Bound upper = ((NonEmptyInterval) this).getUpperBound();

			if (upper instanceof PosInfinity) {
				result = true;
			} else if (lower instanceof NegInfinity) {
				result = true;
			} else if (lower instanceof IntBound && upper instanceof IntBound) {
				if (((IntBound) lower).value <= ((IntBound) upper).value) {
					result = true;
				} else {
					result = false;
				}
			}
		}
		return result;
	}

	public Interval lub(Interval other) {

		/*
		 * TODO return the least upper bound of intervals this and other
		 */
		if(this.isEmpty() && other.isEmpty()){
			return new EmptyInterval();
		}
		else{
			if (this.isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			}

			Bound lower;
			Bound upper;

			Bound thisLower = ((NonEmptyInterval) this).getLowerBound();
			Bound thisUpper = ((NonEmptyInterval) this).getUpperBound();
			Bound otherLower = ((NonEmptyInterval) other).getLowerBound();
			Bound otherUpper = ((NonEmptyInterval) other).getUpperBound();

			if (Bound.smallerThen(thisLower, otherLower)) {
				lower = thisLower;
			} else {
				lower = otherLower;
			}
			if (Bound.smallerThen(thisUpper, otherUpper)) {
				upper = otherUpper;
			} else {
				upper = thisUpper;
			}

			return new NonEmptyInterval(lower, upper);
		}
	}

	public Interval widen(Interval other) {

		/*
		 * TODO return the result of applying the widening operator from the
		 * lecture to this interval (left) and the other given interval (right)
		 */

		if(this.isEmpty() && other.isEmpty()){
			return new EmptyInterval();
		}
		else{
			if (this.isEmpty()) {
				return other;
			} else if (other.isEmpty()) {
				return this;
			}

			Bound lower;
			Bound upper;
			Bound x1 = ((NonEmptyInterval) this).getLowerBound();
			Bound x2 = ((NonEmptyInterval) this).getUpperBound();
			Bound y1 = ((NonEmptyInterval) other).getLowerBound();
			Bound y2 = ((NonEmptyInterval) other).getUpperBound();

			if (!Bound.greaterThen(x1, y1)) { // x1 <= y1
				lower = x1;
			} else {
				lower = new NegInfinity();
			}

			if (!Bound.smallerThen(x2, y2)) { // x2 >= y2
				upper = x2;
			} else {
				upper = new PosInfinity();
			}

			return new NonEmptyInterval(lower, upper);
		}

	}

	public static Interval getLargestElement() {

		/*
		 * TODO return the largest possible interval, i.e., the interval
		 * [-inf,inf]
		 */

		return (new NonEmptyInterval(new NegInfinity(), new PosInfinity()));
	}

	public boolean equals(Object other) {

		/*
		 * TODO return true iff other represents the same interval as this,
		 * i.e., have the same bounds
		 */
		if(this == other)						// same reference?
			return true;
		if(!(other instanceof Interval)){		// same type?
			return false;
		} else{
			Interval otherInterval = (Interval) other;
			if(otherInterval.isEmpty() && this.isEmpty())
				return true;
			if(otherInterval.isNonEmpty() && this.isNonEmpty()){
				NonEmptyInterval otherNE = (NonEmptyInterval) other;
				NonEmptyInterval thisNE = (NonEmptyInterval) this;
				return thisNE.getLowerBound().equals(otherNE.getLowerBound())
						&& thisNE.getUpperBound().equals(otherNE.getUpperBound());
			}
		}

		return false;							// something strange happened if we end up here
	}

	public static Interval plus(Interval left, Interval right) {

		/*
		 * TODO return an interval representing the sum of intervals left and
		 * right
		 */
		if(left instanceof EmptyInterval)
			return right;
		if(right instanceof EmptyInterval)
			return left;
		Interval result = new EmptyInterval();
		if (left.isNonEmpty() && right.isNonEmpty()) {
			Bound lower = Bound.plus(((NonEmptyInterval) left).getLowerBound(),
					((NonEmptyInterval) right).getLowerBound());
			Bound upper = Bound.plus(((NonEmptyInterval) left).getUpperBound(),
					((NonEmptyInterval) right).getUpperBound());

			// check if result is empty interval
			result = new NonEmptyInterval(lower, upper);
			if (result.satisfiableBounds() == false) {
				return new EmptyInterval();
			}else
				return result;
		} else {
			return new EmptyInterval();
		}
	}

	public static Interval minus(Interval left, Interval right) {

		/*
		 * TODO return an interval representing the difference of intervals left
		 * and right
		 */
		if(left instanceof EmptyInterval)
			return right;
		if(right instanceof EmptyInterval)
			return left;
		Interval result = new EmptyInterval();
		if (left.isNonEmpty() && right.isNonEmpty()) {
			Bound lower = Bound.minus(((NonEmptyInterval) left).getLowerBound(),
					((NonEmptyInterval) right).getLowerBound());
			Bound upper = Bound.minus(((NonEmptyInterval) left).getUpperBound(),
					((NonEmptyInterval) right).getUpperBound());

			// check if result is empty interval
			result = new NonEmptyInterval(lower, upper);
			if (result.satisfiableBounds() == false) {
				return new EmptyInterval();
			}else
				return result;
		} else {
			return new EmptyInterval();
		}
	}

	public static Interval mul(Interval left, Interval right) {

		/*
		 * TODO return an interval representing the product of intervals left
		 * and right
		 */
		if(left instanceof EmptyInterval)
			return right;
		if(right instanceof EmptyInterval)
			return left;
		if (left instanceof NonEmptyInterval && right instanceof NonEmptyInterval) {
			// left = [x1,x2], right = [y1,y2]
			Bound[] x = { ((NonEmptyInterval) left).getLowerBound(), ((NonEmptyInterval) left).getUpperBound() };

			Bound[] y = { ((NonEmptyInterval) right).getLowerBound(), ((NonEmptyInterval) right).getUpperBound() };

			Bound lower = new PosInfinity();
			Bound upper = new NegInfinity();

			for (Bound x_i : x){
				for (Bound y_i : y){
					Bound candidate = Bound.mul(x_i,y_i);
					if (Bound.smallerThen(candidate, lower))
						lower = candidate;
					if (Bound.greaterThen(candidate, upper))
						upper = candidate;
				}
			}

			return new NonEmptyInterval(lower, upper);
		} else {
			return new EmptyInterval();
		}
	}

	public String toString() {

		/*
		 * TODO returns a String representing this interval. If the interval is
		 * empty, you should return []. Otherwise, the format should be "[x,y]"
		 * (no additional whitespace), where x,y are either integers, "-inf" for
		 * minus infinity, or "inf" for plus infinity
		 */
		return this.toString();
	}

}

class EmptyInterval extends Interval {
	public EmptyInterval() {
	}

	public String toString() {
		return "[]";
	}
}

class NonEmptyInterval extends Interval {
	public NonEmptyInterval(Bound lower, Bound upper) {
		lowerBound = lower;
		upperBound = upper;
	}

	private boolean empty;

	private Bound lowerBound;
	private Bound upperBound;

	public Bound getLowerBound() {
		return lowerBound;
	}

	public Bound getUpperBound() {
		return upperBound;
	}

	public String toString() {
		return "[" + lowerBound.toString() + "," + upperBound.toString() + "]";
	}

}
