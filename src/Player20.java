import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Player20 implements ContestSubmission {

	Random rnd_;
	ContestEvaluation evaluation_;
	private int population_size_;
	private int generation_;
	private int evaluations_limit_;
	private final int dimensions_ = 10;
	private List<Candidate> population_;
	public int evals_;

	public Player20() {
		population_ = new ArrayList<Candidate>();
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
		} else {
		}
		if (hasStructure) {
		} else {
		}
		if (isSeparable) {
		} else {
		}

	}

	private List<Candidate> selectParents() {
		List<Candidate> parents = null;
		try {
			// select parents from population
			Collections.sort(population_);
			parents = new ArrayList<Candidate>();
			parents.add(population_.get(0));
			parents.add(population_.get(1));
		} catch (NullPointerException npe) {
			System.err.println("NullPointerException npe: "
					+ npe.getLocalizedMessage());
		}
		System.out.println("selectParents succesfully ended.");
		return parents;
	}

	private List<Candidate> createChildren(List<Candidate> parents) {
		List<Candidate> children = null;
		try {
			// select children from population
			// Recombination
			double[] child1 = new double[dimensions_];
			double[] child2 = new double[dimensions_];

			for (int index = 0; index < dimensions_; index++) {
				if (index < dimensions_ / 2) {
					child1[index] = parents.get(0).getParameters()[index];
					child2[index] = parents.get(1).getParameters()[index
							+ (dimensions_ / 2)];
				} else {
					child1[index] = parents.get(1).getParameters()[index];
					child2[index] = parents.get(0).getParameters()[index
							- (dimensions_ / 2)];
				}
			}
			children = new ArrayList<Candidate>();
			children.add(new Candidate(evaluation_, generation_, this, child1));
			children.add(new Candidate(evaluation_, generation_, this, child2));
		} catch (NullPointerException npe) {
			System.err.println("NullPointerException npe: "
					+ npe.getLocalizedMessage());
		}
		System.out.println("createChildren succesfully ended.");
		return children;
	}

	private void selectSurvivors(List<Candidate> children,
			List<Candidate> parents) {
		try {
			// select survivors from parents and children
			// and place them in the population
			for (Candidate c : children) {
				population_.add(c);
			}
			Collections.sort(population_);
			while (population_.size() > population_size_) {
				population_.remove(population_.size() - 1);
			}
		} catch (NullPointerException npe) {
			System.err.println("NullPointerException npe: "
					+ npe.getLocalizedMessage());
		}
		System.out.println("selectSurvivors succesfully ended.");
	}

	public void run() {
		// Run your algorithm here
		population_size_ = 10;
		generation_ = 1;
		evals_ = 0;

		// Initialize population
		for (int index = 0; index < population_size_; index++) {
			population_.add(new Candidate(evaluation_, generation_, this));
		}

		while (evals_ < evaluations_limit_) {

			// parent selection
			List<Candidate> parents = selectParents();

			// child creation
			List<Candidate> children = createChildren(parents);

			// survivor selection
			selectSurvivors(children, parents);

			System.out.println(evals_);
			generation_++;
		}
		System.out.println("Final result: " + evaluation_.getFinalResult());
	}
}