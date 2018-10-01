package org.qmpm.entropy.metrics;

import org.qmpm.entropy.enums.EntropyMetricLabel;

public class KBlockEntropyRateDiff extends KBlockEntropyRate {

	protected double getRate(double H_k, double H_kMinus1, int k) {
		return H_k - H_kMinus1;
	}
	
	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.KBlockRateDiff;
	}

	@Override
	protected int getKCutOff() {
		return this.kCutOff;
	}
}
