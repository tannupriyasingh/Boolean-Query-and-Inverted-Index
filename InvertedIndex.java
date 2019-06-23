import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class InvertedIndex {
	

	public static void taatAND(HashMap<String, LinkedList<Integer>> postingsList, String[] term_arr) {
		LinkedList<Integer> result = new LinkedList<Integer>();
		LinkedList<Integer> temp = new LinkedList<Integer>();
		LinkedList<Integer> temp2 = new LinkedList<Integer>();
		LinkedList<Integer> filteredPosting = new LinkedList<Integer>();
		result = postingsList.get(term_arr[0]);
		LinkedList<Integer> r1 = new LinkedList<Integer>();
		r1.addAll(result);
		int comp = 0;
		for (int x = 1; x < term_arr.length; x++) {

			int i = 0, j = 0, a = 0, b = 0;

			if (!(term_arr[x].equals("\\s\\s+"))) { // ignoring spaces between terms
				filteredPosting = postingsList.get(term_arr[x]);

				while (i < filteredPosting.size() && j < r1.size()) {
					// System.out.println(filteredPosting.get(i)+ " " + r1.get(j));
					if (filteredPosting.get(i).equals(r1.get(j))) {
						temp.add(filteredPosting.get(i));
						i++;
						j++;
						comp++;

					} else if (filteredPosting.get(i) < r1.get(j)) {
						i++;
						comp++;

					} else {
						j++;
						comp++;

					}

				}

				while (a < temp.size() && b < r1.size()) {
					if (temp.get(a).equals(r1.get(b))) {
						temp2.add(temp.get(a));
						a++;
						b++;

					} else if (temp.get(a) < r1.get(b)) {
						a++;
					} else {
						b++;
					}

				}

			}
			r1.removeAll(r1);
			r1.addAll(temp2);

		}
		System.out.print("result taatAND ");
		for (int i = 0; i < r1.size(); i++) {
			System.out.print(r1.get(i) + " ");
		}
		System.out.println("compAND " + comp);
	}

	public static void taatOR(HashMap<String, LinkedList<Integer>> postingsList, String[] term_arr) {
		LinkedList<Integer> result2 = new LinkedList<Integer>();
		LinkedList<Integer> temp3 = new LinkedList<Integer>();
		LinkedList<Integer> temp22 = new LinkedList<Integer>();
		LinkedList<Integer> filteredPosting2 = new LinkedList<Integer>();
		result2 = postingsList.get(term_arr[0]);
		LinkedList<Integer> r2 = new LinkedList<Integer>();
		r2.addAll(result2);
		int comp = 0;
		for (int x = 1; x < term_arr.length; x++) {

			int i = 0, j = 0, a = 0, b = 0;

			if (!(term_arr[x].equals("\\s\\s+"))) { // ignoring spaces between terms
				filteredPosting2 = postingsList.get(term_arr[x]);

				while (i < filteredPosting2.size() && j < r2.size()) {
					// System.out.println(filteredPosting.get(i)+ " " + r1.get(j));
					if (filteredPosting2.get(i).equals(r2.get(j))) {
						temp3.add(filteredPosting2.get(i));
						i++;
						j++;
						comp++;

					} else if (filteredPosting2.get(i) < r2.get(j)) {
						temp3.add(filteredPosting2.get(i));
						i++;
						comp++;

					} else {
						temp3.add(r2.get(j));
						j++;
						comp++;

					}

				}
				while (i < filteredPosting2.size()) {
					temp3.add(filteredPosting2.get(i));
					i++;
				}
				while (j < r2.size()) {
					temp3.add(r2.get(j));
					j++;
				}

				while (a < temp3.size() && b < r2.size()) {
					if (temp3.get(a).equals(r2.get(b))) {

						temp22.add(temp3.get(a));
						a++;
						b++;

					} else if (temp3.get(a) < r2.get(b)) {
						temp22.add(temp3.get(a));
						a++;
					} else {
						temp22.add(r2.get(b));
						b++;
					}

				}
				while (a < temp3.size()) {
					temp22.add(temp3.get(a));
					i++;
				}
				while (b < r2.size()) {
					temp22.add(r2.get(b));
					j++;
				}

			}
			r2.removeAll(r2);
			r2.addAll(temp22);

		}
		System.out.print("result taatOR ");
		for (int i = 0; i < r2.size(); i++) {
			System.out.print(r2.get(i) + " ");
		}
		System.out.println("compOR " + comp);
	}

	
	public static void main(String[] args) throws IOException {

		File index = new File("IRproj2/index");
		Directory indexDir = FSDirectory.open(index.toPath());
		IndexReader indexReader = DirectoryReader.open(indexDir);
		// Fields fields = MultiFields.getFields(indexReader);

		// String outputfile = args[1];

		String term_arr[] = new String[20];

		String outputfile = "IRproj2/output.txt";
		File file=new File("IRproj2/output.txt");
		FileWriter filew = new FileWriter(file);

		// BufferedReader br = new BufferedReader (new FileReader (args[2]));
		// BufferedReader br = new BufferedReader(new FileReader("IRproj2/input1.txt"));
		BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream("IRproj2/input1.txt"), "UTF8"));
		String line; // reading input file using buffered reader

		HashMap<String, LinkedList<Integer>> postingsList = new HashMap<String, LinkedList<Integer>>();

		Iterator<String> fields = MultiFields.getFields(indexReader).iterator();
		while (fields.hasNext()) {

			String field_value = fields.next();
			// System.out.println(field_value);
			if (!field_value.equals("id")) {

				TermsEnum termsEnum = MultiFields.getTerms(indexReader, field_value).iterator();

				while (termsEnum.next() != null) {
					BytesRef term = termsEnum.term();
					PostingsEnum postings = MultiFields.getTermDocsEnum(indexReader, field_value, term);
					LinkedList<Integer> pos = new LinkedList<Integer>();
					// while(postings.nextDoc()!=postings.freq()){
					while (postings.nextDoc() != postings.NO_MORE_DOCS) {
						pos.add(postings.docID());
					}
					postingsList.put(term.utf8ToString(), pos);
				}
			}
		}

		while ((line = br.readLine()) != null) { // reading one line at a time String
			String t_arr = line.trim();
			term_arr = t_arr.split("\\s");
			for (int x = 0; x < term_arr.length; x++) {

				if (!(term_arr[x].equals("\\s\\s+"))) { // ignoring spaces between terms
					LinkedList<Integer> val = postingsList.get(term_arr[x]);

					System.out.println("GetPostings\n" + term_arr[x]);
					filew.write("GetPostings\n" + term_arr[x]);
					System.out.print("Postings list: ");
					filew.write("\nPostings list: ");
					for (int i = 0; i < val.size(); i++) {
						System.out.print(val.get(i) + " ");
						filew.write(val.get(i) + " ");
					}
					System.out.print("\n");
					filew.write("\n");
				}

			}

			/*taatAND(postingsList, term_arr);
			taatOR(postingsList, term_arr);*/
			daatAND(postingsList,term_arr);

		}

		filew.close();
	}

	private static void daatAND(HashMap<String, LinkedList<Integer>> postingsList, String[] term_arr) {
		boolean flag = true;
		java.util.List<LinkedList<Integer>> listOfDocuments = new ArrayList<>();
		LinkedList<Integer> val = new LinkedList<>();
		LinkedList<Integer> compList;// = new LinkedList<>();
		LinkedList<Integer> resultAnd=new LinkedList<>();
		int arrPointer[] = new int[term_arr.length];
		int sizeLOD[] = new int[term_arr.length];
		int i = 0, compCount = 0;

		for (i = 0; i < term_arr.length; i++) {
			val = postingsList.get(term_arr[i]);
			listOfDocuments.add(val);
			sizeLOD[i] = val.size();
		}

		for (int j : arrPointer) {
			arrPointer[j] = 0;
		}
		while (flag) {
			int scoreDoc=0;
			compList=new LinkedList<>();
			for (i = 0; i < term_arr.length; i++) {
				compList.add((listOfDocuments.get(i).get(arrPointer[i])));

			}

			int minValue = Collections.min(compList);
			for (i = 0; i < compList.size(); i++) {
				if (compList.get(i).equals(minValue)) {
					arrPointer[i]++;
					scoreDoc++;
				}
				compCount++;

			}
			if(scoreDoc == term_arr.length) {
				resultAnd.add(minValue);
				System.out.println("\n\n\nDAatresult"+resultAnd);
			}
			for (i = 0; i < arrPointer.length; i++) {
				if (sizeLOD[i] <= arrPointer[i]) {
					flag = false;
					break;
				}
			}

		}

	}
}
