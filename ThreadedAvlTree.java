/**
 * Name: Israel Bango
 * Number: u04865503
 * **/
public class ThreadedAvlTree<T extends Comparable<T>> {
	public Node<T> root;

	public ThreadedAvlTree() {
		this.root = null;
	}

	int getHeight(Node<T> N) {
		if (N == null)
			return 0;

		return N.height;
	}

	static Node getLeftMost(Node node) {
		while (node != null && node.left != null)
			node = node.left;
		return node;
	}

	// Inorder traversal of a threaded avl tree
	void print(Node<T> node) {
		if (node == null)
			return;

		Node<T> cur = getLeftMost(node);

		while (cur != null) {
			System.out.print(" " + cur.data + " ");

			if (cur.rightThread == true)
				cur = cur.right;
			else
				cur = getLeftMost(cur.right);
		}
	}

	/* Do not edit the code above */

	private void convertAVLtoThreadedRec(Node<T> node) {
		if (node == null)
			return;
		
		if (node.rightThread == true) {
			convertAVLtoThreadedRec(node.left);
		} else {
			Node<T> tmp = getLeftMost(node);
			if (tmp != null && tmp.rightThread == false) {
				if (tmp.right == null) {
					Node<T> p = getSuccessor(tmp);
					if(p!= null) {
						tmp.right = p;
						tmp.rightThread = true;
					}
					
				}

			}

			if (node.right == null) {
				if (node == this.root)
					return;

				Node<T> p = getSuccessor(node);
				if(p!= null) {
					node.right = p;
					node.rightThread = true;
				}
				
			}
			
			convertAVLtoThreadedRec(node.left);
			convertAVLtoThreadedRec(node.right);
		}
	}

	// return successor of node
	private Node<T> getSuccessor(Node<T> node) {
		Node<T> n = node, p = getParent(node);
		while (p != null && n == p.right) {
			n = p;
			p = getParent(p);
		}
		return p;
	}

	void convertAVLtoThreaded(Node<T> node) {
		if (node == null)
			return;
		// make deepCopy of tree with root node
		this.root = deepCopy(node);
		// convert this tree to threaded tree
		convertAVLtoThreadedRec(this.root);
		root.height = height(root);
		isThreaded = true;
	}

	private void convertThreadedtoAVL(Node<T> node) {
		if (node == null)
			return;
		
		if (node.rightThread) {
			node.right = null;
			node.rightThread = false;
		}
		
		convertThreadedtoAVL(node.left);
		convertThreadedtoAVL(node.right);
		isThreaded = false;
	}

	private int height(Node<T> node) {
		if (node == null) {
			return -1;
		} else {
			
			int heightOfLeft = -1, heightOfRight = -1;
			if (node.rightThread == false) {
				heightOfLeft = height(node.left);
			}else {
				heightOfLeft = height(node.left);
			}
			if (node.rightThread == false) {
				heightOfRight = height(node.right);
			}else {
				heightOfRight = height(node.left);
			}
			return maxHeight(heightOfLeft, heightOfRight) + 1;
		}
	}

	private int maxHeight(int a, int b) {
		return a > b ? a : b;
	}

	static boolean isThreaded = false;
	private Node<T> deepCopy(Node<T> node) {
		if (node == null)
			return null;

		Node<T> newTreeNode;
		newTreeNode = new Node<T>(node.data);
		newTreeNode.left = deepCopy(node.left);
		newTreeNode.right = deepCopy(node.right);
		return newTreeNode;
	}

	private Node<T> getParent(Node<T> node) {
		Node<T> current = root, prev = null;

		while (current != null) {
			if (node.data.compareTo(current.data) < 0) {
				prev = current;
				current = current.left;
			} else if (node.data.compareTo(current.data) > 0) {
				prev = current;
				current = current.right;
			} else {
				// data is equal parent is prev
				break;
			}
		}

		return prev;
	}

	/**
	 * Insert the given data into the tree. Duplicate data is not allowed. just
	 * return the node.
	 */

	Node<T> insert(Node<T> node, T data) {
		Node<T> current = node, prev = null;
		
		if(isThreaded == true)
			convertThreadedtoAVL(node);
		
		if (root == null) {
			// first node in the tree
			root = new Node<T>(data);
			root.height = height(root);
			return root;
		}

		while (current != null) {
			prev = current;
			if (data.compareTo(current.data) < 0) {
				current = current.left;
			} else if (data.compareTo(current.data) > 0) {
				current = current.right;
			} else if (data.compareTo(current.data) == 0) {
				// data is equal
				// simply return given node/ this.root
				convertAVLtoThreaded(this.root);
				root.height = height(root);
				return this.root;
			}
		}

		if (data.compareTo(prev.data) < 0) {
			prev.left = new Node<T>(data);
			updateBalanceFactorInsert(prev.left);
			convertAVLtoThreaded(this.root);
			root.height = height(this.root);
			return this.root;

		} else {
			prev.right = new Node<T>(data);
			updateBalanceFactorInsert(prev.right);
			convertAVLtoThreaded(this.root);
			root.height = height(root);
			return root;
		}
	}

