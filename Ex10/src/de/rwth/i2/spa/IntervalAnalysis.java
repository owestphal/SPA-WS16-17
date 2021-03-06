package de.rwth.i2.spa;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import soot.Immediate;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.MulExpr;
import soot.jimple.SubExpr;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/*
 * Interval analysis as in SPA lecture 7 using Soot
 */
public class IntervalAnalysis extends ForwardFlowAnalysis<Unit, IntervalDomain> {

	// set of all variables occurring in the given program
	private Set<Value> variables;

	// flag to use widening or least upper bound in the merge operation
	private final boolean useWidening;

	/*******************************************************************************************
	 * START IMPLEMENTING HERE
	 *******************************************************************************************/

	@Override
	protected void flowThrough(IntervalDomain in, Unit unit, IntervalDomain out) {

		/*
		 * TODO apply the transfer function corresponding to statement unit to
		 * the domain element in. The result should be copied into out.
		 *
		 * Hint: Assignments correspond to units of type AssignStmt.
		 */
		
		if(unit instanceof AssignStmt){
			
			AssignStmt assignUnit = (AssignStmt) unit;
			Value leftOp = assignUnit.getLeftOp();
			Value rightOp = assignUnit.getRightOp();

			copy(in, out);
			
			if(!out.delta.containsKey(leftOp.toString())){
				out.delta.put(leftOp.toString(), new EmptyInterval());
			}
			
			//merge(preIntDom,in,out);
			
			if(rightOp instanceof Immediate){
				Bound bounds = new IntBound(Integer.valueOf(rightOp.toString()));
				out.delta.replace(leftOp.toString(), out.delta.get(leftOp.toString()), new NonEmptyInterval(bounds, bounds));
				
			}
			if(rightOp instanceof Local){
				out.delta.replace(leftOp.toString(), out.delta.get(leftOp.toString()), out.delta.get(rightOp.toString()));
			}
			if(rightOp instanceof AddExpr){
				Bound lowerBound = new IntBound(0);
				Bound upperBound = new IntBound(0);
				List<ValueBox> values = rightOp.getUseBoxes();
				ListIterator<ValueBox> lit = values.listIterator();

				while (lit.hasNext()){
					Value currentValue = lit.next().getValue();
					if(currentValue instanceof Local){
						lowerBound = Bound.plus(lowerBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getLowerBound());
						upperBound = Bound.plus(upperBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getUpperBound());
					}else if(currentValue instanceof Immediate){
						lowerBound = Bound.plus(lowerBound, new IntBound(Integer.valueOf(currentValue.toString())));
						upperBound = Bound.plus(upperBound, new IntBound(Integer.valueOf(currentValue.toString())));
					}
				}
				out.delta.replace(leftOp.toString(), out.delta.get(leftOp.toString()), new NonEmptyInterval(lowerBound, upperBound));
			}
			if(rightOp instanceof SubExpr){
				
				List<ValueBox> values = rightOp.getUseBoxes();
				
				Bound lowerBound = new IntBound(0);
				Bound upperBound = new IntBound(0);
				
				Value initValue = values.get(0).getValue();
				if(initValue instanceof Local){
					lowerBound = ((NonEmptyInterval) out.delta.get(initValue.toString())).getLowerBound();
					upperBound = ((NonEmptyInterval) out.delta.get(initValue.toString())).getUpperBound();
				}
				else if(initValue instanceof Immediate){
					lowerBound = new IntBound(Integer.valueOf(initValue.toString()));
					upperBound = new IntBound(Integer.valueOf(initValue.toString()));
				}
				
				for(int i = 1; i < values.size();i++){
					Value currentValue = values.get(i).getValue();
					if(currentValue instanceof Local){
						lowerBound = Bound.minus(lowerBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getLowerBound());
						upperBound = Bound.minus(upperBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getUpperBound());
					}else if(currentValue instanceof Immediate){
						lowerBound = Bound.minus(lowerBound, new IntBound(Integer.valueOf(currentValue.toString())));
						upperBound = Bound.minus(upperBound, new IntBound(Integer.valueOf(currentValue.toString())));
					}
				}
				
				out.delta.replace(leftOp.toString(), out.delta.get(leftOp.toString()), new NonEmptyInterval(lowerBound, upperBound));
			}
			if(rightOp instanceof MulExpr){
				Bound lowerBound = new IntBound(1);
				Bound upperBound = new IntBound(1);
				List<ValueBox> values = rightOp.getUseBoxes();
				ListIterator<ValueBox> lit = values.listIterator();
				while (lit.hasNext()){
					Value currentValue = lit.next().getValue();
					if(currentValue instanceof Local){
						lowerBound = Bound.mul(lowerBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getLowerBound());
						upperBound = Bound.mul(upperBound, ((NonEmptyInterval) out.delta.get(currentValue.toString())).getUpperBound());
					}else if(currentValue instanceof Immediate){
						lowerBound = Bound.mul(lowerBound, new IntBound(Integer.valueOf(currentValue.toString())));
						upperBound = Bound.mul(upperBound, new IntBound(Integer.valueOf(currentValue.toString())));
					}
				}
				out.delta.replace(leftOp.toString(), out.delta.get(leftOp.toString()), new NonEmptyInterval(lowerBound, upperBound));
			}
		}else{
			copy(in,out);
		}
	}

