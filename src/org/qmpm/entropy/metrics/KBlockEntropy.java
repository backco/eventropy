package org.qmpm.entropy.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.qmpm.logtrie.elementlabel.ElementLabel;
import org.qmpm.entropy.enums.EntropyMetricLabel;
import org.qmpm.logtrie.enums.Outcome;
import org.qmpm.logtrie.trie.Trie;
import org.qmpm.logtrie.trie.TrieImpl;
import org.qmpm.logtrie.trie.Trie.Node;
import org.qmpm.logtrie.exceptions.LabelTypeException;
import org.qmpm.logtrie.exceptions.ValueOutOfBoundsException;
import org.qmpm.logtrie.metrics.Metric;
import org.qmpm.logtrie.tools.MathTools;

public class KBlockEntropy extends Metric {

	private final String COUNT = "Count";
	private final String VISITED = "Visited";
	
	private class nGramTrie extends TrieImpl {
		
		@Override
		public Node createNode(ElementLabel l, Node p) {
			Node node = super.createNode(l, p);
			node.setAttribute(COUNT, 0);
			return node;
		}
	}
	
	int k = 1;
	
	@Override
	public Outcome doComputation(Trie t) throws LabelTypeException {
		
		double H = 0.0;
		Trie nGramTrie = new nGramTrie();
		int totalBlockCount = 0;
		int progress = 0;
		int total = t.getLeafNodeSet().size() + t.getNodeSet(true).size()/10;
		
		
		for (Node leafNode : t.getLeafNodeSet()) {
			// If node depth is less than k, then the prefix is too short too find any k-blocks
			if (leafNode.getDepth() >= k) {
				
				Node currentEndNode = leafNode;
				
				for (int j = leafNode.getDepth(); j >= k; j--) {

					List<Object> block = new ArrayList<>();
					Node currentNode = (Node) currentEndNode;
				
					for (int q = k; q > 0; q--) {
						
						ElementLabel element = currentNode.getParentEdgeLabel();
						block.add(element);
						currentNode = currentNode.getParent();
					}
					
					Collections.reverse(block);

					// Flag denotes whether node has already been counted, once this is reached,
					// stop moving further up the trie
					if (!currentEndNode.hasAttribute(VISITED)) currentEndNode.setAttribute(VISITED, false); 
					if (! (boolean) currentEndNode.getAttribute(VISITED)) {
						
						Node node = nGramTrie.search(block);
						if (node == null) {
							node = nGramTrie.insert(block, false);
						}
						int currentCount = 0;
						if (node.hasAttribute(COUNT))	currentCount = (int) node.getAttribute(COUNT);
						node.setAttribute(COUNT, currentCount + currentEndNode.getVisits());
						totalBlockCount += currentEndNode.getVisits();
						currentEndNode.setAttribute("Visited", true);
					} else break;

					currentEndNode = currentEndNode.getParent();
				}
			}
			
			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress / (total + nGramTrie.getEndNodeSet().size()));
			if (getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
		}
		// Reset visit flags on all nodes for next block
		for (Node node : t.getNodeSet(true)) {
			node.setAttribute(VISITED, false);
		}
		
		for (Node nGramNode : nGramTrie.getNodeSet(false)) {
			int blockCount = (int) nGramNode.getAttribute(COUNT);
			double p = (double) blockCount / totalBlockCount;
			try {
				H -= MathTools.information(p);
			} catch (ValueOutOfBoundsException e) {
				e.printStackTrace();
			}
			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress / (total + nGramTrie.getEndNodeSet().size()));
			if (getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
		}
		
		finished();
		
		this.value = H;
		return Outcome.SUCCESS;
	}

	@Override
	public void processArgs(String[] args) {
		for (int i=0; i<args.length; i++) {
			switch(i) {
			case 0: 
				try {
					this.k = Integer.parseInt(args[i]);
				} catch (NumberFormatException e) {
					System.out.println("Integer value expected for k-block parameter. Received: " + args[i]);
					e.printStackTrace();
				};
			}
			
		}
	}
	
	public void setK(int k) {
		this.k = k; 
	}

	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.KBlock;
	}

	@Override
	public String parametersAsString() {
		return String.valueOf(k);
	}
}
