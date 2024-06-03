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

package org.qmpm.entropy.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.qmpm.entropy.metrics.*;
import org.qmpm.logtrie.trie.AbstractTrieMediator;
import org.qmpm.entropy.trie.EntropyTrieMediator;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.tools.MathTools;

class EntropyTest {

	static AbstractTrieMediator trieMediator = new EntropyTrieMediator();
	static AbstractTrieMediator trieMediatorFlat = new EntropyTrieMediator();
	static List<Trie> tries = new ArrayList<>();
	static List<Trie> triesFlat = new ArrayList<>();
	static int sigDigs = 4;
	
	static String[] files = {
			"logs//running_examples//log1.xes", 
			"logs//running_examples//log2.xes", 
			"logs//running_examples//log3.xes", 
			"logs//running_examples//log4.xes"};

	static double[] traceEntropyVals 	= {3.0000, 2.5477, 3.0000, 3.3219, 7.7518};
	static double[] prefixEntropyValsFlat 	= {4.0931, 4.0931, 5.6288, 4.8219, 12.5381};
	static double[] blockEntropyValsFlat 	= {5.7537, 5.7537, 7.0427, 4.7507, 16.2823};
	static double[] klEntropyValsFlat 		= {1.1702, 1.1702, 2.7775, 2.0813};
	static double[] kNNEntropyValsFlat		= {-0.091, -0.091, 1.5614, 0.6867};
	static double[] lempelZivEntropyVal 	= {0.6097, 0.5807, 0.8129, 0.6115};
	//static double[] medianBranchLenVals = {7.0, 7.0, 6.5, 4.0};
	