	@Override
	protected void copy(IntervalDomain source, IntervalDomain dest) {

		/*
		 * TODO assign a deep copy of source to dest
		 */
		Set<String> keySet = source.delta.keySet();
		for(String key : keySet){
			dest.delta.put(key, source.delta.get(key));
		}
	}

	@Override
	protected void merge(IntervalDomain in1, IntervalDomain in2, IntervalDomain dest) {

		/*
		 * TODO Combine interval domain elements in1 and in2 into a single
		 * domain element assigned to dest You should implement two versions of
		 * merge: 1) Use the least upper bound between two interval domain
		 * elements 2) Use the merge operator defined in SPA lecture 7
		 */
		
		if (useWidening) {

			// TODO implement merge with widening here
			dest = in1.widen(in2);
		} else {
			// TODO implement merge using LUB here
			dest = in1.lub(in2);
		}
	}

	@Override
	protected IntervalDomain entryInitialFlow() {

		/*
		 * TODO Returns the initial domain element to start the fixed point
		 * computation
		 */
		return new IntervalDomain(this.variables, Interval.getLargestElement());
	}

	@Override
	protected IntervalDomain newInitialFlow() {

		/*
		 * TODO Returns the initial domain element of all labels
		 */
		IntervalDomain emptySet = new IntervalDomain();
		//IntervalDomain emptySet = new IntervalDomain(this.variables, new EmptyInterval());
		return emptySet;
	}

	/*******************************************************************************************
	 * STOP IMPLEMENTING HERE
	 *******************************************************************************************/

	public IntervalAnalysis(DirectedGraph<Unit> graph, boolean useWidening) {
		super(graph);

		this.useWidening = useWidening;

		variables = new HashSet<Value>();

		// collect all variables
		for (Unit unit : graph) {
			for (ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				// if(isIntegerVariable(v)) {
				variables.add(v);
				// }
			}
		}

		doAnalysis();

		// add tags to generated Jimple code
		addTags();
	}

	private void addTags() {
		for (Unit unit : graph) {
			String output = unitToAfterFlow.get(unit).toString();
			Tag tag = new StringTag(output);
			unit.addTag(tag);
		}
	}

	// provides set of final tags for testing purposes
	public Set<String> getTagging() {

		Set<String> result = new HashSet<>();

		for (Unit unit : graph) {
			result.add(unitToAfterFlow.get(unit).toString());
		}

		return result;
	}
}
