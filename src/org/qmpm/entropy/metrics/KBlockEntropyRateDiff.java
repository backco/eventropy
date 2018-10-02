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
