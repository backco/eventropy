package org.qmpm.entropy.metrics;

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
			String word = "";
			
			for (String activity : traceAsArray) {
				word += activity;
				if (!words.contains(word)) {
					words.add(word);
					word = "";
				}
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
