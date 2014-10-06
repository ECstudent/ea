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
	private double prevAvgFitness_;
	private double avgFitness_;
	private double prevBestFitness_;
	private double bestFitness_;
	private double mutRate_;
	private boolean singleMut_;
	private List<Candidate> population_;
	private List<Double> probs_;
	private List<Double> rwprobs_;
	private boolean isMultimodal_;
	private boolean hasStructure_;
	private boolean isSeparable_;
	private boolean improv_;

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
		// stDevs_; is set in initializePopulation
		// avgParams_; is set in initializePopulation
		// prevAvgFitness_; is set in initializePopulation
		// avgFitness_; is set in initializePopulation
		// prevBestFitness_; is set in initializePopulation
		// bestFitness_; is set in initializePopulation
		// mutRate_; is set in setEvaluation
		singleMut_ = false;
		population_ = new ArrayList<Candidate>();
		probs_ = new ArrayList<Double>();
		rwprobs_ = new ArrayList<Double>();
		// isMultimodal_; is set in setEvaluation
		// hasStructure_; is set in setEvaluation
		// isSeparable_; is set in setEvaluation
		improv_ = false;
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
			numParents_ = 10;
			mutRate_ = 0.3;
			// delete oldest during survivor selection
			// use SUS parent selection
		} else {
			population_size_ = 8;
			numParents_ = 2;
			mutRate_ = 0.1;
		}
		if (hasStructure_) {
		}
		if (isSeparable_) {
		}

	}

	private void initializePopulation() {
		double mu = population_size_;
		double p = 0.0;
		double s = 2.0;
		double pcumul = 0.0;

		if (isMultimodal_) {
			s = 1.5;
		}

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
		bestFitness_ = getBestFitness();
		prevBestFitness_ = bestFitness_;
	}

	private List<Candidate> selectParents() {
		List<Candidate> parents = new ArrayList<Candidate>();
		int index = 0;
		int inner = 0;
		double r = 0.0;

		// sort population by fitness
		Collections.sort(population_);

		if (isMultimodal_) {
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
		} else {
			// Retrieve the best candidates
			for (int index2 = 1; index2 <= numParents_; index2++) {
				parents.add(population_.get(population_size_ - index2));
			}
		}

		return parents;
	}

	private List<Candidate> createChildren(List<Candidate> parents) {
		List<double[]> childStDevs = new ArrayList<double[]>();
		List<double[]> childParams = new ArrayList<double[]>();
		List<double[]> parentStDevs = new ArrayList<double[]>();
		List<double[]> parentParams = new ArrayList<double[]>();
		List<Candidate> children = new ArrayList<Candidate>();
		double minStDev = 0.1;
		double alpha = 0.75;
		double learningRate1 = 1 / Math.sqrt(2 * dimensions_);
		double learningRate2 = 1 / Math.sqrt(2 * Math.sqrt(dimensions_));
		int mutGene = 0;
		long accuracy = 1000000000000000L;
		boolean duplicate = false;

		for (int index = 0; index < numParents_; index++) {
			parentParams.add(parents.get(index).getParameters());
			parentStDevs.add(parents.get(index).getStandardDevs());
		}

		mating: while (children.size() < numParents_) {
			childParams = new ArrayList<double[]>();
			childStDevs = new ArrayList<double[]>();
			for (int index = 0; index < numParents_; index++) {
				childParams.add(new double[dimensions_]);
				childStDevs.add(new double[dimensions_]);
			}

			// recombination
			for (int gene = 0; gene < dimensions_; gene++) {
				for (int candidate = 0; candidate < numParents_; candidate += 2) {
					// Arithmetic recombination per gene
					childParams.get(candidate)[gene] = alpha
							* parentParams.get(candidate)[gene] + (1 - alpha)
							* parentParams.get(candidate + 1)[gene];
					childParams.get(candidate + 1)[gene] = alpha
							* parentParams.get(candidate + 1)[gene]
							+ (1 - alpha) * parentParams.get(candidate)[gene];
					childStDevs.get(candidate)[gene] = alpha
							* parentStDevs.get(candidate)[gene] + (1 - alpha)
							* parentStDevs.get(candidate + 1)[gene];
					childStDevs.get(candidate + 1)[gene] = alpha
							* parentStDevs.get(candidate + 1)[gene]
							+ (1 - alpha) * parentStDevs.get(candidate)[gene];
				}
			}

			// mutation
			for (int child = 0; child < numParents_; child++) {
				if (singleMut_) {
					mutGene = rnd_.nextInt(dimensions_);
					// if (hasStructure_) {
					childStDevs.get(child)[mutGene] *= Math.exp(learningRate1
							* rnd_.nextGaussian() + learningRate2
							* rnd_.nextGaussian());
					childStDevs.get(child)[mutGene] = childStDevs.get(child)[mutGene] < minStDev ? minStDev
							: childStDevs.get(child)[mutGene];
					childParams.get(child)[mutGene] += childStDevs.get(child)[mutGene]
							* rnd_.nextGaussian();
					childParams.get(child)[mutGene] = childParams.get(child)[mutGene] < -5 ? -5
							: childParams.get(child)[mutGene] > 5 ? 5
									: childParams.get(child)[mutGene];
					// childParams.get(child)[mutGene] = -5
					// + (10 * rnd_.nextDouble());
					// } else {
					// }
				} else {
					for (int gene = 0; gene < dimensions_; gene++) {
						if (rnd_.nextDouble() < mutRate_) {
							// if (hasStructure_) {
							childStDevs.get(child)[gene] *= Math
									.exp(learningRate1 * rnd_.nextGaussian()
											+ learningRate2
											* rnd_.nextGaussian());
							childStDevs.get(child)[gene] = childStDevs
									.get(child)[gene] < minStDev ? minStDev
									: childStDevs.get(child)[gene];
							childParams.get(child)[gene] += childStDevs
									.get(child)[gene] * rnd_.nextGaussian();
							childParams.get(child)[gene] = childParams
									.get(child)[gene] < -5 ? -5 : childParams
									.get(child)[gene] > 5 ? 5 : childParams
									.get(child)[gene];
							// childParams.get(child)[gene] = -5
							// + (10 * rnd_.nextDouble());
							// } else {
							// }
						}
					}
				}
			}

			// check for duplicates in the population
			for (int child = 0; child < numParents_; child++) {
				duplicate = false;
				popsearch: for (Candidate c : population_) {
					for (int gene = 0; gene < dimensions_; gene++) {
						if ((long) (c.getParameters()[gene] * accuracy) != (long) (childParams
								.get(child)[gene] * accuracy)) {
							continue popsearch;
						}
					}
					duplicate = true;
					break;
				}
				if (evals_ >= evaluations_limit_) {
					break mating;
				} else if (!duplicate) {
					children.add(new Candidate(evaluation_, generation_, this,
							childParams.get(child), childStDevs.get(child)));
				}
			}
		}

		return children;
	}

	private void selectSurvivors(List<Candidate> children,
			List<Candidate> parents) {
		Candidate oldestC = null;
		int oldest = Integer.MAX_VALUE;
		// int numOldRemoved = 1;
		int exception = 1;

		// Add children to the population
		for (Candidate c : children) {
			population_.add(c);
		}

		// Sort population by fitness
		Collections.sort(population_);

		if (isMultimodal_) {
			while (population_.size() > population_size_) {
				oldest = Integer.MAX_VALUE;
				for (Candidate c : population_) {
					if (c.getGeneration() <= oldest) {
						oldest = c.getGeneration();
						oldestC = c;
					}
				}
				population_.remove(oldestC);
			}
		} else {
			while (population_.size() > population_size_) {
				population_
						.remove(rnd_.nextInt(population_.size() - exception));
			}
		}

		// // delete the numOldRemoved oldest
		// if (isMultimodal_ && generation_ % 10 == 0) {
		// for (int old = 0; old < numOldRemoved
		// && population_.size() > population_size_; old++) {
		// oldest = Integer.MAX_VALUE;
		// for (Candidate c : population_) {
		// if (c.getGeneration() <= oldest) {
		// oldest = c.getGeneration();
		// oldestC = c;
		// }
		// }
		// population_.remove(oldestC);
		// }
		// }
		//
		// // Randomly remove candidates (best candidate excluded)
		// while (population_.size() > population_size_) {
		// population_.remove(rnd_.nextInt(population_.size() - exception));
		// }
	}

	private void evaluatePopulation() {
		prevAvgFitness_ = avgFitness_;
		avgFitness_ = getAverageFitness();

		if (avgFitness_ == prevAvgFitness_) {
			singleMut_ = true;
		} else {
			singleMut_ = false;
		}

		if (generation_ % 10 == 0) {
			prevBestFitness_ = bestFitness_;
			bestFitness_ = getBestFitness();

			if (bestFitness_ > prevBestFitness_) {
				improv_ = true;
			} else {
				improv_ = false;
			}
			System.out.println("Generation: " + generation_ + " Average: "
					+ avgFitness_ + " Best: " + bestFitness_ + " Improvement? "
					+ (improv_ ? true : ""));
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

	private double getBestFitness() {
		double best = Double.MIN_VALUE;
		for (Candidate c : population_) {
			if (c.getFitness() > best) {
				best = c.getFitness();
			}
		}
		return best;
	}

	public void run() {
		long start = System.currentTimeMillis();
		// Run your algorithm here

		// initialize the population
		initializePopulation();

		while (evals_ < evaluations_limit_) {

			// parent selection
			List<Candidate> parents = selectParents();

			// child creation
			List<Candidate> children = createChildren(parents);

			// survivor selection
			selectSurvivors(children, parents);

			// population evaluation
			evaluatePopulation();
		}
		System.out.println(population_);
		System.out.println("Final result: " + evaluation_.getFinalResult());
		System.out.println("Duration: " + (System.currentTimeMillis() - start)
				+ "ms");
	}
}