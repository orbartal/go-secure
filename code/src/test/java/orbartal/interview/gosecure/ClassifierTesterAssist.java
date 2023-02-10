package orbartal.interview.gosecure;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ClassifierTesterAssist {
	
	private static final String[] DAYS = {"Sunday",  "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",  "Saturday"};
	private static final List<String> WEEK_DAYS = Arrays.asList(DAYS).stream().collect(Collectors.toList());
	
	private final ClassifierTesterHelper helper = new ClassifierTesterHelper();
	
	public void testClassifierCountWordsWithWordList(String path, int sizeInBytes, List<String> words) {
		helper.testClassifierCountWords(path, sizeInBytes, words);
	}
	
	public void testClassifierCountWordsOnWeekDaysAsWords(String path, int sizeInBytes) {
		helper.testClassifierCountWords(path, sizeInBytes, WEEK_DAYS);
	}
	
	public void testOneFile(String basePath, int i) {
		String path = basePath + i;
		int sizeInBytes = 1000;
		helper.testClassifierCountWords(path, sizeInBytes, WEEK_DAYS);
	}


}
