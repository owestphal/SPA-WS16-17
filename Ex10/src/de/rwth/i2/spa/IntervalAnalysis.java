package de.rwth.i2.spa;



import java.util.HashSet;
import java.util.Set;

import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/*
 * Interval analysis as in SPA lecture 7 using Soot
 */
public class IntervalAnalysis extends ForwardFlowAnalysis<Unit,IntervalDomain> {


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
		 * TODO
		 * apply the transfer function corresponding to statement unit to the
		 * domain element in.
		 * The result should be copied into out.
		 *
		 * Hint: Assignments correspond to units of type AssignStmt.
		 */

	}


	@Override
	protected void copy(IntervalDomain source, IntervalDomain dest) {

		/*
		 * TODO
		 * assign a deep copy of source to dest
		 */
		
	}



	@Override
	protected void merge(IntervalDomain in1, IntervalDomain in2, IntervalDomain dest) {

		/*
		 * TODO
		 * Combine interval domain elements in1 and in2 into a single domain element assigned to dest
		 * You should implement two versions of merge:
		 * 1) Use the least upper bound between two interval domain elements
		 * 2) Use the merge operator defined in SPA lecture 7
		 */

		if(useWidening) {

			// TODO implement merge with widening here

		} else {

			// TODO implement merge using LUB here
		}
	}

	@Override
	protected IntervalDomain entryInitialFlow() {

		/*
		 * TODO
		 * Returns the initial domain element to start the fixed point computation
		 */

		return null;
	}

	@Override
	protected IntervalDomain newInitialFlow() {

		/*
		 * TODO
		 * Returns the initial domain element of all labels
		 */

		return null;
	}

	/*******************************************************************************************
	 * STOP IMPLEMENTING HERE
	 *******************************************************************************************/


	public IntervalAnalysis(DirectedGraph<Unit> graph, boolean useWidening) {
		super(graph);

		this.useWidening = useWidening;

		variables = new HashSet<Value>();

		// collect all variables
		for(Unit unit : graph) {
			for(ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				//if(isIntegerVariable(v)) {
					variables.add(v);
				//}
			}
		}

		doAnalysis();

		// add tags to generated Jimple code
		addTags();
	}

	private void addTags() {
		for(Unit unit : graph) {
			String output = unitToAfterFlow.get(unit).toString();
			Tag tag = new StringTag( output );
			unit.addTag( tag );
		}
	}

	// provides set of final tags for testing purposes
	public Set<String> getTagging() {

		Set<String> result = new HashSet<>();

		for(Unit unit : graph) {
			result.add( unitToAfterFlow.get(unit).toString() );
		}

		return result;
	}
}
