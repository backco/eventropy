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
