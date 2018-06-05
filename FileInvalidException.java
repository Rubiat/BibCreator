package Assignment3;

public class FileInvalidException extends Exception {

	String s = "";
	
	public FileInvalidException() {
		s = "Error: Input file cannot be parsed due to missing information";
	}
	
	public FileInvalidException (String s) {
		this.s = s;
	}
	
	public FileInvalidException (int i) {
		this.s = "Problem detected with input file: Latex" + i + ".bib";
	}
	
	public String getMessage() {
		return s;
	}
}
