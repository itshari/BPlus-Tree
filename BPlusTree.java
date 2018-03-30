import java.util.ArrayList;

// Class to represent a node in the tree
class Node {
	protected boolean isLeaf;
	protected ArrayList<Double> keys;
}

// A util class to represent a pair of double value and a node while splitting 
class NodeEntry {
	public Double key;
	public Node node;

	// Constructor to create the NodeEntry instance with the given key and value
	public NodeEntry(Double k, Node v) {
		this.key = k;
		this.node = v;
	}
}

class Util {

	// Util class which contains the method to perform binary search
	public static int binarySearch(ArrayList<Double> list, Double k) {
		int left = 0, right = list.size() - 1, middle;
		Double val;
		// Seaches for the given key through the sorted list 
		while (left <= right) {
			middle = (left + right) / 2;
			val = list.get(middle);
			if (k > val) {
				left = middle + 1;
			} else if (k < val) {
				right = middle - 1;
			} else {
				// Return the index in the list where the key was found
				return middle;
			}
		}
		// If the key doesn't exist, it returns the location where it has to be sorted in order to 
		// maintain the array as sorted (negates that value and returns it)  
		return -(left + 1);
	}
}

// Class which encapsulates the necessary members for an internal node in the tree by external the Node class
class InternalNode extends Node {
	protected ArrayList<Node> children;

	// Create a new internal node with the given key and two child nodes
	public InternalNode(Double k, Node n1, Node n2) {
		// When the constructor is called, we need to make sure we initialise isLeaf to False since it is an internal node
		isLeaf = false;
		keys = new ArrayList<Double>();
		keys.add(k);
		children = new ArrayList<Node>();
		children.add(n1);
		children.add(n2);
	}

	// Create a new internal node with given list of keys and children
	public InternalNode(ArrayList<Double> nKeys, ArrayList<Node> nChildren) {
		// When this constructor is called, we again make sure we initialise isLeaf to False as it is an internal node
		isLeaf = false;
		keys = new ArrayList<Double>(nKeys);
		children = new ArrayList<Node>(nChildren);
	}

	// Insert the new node entry in the internal node
	public void insert(NodeEntry e) {
		Double k = e.key;
		Node newChild = e.node;
		// If the new key is greater than the largest key insert at the last in the lists
		if (k > keys.get(keys.size() - 1)) {
			keys.add(k);
			children.add(newChild);
		}
		// If new is key is less than the smallest key insert at the beginning of the lists
		else if (k < keys.get(0)) {
			keys.add(0, k);
			children.add(1, newChild);
		} else {
			// Collections.sort(keys);
			// Search for exact position in the internal node where we need to insert the new node
			// Binary search function returns ideal position to insert the new key in the given list of sorted keys
			int i = Util.binarySearch(keys, k);
			if (i < 0) {
				keys.add(-1 * (i + 1), k);
				children.add(-1 * (i + 1) + 1, newChild);
			}
		}
	}

}

class DataNode extends Node {
	// To handle duplicate keys in the tree, we create a list of values and append values of same keys to it
	protected ArrayList<ArrayList<String>> values;
	protected DataNode next;
	protected DataNode previous;

	// New DataNode instance is created with the given (key, value) pair
	public DataNode(Double k, String v) {
		// When the constructor is called, we need to make sure we initialise isLeaf to True since it is a Data node
		isLeaf = true;
		keys = new ArrayList<Double>();
		values = new ArrayList<ArrayList<String>>();
		keys.add(k);
		ArrayList<String> list = new ArrayList<String>();
		list.add(v);
		values.add(list);
	}

	// New DataNode instance is created with the given list of keys and value
	public DataNode(ArrayList<Double> keys, ArrayList<ArrayList<String>> values) {
		// When this constructor is called, we again make sure we initialise isLeaf to True as it is a Data node
		isLeaf = true;
		this.keys = new ArrayList<Double>(keys);
		this.values = new ArrayList<ArrayList<String>>(values);
	}

	// Inserts a new (key,value) pair at the appropriate position in this data node
	public void insert(Double k, String v) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(v);
		// To maintain the sorted lists, check if new key is greater than the largest key - insert at the last in the lists
		if (k > keys.get(keys.size() - 1)) {
			keys.add(k);
			values.add(list);
		}
		// If the key is smaller than the smallest, insert at the front of the list
		else if (k < keys.get(0)) {
			keys.add(0, k);
			values.add(0, list);
		} else {
			// Collections.sort(keys);
			// BinarySearch gives the appropriate position to insert the new key
			int i = Util.binarySearch(keys, k);
			if (i < 0) {
				keys.add(-1 * (i + 1), k);
				values.add(-1 * (i + 1), list);
			} else {
				// If the key already exists, get its list of values and append the new value to it
				list = values.get(i);
				list.add(v);
			}
		}
	}

}

