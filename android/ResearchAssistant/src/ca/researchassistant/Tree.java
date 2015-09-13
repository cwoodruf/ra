package ca.researchassistant;

// see http://stackoverflow.com/questions/12569109/android-double-expandable-listview-hides-items-if-list-is-too-big

import java.util.Iterator;
import java.util.LinkedList;

public final class Tree {
	
	final static String TAG=Tree.class.getSimpleName();
	private String name;
	private int id;	
	private Tree parent;
	private LinkedList<Tree> children = new LinkedList<Tree>();
	
	public Tree(String name, int id) {
	    parent = null;
	    this.name = name;
	    this.id = id;
	}
	
	public void addChild(Tree child){
	    child.parent = this;
	    children.add(child);
	}
	
	public int getId() {
	    return id;
	}
	
	public void setId(int newid) {
		id = newid;
	}
	
	public String getName() {
	    return name;
	}
	
	public void setName(String newname) {
		name = newname;
	}
	
	public Tree getParent() {
	    return parent;
	}
	
	public void setParent(Tree newparent) {
		parent = newparent;
	}
	
	public LinkedList<Tree> getChildren() {
	    return children;
	}
	
	@Override
	public String toString() {
	    Iterator<Tree> iter = children.iterator();
	    String children = "[";
	    while(iter.hasNext()){
	        Tree ch = iter.next();
	        children = children.concat(ch+",");
	    }
	    children = children.concat("]");
	    return name + " "+ id + children;
	}
}