package vn.com.datasection.classification.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

import com.google.gson.JsonObject;

import vn.com.datasection.classification.article.Article;
import vn.com.datasection.classification.article.ArticleClass;
import vn.com.datasection.classification.processing_file.FileReadWrite;

public class Main {
	
	public static int totalArticles;
	public static LinkedList<ArticleClass> classes, classesTraningOutput;
	public static LinkedList<File> ListInferenceInput;
	
	public static void prepare() {
		totalArticles = 0;
		classes = new LinkedList<ArticleClass>();
		classesTraningOutput = new LinkedList<ArticleClass>();
	}
	
	public static void getArticlesInClass() {
		FileReadWrite forderSource = new FileReadWrite("0_DATA_Traning_Input");
		File[] forderClasses = forderSource.getFile().listFiles();
		
		for (File forderClass : forderClasses) {
			FileReadWrite forder = new FileReadWrite(forderClass);
			ArticleClass articleClass = new ArticleClass();
			articleClass.setName(forder.getNameFile());
			try {
				articleClass.setListArticles(forder.getAllFile(forder.getFile()));
			} catch (IOException e) {
				System.out.println("Get all file in class... ERROR!");
			}
			classes.add(articleClass);
			totalArticles += articleClass.getListArticles().size();
		}
	}
	
	public static void processArticlesInClasses() {		
		for (ArticleClass articleClass : classes) {
			
			articleClass.getKeywordsInArticles();
			articleClass.statisticKeywordInArticles();
		}
	}
	
	public static void getBestKeyword() {
		
		LinkedList<String> listKeywordsBest = new LinkedList<String>();
		
		for (ArticleClass articleClass : classes ) {
			try {
				FileReadWrite outPut = new FileReadWrite(articleClass.getName() + ".txt", "0_DATA_Traning_Output");
				outPut.setBufferWrite();
				
				System.out.println("Word total: " + articleClass.getWordTotal());
				
				outPut.bufferWrite().write((double)articleClass.getListArticles().size() / totalArticles + "\n");
				
				listKeywordsBest = articleClass.getKeywordsBest();
				for (String keyword : listKeywordsBest) {
					outPut.bufferWrite().write(keyword + "\n" + articleClass.wordProbability(keyword) + "\n");
				}
				
				listKeywordsBest.clear();
				outPut.closeWrite();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void getClassesTraningOutput() {
		File forder = new File("0_DATA_Traning_Output");
		
		for (File file : forder.listFiles()) {
			try {
				FileReadWrite fileClassTraningOutput = new FileReadWrite(file);
				
				ArticleClass articleClass = new ArticleClass();
				articleClass.setName(file.getName().split("\\.")[0]);
				
				fileClassTraningOutput.setBufferRead();
				String line = "";
				double p;
				p = Double.parseDouble(fileClassTraningOutput.bufferRead().readLine());
				articleClass.setPci(p);
				while ((line = fileClassTraningOutput.bufferRead().readLine()) != null) {
					p = Double.parseDouble(fileClassTraningOutput.bufferRead().readLine());
					articleClass.getListBetsKeyword().put(line, p);
				}
				
				classesTraningOutput.add(articleClass);
			} catch(IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void processInferenceInput() throws IOException {
		JsonObject jsonObject = new JsonObject();
		Article article = new Article();
		
		int i = 1;
		FileReadWrite forder = new FileReadWrite("1_DATA_Inference_Input");
		
		for (File file : forder.getAllFile(forder.getFile())) {
			try {
				FileReadWrite articleFile = new FileReadWrite(file);
				articleFile.setBufferRead();
				
				jsonObject = articleFile.getContentFile();
				
				String className = "";
				double pCi_xMax = -1, pCi_x = 0;
				
				for (ArticleClass articleClass : classesTraningOutput) {
										
					String content = jsonObject.get("Content").toString();
					content = Article.processString(content);	
					if (content.length() > 0)
						article.statisticWord(content, articleClass);
					
					String title = jsonObject.get("Title").toString();
					title = Article.processString(title);	
					if (title.length() > 0)
						article.statisticWord(title, articleClass);
					
					String description = jsonObject.get("Description").toString();
					description = Article.processString(description);	
					if (description.length() > 0)
						article.statisticWord(description, articleClass);
					
					pCi_x = article.pCi_x(articleClass);
					
					//System.out.println("\tclass: " + articleClass.getName() + " /pC_x: " + pC_x);
					if (pCi_xMax < pCi_x) {
						pCi_xMax = pCi_x;
						className = articleClass.getName();
					}
					article.getListKeywords().clear();
				}
				
				System.out.println("nameFile: " + file.getName() + ":\n\tclassOld    :" + jsonObject.get("Category").toString() + "\n\tclassified  : " + className);
				
				FileReadWrite fileClassification = new FileReadWrite(file.getName(), "1_DATA_Inference_Output\\" + className);
				
				while (fileClassification.getFile().exists()) {
					fileClassification = new FileReadWrite(i + "_" + file.getName(), "1_DATA_Inference_Output\\" + className);
					i++;
				}
				Files.copy(file.toPath(), fileClassification.getFile().toPath());
				
				articleFile.closeRead();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	public static void main(String[] argts) throws IOException {
		
		prepare();
		
		File fileTraning = new File("0_DATA_Traning_Output");
		if (!fileTraning.exists()) {
			System.out.println("TRANING STARTING !...");
			getArticlesInClass();
			processArticlesInClasses();
			getBestKeyword();
		}
		
		System.out.println("TRANING DONE !...");
		
		getClassesTraningOutput();
		processInferenceInput();
			
	}
}
