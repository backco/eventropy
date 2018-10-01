package org.qmpm.entropy.metrics;

import java.util.List;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

public class TraceEntropy extends Metric {

	@Override
	public Outcome doComputation(Trie t) {
		
		double H = 0.0;
		List<Node> endNodes = t.getEndNodeSet();
		int total = t.getTotalEndVisits(true);
		int progress=0;
		
		for (Node node : endNodes) {
			
			double p = (double) node.getEndVisits() / total;
			try {
				H -= MathTools.information(p);
			} catch (ValueOutOfBoundsException e) {
				e.printStackTrace();
			}

			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress/endNodes.size());
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
		return EntropyMetricLabel.Trace;
	}

	@Override
	public String parametersAsString() {
		return "";
	}
	
}
