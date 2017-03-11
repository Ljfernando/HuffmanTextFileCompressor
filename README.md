Readme from SPRING16 CS245: Data Structures and Algorithms. Instructor David Galles.

For your second project, you will write a program that compresses and uncompresses files using Huffman coding. To compress a file, your program will follow the following steps:
	
	•	Read in the entire input file, and calculate the frequencies of all characters.
	
	•	Build a Huffman tree for all characters that appear in the input file (characters that do not appear in the input file should not appear in your Huffman tree)
	
	•	Build a lookup table, which contains the codes for all characters in the input file
	
	•	Check to see if the compressed file would be smaller than the original file. If not, stop -- don't do any compression. Print out a message instead that the file cannot be compressed
	
	•	If the compressed file will be smaller, create the encoded file:
	
	◦	Print out a "Magic Number", which will be used to guard against uncompressing files that we didn't compress
	
	◦	Print out the Huffman tree to the output file
	
	◦	Use the lookup table to encode the file

To uncompress a file, your program will follow the following steps:

	
	•	Read in the "Magic Number", and make sure that it matches the number for the this program (exiting if it does not match)
	
	•	Read in the Huffman tree from the input file
	
	•	Decode the input, using the Huffman tree

If your program is called with the ``verbose'' flag (-v), you will also need to print some debugging information to standard out. If your program is called with the ``force'' flag (-f), then the file will be compressed even if the compressed file would be larger than the original file.

Your program should expect to be called as follows:

% java Huffman (-c|-u) [-v] [-f]  infile outfile

where:
	
	•	(-c|-u) stands for either "-c" (for compress), or "-u"(for uncompress)
	
	•	[-v] stands for an optional "-v" flag (for verbose)
	
	•	[-f] stands for an optional "-f" flag, that forces compression even if the compressed file will be larger than the original file
	
	•	infile is the input file
	
	•	outfile is the output file

The flags -f and -v can be in either order.  So, the following would all be legal:
	
	•	java Huffman -c test test.huff
	
	•	java Huffman -c -v myTestFile myCompressedFile
	
	•	java Huffman -c -f -v test test.huff
	
	•	java Huffman -u -f test1.huff test2
	
	•	java Huffman -u -f -v test1.huff test2

If a file is compressed with the "-v" option, you should print the following to standard output (using System.out.print(ln)):

	•	The frequency of each character in the input file (print the ASCII values of the characters, instead of the characters themselves, to make this more readable for binary files)
	
	•	The Huffman tree (see class notes on printing trees for pointers on how this can be done)
	
	•	The Huffman codes for each character that has a code (characters which do not appear in the input file will not have codes.  Again, print the ASCII values of characters instead of the characters themselves)
	
	•	The size of the uncompressed file and the size of the compressed file

If a file is uncompressed with the "-v" option, you should print out following to standard output (using System.out.print(ln)):

	•	The Huffman tree (see class notes on printing trees for pointers on how this can be done)
