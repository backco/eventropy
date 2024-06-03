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

import java.util.HashMap;
import java.util.Map;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

abstract public class KBlockEntropyRate extends Metric {
	
	/*
	 * Explanation of variable names
	 * h := current estimate of entropy rate
	 * k := size of block
	 * K := total length of sequence
	 * S := size of alphabet(Sigma)
	 */
	
	public enum Constraint {
		One("1","k < log(K)/h", 					(h,k,K,S) -> (k > MathTools.binaryLog(K) / h)), 
		Two("2", "k < kh/log(|S|)",					(h,k,K,S) -> (k > K * h / MathTools.binaryLog(S))), 
		Three("3","k|S|^k <= K",					(h,k,K,S) -> (k * Math.pow(S, k) > K)), 
		Four("4","log(|S|)k|S|^k <= Kh",			(h,k,K,S) -> (K * h < k * Math.pow(S, k) * MathTools.binaryLog(S))), 
		//Five("5","log(|S|)ke^(kh) <= Kh",			(h,k,K,S) -> (K * h < k * Math.pow(Math.E, k * h) * MathTools.binaryLog(S)));
		Five("5","log(|S|)ke^(kh) <= Kh",			(h,k,K,S) -> (K * h < k * Math.pow(2, k * h) * MathTools.binaryLog(S)));
		
		private String desc;
		@SuppressWarnings("unused")
		private String description;
		private Condition cond;
		
		Constraint(String shortDesc, String longDesc, Condition c) { 
			desc = shortDesc; 
			description = longDesc;
			cond = c;
		}
		
		public boolean isViolated(double h, int k, int K, int S) {
			return cond.isViolated(h,k,K,S);
		}
		
		@Override
		public String toString() {
			return desc;
		}
		
		interface Condition { boolean isViolated(double h, int k, int K, int S); }
	}
	

	private Constraint constraint = Constraint.One;;
	protected Double lastValue;
	protected int kCutOff = 0;
	
	protected abstract double getRate(double H_k, double H_kMinus1, int k);
	protected abstract int getKCutOff();
	//protected abstract Double getValue();
	
	@Override
	public Outcome doComputation(Trie t) {
		return doComputation(t, null);
	}
	
	public Outcome doComputation(Trie t, Double hEstimate) {
		
		// TODO: Find safer solution storing KBlock values
		Map<Trie, HashMap<Integer, Double>> kBlockMapMap = EntropyMetricLabel.KBlock.kBlockMap;
		kBlockMapMap.putIfAbsent(t, new HashMap<>());
		Map<Integer, Double> kBlockMap = kBlockMapMap.get(t);
		
		//double h;	
		int K = t.getLongestBranch();
		double H_kMinus1 = 0.0;
		int progress = 0;
		
		int S = t.getElementLabels().size();

		for (Integer k = 1; k < K; k++) {
			
			double H_k;
			
			if (kBlockMap.containsKey(k)) {
			
				H_k = kBlockMap.get(k);
			
			} else {
			
				KBlockEntropy kbe = new KBlockEntropy();
				long elapsed = progObs.getTimeElapsed(this);

				kbe.registerProgObs(progObs);
				kbe.setTimeout(timeout-elapsed); // Not allowed to use more than remaining time limit at most
				kbe.setK(k);
				progObs.startTimer(kbe);
				kbe.compute(t);
				
				if (kbe.getOutcome() != Outcome.CONTINUE) {
					return kbe.getOutcome();
				}
				
				H_k = kbe.getValue();
				kBlockMap.put(k, H_k);
			}
			
			double h_k = getRate(H_k, H_kMinus1, k);
			H_kMinus1 = H_k;

			this.lastValue = value;
			this.value = h_k;
			this.kCutOff = k;
			
			updateProgress((double) ++progress / K);
			
			if(getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
			
			// Use provide estimate of h for constraint evaluation if provided, otherwise use most recent h_k
			if (constraint.isViolated(hEstimate == null ? h_k : hEstimate, k, K, S)) {					
				updateProgress(1.0);
				finished();
				return Outcome.SUCCESS;
			}
		}
		
		finished();
		return Outcome.SUCCESS;
	}

	public void processArgs(String[] args) {
		switch(Integer.parseInt(args[0])) {
		case 1: this.constraint = Constraint.One; break;
		case 2: this.constraint = Constraint.Two; break;
		case 3: this.constraint = Constraint.Three; break;
		case 4: this.constraint = Constraint.Four; break;
		case 5: this.constraint = Constraint.Five; break;
		}
	}
	
	
	protected String setName() {
		return "K Block Entropy Rate";
	}
	
	
	@Override
	public String parametersAsString() {
		return constraint.toString() + " [k=" + getKCutOff() + "]";
	}
}
