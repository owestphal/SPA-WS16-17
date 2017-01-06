package de.rwth.i2.spa;

/*
 * algebraic datatype representation for interval bounds
 */
abstract class Bound {
	static Bound plus(Bound left, Bound right) {
		Bound result;
		if (left instanceof IntBound && right instanceof IntBound) {
			result = new IntBound(((IntBound)left).value + ((IntBound)right).value);
		}
		else if (left instanceof PosInfinity || right instanceof PosInfinity) {
			result = new PosInfinity();
		}
		else {
			result = new NegInfinity();
		}
		return result;
	}
	static Bound minus(Bound left, Bound right) {
		Bound result;
		if (left instanceof IntBound && right instanceof IntBound) {
			result = new IntBound(((IntBound)left).value - ((IntBound)right).value);
		}
		else if (left instanceof PosInfinity || right instanceof NegInfinity) {
			result = new PosInfinity();
		}
		else {
			result = new NegInfinity();
		}
		return result;
	}
	static Bound mul(Bound left, Bound right) {
		Bound result;
		if (left instanceof IntBound && right instanceof IntBound) {
			result = new IntBound(((IntBound)left).value * ((IntBound)right).value);
		}
		else if ((left instanceof PosInfinity && !(right instanceof NegInfinity))
				 || (!(left instanceof NegInfinity) && right instanceof PosInfinity)
				 || (left instanceof NegInfinity || (right instanceof NegInfinity))) {
			result = new PosInfinity();
		}
		else {
			result = new NegInfinity();
		}
		return result;
	}

	static public boolean smallerThen (Bound left, Bound right) {
		boolean result = false;
		if (left instanceof PosInfinity || right instanceof NegInfinity) {
			result = false;
		}
		else if (left instanceof NegInfinity || right instanceof PosInfinity) {
			result = true;
		}
		else if (left instanceof IntBound && right instanceof IntBound) {
			result = ((IntBound)left).value < ((IntBound)right).value;
		}
		return result;
	}

	static public boolean greaterThen (Bound left, Bound right) {
		boolean result = false;
		if (left instanceof NegInfinity || right instanceof PosInfinity) {
			result = false;
		}
		else if (left instanceof PosInfinity || right instanceof NegInfinity) {
			result = true;
		}
		else if (left instanceof IntBound && right instanceof IntBound) {
			result = ((IntBound)left).value > ((IntBound)right).value;
		}
		return result;
	}

}

class PosInfinity extends Bound {
	public PosInfinity() {}
	public String toString() {return "inf";}
	public boolean equals(Object other) {
		boolean eq = false;
		if (other instanceof PosInfinity) {
			eq = true;
		}
		else {
			eq = false;
		}
		return eq;
	}
}
class NegInfinity extends Bound {
	public NegInfinity() {}
	public String toString() {return "-inf";}
	public boolean equals(Object other) {
		boolean eq = false;
		if (other instanceof NegInfinity) {
			eq = true;
		}
		else {
			eq = false;
		}
		return eq;
	}
}

class IntBound extends Bound {
	public int value;
	public IntBound(int v) {value = v;}
	public String toString() {return String.valueOf(value);}
	public boolean equals(Object other) {
		boolean eq = false;
		if (other instanceof IntBound) {
			IntBound otherBound = (IntBound) other;
			eq = this.value == otherBound.value;
		}
		else {
			eq = false;
		}
		return eq;
	}
}
