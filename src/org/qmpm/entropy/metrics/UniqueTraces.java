package org.qmpm.entropy.metrics;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.metrics.Metric;

public class UniqueTraces extends Metric {

	@Override
	public Outcome doComputation(Trie t) {
		
		int totalTraces = t.getTotalEndVisits(false);
		int uniqueTraces = t.getEndNodeSet().size();
		
		finished();
		
		this.value = (double) uniqueTraces / totalTraces;
		
		return Outcome.SUCCESS;
	}

	@Override
	public void processArgs(String[] args) {
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.UniqueTraces;
	}

	@Override
	public String parametersAsString() {
		return "";
	}
	
}
