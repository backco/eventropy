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
