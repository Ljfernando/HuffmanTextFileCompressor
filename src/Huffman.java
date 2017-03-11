/**
 * This class compresses text files and decompresses binary files.
 * Through compression, a text file can be compressed using huffman
 * encoding to essentially create a smaller file.
 * These "huffman" files can then be decompressed to 
 * convert the bits into actual text from the original file.
 */
public class Huffman {
	
	//Sizes of input and output files
	private static int inputFileSize;
	private static int outputFileSize;
	
	//Table which stores the frequency of element at index ASCII
	private int[] freqTable;
	
	//Heap data structure utilized to create the Huffman tree
	private MinHeap treeHeap;
	
	//Array of tree nodes in Huffman Tree utilized to calculate 
	//the size of tree outputted to file.
	private Tree[] allNodes;
	
	//Array of huffman codes for each elem ASCII. 
	//Elements not in file have value null.
	private String[] lookupTable;
	

	Huffman(){
		freqTable = new int[255];
		treeHeap = new MinHeap(255);
		allNodes = new Tree[255];
		lookupTable = new String[255];
		
	}

	/**
	 * Creates a frequency table by reading each
	 * char in input file and accumulating the frequency
	 * by 1 at the ASCII index in freqTable array of that char.
	 * It will then increment the inputFileSize variable by 8
	 * for each char in file.
	 * 
	 * @param file 
	 * 		File given to read characters from.
	 * 
	 */
	public void createFreqTable(TextFile file){
		while(!file.EndOfFile()){
			
			int asciiChar = (int)file.readChar();
			freqTable[asciiChar] += 1;
			inputFileSize += 8;
		}
		
		file.rewind();
	}
	
	/**
	 * Creates the min heap by traversing through
	 * freqTable and creating Tree nodes for each
	 * ASCII whose frequency does not equal 0. That node
	 * is then inserted into the treeHeap.
	 * 
	 */
	public void createHeap(){
		Tree tree;

		for(int i = 0; i < freqTable.length; i++){

			if(freqTable[i] != 0){
				
				tree = new Tree((char)i, freqTable[i]);
				treeHeap.insert(tree);
			}
		}
	}
	
	/**
	 * Creates a huffman tree by removing the two smallest
	 * tree nodes from the treeHeap and creating their parent.
	 * The Tree class forces Tree nodes utilizing constructor
	 * Tree(Tree left, Tree right) to have a frequency of the sum 
	 * of both children.
	 * Each tree node is then added to allNodes array at the next
	 * available index.
	 * 
	 */
	public void createHuffmanTree(){
		int index = 0;
		while(treeHeap.size() -1  != 0){
			
			Tree firstChild = treeHeap.removemin();
			Tree secondChild = treeHeap.removemin();
			
			Tree parent = new Tree(firstChild, secondChild);	
			treeHeap.insert(parent);
			
			allNodes[index] = firstChild;
			allNodes[index + 1] = secondChild;
			allNodes[index + 2] = parent;
			
			index += 2;
		}
		
	}
	
	/**
	 * Uses recursion to build the lookup table by
	 * locating the leaves of the huffman tree starting 
	 * at the root and recording the path to get there.
	 * The 0 is left and 1 is right.
	 * 
	 * @param root
	 * 		Initially the root of huffman tree. But changes
	 * 		to current node through recursion.
	 * @param code
	 * 		Huffman code based on going left or right.
	 */
	public void buildLookupTable(Tree root, String code){
		if(root.left() == null && root.right() == null){
			
			lookupTable[(int)root.elem()] = code;
		}
		
		if(root.left() != null && root.right() != null){

			buildLookupTable(root.left(), code + 0);

			buildLookupTable(root.right(), code + 1);
		}
		
		if(root.left() != null && root.right() == null){
			
			buildLookupTable(root.left(), code + 0);
		}
		
		if(root.left() == null && root.right() != null){
			
			buildLookupTable(root.right(), code + 1);
		}

	}

	/**
	 * Checks the file sizes of output files when doing 
	 * compression. First it will traverse the lookupTable and 
	 * multiply the length of each huffman code by the frequency.
	 * 
	 * Then it traverses allNodes and adds 9 bits(8 for the binary code and 
	 * 1 for leaf indicator) for each leaf and 1 bit for each internal
	 * node.
	 * 
	 * Finally, it will round the compressedFile size up to the nearest 8
	 * and compare it to inputFileSize.
	 * 
	 * @return
	 * 		True if input is larger, False if otherwise
	 */
	public boolean checkFileSizes(){
		int compressedFile = 0;
		
		for(int i = 0; i < lookupTable.length; i++){
			
			if(lookupTable[i] != null)
				compressedFile += lookupTable[i].length() * freqTable[i];
			
		}
		
		for(Tree tree : allNodes){
			
			if(tree != null){
				
				if (tree.isLeaf())
					compressedFile += 9;
				
				
				else
					
					compressedFile += 1;
			}
		}
		
		//16 bits for Magic number
		//32 bits for header info from BinaryFile class
		compressedFile += 48;
		
		while(compressedFile % 8 != 0)
			compressedFile +=1;
				
		outputFileSize = compressedFile;
		
		return (inputFileSize > compressedFile);
	}
	
