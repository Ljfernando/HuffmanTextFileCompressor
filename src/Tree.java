/**
 * This class creates a Tree node that contains certain attributes
 * depending on the type of node it is. When first creating the tree from
 * a TextFile object, the nodes will have frequency and elem attributes. 
 * Internal nodes will have left/right children and isLeaf will be set
 * to False. 
 * (Code taken and altered from CS245 lecture notes)
 */
public class Tree{
		private char elem;
		private int freq = 0;
		private Tree left;
		private Tree right;
		private boolean isLeaf = true;
		Tree(char elem, int freq){
			this.elem = elem;
			this.freq = freq;
			left = null;
			right = null;
		}
		Tree(Tree left, Tree right){
			freq = left.freq() + right.freq();
			this.left = left;
			this.right = right;
			this.isLeaf = false;
			
			
		}
		Tree(char elem){
			this.elem = elem;
		}
		Tree(int freq){
			this.freq = freq;
		}
		Tree(){
			
		}
		public int freq(){
			return freq;
		}
		public void setElem(char elem){
			this.elem = elem;
		}
		public char elem(){
			return elem;
		}
		public Tree left(){
			return left;
		}
		public Tree right(){
			return right;
		}
		public void setLeft(Tree left){
			this.left = left;
			this.isLeaf = false;
		}
		public void setRight(Tree right){
			this.right = right;
			this.isLeaf = false;
		}
		
		public boolean isLeaf(){
			return isLeaf;
		}
		public String toString(){
			return "[elem: " + elem + " freq: " + freq + " isInternal: " + isLeaf + "]";
		}

	}
