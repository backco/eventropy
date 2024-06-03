/*
 * 	Eventropy - entropy estimation for XES event logs and other sequential data
 * 
 * 	Author: Christoffer Olling Back	<www.christofferback.com>
 * 
 * 	Copyright (C) 2018 University of Copenhagen 
 * 
 *	This file is part of Eventropy.
 *
 *	Eventropy is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Eventropy is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with Eventropy.  If not, see <https://www.gnu.org/licenses/>.
 */

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

		double V = Math.pow(Math.PI, p / 2.0) / Gamma.gamma((p / 2.0) + 1);
		for (ArrayList<Double> distances : distancesSet) {
			double d;
			if (!distances.isEmpty()) {
				d = (double) distances.get(0); // Distance to first nearest neighbor
			} else {
				return Outcome.ERROR;
			}
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
