package de.rwth.i2.spa;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class Main {

	public static void main(String[] args) {

		// enable / disable to apply widening
		boolean useWidening = true;

		PackManager.v().getPack("jtp").add(new Transform("jtp.IntervalAnalysis", new BodyTransformer() {
			protected void internalTransform(Body body, String phase, @SuppressWarnings("rawtypes") Map options) {
				new IntervalAnalysis(new ExceptionalUnitGraph(body), useWidening);
			}
		}));

		// if you encounter any problems related to your class path, try to
		// insert your class path manually here
		String sootClassPath = defaultJavaClassPath() + File.pathSeparator + "examples/";
		Scene.v().setSootClassPath(sootClassPath);

		String[] arguments = getArguments(args);

		soot.Main.main(arguments);
	}

	private static String[] getArguments(String[] args) {

		ArrayList<String> options = new ArrayList<>();

		if (args.length == 0) {
			System.out.println("No classname has been specified. Analyzing all classes in examples/");
			options.add("-process-path");
			options.add("examples/");
		} else {
			for (String arg : args) {
				options.add(arg);
			}
		}

		// generate Jimple
		options.add("-f");
		options.add("J");

		// add analysis results to created Jimple files
		options.add("-print-tags");

		String[] result = new String[options.size()];
		return options.toArray(result);
	}

	/*
	 * This is a workaround since Soot is a mess on certain versions of MacOS
	 */
	private static String defaultJavaClassPath() {
		StringBuilder sb = new StringBuilder();

		File rtJar = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar");
		if (rtJar.exists() && rtJar.isFile()) {
			// G.v().out.println("Using JRE runtime: " +
			// rtJar.getAbsolutePath());
			sb.append(rtJar.getAbsolutePath());
		} else {
			// in case we're not in JRE environment, try JDK
			rtJar = new File(System.getProperty("java.home") + File.separator + "jre" + File.separator + "lib"
					+ File.separator + "rt.jar");
			if (rtJar.exists() && rtJar.isFile()) {
				// G.v().out.println("Using JDK runtime: " +
				// rtJar.getAbsolutePath());
				sb.append(rtJar.getAbsolutePath());
			} else if (System.getProperty("os.name").equals("Mac OS X")) {
				// in older Mac OS X versions, rt.jar was split into classes.jar
				// and ui.jar
				sb.append(System.getProperty("java.home"));
				sb.append(File.separator);
				sb.append("..");
				sb.append(File.separator);
				sb.append("Classes");
				sb.append(File.separator);
				sb.append("classes.jar");

				sb.append(File.pathSeparator);
				sb.append(System.getProperty("java.home"));
				sb.append(File.separator);
				sb.append("..");
				sb.append(File.separator);
				sb.append("Classes");
				sb.append(File.separator);
				sb.append("ui.jar");
				sb.append(File.pathSeparator);
			} else {
				// not in JDK either
				throw new RuntimeException("Error: cannot find rt.jar.");
			}
		}

		return sb.toString();
	}

}
