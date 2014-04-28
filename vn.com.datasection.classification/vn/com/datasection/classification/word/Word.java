package vn.com.datasection.classification.word;

public class Word {
	private String word;
	private String type;
	
	public Word(String x) {
		String[] word;
		word = x.split("/");
		word[0] = word[0].replace('_', ' ');	
		
		this.word = word[0];
		this.type = word[1];
	}
	
	public String getWord() {
		return this.word;
	}
	public String getType () {
		return this.type;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public boolean checkWordNecessary() {
		
		if (this.word.length() == 1 && this.word.charAt(0) >= 'a' && this.word.charAt(0) <= 'z')
				return false;
		
		if (this.type.compareTo("Np") == 0 || this.type.compareTo("N") == 0 || this.type.compareTo("V") == 0 
				|| this.type.compareTo("A") == 0 || this.type.compareTo("P") == 0 || this.type.compareTo("I") == 0 
				|| this.type.compareTo("Y") == 0 || this.type.compareTo("Z") == 0 || this.type.compareTo("X") == 0)
			return true;
		
		return false;
		
		/*if (this.type.compareTo("Nc") == 0 || this.type.compareTo("Nu") == 0 || this.type.compareTo("R") == 0 
				|| this.type.compareTo("L") == 0 || this.type.compareTo("M") == 0 || this.type.compareTo("E") == 0 
				|| this.type.compareTo("C") == 0 || this.type.compareTo("CC") == 0 || this.type.compareTo("T") == 0
				|| this.word.compareTo(".") == 0 || this.word.compareTo(",") == 0 || this.word.compareTo(";") == 0 
				|| this.word.compareTo("?") == 0 || this.word.compareTo(":") == 0 || this.word.compareTo("...") == 0
				|| this.word.length() == 1)
			return false;*/
		
	}
}
