package org.qmpm.entropy.metrics;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.special.Gamma;
import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.metrics.Metric;

public class KLEntropy extends Metric {

	private int p = 1;
	
	@Override
	public Outcome doComputation(Trie t) {
		
		if (t.getEndNodeSet().size() < 2) {
			return Outcome.ERROR;
		}
		
		List<ArrayList<Double>> distancesSet = EditDistance.kNearestLevenshtein(1, true, t, this);
		
		// Update progress of associated Metric object, check for timeout or error
		if (getOutcome() != Outcome.CONTINUE) {
			return getOutcome();
		}
		
		double H = 0.0;
		int n = distancesSet.size();
		
		for (ArrayList<Double> distances : distancesSet) {
			double d;
			if (!distances.isEmpty()) {
				d = (double) distances.get(0); // Distance to first nearest neighbor
			} else {
				return Outcome.ERROR;
			}
			double V = Math.pow(Math.PI, p / 2.0) / Gamma.gamma((p / 2.0) + 1);
			H += Math.log(d) + Math.log(V) + Gamma.GAMMA + Math.log(n - 1);
		}
		
		// Update progress of associated Metric object
		updateProgress(1.0);
		finished();
		
		H = ((double) p / n) * H;
		value = H;
		return Outcome.SUCCESS;
	}

	@Override
	public void processArgs(String[] args) {
		for (int i=0; i<args.length; i++) {
			switch(i) {
			case 0: 
				try {
					p = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					System.out.println("Integer value expected for dimension parameter. Received: " + args[i]);
					e.printStackTrace();
				};
			}
		}
	}
	

	public void setDimensions(int dim) {
		p = dim;
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.KL;
	}

	@Override
	public String parametersAsString() {
		return String.valueOf(p);
	}
}
