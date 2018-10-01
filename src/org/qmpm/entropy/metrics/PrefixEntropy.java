package org.qmpm.entropy.metrics;

import java.util.Set;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

public class PrefixEntropy extends Metric {

	@Override
	public Outcome doComputation(Trie t) {
		
		double H = 0.0;
		Set<Node> prefixNodes = t.getNodeSet(false);
		int total = t.getTotalVisits(false);
		int progress = 0;
		
		for (Node node : prefixNodes) {
			
			double p = 0.0;
			if (!node.getIsRoot()) p = (double) node.getVisits() / total;
			try {
				H -= MathTools.information(p);
			} catch (ValueOutOfBoundsException e) {
				e.printStackTrace();
			}
			
			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress/prefixNodes.size());
			if (getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
		}
		
		finished();
		this.value = H;
		return Outcome.SUCCESS;
	}

	@Override
	public void processArgs(String[] args) {
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.Prefix;
	}

	@Override
	public String parametersAsString() {
		return "";
	}

}
