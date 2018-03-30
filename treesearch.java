import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class treesearch {

	public static void main(String[] args) {
		try {
			// Ensuring that the input file name is provided as a command line argument 
			if (args.length > 0) {
				File file = new File(args[0]);
				FileReader fileReader = new FileReader(file);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String line, str, t;
				int order = -1;
				// First read the order from input file for the BPlus tree
				if ((line = bufferedReader.readLine()) != null) {
					order = Integer.parseInt(line);
				}
				// System.out.println("Order is:"+order);

				// Min order for a BPlusTree has to 3, else we terminate the program 
				if (order < 3) {
					System.out.println("Order cannot be less than 3! Exiting!");
					// Closing the readers that were opened initially
					fileReader.close();
					bufferedReader.close();
					return;
				}
				// Initializing the necessary writer objects to write output to the file 
				FileWriter fileWriter = new FileWriter(new File("output_file.txt"));
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				// Initialize the tree with the given order
				BPlusTree bPlusTree = new BPlusTree(order);

				while ((line = bufferedReader.readLine()) != null) {
					// If the instruction is insert, perform insertion of the given values in the tree
					if (line.startsWith("Insert")) {
						str = line.split("Insert")[1];
						str = str.substring(1, str.length() - 1);
						String[] val = str.split(",");
						bPlusTree.insert(Double.parseDouble(val[0]), val[1]);
						// System.out.println("Insertion of:: " + val[0] + " " + val[1]);
					} else if (line.startsWith("Search")) {
						str = line.split("Search")[1];
						str = str.substring(1, str.length() - 1);

						// If search query contains comma (,) perform range search on the tree
						if (str.contains(",")) {
							String[] val = str.split(",");
							// System.out.println("Range search for::" + str);
							ArrayList<String> results = bPlusTree.search(Double.parseDouble(val[0]),
									Double.parseDouble(val[1]));
							if (results.isEmpty()) {
								t = "Null";
							} else {
								StringBuffer toPrint = new StringBuffer();
								for (String s : results) {
									toPrint.append(s + ", ");
								}
								t = toPrint.toString();
								t = t.substring(0, t.length() - 2);
							}
						} else {
							// Search for a particular key and get the result
							// System.out.println("Searching for:: " + str);
							t = bPlusTree.search(Double.parseDouble(str));
							// Write Null to the output if the key doesn't exist
							if (t == null) {
								t = "Null";
							}
						}
						//System.out.println(t);
						// Write the search results to output_file
						bufferedWriter.write(t);
						bufferedWriter.newLine();
					}
				}
				// System.out.println(bPlusTree.root.keys);
				// Close all the file-related connections finally when we are done
				fileReader.close();
				bufferedReader.close();
				bufferedWriter.flush();
				fileWriter.close();
				bufferedWriter.close();
			}
		} catch (IOException e) {
			System.out.println("Encountered an exception while accessing the file!");
			e.printStackTrace();
		}
	}
}