// The core class which creates the relevant members for the tree, and performs insert and search on it 
public class BPlusTree {
	private int M;
	public Node root;

	// Constructor to create an instance of the tree with the specified order
	public BPlusTree(int m) {
		this.M = m;
	}

	// Function to insert the given (key, value) pair to Bplus tree
	public void insert(Double k, String v) {
		// Creates a new DataNode with this key and value
		DataNode newdNode = new DataNode(k, v);
		NodeEntry nodeEntry = new NodeEntry(k, newdNode);
		// If the root is null or empty without keys we need to make the new DataNode as the tree's root 
		if (root == null || root.keys.size() == 0) {
			root = newdNode;
		} else {
			// Call the function to split the nodes as they overflow due to this new addition
			NodeEntry splitEntry = getSplitEntry(root, nodeEntry, null);
			if (splitEntry != null) {
				// Update the root if the split propagate up until the root
				root = new InternalNode(splitEntry.key, root, splitEntry.node);
			}
		}
	}

	// Function to seach for a given key in the tree
	public String search(Double k) {
		if (root == null || k == null) {
			return null;
		}
		// Call the function which returns the exact data node where our search key resides
		DataNode dNode = (DataNode) findDataNode(root, k);
		// Since the keys are sorted, do a binary search to find the value
		int i = Util.binarySearch(dNode.keys, k);
		if (i >= 0 && i < dNode.values.size()) {
			StringBuffer str = new StringBuffer();
			// Since we could have duplicate keys, iterate over the list of values for this key and append them to the result
			for (int t = 0; t < dNode.values.get(i).size(); t++) {
				if (t == dNode.values.get(i).size() - 1) {
					str.append(dNode.values.get(i).get(t));
				} else {
					str.append(dNode.values.get(i).get(t) + ", ");
				}
			}
			// Return the bunch of resulting strings
			return str.toString();
		}
		return null;
	}

	// Function which searches for the all keys and their values lying in the range from k1 to k2
	public ArrayList<String> search(Double k1, Double k2) {
		if (root == null || k1 == null) {
			return null;
		}
		ArrayList<String> result = new ArrayList<String>();
		// Search for the data node that could possibly contain our key k1
		DataNode dNode = (DataNode) findDataNode(root, k1);
		int i = Util.binarySearch(dNode.keys, k1);
		// if k1 doesn't exist start from the key that just greater than it
		if (i < 0) {
			i = -1 * (i + 1);
		}
		do {
			while (i < dNode.values.size()) {
				// Iterate the leaves on the right and collect their values until we reach a key greater than k2
				if (k2 < dNode.keys.get(i)) {
					break;
				}
				StringBuffer str = new StringBuffer();
				for (int t = 0; t < dNode.values.get(i).size(); t++) {
					if (t == dNode.values.get(i).size() - 1) {
						str.append("(" + dNode.keys.get(i) + "," + dNode.values.get(i).get(t) + ")");
					} else {
						str.append("(" + dNode.keys.get(i) + "," + dNode.values.get(i).get(t) + "), ");
					}
				}
				result.add(str.toString());
				i++;
			}
			if (i != dNode.values.size()) {
				break;
			}
			dNode = dNode.next;
			i = 0;
		} while (dNode != null);
		return result;
	}

	// Function which identifies the data node containing the key k
	private Node findDataNode(Node node, Double k) {
		// If the starting node is already a data node return it 
		if (node.isLeaf) {
			return node;
		} else {
			InternalNode iNode = (InternalNode) node;
			// If the key is less than the smallest key in the list, recursively search on the first child 
			if (k < iNode.keys.get(0)) {
				return findDataNode(iNode.children.get(0), k);
			} // If the key is more than the largest key in the list, recursively search on the last child 
			else if (k >= iNode.keys.get(node.keys.size() - 1)) {
				return findDataNode(iNode.children.get(iNode.children.size() - 1), k);
			} else {
				// Search for exact range into which this key falls and recursively search on corresponding child
				for (int i = 0; i < iNode.keys.size() - 1; i++) {
					if (k >= iNode.keys.get(i) && k < iNode.keys.get(i + 1)) {
						return findDataNode(iNode.children.get(i + 1), k);
					}
				}
			}
			return null;
		}
	}

