package de.rwth.i2.spa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import soot.Immediate;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.MulExpr;
import soot.jimple.SubExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JSubExpr;
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
			
			if(rightOp instanceof Immediate){												//TODO: Nasty Type Collisions possible here currently we support only IntBound for Constants
				Bound bounds = new IntBound(Integer.valueOf(rightOp.toString()));
				in.delta.replace(leftOp.toString(), in.delta.get(leftOp.toString()), new NonEmptyInterval(bounds, bounds));			
				copy(in,out);
			}
			if(rightOp instanceof Local){													//TODO: We never get here, I changed the ExSixOne.java and compiled it in order to get here, but we still never enter this case
				System.out.println("Found Variable");
				in.delta.replace(leftOp.toString(), in.delta.get(leftOp.toString()), in.delta.get(rightOp.toString()));			
				copy(in,out);
			}
			if(rightOp instanceof AddExpr){
				List<ValueBox> values = rightOp.getUseBoxes();
				
				System.out.println("Found AddExpr");
				BiConsumer<String, Interval> computeVariable = (k, v) -> {
					if(k.equals(leftOp.toString())){
						NonEmptyInterval i = (NonEmptyInterval) in.delta.get(k);
						
						in.delta.replace(k, i, in.delta.get(rightOp.toString()));
					}
				};
				in.delta.forEach(computeVariable);
				copy(in,out);
			}
			if(rightOp instanceof SubExpr){
				System.out.println("Found SubExpr");
			}
			if(rightOp instanceof MulExpr){
				System.out.println("Found MulExpr");
			}
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
		Interval initialIn = Interval.getLargestElement();
		IntervalDomain initialInDom = new IntervalDomain(this.variables, initialIn);
		return initialInDom;
	}

	@Override
	protected IntervalDomain newInitialFlow() {

		/*
		 * TODO Returns the initial domain element of all labels
		 */

		Interval initialIn = Interval.getLargestElement();
		IntervalDomain initialInDom = new IntervalDomain(this.variables, initialIn);
		return initialInDom;
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
