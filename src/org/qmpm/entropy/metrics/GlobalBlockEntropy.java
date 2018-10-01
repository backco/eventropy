package org.qmpm.entropy.metrics;

import java.util.Arrays;
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

public class GlobalBlockEntropy extends Metric {
	
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

	@Override
	public Outcome doComputation(Trie t) throws LabelTypeException {
		
		double H = 0.0;
		
		int totalBlockCount = 0;
		for (Node n : t.getEndNodeSet()) {
			totalBlockCount += (n.getDepth() * (n.getDepth() + 1)) * n.getEndVisits() / 2;
		}
		
		int progress = 0;
		int total = t.getLongestBranch() * t.getLeafNodeSet().size();
		
		for (int i = t.getLongestBranch(); i > 0; i--) {
			
			Trie nGramTrie = new nGramTrie();
			// Reset visit flags on all nodes for next block
			for (Node node : t.getNodeSet(true)) {
				node.setAttribute(VISITED, false);
			}

			for (Node leafNode : t.getLeafNodeSet()) {
				if (leafNode.getDepth() >= i) {
					Node currentEndNode = leafNode;
					for (int j = leafNode.getDepth(); j >= i; j--) {
						
						// VISITED denotes whether node has already been counted, once this is reached,
						// stop moving further up the trie
						if (! (boolean) currentEndNode.getAttribute(VISITED)) {
						
							// Reconstruct block of length k
							Object[] block = new Object[i];
							Node currentNode = (Node) currentEndNode;
													
							for (int k = i-1; k >= 0; k--) {
								ElementLabel element = currentNode.getParentEdgeLabel();
								block[k] = element;
								currentNode = currentNode.getParent();
							}

							List<Object> blockList = Arrays.asList(block);
							
							Node nGramNode = nGramTrie.insert(blockList, false);
							int currentCount = (int) nGramNode.getAttribute(COUNT);
							nGramNode.setAttribute(COUNT, currentCount + currentEndNode.getVisits());
							currentEndNode.setAttribute(VISITED, true);
						
						} else break;

						currentEndNode = currentEndNode.getParent();
					}
				}

				// Update progress of associated Metric object, check for timeout or error
				updateProgress((double) ++progress / total);
				if (getOutcome() != Outcome.CONTINUE) {
					return getOutcome();
				}
			}
			
			for (Node nGramNode : nGramTrie.getNodeSet(false)) {
				
				int blockCount = (int) nGramNode.getAttribute(COUNT);
				double p = (double) blockCount / totalBlockCount;
				
				try {
					H -= MathTools.information(p);
				} catch (ValueOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
			
			nGramTrie.kill();
			nGramTrie = null;			
			
			// Update progress of associated Metric object, check for timeout or error
			updateProgress((double) ++progress / total);
			if (getOutcome() != Outcome.CONTINUE) {
				return getOutcome();
			}
			
			
		}
		
		progress = total / 2;
		total = total / 2 + totalBlockCount; //nGramTrie.getSize();
		
		finished();
		value = H;
		
		return Outcome.SUCCESS;
	}

	public void processArgs(String[] args) {
	}
	
	@Override
	public EntropyMetricLabel getLabel() {
		return EntropyMetricLabel.GlobalBlock;
	}

	@Override
	public String parametersAsString() {
		return "";
	}
}
