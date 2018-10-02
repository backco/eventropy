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
