import org.vu.contest.ContestSubmission;
import org.vu.contest.ContestEvaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class player20 implements ContestSubmission {

	Random rnd_;
	ContestEvaluation evaluation_;
	// ExcelFile ef;
	public int evals_;
	private int numParents_;
	private int dimensions_;
	private int generation_;
	private int evaluations_limit_;
	private int population_size_;
	private double stDev_;
	private double prevAvgFitness_;
	private double avgFitness_;
	private boolean singleMut_;
	private List<Candidate> population_;
	private List<Double> probs_;
	private List<Double> rwprobs_;
	private boolean isMultimodal_;
	private boolean hasStructure_;
	private boolean isSeparable_;

	public player20() {
		rnd_ = new Random();
		// evaluation_; is set in setEvaluation
		// ExcelFile ef; unused
		evals_ = 0;
		numParents_ = 2;
		dimensions_ = 10;
		generation_ = 1;
		// evaluations_limit_; is set in setEvaluation
		// population_size_; is set in setEvaluation
		// stDev_; is set in initializePopulation
		// prevAvgFitness_; is set in initializePopulation
		// avgFitness_; is set in initializePopulation
		singleMut_ = false;
		population_ = new ArrayList<Candidate>();
		probs_ = new ArrayList<Double>();
		rwprobs_ = new ArrayList<Double>();
		// isMultimodal_; is set in setEvaluation
		// hasStructure_; is set in setEvaluation
		// isSeparable_; is set in setEvaluation
	}

	public static void main(String[] args) {
		// Initialize population
		player20 p = new player20();
		p.setSeed(System.nanoTime());
		p.setEvaluation(new SphereEvaluation());
		p.run();
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
			population_size_ = numParents_ * 2;
		}
		if (hasStructure_) {
		}
		if (isSeparable_) {
		}

	}

	private void initializePopulation() {
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

		avgFitness_ = getAverageFitness();
		prevAvgFitness_ = avgFitness_;
		stDev_ = getStandardDeviation();
	}

	private List<Candidate> selectParents() {
		int index = 0;
		int inner = 0;
		double r = 0.0;
		List<Candidate> parents = new ArrayList<Candidate>();

		// sort population by fitness
		Collections.sort(population_);

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
		while (index < numParents_) {
			while (r <= rwprobs_.get(inner)) {
				parents.add(population_.get(inner));
				r += (1.0 / population_size_);
				index++;
			}
			inner++;
		}

		return parents;
	}

	private List<Candidate> createChildren(List<Candidate> parents) {
		List<Candidate> children = new ArrayList<Candidate>();
		double[] child1 = new double[dimensions_];
		double[] child2 = new double[dimensions_];
		double[] parent1 = parents.get(0).getParameters();
		double[] parent2 = parents.get(1).getParameters();
		// (((OldValue - OldMin) * NewRange) / OldRange) + NewMin
		// double dynMut = ((double) evals_) / evaluations_limit_;
		// double convDynMut = 0.1 + (0.8 * dynMut);
		double alpha = 0.6;

		// Arithmetic recombination
		for (int index = 0; index < dimensions_; index++) {
			child1[index] = alpha * parent1[index] + (1 - alpha)
					* parent2[index];
			child2[index] = alpha * parent2[index] + (1 - alpha)
					* parent1[index];
		}

		// mutation
		if (singleMut_) {
			child1[rnd_.nextInt(dimensions_)] = -5 + (10 * rnd_.nextDouble());
			child2[rnd_.nextInt(dimensions_)] = -5 + (10 * rnd_.nextDouble());
		} else {
			for (int index = 0; index < dimensions_; index++) {
				if (rnd_.nextDouble() < 0.1) {
					child1[index] = -5 + (10 * rnd_.nextDouble());
				}
				if (rnd_.nextDouble() < 0.1) {
					child2[index] = -5 + (10 * rnd_.nextDouble());
				}
			}
		}

		children.add(new Candidate(evaluation_, generation_, this, child1));
		children.add(new Candidate(evaluation_, generation_, this, child2));

		return children;
	}

	private void selectSurvivors(List<Candidate> children,
			List<Candidate> parents) {
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
	}

	private void evaluatePopulation() {
		prevAvgFitness_ = avgFitness_;
		avgFitness_ = getAverageFitness();
		stDev_ = getStandardDeviation();

		// if the population has not improved
		if (avgFitness_ == prevAvgFitness_) {
			singleMut_ = true;
		} else {
			singleMut_ = false;
		}

		if (generation_ % 100 == 0) {
			System.out.println("Average fitness of generation " + generation_
					+ " is: " + avgFitness_);
		}
		generation_++;
	}

	private double getAverageFitness() {
		double sum = 0.0;
		for (Candidate c : population_) {
			sum += c.getFitness();
		}
		return sum / population_.size();
	}

	private double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}

	private double getVariance() {
		double sum = 0.0;
		double sqdiff = 0.0;
		for (Candidate c : population_) {
			sqdiff = Math.abs(c.getFitness() - avgFitness_);
			sqdiff *= sqdiff;
			sum += sqdiff;
		}
		return sum / population_.size();
	}

	public void run() {
		// Run your algorithm here

		// initialize the population
		initializePopulation();

		while (/* generation_ < 10 */evals_ < evaluations_limit_) {

			// parent selection
			List<Candidate> parents = selectParents();

			// child creation
			List<Candidate> children = createChildren(parents);

			// survivor selection
			selectSurvivors(children, parents);

			// population evaluation
			evaluatePopulation();
		}
		System.out.println("Final result: " + evaluation_.getFinalResult());
	}
}