	/**
	 * Writes the byte code of the huffman tree
	 * to a given binary file using recursion. It
	 * recursively does a preorder traversal on the huffman
	 * tree starting at the root. If it reaches a leaf, it
	 * will write 0 to the file followed by the 8 bits 
	 * representing the char. If it reaches an internal node, 
	 * a 1 is written to the file.
	 * 
	 * @param file
	 * 		Output file that is written to.
	 * @param tree
	 * 		Huffman tree nodes which is initially the root.
	 */
	public void writeHuffmanTree(BinaryFile file, Tree tree){
		if(tree.left() == null && tree.right() == null){
			
			file.writeBit(false);
			file.writeChar(tree.elem());
			return;
		}

		else{
			
			file.writeBit(true);
			
			writeHuffmanTree(file, tree.left());
			writeHuffmanTree(file, tree.right());
		}

		
	}
	
	/**
	 * This method will write the compressed version of an 
	 * inputfile to a binary file. First it will write magic 
	 * numbers to the file to ensure it was created by this program.
	 * It will then print the huffman tree to the file. Finally,
	 * it will convert the original text to 0s and 1s using the 
	 * lookupTable. 
	 * 
	 * @param outputFile
	 * 		Binary file that is being written to.
	 * @param inputFile
	 * 		Original text file that was given.
	 * @param root
	 * 		Root of the huffman tree.
	 */
	public void printCompressedFile(BinaryFile outputFile, TextFile inputFile, Tree root){
		outputFile.writeChar('H');
		outputFile.writeChar('F');
		
		writeHuffmanTree(outputFile, root);
		
		while(!inputFile.EndOfFile()){
			
			int character = (int)inputFile.readChar();
			
			for(int i = 0; i < lookupTable[character].length(); i++){
				
				if(lookupTable[character].charAt(i) == '0')
					outputFile.writeBit(false);
				

				else if(lookupTable[character].charAt(i) == '1')
					outputFile.writeBit(true);
			}
		}
	}
	
	/**
	 * This method takes in a BinaryFile object and creates the 
	 * huffman tree from its code. Using recursion similar to
	 * preorder traversal of trees, the method creates a leaf
	 * if it reads a 0 and sets the element of that leaf to
	 * the character of the next 8 bits read. When it reads a
	 * 1, it creates a two children and sets the current node to
	 * the parent of those children and calls the method on each child.
	 * 
	 * @param binFile
	 * 		Input binary file given.
	 * @param node
	 * 		The current node.
	 */
	public void getHuffmanTreeFromFile(BinaryFile binFile, Tree node){
		if(!binFile.readBit()){
			
			node.setElem(binFile.readChar());
			return;
		}
		
		else{
			
			Tree leftChild = new Tree();
			Tree rightChild = new Tree();
			
			node.setLeft(leftChild);
			node.setRight(rightChild);
			
			getHuffmanTreeFromFile(binFile, leftChild);
			getHuffmanTreeFromFile(binFile, rightChild);
		}
	
	}
	
	
	/**
	 * This method will print out a huffman tree utilizing
	 * recursion and a preorder traversal on the tree. Indents
	 * are printed to create the imitate Directories and Files.
	 * (Code taken and altered from CS245 lecture notes)
	 * 
	 * @param tree
	 * 		Current tree node, however initially the root of huffman tree.
	 * @param indent
	 * 		The amount of indents needed to print for the node.
	 */
	public void printHuffman(Tree tree, int indent) {
		if (tree != null) {
			
			for(int i=0; i<indent; i++)
				System.out.print("\t");
				
			
			if(tree.isLeaf()){
				
				System.out.println("[ASCII: " + (int)tree.elem() + "]");
			}

			else
				
				System.out.println("[Internal Node]");
			
			printHuffman(tree.left(), indent + 1);
			printHuffman(tree.right(), indent + 1);
		}
	
	}

	/**
	 * This method is called when the program compresses a file
	 * and is called with the verbose -v flag. It will first 
	 * print out the frequency of each character utilizing the freqTable.
	 * 
	 * Then it will print out the huffman tree.
	 * 
	 * Next it will print out the code of each char utilizing the
	 * lookupTable.
	 * 
	 * Finally, it will print out the size of the input and output files.
	 * 
	 */
	public void doVerboseCompress(){
		System.out.println("------Frequency of each char------");
		for(int i = 0; i < freqTable.length; i ++){
			
			if (freqTable[i] > 0){
				
				System.out.println("ASCII " + i + ": " + freqTable[i]);
			}
		}
		
		System.out.println("\n------Huffman Tree------");

		printHuffman(treeHeap.getRoot(), 1);
		
		System.out.println("\n------Code of each char------");
		for(int i = 0; i < lookupTable.length; i ++){
			
			if (lookupTable[i] != null){
				
				System.out.println("ASCII " + i + ": " + lookupTable[i]);
			}
		}
		System.out.println("\n------File Sizes------");
		System.out.println("Size of input file: " + Huffman.inputFileSize);
		System.out.println("Size of outputFile: " + Huffman.outputFileSize);

	}
	