	private void updateBalanceFactorInsert(Node<T> node) {
		Node<T> insertedVal = node;
		Node<T> parent = getParent(node); // node.parent;
		int balanceFactor = 0;
		if (isLeftChild(node, parent)) {
			balanceFactor = getBalanceFactor(parent);
		} else if (isRightChild(node, parent)) {
			balanceFactor = getBalanceFactor(parent);
		}

		while (parent != null && parent != root && Math.abs(balanceFactor) != 2) {
			node = parent;
			parent = getParent(node);// node.parent;

			if (getBalanceFactor(node) == 0) {
				return;
			}

			if (isLeftChild(node, parent)) {
				balanceFactor = getBalanceFactor(parent);
			} else if (isRightChild(node, parent)) {
				balanceFactor = getBalanceFactor(parent);
			}
		}

		if (parent != null && Math.abs(getBalanceFactor(parent)) == 2) {
			// rebalance the subtree with root p
			rebalanceAfterInsert(parent, insertedVal);
		}
	}

	// rebalance subtree rooted p after insert
	private void rebalanceAfterInsert(Node<T> p, Node<T> ch) {
		// case 1: Inserted into left subtree of the left child

		if (getBalanceFactor(p) < 0) {
			// Left branch insert happened
			if (ch.data.compareTo(p.left.data) > 0) {
				// node was inserted in right subtree of left child
				// perform two rotations: left and right
				// Only rotate left if child's grand parent is not p
				if (p.left.right != null) {
					rotateLeft(p, p.left, p.left.right);
				}

				rotateRight(getParent(p), p, p.left);
			} else {
				// node was inserted in the left subtree of left child
				// do a single right rotation

				rotateRight(getParent(p), p, p.left);
			}

		} else if (getBalanceFactor(p) > 0) {
			// right insert happened
			if (ch.data.compareTo(p.right.data) < 0) {
				// node was inserted in left subtree of right child
				// perform two rotations: right and Left
				if (p.right.left != null) {
					rotateRight(p, p.right, p.right.left);
				}
				rotateLeft(getParent(p), p, p.right);
			} else {
				// node was inserted in the right subtree of right child
				// do a single left rotation

				rotateLeft(getParent(p), p, p.right);
			}
		}

	}

	// perform right rotation about parent
	private void rotateRight(Node<T> Gr, Node<T> par, Node<T> ch) {

		if (par == root) {
			Node<T> temp = ch.right;
			ch.right = par;
			ch.right.left = temp; // Parent.left = temp;
			root = ch;

		} else {
			Node<T> temp = ch.right;
			ch.right = par;
			par.left = temp;

			if (Gr.right == par) {
				Gr.right = ch;
			} else {
				Gr.left = ch;
			}

		}
	}

	// perform left rotation about parent
	private void rotateLeft(Node<T> Gr, Node<T> par, Node<T> ch) {
		if (par == root) {
			Node<T> temp = ch.left;
			ch.left = par;
			ch.left.right = temp; // Parent.right = temp;
			root = ch;

		} else {
			Node<T> temp = ch.left;
			ch.left = par;
			par.right = temp;

			if (Gr.right == par) {
				Gr.right = ch;
			} else {
				Gr.left = ch;
			}

		}
	}

