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

package org.qmpm.entropy.trie;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.trie.AbstractTrieMediator;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.TrieImpl;
import org.qmpm.logtrie.tools.FileInfo;

public class EntropyTrieMediator extends AbstractTrieMediator {

	private Map<Trie, TrieAttributes> trieAttMap = new HashMap<>();
	
	@Override
	protected void beforeTrieBuild(ListIterator<Trie> i, Trie t, int current, int total, String labelFormat) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getLastMetric() {
		return EntropyMetricLabel.class.getSimpleName();
	}

	@Override
	public TrieAttributes getTrieAttributes(Trie t) {
		return trieAttMap.get(t);
	}

	@Override
	protected void setTrieAttributes(Trie t, TrieAttributes ta) {
		trieAttMap.put(t, ta);
	}

	@Override
	public void setupTries() {
		
		for (FileInfo fi : files) {
			
			Trie t = new TrieImpl();
			TrieAttributes trieAtt = new TrieAttributes(t, fi.getFile().getName(), fi);
			tries.add(t);
			setTrieAttributes(t, trieAtt);
		}
	}

}
