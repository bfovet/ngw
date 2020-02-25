package gov.sandia.dart.workflow.editor.tree;

import gov.sandia.dart.workflow.domain.WFNode;

public class WFTreeNode {
	private WFNode node_;
	private Object parent_;
	
	public WFTreeNode(WFNode node, Object parent){
		node_ = node;
		parent_ = parent;
	}
	
	public WFNode getNode() {
		return node_;
	}
	
	public Object getParent() {
		return parent_;
	}
	
	@Override
	public int hashCode() {
		return getNode().hashCode() + parent_.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof WFTreeNode) {
			WFTreeNode otherNode = (WFTreeNode) other;
			return node_.equals(otherNode.node_) && parent_.equals(otherNode.parent_);
		}
		
		return super.equals(other);
	}
}