	// return true if node is left child
	private boolean isLeftChild(Node<T> node, Node<T> parent) {
		if (parent != null) {
			if (parent.left != null) {
				if (parent.left == node) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// return true if node is right child
	private boolean isRightChild(Node<T> node, Node<T> parent) {
		if (parent != null) {
			if (parent.right != null) {
				if (parent.right == node) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private int getBalanceFactor(Node<T> node) {
		int countLeft = height(node.left) + 1;
		int countRight = height(node.right) + 1;
		return countRight - countLeft;
	}

	/**
	 * Delete the given element \texttt{data} from the tree. Re-balance the tree
	 * after deletion. If the data is not in the tree, return the given node / root.
	 */
	Node<T> removeNode(Node<T> root, T data) {
	
		Node<T> current = root, predecessor;

		if (this.root == null) {
			return root;
		}
		
		if(isThreaded == true)
			convertThreadedtoAVL(this.root);

		// node to delete is root
		if (this.root.data.compareTo(data) == 0) {
			// replace root with predecessor
			// delete predecessor
			predecessor = getLeftRightMostNode(root);
			if (predecessor == null) {
				// There is no left subtree simply delete root
				this.root = this.root.right;
				convertAVLtoThreaded(this.root);
				if (this.root != null) {
					this.root.height = height(this.root);
				}
				return this.root;
			} else {
				// replace root with predecessor
				// Delete predecessor
				if (getParent(predecessor) == this.root) {
					this.root.left = predecessor.left;
					this.root.data = predecessor.data;
					if (predecessor.left != null)
						predecessor.left = null;
					updateBalanceFactorDelete(this.root);
					convertAVLtoThreaded(this.root);
					this.root.height = height(this.root);
					return this.root;
				} else {
					Node<T> tmp = getParent(predecessor);
					getParent(predecessor).right = predecessor.left;
					this.root.data = predecessor.data;
					if (predecessor.left != null)
						predecessor.left = null;
					updateBalanceFactorDelete(tmp/* getParent(predecessor) */);
					convertAVLtoThreaded(this.root);
					this.root.height = height(this.root);
					return this.root;
				}

			}
		}

		while (current != null) {
			if (data.compareTo(current.data) < 0) {
				current = current.left;
			} else if (data.compareTo(current.data) > 0) {
				current = current.right;
			} else if (data.compareTo(current.data) == 0) {
				// This is the node to delete
				predecessor = getLeftRightMostNode(current);
				// node to delete does not have predecessor
				if (predecessor == null) {
					if (isLeftChild(current, getParent(current))) {
						Node<T> tmp = getParent(current);
						getParent(current).left = current.right;
						if (current.right != null)
							current.right = null;
						// balance tree
						updateBalanceFactorDelete(tmp /* getParent(current) */);
						break;
					} else if (isRightChild(current, getParent(current))) {
						Node<T> tmp = getParent(current);
						getParent(current).right = current.right;
						if (current.right != null)
							current.right = null;
						// balance tree
						updateBalanceFactorDelete(tmp /* getParent(current) */);
						break;
					}

				} else {
					// replace target node with predecessor
					// delete predecessor
					if (getParent(predecessor) == current) {
						current.left = predecessor.left;
						current.data = predecessor.data;
						if (predecessor.left != null)
							predecessor.left = null;
						// balance tree
						updateBalanceFactorDelete(current);
						break;

					} else {
						Node<T> tmp = getParent(predecessor);
						getParent(predecessor).right = predecessor.left;
						current.data = predecessor.data;
						if (predecessor.left != null)
							predecessor.left = null;
						// balance tree
						updateBalanceFactorDelete(tmp /* getParent(predecessor) */);
						break;
					}
				}

			} else {
				convertAVLtoThreaded(this.root);
				this.root.height = height(this.root);
				return this.root;
			}
		}
		
		convertAVLtoThreaded(this.root);
		this.root.height = height(this.root);
		return this.root;

	}
	
	 private void updateBalanceFactorDelete(Node<T> node) {
 	   	Node<T>insertedVal = node;
     	Node<T>parent = node;
     	int balanceFactor =  getBalanceFactor(parent);        	
     		while(parent!= null && parent != root && Math.abs(balanceFactor) != 2) {
     			node = parent;
         		parent = getParent(node);// node.parent;
         		
         		if(getBalanceFactor(node)== 0) {
//         			return;
         		}
         		
         		if(isLeftChild(node, parent)) {
         			balanceFactor = getBalanceFactor(parent);
         		} else if(isRightChild(node, parent)) {
         			balanceFactor = getBalanceFactor(parent);
             	}
     		}
     
     		if(parent != null && Math.abs(getBalanceFactor(parent)) >= 2) {
     			rebalanceAfterDelete(parent, insertedVal);
     		}
     	     	 
  }
	 
	// rebalance subtree rooted p after delete
	    private Node<T> rebalanceAfterDelete(Node<T> p, Node<T> ch) {
	    	Node<T> node = null;
	    	// case 1 and 2:Left child of p was deleted and right branch of p has +1/0 balance
	    	// Left branch becomes too small
	    	if(getBalanceFactor(p) >= 2 && getBalanceFactor(p.right) >= 0) {
	    		// one left rotation about the unbalanced node p
	    		rotateLeft(getParent(p), p, p.right);
	    		node = getParent(p);
	    		updateBalanceFactorDelete(node);
	    		
	    	}
	    	// case 1 and 2 mirros: right child of p was deleted and left branch of p has +1/0 balance 
	    	// right branch becomes too small
	    	if(getBalanceFactor(p) <= -2 && getBalanceFactor(p.left) <= 0) {
	    		// one right rotation about the unbalanced node p
	    		rotateRight(getParent(p), p, p.left);
	    		node = getParent(p);
	    		updateBalanceFactorDelete(node);
	    	}
	    	
	    	// case 3 and 4: left child of p was deleted
	    	// Left branch becomes too small, the right branch has -1 balance
	    	if(getBalanceFactor(p) >= 2 && getBalanceFactor(p.right) < 0) {
	    			rotateRight(p, p.right, p.right.left);
	    			rotateLeft(getParent(p), p, p.right);
	    			node = getParent(p);
	    			updateBalanceFactorDelete(node);
	    			
	    	}
	    	
	    	// case 3 and 4 mirrors: right child of p was deleted
	    	// right branch becomes too small, the right branch has -1 balance
	    	if(getBalanceFactor(p) <= -2 && getBalanceFactor(p.left) > 0) {
	    			rotateLeft(p, p.left, p.left.right);
	    			rotateRight(getParent(p), p, p.left);
	    			node = getParent(p);
	    			updateBalanceFactorDelete(node);
	    	}
	    	
	    	return node;
	    }
	 
    // get predecessor of node
    private Node<T> getLeftRightMostNode(Node<T> node){
    	Node<T> current = node;
    	if(current.left != null) {
    		current = current.left;
    		while(current.right != null) {
    			current = current.right;
    		}
    	}else {
    		return null;
    	}
    	return current;
    }
}
