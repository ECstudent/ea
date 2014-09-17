import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class player20 implements ContestSubmission {

	public static void Main() {
		player20 p = new player20();
		p.run();
	}

	Random rnd_;
	ContestEvaluation evaluation_;
	private int evaluations_limit_;

	public player20() {
		rnd_ = new Random();
	}

	public void setSeed(long seed) {
		// Set seed of algorithms random process
		rnd_.setSeed(seed);
	}

	public void setEvaluation(ContestEvaluation evaluation) {
		// Get evaluation properties
		evaluation_ = evaluation;

		// Get evaluation properties
		Properties props = evaluation.getProperties();
		evaluations_limit_ = Integer.parseInt(props.getProperty("Evaluations"));
		boolean isMultimodal = Boolean.parseBoolean(props
				.getProperty("Multimodal"));
		boolean hasStructure = Boolean.parseBoolean(props
				.getProperty("GlobalStructure"));
		boolean isSeparable = Boolean.parseBoolean(props
				.getProperty("Separable"));

		// Change settings(?)
		if (isMultimodal) {
			// Do sth
		} else {
			// Do sth else
		}
		if (hasStructure) {
			// Do sth
		} else {
			// Do sth else
		}
		if (isSeparable) {
			// Do sth
		} else {
			// Do sth else
		}

		int population_size = 4;

		Double fitness = 0.0;

		// Initialize population
		List<List<Double>> population = new ArrayList<List<Double>>();
		for (int j = 0; j < population_size; j++) {
			for (int i = 0; i < 10; i++) {
				population.get(j).add(-5 + (10 * rnd_.nextDouble()));
			}
			System.out.println("Fitness of candidate " + j + " is: "
					+ evaluation_.evaluate(population.get(j)));
		}

	}

	public void run() {
		// Run your algorithm here

		int evals = 0;
		while (evals < evaluations_limit_) {
			// Select parents
			// Apply variation operators and get children
			// double child[] = ...
			// Double fitness = evaluation_.evaluate(child);
			evals++;
			// Select survivors
		}

		// Getting data from evaluation problem (depends on the specific
		// evaluation implementation)
		// E.g. getting a vector of numbers
		// Vector<Double> data =
		// (Vector<Doulbe>)evaluation_.getData("trainingset1");

		// Evaluating your results
		// E.g. evaluating a series of true/false predictions
		// boolean pred[] = ...
		// Double score = (Double)evaluation_.evaluate(pred);
	}
}