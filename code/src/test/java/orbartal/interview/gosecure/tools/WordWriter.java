package orbartal.interview.gosecure.tools;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordWriter {

	private static final String UTF_8 = "UTF-8";

	@SuppressWarnings("serial")
	private static class CountByKey extends HashMap<String, Long> {
		public void increase (String key) {
			Long value = this.get(key);
			value = (value!=null)? value : 0L;
			this.put(key, value + 1L);
		}
	}

	private static class MyPrintWriter extends PrintWriter implements AutoCloseable {

		private List<String> words;
		private CountByKey countByWord;

		public static MyPrintWriter create(File file, List<String> words){
			try {
				return new MyPrintWriter(file, words);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		}

		private MyPrintWriter(File file, List<String> words) throws Exception {
				super(file, UTF_8);
				this.words = words;
				countByWord = new CountByKey();
		}

		public void writeRandomLine(int wordsInLine) {
			for (int i = 0; i<wordsInLine; i++) {
				writeRandomWord();
			}
			this.println();
		}

		private void writeRandomWord() {
			int wordsMaxIndex = words.size()-1;
			int index = (int)(Math.random() * wordsMaxIndex);
			String w = words.get(index);
			this.print(w + " ");
			countByWord.increase(w);
		}

		public Map<String, Long> getCountByWord() {
			return countByWord;
		}

	}

	public Map<String, Long> writeWordsIntoFile(File file, int sizeInBytes, List<String> words) {
		int numberOfRows = (sizeInBytes / 80)+1;
		try (MyPrintWriter writer = MyPrintWriter.create(file, words)) {
			for (int k = 0; k<numberOfRows; k++) {
				writer.writeRandomLine(10);
			}
			return writer.getCountByWord();
		}
	}

}
