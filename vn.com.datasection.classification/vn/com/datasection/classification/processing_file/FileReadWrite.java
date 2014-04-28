package vn.com.datasection.classification.processing_file;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FileReadWrite {
	
	private String nameFile;
	private String path;
	private File file;
	private BufferedReader bufferRead;
	private BufferedWriter bufferWrite;
	
	public FileReadWrite() {
		// TODO Auto-generated constructor stub
	}
	
	public FileReadWrite(File file) {
		this.file = file;
		this.nameFile = file.getName();
		this.path = file.getPath();
	}
	
	public FileReadWrite(String nameFile) {
		this.file = new File(nameFile);
		this.nameFile = nameFile;
	}
	
	public FileReadWrite(String nameFile, String path) throws IOException {		
		this.nameFile = nameFile;
		this.path = path;
		
		this.file = new File(path);
		if (!file.exists())
			new File(path).mkdirs();
		this.file = new File(path, nameFile);
	}
	
	public void setBufferRead () throws FileNotFoundException {
		FileInputStream fileIn = new FileInputStream(this.file);
		InputStreamReader inputStream = new InputStreamReader(fileIn, StandardCharsets.UTF_8);
		this.bufferRead = new BufferedReader(inputStream);
	}
	
	public void setBufferWrite() throws FileNotFoundException {
		FileOutputStream fileOut = new FileOutputStream(this.file);
		OutputStreamWriter outputStream = new OutputStreamWriter(fileOut, StandardCharsets.UTF_8);
		this.bufferWrite = new BufferedWriter(outputStream);
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	public File getFile() {
		return this.file;
	}
	
	public String getNameFile() {
		return this.nameFile;
	}
	public String getPathFile() {
		return this.path;
	}

	public BufferedReader bufferRead() {
		return this.bufferRead;
	}
	public BufferedWriter bufferWrite() {
		return this.bufferWrite;
	}
	
	public void closeRead() throws IOException {
		this.bufferRead.close();
	}
	public void closeWrite() throws IOException {
		this.bufferWrite.close();
	}
	
	public LinkedList<File> getAllFile(File file) throws IOException {
		LinkedList<File> listFiles = new LinkedList<File>();
		File[] files = file.listFiles();
		
		for (int i = 0; i < files.length; i++)
			if (files[i].isDirectory()) {
				FileReadWrite forderSub = new FileReadWrite(files[i].getName(), files[i].getPath());
				listFiles.addAll(forderSub.getAllFile(files[i]));
			} else
				if (files[i].getName().compareTo("0_LinkOfArticle.txt")!=0)
					listFiles.add(files[i]);
			
		return listFiles;
	}
	
	/**
	 * Lấy nội dung các thành phần của bài báo
	 * @return 
	 * @throws IOException
	 */
	public JsonObject getContentFile() throws IOException {
		JsonParser jsonParser = new JsonParser();
		String contentFile = "", line;
		while ((line = this.bufferRead().readLine()) != null)
			contentFile = contentFile + line;
		contentFile = "[ " + contentFile + " ]";
		
		JsonArray jsonArray = (JsonArray)jsonParser.parse(contentFile);
		return (JsonObject)jsonArray.get(0);
	}
}
