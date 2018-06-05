// --------------------------------------------------------------------------
// Written by Rubiat Zaman
/* This program reads 10 files and writes the contents of the valid files into 3 separate 
 * .json files */
// --------------------------------------------------------------------------

/** Rubiat Zaman
 * 	Comp 249
 * 	Assignment 3
 * 	Due Date: 19/03/2018
 * */

package Assignment3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BibCreator {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Welcome to Rubiat's BibCreator!\n");

		openInputFiles();
		openOutputFiles();
		processFilesForValidation();
		displayFile();

		System.out.println("Goodbye! Hope you have enjoyed using the BibCreator!");
	}

	static Scanner [] reader = new Scanner [10];
	static PrintWriter [] writerIEEE = new PrintWriter [10];
	static PrintWriter [] writerACM = new PrintWriter [10];
	static PrintWriter [] writerNJ = new PrintWriter [10];
	static String pathIEEE = "C:\\Users\\Rubiat Zaman\\Desktop\\Comp249_W18_Assg3_Files\\IEEE";
	static String pathACM = "C:\\Users\\Rubiat Zaman\\Desktop\\Comp249_W18_Assg3_Files\\ACM";
	static String pathNJ = "C:\\Users\\Rubiat Zaman\\Desktop\\Comp249_W18_Assg3_Files\\NJ";

	static String IEEE, ACM, NJ;  // strings that will be used to store the formatted articles

	static int numOfInvalidFiles;


	/** This method tries to open all 10 files for reading using scanner objects. System exits 
	 * if one cannot be opened */
	public static void openInputFiles() {
		for (int i = 0; i < 10; i++) {
			String path = "C:\\Users\\Rubiat Zaman\\Desktop\\Comp249_W18_Assg3_Files\\Latex" + (i+1) + ".bib";

			try {
				reader[i] = new Scanner(new FileInputStream(path));
			} 
			catch (FileNotFoundException e) {
				System.out.println("Could not open input file Latex" + (i+1) + ".bib for reading.");
				System.out.println("Please check if file exists. Program will terminate after closing any already opened files");

				for (int j = 0; j < i; j++) {
					reader[j].close(); 
				}
				System.exit(0);
			}
		}
	}

	/** This method tries to open all the json output files. If an exception is thrown 
	 * and it cannot create a certain file, it deletes all the already created files before that file and exits system*/
	public static void openOutputFiles() {

		for (int i = 0; i < 10; i++) {

			try {
				writerIEEE[i] = new PrintWriter(new FileOutputStream(pathIEEE + (i+1) + ".json"));
				writerACM[i] = new PrintWriter(new FileOutputStream(pathACM + (i+1) + ".json"));
				writerNJ[i] = new PrintWriter(new FileOutputStream(pathNJ + (i+1) + ".json"));
			} 
			catch (FileNotFoundException e) {
				System.out.println("Could not generate output file for Latex" + (i+1) + ".bib");

				File [] fileIEEE = new File[10];
				File [] fileACM = new File[10];
				File [] fileNJ = new File[10];

				for (int j = 0; j < 10; j++) {
					fileIEEE[j] = new File(pathIEEE + (j+1) + ".json");

					if (fileIEEE[j].exists()) {
						if (writerIEEE[j] != null)
							writerIEEE[j].close();
						fileIEEE[j].delete();
					}

					fileACM[j] = new File(pathACM + (j+1) + ".json");

					if (fileACM[j].exists()) {
						if (writerACM[j] != null)
							writerACM[j].close();
						fileACM[j].delete();
					}

					fileNJ[j] = new File(pathNJ + (j+1) + ".json");

					if (fileNJ[j].exists()) {
						if (writerNJ[j] != null)
							writerNJ[j].close();
						fileNJ[j].delete();
					}
				}

				for (int k = 0; i < 10; i++)
					reader[k].close(); //close input streams too

				System.exit(0);
			}
		}
	}

	/** This method first checks for file validity, and then calls on other methods that 
	 * create the actual formats */
	public static void processFilesForValidation() {

		for (int i = 0; i < 10; i++) {
			reader[i].useDelimiter("=\\{\\}");  //  This ultimately sets "={}" as the new delimiter

			String search = reader[i].next(); // now it either reads the whole document (means its valid) or reads until the first "{} (means invalid)
			// even if the file doesn't have "{}" there'll be one token which is the whole file

			try {

				if (reader[i].hasNext()) { // if the file still has another/more "{}" token(s)
					numOfInvalidFiles++;
					throw new FileInvalidException(i+1);
				}
				else {
					createOutputFiles(i);
					writerIEEE[i].close();
					writerACM[i].close();
					writerNJ[i].close();

					reader[i].close();
				}
			}
			catch (FileInvalidException e) {  // uses constructor that takes an int parameter
				System.out.println("Error: Detected empty field! \n=============================\n");
				System.out.println(e.getMessage());
				String invalidField = search.substring(search.lastIndexOf("\n")+1);  // invalidField is the word in the last line right before the {}
				System.out.println("File is invalid: Field \"" + invalidField + "\" is empty. "
						+ "Processing stopped at this point. Other empty fields may be present as well\n");

				reader[i].close(); 

				File [] toDelete = new File [3]; // will delete the 3 output files for that particular invalid .bib file

				toDelete[0] = new File(pathIEEE + (i+1) + ".json");
				toDelete[1] = new File(pathACM + (i+1) + ".json");
				toDelete[2] = new File(pathNJ + (i+1) + ".json");

				writerIEEE[i].close();  // have to close printwriters before deleting!
				writerACM[i].close();
				writerNJ[i].close();

				toDelete[0].delete();
				toDelete[1].delete();
				toDelete[2].delete();
			}
		}

		System.out.println("A total of " + numOfInvalidFiles + " were invalid, and could not "
				+ "be processed. All other " + (10 - numOfInvalidFiles) + " \"valid\" files"
				+ " have been created.\n");
	}

	/** This method reads through the articles of the file that called it and sorts out all 
	 * the different fields in separate strings. It then calls on other methods that create 
	 * the ACM, NJ, and IEEE formats
	 * @param i */
	private static void createOutputFiles (int i) {

		for (int z = 0; z < 10; z++) 
			reader[z].close();
		// closing and reopening because it already read to the end of file once
		openInputFiles();

		reader[i].useDelimiter("@ARTICLE");

		int count = 1; // keeping count of number of articles in each particular .bib file (for ACM files)

		String author = "", year = "", journal = "", title = "", volume = "", number = "",
				pages = "", doi = "", month = "", ISSN = "";  // going to use these strings to store the fields' names neatly

		while (reader[i].hasNext()) { // while loop for each articles in a particular .bib file


			String article = reader[i].next();  // reads the next article (whole article paragraph)
			String [] articleLine = null;  // 'articleLine' array will cut the article paragraphs into individual lines

			if (article.contains("\n"))   // doing this to make sure because some of the files have some weird sh*t on top of file
				articleLine = article.split("\n");
			else
				continue;  // else: just go to the next 'article' directly because this article is abnormal and creates problem

			for (int a = 0; a < articleLine.length; a++) { // loops through each line of article
				// different if statements to store all the different fields whenever it finds them

				if (articleLine[a].contains("author=")) {
					author = articleLine[a];
					author = author.substring(author.indexOf("={"), author.indexOf("},"));
					author = author.replace("={", ""); // now it's narrowed down to the exact word
					continue;
				}

				else if (articleLine[a].contains("year=")) {
					year = articleLine[a];
					year = year.substring(year.indexOf("={"), year.indexOf("},"));
					year = year.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("journal=")) {
					journal = articleLine[a];
					journal = journal.substring(journal.indexOf("={"), journal.indexOf("},"));
					journal = journal.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("title=")) {
					title = articleLine[a];
					title = title.substring(title.indexOf("={"), title.indexOf("},"));
					title = title.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("volume=")) {
					volume = articleLine[a];
					volume = volume.substring(volume.indexOf("={"), volume.indexOf("},"));
					volume = volume.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("number=")) {
					number = articleLine[a];
					number = number.substring(number.indexOf("={"), number.indexOf("},"));
					number = number.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("pages=")) {
					pages = articleLine[a];
					pages = pages.substring(pages.indexOf("={"), pages.indexOf("},"));
					pages = pages.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("doi=")) {
					doi = articleLine[a];
					doi = doi.substring(doi.indexOf("={"), doi.indexOf("},"));
					doi = doi.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("month=")) {
					month = articleLine[a];
					month = month.substring(month.indexOf("={"), month.indexOf("},"));
					month = month.replace("={", "");
					continue;
				}

				else if (articleLine[a].contains("ISSN=")) {
					ISSN = articleLine[a];
					ISSN = ISSN.substring(ISSN.indexOf("={"), ISSN.indexOf("},"));
					ISSN = ISSN.replace("={", "");
					continue;
				}

			}

			// by now, after the for loop, all needed fields should be found
			// will use these fields to create the 3 formats

			IEEE = createIEEE(author, year, journal, title, volume, number, pages, month);
			writerIEEE[i].println(IEEE);
			writerIEEE[i].println();

			ACM = createACM(author, year, journal, title, volume, number, pages, doi);
			writerACM[i].println("[" + count + "]\t" + ACM);
			writerACM[i].println();
			count++;

			NJ = createNJ(author, year, journal, title, volume, pages);
			writerNJ[i].println(NJ);
			writerNJ[i].println();

		}


	}

	/** This method puts the strings together in ACM format 
	 * 	@param author, year, journal, title, volume, number, pages, doi
	 */
	private static String createACM (String author, String year, String journal, String title, 
			String volume, String number, String pages, String doi) {

		String [] firstAuthor = author.split("and"); // separate all the authors' names

		return (firstAuthor[0] + "et al. " + year + ". " + title + ". " + journal + ". " + 
				volume + ", " + number + " (" + year + "), " + pages + ". DOI:https://doi.org/" + doi + ".");
		// the above is in ACM format
	}

	/** This method puts together the strings in IEEE format */
	private static String createIEEE (String author, String year, String journal, String title, 
			String volume, String number, String pages, String month) {

		author = author.replace(" and", ",");

		return (author + ". \"" + title + "\", " + journal + ", vol. " + volume + ", no. " + 
				number + ", p. " + pages + ", " + month + " " + year + ".");
	}

	/** This methods puts together the strings in NJ format */
	private static String createNJ (String author, String year, String journal, String title, 
			String volume, String pages) {
		author = author.replace("and", "&");

		return (author + ". " + title + ". " + journal + ". " + volume + ", " + pages + 
				"(" + year + ").");
	}

	/** this method prompts the user for the name of file to read and displays it if it exists.
	 *  If it doesn't exist the user gets a second try, if the second file doesn't exist, the 
	 *  program ends */
	public static void displayFile() {
		Scanner input = new Scanner(System.in);
		String filePath;
		String path = "C:\\Users\\Rubiat Zaman\\Desktop\\Comp249_W18_Assg3_Files";
		System.out.println("Please enter the name of one of the files that you need to review:");
		filePath = input.next();

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(path + "\\" + filePath));
			String x = reader.readLine();
			System.out.println("Here are the contents of the succesfully created file: " + filePath + "\n");
			while (x != null) {
				System.out.println(x);
				x = reader.readLine();
			}
			input.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not open desired file. File does not exist");
			System.out.println("\nHowever you will be given another chance to enter a valid file name."
					+ " \nPlease enter the name of one of the files you need to review:");
			filePath = input.next();

			try {
				reader = new BufferedReader(new FileReader(path + "\\" + filePath));
				String x = reader.readLine();
				System.out.println("Here are the contents of the succesfully created file: " + filePath + "\n");
				while (x != null) {
					System.out.println(x);
					x = reader.readLine();
				}
				input.close();
			} catch (FileNotFoundException e1) {
				System.out.println("Could not open the input file again! The file does not "
						+ "exist or could not be created. Program will exit.");
				System.exit(0);
			} catch (IOException e1) {
				System.out.println("Problem reading file");
			}
		} catch (IOException e) {
			System.out.println("Problem reading the file");
		}
	}
}
