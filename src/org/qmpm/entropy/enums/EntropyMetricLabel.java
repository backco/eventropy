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

package org.qmpm.entropy.enums;

import java.util.HashMap;
import java.util.Map;

import org.qmpm.entropy.metrics.GlobalBlockEntropy;
import org.qmpm.entropy.metrics.KBlockEntropy;
import org.qmpm.entropy.metrics.KBlockEntropyRateDiff;
import org.qmpm.entropy.metrics.KBlockEntropyRateRatio;
import org.qmpm.entropy.metrics.KLEntropy;
import org.qmpm.entropy.metrics.KNNEntropy;
import org.qmpm.entropy.metrics.LempelZivEntropyRate;
import org.qmpm.entropy.metrics.PrefixEntropy;
import org.qmpm.entropy.metrics.TraceEntropy;
import org.qmpm.entropy.metrics.UniqueTraces;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.enums.MetricLabel;

public enum EntropyMetricLabel implements MetricLabel {
	
	GlobalBlock("Global block entropy"), 
	KBlock("K-block entropy"), 
	KBlockRateDiff("K-block entropy rate - diff"), 
	KBlockRateRatio("K-block entropy rate - ratio"), 
	KL("Kozachenko-Leonenko entropy"), 
	KNN("K-nearest neighbor entropy"), 
	LempelZiv("Lempel-Ziv entropy"), 
	Prefix("Prefix based entropy"), 
	Trace("Trace entropy"),
	UniqueTraces("Unique traces");
	
	private String desc;
	public Map<Trie, HashMap<Integer, Double>> kBlockMap;
	
	EntropyMetricLabel(String shortDesc) {
		desc = shortDesc;
	}
	
	public Metric delegate(String args[]) {
		
		switch(this) {
		case Trace: 		
			return new TraceEntropy();
		case Prefix: 		
			return new PrefixEntropy();
		case KBlock: 		
			KBlock.kBlockMap = new HashMap<>();
			KBlockEntropy kbe = new KBlockEntropy();
			kbe.processArgs(args);
			return kbe;
		case GlobalBlock: 	return new GlobalBlockEntropy();
		case KL: 			
			KLEntropy kle = new KLEntropy();
			kle.processArgs(args);
			return kle;
		case KNN: 			
			KNNEntropy knne = new KNNEntropy();
			knne.processArgs(args);
			return knne;
		case LempelZiv:		
			return new LempelZivEntropyRate();
		case KBlockRateDiff:
			KBlock.kBlockMap = new HashMap<>();
			KBlockEntropyRateDiff erd = new KBlockEntropyRateDiff();
			erd.processArgs(args);
			return erd;
		case KBlockRateRatio:
			KBlock.kBlockMap = new HashMap<>();
			KBlockEntropyRateRatio err = new KBlockEntropyRateRatio();
			err.processArgs(args);
			return err;
		case UniqueTraces:
			return new UniqueTraces();
		default: 			
			return null;
		}
	}
	
	@Override
	public String labelType() {
		return this.getClass().getSimpleName();
	}

	public String shortDescription() {
		return desc;
	}
	
}