	static List<List<List<Double>>> kNNVals = Arrays.asList(
			Arrays.asList(	
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.1429, 0.1429, 0.1667, 0.25, 0.2857, 0.2857, 0.375),
					Arrays.asList(0.1429, 0.1429, 0.1667, 0.25, 0.2857, 0.2857, 0.375),
					Arrays.asList(0.125, 0.125, 0.125, 0.25, 0.25, 0.25, 0.375),
					Arrays.asList(0.125, 0.125, 0.125, 0.25, 0.25, 0.25, 0.375),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286)
			), Arrays.asList(	
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.1429, 0.1429, 0.1667, 0.25, 0.2857, 0.2857, 0.375),
					Arrays.asList(0.1429, 0.1429, 0.1667, 0.25, 0.2857, 0.2857, 0.375),
					Arrays.asList(0.125, 0.125, 0.125, 0.25, 0.25, 0.25, 0.375),
					Arrays.asList(0.125, 0.125, 0.125, 0.25, 0.25, 0.25, 0.375),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286),
					Arrays.asList(0.125, 0.1429, 0.1429, 0.25, 0.2857, 0.2857, 0.4286)
			),  Arrays.asList(	
					Arrays.asList(0.7, 0.75, 0.8, 0.8, 0.8, 0.8182, 0.875),
					Arrays.asList(0.6364, 0.6364, 0.7273, 0.8182, 0.8182, 0.8182, 0.9091),
					Arrays.asList(0.6, 0.625, 0.6364, 0.75, 0.875, 0.875, 1.0),
					Arrays.asList(0.625, 0.7, 0.8, 0.8, 0.8, 0.8182, 0.875),
					Arrays.asList(0.6, 0.7, 0.7, 0.7273, 0.8, 0.8, 0.9),
					Arrays.asList(0.6364, 0.8, 0.8, 0.8, 0.8, 0.875, 1.0),
					Arrays.asList(0.75, 0.75, 0.8, 0.8, 0.8, 0.8, 0.9091),
					Arrays.asList(0.625, 0.625, 0.75, 0.75, 0.8182, 0.875, 0.9)
			),  Arrays.asList(	
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75),
					Arrays.asList(0.25, 0.25, 0.25, 0.25, 0.5, 0.75, 0.75, 0.75, 0.75)
			));
	
	@BeforeAll
	public static void init() {
		
		try {
			//System.out.println("adding files: " + Arrays.toString(files));
			trieMediator.addFiles(Arrays.asList(files));
			trieMediatorFlat.addFiles(Arrays.asList(files));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Framework.setQuiet(true);
		
		trieMediator.setupTries();
		trieMediatorFlat.setupTries();
		
		trieMediator.loadFiles();
		trieMediatorFlat.loadFiles();
		
		trieMediator.buildTries(false);
		trieMediatorFlat.buildTries(true);

		tries = trieMediator.getTries();
		triesFlat = trieMediatorFlat.getTries();
	}
	
	@Test
	public void traceEntropy() {
		
		for (int i=0; i<Math.min(files.length, traceEntropyVals.length); i++) {
			TraceEntropy e = new TraceEntropy(); 
			e.compute(tries.get(i));
			assertEquals(traceEntropyVals[i], MathTools.round(e.getValue(), sigDigs));
		}
	}
	
	@Test
	public void prefixEntropyFlat() {
		for (int i=0; i<Math.min(files.length, prefixEntropyValsFlat.length); i++) {
			PrefixEntropy e = new PrefixEntropy(); 
			e.compute(triesFlat.get(i));
			assertEquals(prefixEntropyValsFlat[i], MathTools.round(e.getValue(), sigDigs));
		}
	}

	@Test
	public void globalBlockEntropyFlat() {
		for (int i=0; i<Math.min(files.length, blockEntropyValsFlat.length); i++) {
			GlobalBlockEntropy e = new GlobalBlockEntropy(); 
			e.compute(triesFlat.get(i));
			assertEquals(blockEntropyValsFlat[i], MathTools.round(e.getValue(), sigDigs));
		}
	}
	
	@Test
	public void levenshtein() {
		
		String[] s1 = {"s","i","t","t","i","n","g"};
		String[] s2 = {"k","i","t","t","e","n"};
		String[] s3 = {"s","a","t","u","r","d","a","y"};
		String[] s4 = {"s","u","n","d","a","y"};
		String[] s5 = {"g","u","m","b","o"};
		String[] s6 = {"g","a","m","b","o","l"};
		
		
		assertEquals(3, EditDistance.levenshtein(s1,s2));
		assertEquals(3, EditDistance.levenshtein(s3,s4));
		assertEquals(2, EditDistance.levenshtein(s5,s6));
	}
	
	@Test
	public void kNearestLevenshteinFlat() {
		for (int i=0; i<Math.min(files.length, kNNVals.size()); i++) {
			
			Trie t = triesFlat.get(i);
			List<ArrayList<Double>> distances = EditDistance.kNearestLevenshtein(11, true, t);
			
			for (List<Double> dist : distances) {

				List<Double> distRounded = new ArrayList<Double>();
				
				for (Double d : dist) {
					distRounded.add(MathTools.round(d, sigDigs));
				}
				
				assertTrue(kNNVals.get(i).contains(distRounded));				
			}
		}
	}
	
	@Test
	public void klEntropyFlat() {
		for (int i=0; i<Math.min(files.length, klEntropyValsFlat.length); i++) {
			//System.out.println(i + " of " + klEntropyValsFlat.length);
			KLEntropy e = new KLEntropy();
			e.setDimensions(1);
			e.compute(triesFlat.get(i));
			assertEquals(klEntropyValsFlat[i], MathTools.round(e.getValue(), sigDigs));
		}
	}
		
	@Test
	public void kNNEntropyFlat() {
		for (int i=0; i<Math.min(files.length, kNNEntropyValsFlat.length); i++) {
			KNNEntropy e = new KNNEntropy();
			e.setK(3);
			e.compute(triesFlat.get(i));
			assertEquals(kNNEntropyValsFlat[i], MathTools.round(e.getValue(), sigDigs));
		}
	}

	@Test
	public void lempelZivEntropy() {

		for (int i=0; i<Math.min(files.length, lempelZivEntropyVal.length); i++) {
			LempelZivEntropyRate e = new LempelZivEntropyRate();
			e.compute(tries.get(i));
			assertEquals(lempelZivEntropyVal[i], MathTools.round(e.getValue(), sigDigs));
		}
	}
}
