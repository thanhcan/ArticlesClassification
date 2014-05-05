package vn.com.datasection.classification.article;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.gson.JsonObject;

import vn.com.datasection.classification.word.Word;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;

public class Article extends ArticleClass {
	private String nameFile;
	private String content;
	private HashMap<String, Integer> listKeywords;
	
	public Article() {
		listKeywords = new HashMap<String, Integer>();
	}
	
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	public String getNameFile() {
		return this.nameFile;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return this.content;
	}
	
	public void setListKeywords(String[] listKeywords) {
		for (String keyword : listKeywords)
			this.listKeywords.put(keyword, 0);
	}
	public HashMap<String, Integer> setListKeywords(JsonObject jsonObject) {
		
		String listKeywrodS = jsonObject.get("KeyWords").toString();
		listKeywrodS = listKeywrodS.replaceAll("\\[", ""); listKeywrodS = listKeywrodS.replaceAll("\\]", "");
		listKeywrodS = listKeywrodS.replaceAll("\"", "");
		String[] listKeyword = listKeywrodS.split(",");
		
		this.setListKeywords(listKeyword);
		
		return this.listKeywords;
	}
	public HashMap<String, Integer> getListKeywords() {
		return this.listKeywords;
	}
	
	public void statisticWord(String s, ArticleClass articleClass) {
		String[] listWord = null;
		VietnameseMaxentTagger tagger = new VietnameseMaxentTagger();
		TaggerOptions.UNDERSCORE = true;
	
	    listWord = tagger.tagText(s).split(" ");
	    
		for (int i = 0; i < listWord.length; i++) {			
			String word1 = new Word(listWord[i]).getWord();
			if (articleClass.getListBetsKeyword().containsKey(word1)) {
				if (this.listKeywords.containsKey(word1)) {
					int a = this.listKeywords.get(word1) + 1;
					this.listKeywords.put(word1, a);
				} else
					this.listKeywords.put(word1, 1);
			} else
				if (i < (listWord.length - 1)) {					
					String word2 = word1 + " " + new Word(listWord[i+1]).getWord();
					if (articleClass.getListBetsKeyword().containsKey(word2)) {						
						if (this.listKeywords.containsKey(word1)) {
							int a = this.listKeywords.get(word1) + 1;
							this.listKeywords.put(word2, a);
						} else
							this.listKeywords.put(word2, 1);
					}
				}
		}
	}
	
	public double pCi_x(ArticleClass classArticle) {
		double probability = 1;
		Set<String> listKeywords = this.listKeywords.keySet();
		
		for (String keyword : listKeywords) {
			for (int i = 0; i < this.listKeywords.get(keyword); i++)
				probability *= classArticle.getListBetsKeyword().get(keyword);
				//probability /= 100;
		}
		
		probability *= classArticle.getPCi();
		
		DecimalFormat df = new DecimalFormat("0.000000000000000");
		df.setRoundingMode(RoundingMode.UP);
		probability = Double.parseDouble(df.format(probability));
		
		return probability;
	}
	
	public static String processString(String s){
		s = s.replaceAll("\"", "");
		s = s.replaceAll("'", "\"");
		Document jsoup = Jsoup.parse(s);
		s = jsoup.text();
		
		//String sepecialChats = "<>\\!~@#$%&()+*^\'“”\"";
		String sepecialChats = "<>\\!~@#$%&()+*^";
		for (int i = 0; i < sepecialChats.length(); i++)
			s = s.replace(sepecialChats.charAt(i), '$');
		s = s.replaceAll("\\$", "");
		s = s.replaceAll(" [a-zA-Z] ", " ");
		
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern.compile("[^ ]");
		matcher = pattern.matcher(s);				
		if (matcher.find() == false)
			s = "";
		
		return s;
	}
}