	// Function to add a new entry to the tree and propagate the split node to the top if it exists
	private NodeEntry getSplitEntry(Node node, NodeEntry entry, NodeEntry nSplitEntry) {
		if (node.isLeaf) {
			DataNode dNode = (DataNode) node;
			DataNode newDNode = (DataNode) entry.node;
			// If it's a data node insert the new (key,value) at the appropriate position
			dNode.insert(entry.key, newDNode.values.get(0).get(0));
			// If there is no overflow we can just return
			if (dNode.keys.size() < M) {
				return null;
			} else {
				// We do a split of this overflowed node and update the root accordingly
				nSplitEntry = splitNode(dNode);
				if (dNode == root) {
					InternalNode newRoot = new InternalNode(nSplitEntry.key, dNode, nSplitEntry.node);
					root = newRoot;
					return null;
				}
				return nSplitEntry;
			}
		} else {
			InternalNode iNode = (InternalNode) node;
			int i;
			for (i = 0; i < iNode.keys.size(); i++) {
				if (entry.key < iNode.keys.get(i)) {
					break;
				}
			}
			// recursively pick the appropriate child where this new internal node has to be inserted and perform split
			nSplitEntry = getSplitEntry((Node) iNode.children.get(i), entry, nSplitEntry);
			if (nSplitEntry == null) {
				return null;
			} else {
				// Insert this split entry that was propagate by the child at the current node 
				iNode.insert(nSplitEntry);
				// Make sure this internal node reorganises if it gets overflowed
				if (iNode.keys.size() < M) {
					return null;
				} else {
					// Split the current overflowed node and update the parent (root) if needed
					nSplitEntry = splitNode(iNode);
					if (iNode == root) {
						InternalNode newRoot = new InternalNode(nSplitEntry.key, root, nSplitEntry.node);
						root = newRoot;
						return null;
					}
					return nSplitEntry;
				}
			}
		}
	}

	// Function which splits the given node and returns the splitEntry - (key, node) pair
	private NodeEntry splitNode(Node node) {
		ArrayList<Double> nKeys = new ArrayList<Double>();
		NodeEntry splitEntry;
		Double splitKey;
		if (!node.isLeaf) {
			// If the node is an internal node split it
			InternalNode iNode = (InternalNode) node;
			ArrayList<Node> nChildren = new ArrayList<Node>();
			// Pick the middle element, and make it as a parent for the left half as one child and right half as the other
			splitKey = iNode.keys.get(M / 2);
			iNode.keys.remove(M / 2);
			nChildren.add(iNode.children.get((M / 2) + 1));
			iNode.children.remove((M / 2) + 1);
			while (M / 2 < iNode.keys.size()) {
				nKeys.add(iNode.keys.get(M / 2));
				iNode.keys.remove(M / 2);
				nChildren.add(iNode.children.get((M / 2) + 1));
				iNode.children.remove((M / 2) + 1);
			}
			// If the split propagates to the top return the (key, InternalNode) pair
			InternalNode nNode = new InternalNode(nKeys, nChildren);
			splitEntry = new NodeEntry(splitKey, nNode);
		} else {
			//  Perform the split operation on the data node
			DataNode dNode = (DataNode) node;
			ArrayList<ArrayList<String>> nValues = new ArrayList<ArrayList<String>>();
			// Pick the middle element, and make it as a parent for the left half as one child 
			// and (middle + right half) as the other child
			int c = dNode.keys.size() - (M / 2);
			while (c > 0) {
				nKeys.add(0, dNode.keys.get(dNode.keys.size() - 1));
				dNode.keys.remove(dNode.keys.size() - 1);
				nValues.add(0, dNode.values.get(dNode.values.size() - 1));
				dNode.values.remove(dNode.values.size() - 1);
				c--;
			}
			splitKey = nKeys.get(0);
			// When we insert the data node, update the previous and next pointers appropriately
			DataNode nNode = new DataNode(nKeys, nValues);
			DataNode t = dNode.next;
			dNode.next = nNode;
			if (t != null) {
				t.previous = nNode;
			}
			nNode.previous = dNode;
			nNode.next = t;
			// If the split propagates to the top return the (key, DataNode) pair
			splitEntry = new NodeEntry(splitKey, nNode);
		}
		return splitEntry;
	}
}
