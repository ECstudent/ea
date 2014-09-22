import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import org.vu.contest.ContestEvaluation;

public class Candidate implements Comparable<Candidate> {

	private ContestEvaluation evaluation_;
	private double[] parameters_;
	private Random rnd_;
	private Double fitness_;
	private int generation_;
	private final int dimensions_ = 10;
	private final int rangeMin_ = -5;
	private final int rangeMax_ = 5;

	/**
	 * Creates a new candidate.
	 * 
	 * @param evaluation
	 * @param generation
	 */
	public Candidate(ContestEvaluation evaluation, int generation, Player20 p) {
		evaluation_ = evaluation;
		generation_ = generation;
		parameters_ = new double[dimensions_];
		rnd_ = new Random();
		rnd_.setSeed(System.nanoTime());
		for (int index = 0; index < dimensions_; index++) {
			parameters_[index] = rangeMin_
					+ ((rangeMax_ - rangeMin_) * rnd_.nextDouble());
		}
		fitness_ = (Double) evaluation_.evaluate(parameters_);
		p.evals_++;
	}

	/**
	 * Creates a candidate using an existing list of parameters.
	 * 
	 * @param evaluation
	 * @param generation
	 * @param parameters
	 */
	public Candidate(ContestEvaluation evaluation, int generation, Player20 p,
			double[] parameters) {
		evaluation_ = evaluation;
		generation_ = generation;
		parameters_ = parameters;
		fitness_ = (Double) evaluation_.evaluate(parameters_);
		p.evals_++;
	}

	public double[] getParameters() {
		return parameters_;
	}

	public int getGeneration() {
		return generation_;
	}

	public Double getFitness() {
		return fitness_;
	}

	@Override
	public int compareTo(Candidate c) {
		if (this.fitness_ > c.fitness_) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Candidate fitness: " + fitness_;
	}

}
