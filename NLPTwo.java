import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class NLPTwo {
	static DecimalFormat format = new DecimalFormat("##.00000");

	// count the number of unique word and store their counts
	public static HashMap<String, Integer> countUniqueWord(String s1) {
		String[] wordsStore = s1.split(" ");
		HashMap<String, Integer> wordCount = new HashMap<>();
		for (int i = 0; i < wordsStore.length; i++) {
			if (wordCount.get(wordsStore[i]) == null) {
				wordCount.put(wordsStore[i], 1);
			} else {
				wordCount.put(wordsStore[i], wordCount.get(wordsStore[i]) + 1);
			}
		}
		return wordCount;
	}

	// function to get unique words from the two sentences
	public static String[] getUniqueWords(String s1, String s2) {
		HashSet<String> hs = new HashSet<>();
		String[] temp = s1.split(" ");

		for (int i = 0; i < temp.length; i++) {
			hs.add(temp[i]);
		}

		temp = s2.split(" ");

		for (int i = 0; i < temp.length; i++) {
			hs.add(temp[i]);
		}

		String[] result = hs.toArray(new String[hs.size()]);
		return result;
	}

	// function to form matrix by parsing the corpus
	public static float[][] getMatrix(String[] a, String[] b, float[][] bigramTable) {

		for (int i = 0; i < a.length; i++) {
			String res = a[i];
			String res1 = null;
			for (int k = 0; k < b.length; k++) {
				if (res.equals(b[k])) {
					res1 = b[k + 1];
					for (int j = 0; j < a.length; j++) {
						if (a[j].equals(res1)) {
							bigramTable[i][j] += 1;
						} else {
							bigramTable[i][j] += 0;
						}
					}
				}
			}
		}
		return bigramTable;
	}

	// function to form the table of probabilities with bigram / number of
	// occurrences
	public static float[][] getProbabiltiytable(String[] a, HashMap<String, Integer> hm, float[][] bigramTable) {

		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				if (bigramTable[i][j] != 0) {
					bigramTable[i][j] = bigramTable[i][j] / hm.get(a[i]);
				}
			}

		}
		return bigramTable;
	}

	// calculate the probability of sentences
	public static float getProbabilitysentence(String a, String[] uniqueWords, float[][] bigramTable) {
		String[] temp = a.split(" ");
		float prob = 0.5f; //probability of first of the sentence . first word out of 2 sentences. so 1/2
		int row = 0, col = 0;
		for (int i = 0; i < temp.length - 2; i++) {
			for (int j = 0; j < uniqueWords.length; j++) {
				if (temp[i].equals(uniqueWords[j]))
					row = j;
			}

			for (int j = 0; j < uniqueWords.length; j++) {
				if (temp[i + 1].equals(uniqueWords[j]))
					col = j;
			}

			prob = prob * bigramTable[row][col];

		}
		return prob;

	}

	public static void main(String[] args) throws IOException {
		// read the corpus
		String line = "";
		FileInputStream inputStream = null;
		Scanner sc = null;
		inputStream = new FileInputStream("corpus.txt");
		sc = new Scanner(inputStream, "UTF-8");
		sc.useDelimiter(" +");
		while (sc.hasNext()) {
			line += " " + sc.next();
		}

		
		line = line.trim().replaceAll("[-+.^:,\")($%&{}<>;?@\']", "");
		line=line.replaceAll("\\bThe", "");
		line=line.replaceAll("\\bthe", "");
		String s1 = "Facebook announced plans to built a new datacenter in 2018 .";
		String s2 = "Facebook is an American social networking service company in California .";

		HashMap<String, Integer> wordCount = new HashMap<>();
		wordCount = countUniqueWord(line.substring(0, line.length() - 1));

		//System.out.println(wordCount.get("Facebook"));

		// array of unique words [ Facebook, announced, plans,......]
		String[] uniqueWords = getUniqueWords(s1.substring(0, s1.length() - 2), s2.substring(0, s2.length() - 2));

		// 2D matrix get each element from the array and form the table of bigrams (two
		// for loops)

		String[] temp = line.split(" ");
		float[][] bigramTable = new float[uniqueWords.length][uniqueWords.length];

		// parse the first string
		bigramTable = getMatrix(uniqueWords, temp, bigramTable);

		System.out.println("bigram table with counts: " + "\n");
		
		for (int i = 0; i < bigramTable.length; i++) {
			System.out.print(uniqueWords[i] + " \t");
			for (int j = 0; j < bigramTable[i].length; j++) {
				System.out.print(format.format(bigramTable[i][j]) + "  ");
			}
			System.out.println();
		}
		
		// create one more 2D array takes each value from previous 2D array and divides
		// by the number of counts
		bigramTable = getProbabiltiytable(uniqueWords, wordCount, bigramTable);

		System.out.println("\n" + "Matrix with probabilities without smmothing: "+ "\n");
		for (int i = 0; i < bigramTable.length; i++) {
			System.out.print(uniqueWords[i] + " \t");
			for (int j = 0; j < bigramTable[i].length; j++) {
				System.out.print(format.format(bigramTable[i][j]) + "  ");
			}
			System.out.println();
		}
		
		float prob;

		prob = getProbabilitysentence(s1, uniqueWords, bigramTable);
		System.out.println("\n" + "PROBABILTY OF SENETENCE 1:  " + prob);
		
		prob = getProbabilitysentence(s2, uniqueWords, bigramTable);
		System.out.println("PROBABILTY OF SENETENCE 2:  " + prob);

		

		// laplace smoothing
		for (int i = 0; i < bigramTable.length; i++) {
			for (int j = 0; j < bigramTable[i].length; j++) {
				bigramTable[i][j] = 1;
			}
		}

		// parse the corpus
		bigramTable = getMatrix(uniqueWords, temp, bigramTable);
		
		System.out.println("\n" + "bigram table with counts smoothing: " + "\n");
		
		for (int i = 0; i < bigramTable.length; i++) {
			System.out.print(uniqueWords[i] + " \t");
			for (int j = 0; j < bigramTable[i].length; j++) {
				System.out.print(format.format(bigramTable[i][j]) + "  ");
			}
			System.out.println();
		}
		
		bigramTable = getProbabiltiytable(uniqueWords, wordCount, bigramTable);
		
		System.out.println("\n" + "Matrix with probabilities with smmothing: "+ "\n");
		for (int i = 0; i < bigramTable.length; i++) {
			System.out.print(uniqueWords[i] + " \t");
			for (int j = 0; j < bigramTable[i].length; j++) {
				System.out.print(format.format(bigramTable[i][j]) + "  ");
			}
			System.out.println();
		}
		

		prob = getProbabilitysentence(s1, uniqueWords, bigramTable);
		System.out.println( "\n" + "PROBABILTY OF SENETENCE 1 smoothing:  " + prob);
		
		prob = getProbabilitysentence(s2, uniqueWords, bigramTable);
		System.out.println("PROBABILTY OF SENETENCE 2 smoothing:  " + prob);
		
		//close
		sc.close();
	}

}
