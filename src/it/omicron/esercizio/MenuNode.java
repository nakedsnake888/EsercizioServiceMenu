package it.omicron.esercizio;

import java.util.*;

public class MenuNode {
	
	//Attributi della classe
	
	private int nodeid;
	private String nodeName;
	private String nodeType;
	private String groupType;
	private int startValidityTs;
	private int endValidityTs;
	private List<MenuNode> nodes;
	private Resource resource;
	
	//Metodi Setter e Getter
	public int getNodeid() {
		return nodeid;
	}
	
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	
	public String getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public String getGroupType() {
		return groupType;
	}
	
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	
	public int getStartValidityTs() {
		return startValidityTs;
	}
	
	public void setStartValidityTs(int startValidityTs) {
		this.startValidityTs = startValidityTs;
	}
	
	public int getEndValidityTs() {
		return endValidityTs;
	}
	
	public void setEndValidityTs(int endValidityTs) {
		this.endValidityTs = endValidityTs;
	}
	
	public List<MenuNode> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<MenuNode> nodes) {
		this.nodes = nodes;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
