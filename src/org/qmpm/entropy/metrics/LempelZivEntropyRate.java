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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

public class LempelZivEntropyRate extends Metric {
	
	private int i = 0;
	
	@Override
	public Outcome doComputation(Trie t) {
		
		double h;
		int N = 0;
		int N_w = 0;
		Set<String> words = new HashSet<String>(); // TODO: more efficient dictionary for Lempel-Ziv
		int progress = 0;
		int total = t.getEndNodeSet().size() + this.i;

		for (int j = this.i; j < total; j++) {

			int index = j % t.getEndNodeSet().size();
			Node node = t.getEndNodeSet().get(index);
			List<String> traceAsArray = new ArrayList<String>();
			Node currentNode = node;
			
			while (!currentNode.getIsRoot()) {
				traceAsArray.add(currentNode.getParentEdgeLabel().toString());
				currentNode = currentNode.getParent();
			}
			
			Collections.reverse(traceAsArray);
			StringBuilder word = new StringBuilder();
			for (String activity : traceAsArray) {
				word.append(activity);
				String wordStr = word.toString();
				if (!words.contains(wordStr)) {
					words.add(wordStr);
					word.setLength(0);
					continue;
				}
				word.append("/");
			}
			
			N += traceAsArray.size() * node.getEndVisits();
			
			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress / total);
			if (getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
		}
		
		N_w = words.size();
		h = (double) (N_w * MathTools.binaryLog(N)) / N;
		finished();
		this.value = h;
		return Outcome.SUCCESS;
	}

	public void processArgs(String[] args) {
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.LempelZiv;
	}

	@Override
	public String parametersAsString() {
		return "";
	}


}
