package vn.com.datasection.classification.article;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import vn.com.datasection.classification.processing_file.FileReadWrite;
import vn.com.datasection.classification.word.Word;
import vn.hus.nlp.tagger.TaggerOptions;
import vn.hus.nlp.tagger.VietnameseMaxentTagger;


public class ArticleClass {

	private static final int KEYWORDSNUMBER = 1000;
	
	private String nameClass;
	private LinkedList<File> listAritcles;
	private int wordsTotal;
	private HashMap<String, Integer> listKeywords;
	private HashMap<String, Double> listBestKeyword;
	private double pCi;
	
	public ArticleClass() {
		this.listAritcles = new LinkedList<File>();
		this.listKeywords = new HashMap<String, Integer>();
		this.wordsTotal = 0;
		this.listBestKeyword = new HashMap<String, Double>();
		this.pCi = 0;
	}
	
	public void setName(String className) {
		this.nameClass = className;
	}
	public String getName() {
		return this.nameClass;
	}
	
	public void setListArticles(LinkedList<File> listFiles) {
		this.listAritcles = listFiles;
	}
	public LinkedList<File> getListArticles() {
		return this.listAritcles;
	}
	
	public int getWordTotal() {
		return this.wordsTotal;
	}
	public void calculatingWordTotal(int x) {
		this.wordsTotal += x;
	}
	
	public void setListBetsKeyword (HashMap<String, Double> listBestKeyword) {
		this.listBestKeyword = listBestKeyword;
	}
	public HashMap<String, Double> getListBetsKeyword () {
		return this.listBestKeyword;
	}
	
	public void setPci (double d) {
		this.pCi = d;
	}
	public double getPCi() {
		return this.pCi;
	}
	
	public HashMap<String, Integer> getListKeyWords() {
		return this.listKeywords;
	}
	private void addKeyword(HashMap<String, Integer> articleListKeywords) {
		Set<String> listKeywordsAdd = articleListKeywords.keySet();
		
		for (String keyword : listKeywordsAdd) {
			if (this.listKeywords.containsKey(keyword)) {
				int a = this.listKeywords.get(keyword) + articleListKeywords.get(keyword);
				this.listKeywords.put(keyword, a);
			} else
				this.listKeywords.put(keyword, articleListKeywords.get(keyword));
		}
	}
	
	public void getKeywordsInArticles() {
		JsonObject jsonObject = new JsonObject();
		Article article = new Article();
		
		
		for (File file : this.getListArticles()) {
			System.out.println("\t" + file.getName() + ": " + this.nameClass);
			try {
				FileReadWrite articleFile = new FileReadWrite(file);
				articleFile.setBufferRead();
				
				article.setNameFile(articleFile.getNameFile());
	
				jsonObject = articleFile.getContentFile();	
				article.setListKeywords(jsonObject);
				
				articleFile.closeRead();
				
				this.addKeyword(article.getListKeywords());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void statisticKeywordInArticles() {
		JsonObject jsonObject = new JsonObject();
		Article article = new Article();
		
		for (File file : this.getListArticles()) {
			try {
				
				FileReadWrite articleFile = new FileReadWrite(file);
				articleFile.setBufferRead();
				
				jsonObject = articleFile.getContentFile();
				
				String content = jsonObject.get("Content").toString();
				article.setContent(Article.processString(content));	
				if (article.getContent().length() > 0)
					this.statisticWord(article.getContent());
				
				String title = jsonObject.get("Title").toString();
				title = Article.processString(title);	
				if (title.length() > 0)
					this.statisticWord(title);
				
				String description = jsonObject.get("Description").toString();
				description = Article.processString(description);	
				if (description.length() > 0)
					this.statisticWord(description);
				
				articleFile.closeRead();
			} catch (IOException e) {
				System.out.println("Get content... ERROR");
			}
		}
	}
	
	private void statisticWord(String s) {
		String[] listWord = null;
		VietnameseMaxentTagger tagger = new VietnameseMaxentTagger();
		TaggerOptions.UNDERSCORE = true;
	
	    listWord = tagger.tagText(s).split(" ");
	    
	    this.wordsTotal += listWord.length;
	    
		for (int i = 0; i < listWord.length; i++) {
			String word1 = new Word(listWord[i]).getWord();
			if (this.listKeywords.containsKey(word1)) {
				int a = this.listKeywords.get(word1) + 1;
				this.listKeywords.put(word1, a);
			} else
				if (i < (listWord.length - 1)) {
					String word2 = word1 + " " + new Word(listWord[i+1]).getWord();
					if (this.listKeywords.containsKey(word2)) {
						int a = this.listKeywords.get(word2) + 1;
						this.listKeywords.put(word2, a);
						i++;
					}
				}
		}
	}
	
	private static Map sortByComparator(Map<String, Integer> unsortMap) {
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
                                       .compareTo(((Map.Entry) (o2)).getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	public LinkedList<String> getKeywordsBest() {
		Map sortByValue = sortByComparator(this.listKeywords);
		Set<String> listKeyword = sortByValue.keySet();
		
		LinkedList<String> listKeywords = new LinkedList<String>();
		int i = 0;
		for (String keyword : listKeyword) {
			listKeywords.add(keyword);
			i++;
			if (i == KEYWORDSNUMBER)
				break;
		}
		return listKeywords;
	}
	
	public double wordProbability(String keyword) {
		DecimalFormat df = new DecimalFormat("0.000000000000000");
		df.setRoundingMode(RoundingMode.UP);
		
		double x, y, z;
		
		x = this.listKeywords.get(keyword) + 1;
		if (this.listKeywords.size() > KEYWORDSNUMBER)
			y = this.wordsTotal + KEYWORDSNUMBER;
		else y = this.wordsTotal + this.listKeywords.size();
		
		z = x / y;
		z = z * 100000;
		z = Double.parseDouble(df.format(z));
		
		return z;
	}
	
	
}
