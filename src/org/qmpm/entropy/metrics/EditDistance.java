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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.qmpm.logtrie.tools.MathTools;
//import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.elementlabel.ElementLabel;
//import org.qmpm.logtrie.core.Framework;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.Trie.Node;

public class EditDistance {
	
	public static <E> int levenshtein(E[] s, E[] t, int max) {
		return levenshtein(Arrays.asList(s), Arrays.asList(t), max);
	}
	
	public static <E> int levenshtein(E[] s, E[] t) {
		return levenshtein(Arrays.asList(s), Arrays.asList(t));
	}
	
	public static <E> int levenshtein(List<E> s, List<E> t) {
		
		int[] lengths = {s.size(), t.size()};
		int max = MathTools.maximum(lengths);
		
		return levenshtein(s, t, max);
	}
	
	public static <E> int levenshtein(List<E> s, List<E> t, double max) {
		
		int m = s.size();
		int n = t.size();
		int shortest = Math.min(m, n);
	    
	    // skip matching prefixes
	    int k = 0;
	    while(s.get(k).equals(t.get(k))) {
	    	if (k==shortest-1) {
	    		break;
	    	}
	    	k++;
	    }
	    
	    // skip matching suffixes
	    int l = 0;
	    
	    while(s.get(m-l-1).equals(t.get(n-l-1)) ) {
	    	l++;
	    	if (shortest - l - k <= 0) break;
	    }
	    	    
	    List<E> shrt = s.subList(k, s.size()-l);
	    List<E> lng = t.subList(k, t.size()-l);
	    
	    if (shrt.size() > lng.size()) {
	    	shrt = t;
	    	lng = s;
	    }

	    m = shrt.size();
	    n = lng.size();
	    
	    int[] buffer = new int[m+1];
	    
	    for (int i=0; i <= m; i++) {
	        buffer[i] = i;
	    }
	    
	    for (int i = 1; i <= n; ++i) {
	    	
	    	int tmp = buffer[0]++;
	    	
	    	for (int j = 1; j < buffer.length; ++j) {
	    		
	    		int p = buffer[j - 1];
	    		int r = buffer[j];
	    		int eql = (shrt.size() == 0 || lng.size() == 0) ? 1 : (lng.get(i - 1).equals(shrt.get(j - 1)) ? 0 : 1);
	    		tmp = Math.min(Math.min(p + 1, r + 1), tmp + eql);
	    		int temp = tmp;
	    		tmp = buffer[j];
	    		buffer[j] = temp;
	    	}
	    }

	    return buffer[m];

	}

	public static <E> float levenshteinNormalized(List<E> s, List<E> t, double max) {
		
		int[] lengths = {s.size(), t.size()};
		double m = MathTools.round(max * MathTools.maximum(lengths), 0);
		return (float) levenshtein(s, t, m) / Math.max(s.size(), t.size());
	}
	
	public static <E> double levenshteinNormalized(List<E> s, List<E> t) {
		
		return (double) levenshtein(s, t) / Math.max(s.size(), t.size());
	}
	
	public static List<ArrayList<Double>> kNearestLevenshtein(int k, boolean normalize, Trie trie) {
		return kNearestLevenshtein(k, normalize, trie, null);
	}
	
	public static List<ArrayList<Double>> kNearestLevenshtein(int k, boolean normalize, Trie trie, Metric met) {
		
		List<ArrayList<Double>> result = new ArrayList<>();
		Collections.sort(trie.getEndNodeSet(), (Node n, Node m) -> n.getID() == m.getID() ? 0 : (n.getID() > m.getID() ? 1 : -1));

		int progress = 0;
		double total = trie.getEndNodeSet().size();
		int n = trie.getEndNodeSet().size();		
		boolean fitsInMemory = ((long) 5 * n * n) < Runtime.getRuntime().maxMemory();
		int y = fitsInMemory ? n : k;		
		float[][] distanceMatrix = new float[n][y]; 
		
		k = k > n-1 ? n-1 : k;
		
		for (int i=0; i<n; i++) {
			
			//Framework.permitOutput();
			//if (i % 100 == 0) System.out.println(i + " of " + n);
			//Framework.resetQuiet();
			
			float[] distances = new float[fitsInMemory ? 0 : n];
			
			for (int j= fitsInMemory ? i : 0; j<n; j++) {
				
				List<ElementLabel> s = trie.getVisitingPrefix(trie.getEndNodeSet().get(i));
				List<ElementLabel> t = trie.getVisitingPrefix(trie.getEndNodeSet().get(j));
				//byte d = (byte) (i==j ? Byte.MAX_VALUE : MathTools.round(levenshteinNormalized(s, t) * 100, 0));
				float d = (float) (i==j ? Float.MAX_VALUE : (normalize ? levenshteinNormalized(s, t) : levenshtein(s, t)));
				
				if (fitsInMemory) {
					distanceMatrix[i][j] = d;
					distanceMatrix[j][i] = d;
				} else {
					distances[j] = d;
				}
			}
			
			if (!fitsInMemory) {
				distanceMatrix[i] = MathTools.minimum(distances, k);
			}
			
			// Update progress of associated Metric object, check for timeout or error
			if (met != null) {
				
				met.updateProgress(++progress / total);
				
				if (met.getOutcome() != Outcome.CONTINUE) {
					met.setOutcome(Outcome.TIMEOUT);
					return null;
				}
			}
		}
		
		for (int i=0; i<n; i++) {

			float[] min = MathTools.minimum(distanceMatrix[i], k);
			
			ArrayList<Double> row = new ArrayList<>();
			
			for (float f : min) {
				double d = f;
				row.add((Double) d);
			}
			
			result.add(row);			
		}

		return result;
	}

	public static ArrayList<Double> addDistance(ArrayList<Double> distances, Double d, int k) {
		
		if (distances.size() < k) {
			distances.add(d);
			return distances;
		} else {
			Double max = MathTools.maximum(distances);
			if (d >= max) {
				return distances;
			} else {
				distances.remove(max);
				distances.add(d);
				return distances;
			}
		}
	}

}


