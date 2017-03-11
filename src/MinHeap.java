/**
 * This class creates a priority queue with a heap data structure where the
 * smallest element is at index 1 of the array. Tree elements are
 * sorted by their frequency value.
 * (Code taken and altered from CS245 lecture notes)
 */
public class MinHeap {
	private Tree[] Heap;
	private int maxsize;
	private int size;

	public MinHeap(int max) {
		maxsize = max;
		Heap = new Tree[maxsize];
		size = 0 ;
		Heap[0] = new Tree(Integer.MIN_VALUE);
	}

	private int leftchild(int pos) {
		return 2*pos;
	}
	private int rightchild(int pos) {
		return 2*pos + 1;
	}

	private int parent(int pos) {
		return  pos / 2;
	}

	private boolean isleaf(int pos) {
		return ((pos > size/2) && (pos <= size));
	}

	private void swap(int pos1, int pos2) {
		Tree tmp;

		tmp = Heap[pos1];
		Heap[pos1] = Heap[pos2];
		Heap[pos2] = tmp;
	}

    public void insert(Tree elem) {
    	size++;
    	Heap[size] = elem;
    	int current = size;
    	while (Heap[current].freq() < Heap[parent(current)].freq()) {
    		swap(current, parent(current));
    		current = parent(current);
    	}	
    }
    public int size(){
    	return size;
    }

    public void print() {
    	int i;
    	for (i=1; i<=size;i++)
    		System.out.print(Heap[i] + " ");
    	System.out.println();
    }

    public Tree removemin() {
    	swap(1,size);
    	size--;
    	if (size != 0)
    		pushdown(1);
    	return Heap[size+1];
    }

    private void pushdown(int position) {
    	int smallestchild;
    	while (!isleaf(position)) {
    		smallestchild = leftchild(position);
    		if ((smallestchild < size) && (Heap[smallestchild].freq() > Heap[smallestchild+1].freq()))
    			smallestchild = smallestchild + 1;
    		if (Heap[position].freq() <= Heap[smallestchild].freq()) return;
    		swap(position,smallestchild);
    		position = smallestchild;
    	}
    }
    
    public Tree getRoot(){
    	return Heap[1];
    }

}