	/**
	 * This method is called when the program decompresses
	 * a file and is called with the verbose -v flag. 
	 * It will call printHuffman() to print out the Huffman
	 * tree given the root.
	 * 
	 * @param root
	 * 		Root of huffman tree.
	 */
	public void doVerboseDecompress(Tree root){
		System.out.println("------Huffman Tree------");
		printHuffman(root, 1);
	}
	
	/**
	 * Compresses given input file first by creating the freqTable. Then
	 * It will create the heap followed by the huffman tree and lookup table.
	 * If checkFileSizes returns True or program is called with "force" flag, it 
	 * will print the compressed file to the output binary file.
	 * Otherwise it will print out a message explaining that it could not be compressed
	 * due to file sizes.
	 * If called with verbose, it will run additional verbose commands.
	 * 
	 * @param inputFile
	 * 		Given input text file.
	 * @param outputFile
	 * 		Binary file written to.
	 * @param force
	 * 		true if "-f" is used in command line arguments, otherwise false
	 * @param verbose
	 * 		true if "-v" is used in command line arguments, otherwise false
	 */
	public void compress(TextFile inputFile, BinaryFile outputFile, boolean force, boolean verbose){
		createFreqTable(inputFile);
			
		createHeap();
		
		createHuffmanTree();
		
		buildLookupTable(treeHeap.getRoot(), "");
		
		boolean compressable = checkFileSizes();
		if(compressable || force){
			
			printCompressedFile(outputFile, inputFile, treeHeap.getRoot());
		}
		else
			
			System.out.println("FILE NOT COMPRESSED. OUTPUT FILE LARGER THAN INPUT FILE.\n");
		
		if(verbose)
			
			doVerboseCompress();
		
	}
	
	/**
	 * This method will decompress binary files that start with
	 * "HF". It will call getHuffmanTreeFromFile to create the huffman tree.
	 * Variable (branch) is the root of that huffman tree. The remaining
	 * code in the binary file is the original text written in huffman coding.
	 * The method will then utilize the huffman tree to decode the huffman coding
	 * and convert it to actual text, writing each character to the output text file
	 * and resetting the branch to the root of the huffman tree.
	 * If program is called with verbose, it will run additional verbose commands.
	 * 
	 * @param binFile
	 * 		Input binary file that is being decompressed.
	 * @param outputFile
	 * 		Ouput text file containing decoded text from original file.
	 * @param verbose
	 * 		true if "-v" is used in command line arguments, otherwise false
	 */
	public void decompress(BinaryFile binFile, TextFile outputFile, boolean verbose){
		if(binFile.readChar() == 'H' && binFile.readChar() == 'F'){
			
			Tree root = new Tree();
			getHuffmanTreeFromFile(binFile, root);
			
			Tree branch = root;
			
			while(!binFile.EndOfFile()){
				
				while(branch.left()!= null && branch.right() != null){
					
					if(!binFile.readBit())
						
						branch = branch.left();
					else
						
						branch = branch.right();
				}
				outputFile.writeChar(branch.elem());
				branch = root;
			}
			
			if(verbose)
				
				doVerboseDecompress(root);

		}
		else
			System.out.println("UNABLE TO DECOMPRESS FILE. FILE NOT ENCODED WITH CORRECT MAGIC NUMBERS");
	}
		
	/**
	 * Main method that creates an object of type Huffman. It will
	 * then check for optional -v and -f flags.
	 * 
	 * If compressing a file, it will create an object of type TextFile
	 * using the input file as well as a BinaryFile using the output file.
	 * It will then compress the file and close both input and output files.
	 * 
	 * If decompressing a file, it will create a BinaryFile object using the 
	 * input file and a TextFile object using the output file. It will
	 * then decompress the file and close both input and output files.
	 * 
	 * @param args
	 * 		Command line arguments containing flags and input/output file names.
	 */
	public static void main(String args[]){
		Huffman huffman = new Huffman();
		
		boolean verbose = false;
		if(args[1].equals("-v") || args[2].equals("-v"))
			
			verbose = true;

		boolean force = false;
		if(args[1].equals("-f") || args[2].equals("-f")){
			
			force = true;

		}

		if(args[0].equals("-c")){

			TextFile inputFile = new TextFile(args[args.length - 2], 'r');

			BinaryFile outputFile = new BinaryFile(args[args.length-1], 'w');
			huffman.compress(inputFile, outputFile, force, verbose);
			inputFile.close();
			outputFile.close();
			
		}
		
		else if(args[0].equals("-u")){
			
			BinaryFile inputFile = new BinaryFile(args[args.length - 2], 'r');
			TextFile outputFile = new TextFile(args[args.length - 1], 'w');

			huffman.decompress(inputFile, outputFile, verbose);
			inputFile.close();
			outputFile.close();

		}
	}
}
