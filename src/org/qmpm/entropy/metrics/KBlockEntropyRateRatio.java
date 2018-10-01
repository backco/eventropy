package org.qmpm.entropy.metrics;

import org.qmpm.entropy.enums.EntropyMetricLabel;

public class KBlockEntropyRateRatio extends KBlockEntropyRate {
	
	protected double getRate(double H_k, double H_kMinus1, int k) {
		return H_k / k;
	}
	
	@Override
	public Double getValue() {
		if (lastValue == null) {
			return value;
		} else {
			return lastValue;
		}
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.KBlockRateRatio;
	}

	@Override
	protected int getKCutOff() {
		if (lastValue == null) {
			return this.kCutOff;
		} else {
			return this.kCutOff-1;
		}
	}

}
