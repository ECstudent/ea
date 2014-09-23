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
	ExcelFile ef;
	public int evals_;
	private int generation_;
	private int evaluations_limit_;
	private int population_size_;
	private final int dimensions_ = 10;
	private List<Candidate> population_;

	public Player20() {
		population_ = new ArrayList<Candidate>();
		rnd_ = new Random();
		ef = new ExcelFile("AlgorithmResults.xls");
		ef.createWorksheet("SphereEvaluation");
		ef.addLabel("SphereEvaluation", 5, 2, "It works!");
		ef.addNumber("SphereEvaluation", 6, 2, 12345.6789);
		ef.close();
	}

	public static void main(String[] args) {
		// Initialize population
		Player20 p = new Player20();
		//p.setSeed(System.nanoTime());
		//p.setEvaluation(new SphereEvaluation());
		//p.run();
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
			// Retrieve the two best candidates
			parents.add(population_.get(0));
			parents.add(population_.get(1));
		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
		return parents;
	}

	private List<Candidate> createChildren(List<Candidate> parents) {
		List<Candidate> children = null;
		try {
			// select children from population
			// Recombination
			double[] child1 = new double[dimensions_];
			double[] child2 = new double[dimensions_];
			double[] parent1 = parents.get(0).getParameters();
			double[] parent2 = parents.get(1).getParameters();

			// classic crossover
			for (int index = 0; index < dimensions_; index++) {
				if (index < dimensions_ / 2) {
					child1[index] = parent1[index];
					child2[index] = parent2[index + (dimensions_ / 2)];
				} else {
					child1[index] = parent2[index];
					child2[index] = parent1[index - (dimensions_ / 2)];
				}
			}

			// mutation
			// perform mutation on child1 50% chance
			if (rnd_.nextDouble() < 0.5) {
				int gene = rnd_.nextInt(10);
				child1[gene] = -5 + (10 * rnd_.nextDouble());
			} else {
				// no mutation takes place
			}

			// perform mutation on child2 50% chance
			if (rnd_.nextDouble() < 0.5) {
				int gene = rnd_.nextInt(10);
				child2[gene] = -5 + (10 * rnd_.nextDouble());
			} else {
				// no mutation takes place
			}

			children = new ArrayList<Candidate>();
			children.add(new Candidate(evaluation_, generation_, this, child1));
			children.add(new Candidate(evaluation_, generation_, this, child2));
		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
		return children;
	}

	private void selectSurvivors(List<Candidate> children,
			List<Candidate> parents) {
		try {
			// Add children to the population
			for (Candidate c : children) {
				population_.add(c);
			}
			// Sort population by fitness
			Collections.sort(population_);
			// Remove the two(!) candidates with the lowest fitness
			while (population_.size() > population_size_) {
				population_.remove(population_.size() - 1);
			}
		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
	}

	private void evaluatePopulation() {
		try {
			double sum = 0.0;
			for (Candidate c : population_) {
				sum += c.getFitness();
			}
			System.out.println("Average fitness of generation " + generation_
					+ " is: " + (sum / population_.size()));
		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
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

		while (/* generation_ < 100 && */evals_ < evaluations_limit_) {

			evaluatePopulation();

			// parent selection
			List<Candidate> parents = selectParents();

			// child creation
			List<Candidate> children = createChildren(parents);

			// survivor selection
			selectSurvivors(children, parents);

			generation_++;
		}
		System.out.println("Final result: " + evaluation_.getFinalResult());
	}
}