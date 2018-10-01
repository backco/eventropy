package org.qmpm.entropy.core;

import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.trie.AbstractTrieMediator;
import org.qmpm.logtrie.ui.CLI;
import org.qmpm.entropy.trie.EntropyTrieMediator;
import org.qmpm.entropy.ui.EntropyCLI;

public class Main {

	public static void main(String[] args) {

		CLI entropyCLI = new EntropyCLI();
		AbstractTrieMediator entropyTrieMediator = new EntropyTrieMediator();
		
		Framework.run(entropyCLI, args, entropyTrieMediator);
	}

}
