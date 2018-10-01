package org.qmpm.entropy.metrics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.special.Gamma;
import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

public class KNNEntropy extends Metric {

	int k = 1;
	int p = 1;
	String max = "";

	@Override
	public Outcome doComputation(Trie t) {
		
		/*
		int k;
		
		if (this.k > t.getEndNodeSet().size()) {
			k = t.getEndNodeSet().size()-1;
			this.max = "{MAX:" + k + "}";
		} else {
			k = this.k;
		}
		*/
		
		List<ArrayList<Double>> distancesSet = EditDistance.kNearestLevenshtein(k, true, t, this);
		
		if (getOutcome() != Outcome.CONTINUE) {
			return getOutcome();
		}
		
		double H = 0.0;
		int n = distancesSet.size();
		
		for (ArrayList<Double> distances : distancesSet) {
			double d;
			if (!distances.isEmpty()) {
				d = MathTools.maximum(distances);
			} else {
				return Outcome.ERROR;
			}
			double V = Math.pow(Math.PI, p / 2.0) / Gamma.gamma((p / 2.0) + 1);
			H += Math.log(d) + Math.log(V) + Gamma.GAMMA - L(k - 1) + Math.log(n);
		}
		
		updateProgress(1.0);
		finished();		
		
		H = ((double) p / n) * H;
		this.value = H;
		return Outcome.SUCCESS;
	}

	private Double L(int j) {
		if (j < 0) {
			return null;
		} else if (j == 0) {
			return 0.0;
		} else {
			Double L_j = 0.0;
			for (int i = 1; i <= j; i++) {
				L_j += 1.0 / i;
			}
			return L_j;
		}
	}
	
	@Override
	public void processArgs(String[] args) {
		
		// TODO: add breaks / throwException instead of print to console
		
		for (int i=0; i<args.length; i++) {
		
			switch(i) {
			case 0: 
				try {
					this.k = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					System.out.println("Integer value expected for k-block parameter. Received: " + args[i]);
					e.printStackTrace();
				};
			case 1: 
				try {
					this.p = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					System.out.println("Integer value expected for dimension parameter. Received: " + args[i]);
					e.printStackTrace();
				};
			}
		}
	}
	
	protected String setName() {
		return "K Nearest Neighbors Entropy";
	}

	public void setK(int K) {
		k = K;
	}
	
	public void setDimensions(int dim) {
		p = dim;
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.KNN;
	}

	@Override
	public String parametersAsString() {
		return String.valueOf(k) + "," + String.valueOf(p);
	}
}
