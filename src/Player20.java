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
	private int dimensions_;
	private int generation_;
	private int evaluations_limit_;
	private int population_size_;
	private List<Candidate> population_;
	private List<Double> probs_;
	private List<Double> rwprobs_;
	private boolean isMultimodal_;
	private boolean isSeparable_;
	private boolean hasStructure_;

	public Player20() {
		population_ = new ArrayList<Candidate>();
		probs_ = new ArrayList<Double>();
		rwprobs_ = new ArrayList<Double>();
		rnd_ = new Random();
		// ef = new ExcelFile("AlgorithmResults.xls");
		// ef.createWorksheet("SphereEvaluation");
		// ef.addLabel("SphereEvaluation", 5, 2, "It works!");
		// ef.addNumber("SphereEvaluation", 6, 2, 12345.6789);
		// ef.close();
	}

	public static void main(String[] args) {
		// Initialize population
		// Player20 p = new Player20();
		// p.setSeed(System.nanoTime());
		// p.setEvaluation(new SphereEvaluation());
		// p.run();
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
		isMultimodal_ = Boolean.parseBoolean(props.getProperty("Multimodal"));
		hasStructure_ = Boolean.parseBoolean(props
				.getProperty("GlobalStructure"));
		isSeparable_ = Boolean.parseBoolean(props.getProperty("Separable"));

		// Change settings(?)
		if (isMultimodal_) {
			population_size_ = 100;
		} else {
			population_size_ = 2;
		}
		if (hasStructure_) {
		}
		if (isSeparable_) {
		}

	}

	private List<Candidate> selectParents() {

		int index = 0;
		int inner = 0;
		int numParents = 2;
		double r = 0.0;
		List<Candidate> parents = null;

		try {
			// select parents from population
			Collections.sort(population_);
			parents = new ArrayList<Candidate>();

			// Using roulette wheel selection
			// while (index < numParents/* population_size_ */) {
			// r = rnd_.nextDouble();
			// inner = 0;
			// while (rwprobs_.get(inner) < r) {
			// inner++;
			// }
			// parents.add(population_.get(inner));
			// index++;
			// }

			// Using stochastic universal sampling
			r = (1.0 / population_size_) * rnd_.nextDouble();
			while (index < numParents) {
				while (r <= rwprobs_.get(inner)) {
					parents.add(population_.get(inner));
					r += (1.0 / population_size_);
					index++;
				}
				inner++;
			}

		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
		return parents;
	}

	private List<Candidate> createChildren(List<Candidate> parents) {
		List<Candidate> children = null;
		try {
			// select children from population
			double[] child1 = new double[dimensions_];
			double[] child2 = new double[dimensions_];
			double[] parent1 = parents.get(0).getParameters();
			double[] parent2 = parents.get(1).getParameters();
			// double dynMut = 1.0 - ((double) evals_ / evaluations_limit_);
			// adapt depending on fitness?
			// double convDynMut = 0.1 + (0.8 * dynMut);

			// Recombination
			// classic crossover
			for (int index = 0; index < dimensions_; index++) {
				if (rnd_.nextDouble() < 0.5) {
					child1[index] = parent1[index];
					child2[index] = parent2[index];
				} else {
					child1[index] = parent2[index];
					child2[index] = parent1[index];
				}
			}

			// mutation
			for (int index = 0; index < dimensions_; index++) {
				if (rnd_.nextDouble() < 0.1) {
					child1[index] = -5 + (10 * rnd_.nextDouble());
				}
				if (rnd_.nextDouble() < 0.1) {
					child2[index] = -5 + (10 * rnd_.nextDouble());
				}
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
				population_.remove(0);
			}
		} catch (Exception e) {
			System.err.println("Exception e: " + e.getLocalizedMessage());
		}
	}

	private void evaluatePopulation() {
		// try {
		// double sum = 0.0;
		//
		// for (Candidate c : population_) {
		// sum += c.getFitness();
		// }
		// System.out.println("Average fitness of generation " + generation_
		// + " is: " + (sum / population_size_));
		//
		// } catch (Exception e) {
		// System.err.println("Exception e: " + e.getLocalizedMessage());
		// }
	}

	public void run() {
		// Run your algorithm here
		dimensions_ = 10;
		generation_ = 1;
		evals_ = 0;

		double mu = population_size_;
		double p = 0.0;
		double s = 1.5;
		double pcumul = 0.0;

		// Initialize population and selection probability distribution
		for (int index = 0; index < population_size_; index++) {
			population_.add(new Candidate(evaluation_, generation_, this));
			// Ranking selection
			p = ((2 - s) / mu) + ((2 * index * (s - 1)) / (mu * (mu - 1)));
			probs_.add(p);
			pcumul = 0.0;
			for (Double d : probs_) {
				pcumul += d;
			}
			rwprobs_.add(pcumul);
		}

		while (/* generation_ < 10 */evals_ < evaluations_limit_) {

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