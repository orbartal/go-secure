
package orbartal.interview.gosecure;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;

import io.restassured.RestAssured;

//All the tests pass but please only run them manually and one at a time.
@Ignore
public class ClassifierControllerTest {

	private static final int KB1 = 1000;
	private static final int MB1 = 1000 * KB1;
	private static final int MB100 = 100 * MB1;
	private static final int GB1 =  1000 * MB1;

	private static final String PATH_TO_SMALL_FILE = Configuration.get().getPathToSmallFile();
	private static final String PATH_TO_MEDIUM_FILE = Configuration.get().getPathToMediumFile();
	private static final String PATH_TO_LARGE_FILE = Configuration.get().getPathToLargeFile();
	private static final String PATH_TO_HUGE_FILE = Configuration.get().getPathToHugeFile();

	private final ClassifierTesterAssist assist = new ClassifierTesterAssist();

	public ClassifierControllerTest() {
		RestAssured.baseURI = Configuration.get().getServerBaseUrl();
	}

	@Test
	public void testOneSmallFile() {
		assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_SMALL_FILE, KB1);
	}

	@Test
	public void testOneMediumFile() {
		assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_MEDIUM_FILE, MB1);
	}

	@Test
	public void testOneLargeFile() {
		assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_LARGE_FILE, MB100);
	}

	@Test
	public void testOneHugeFile() throws FileNotFoundException, UnsupportedEncodingException {
		assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_HUGE_FILE, GB1);
	}
	
	@Test
	public void testManySmallFilesInSequence() {
		for (int i=0; i<100; i++) {
			assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_SMALL_FILE+i, KB1);
		}
	}

	@Test
	public void testManySmallFilesInParallel () throws Exception {
		ForkJoinPool myPool = new ForkJoinPool(20);
		myPool.submit(() ->
			IntStream.range(1, 100).boxed().parallel().forEach
			(i->assist.testClassifierCountWordsOnWeekDaysAsWords(PATH_TO_SMALL_FILE+i, KB1))
		).get();
	}
	
	@Test
	public void testOneSmallFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(100);
		assist.testClassifierCountWordsWithWordList(PATH_TO_SMALL_FILE, KB1, words);
	}
	
	@Test
	public void testOneLargeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 10);
		assist.testClassifierCountWordsWithWordList(PATH_TO_LARGE_FILE, MB100, words);
	}

	//Oxford Dictionary define 273,000 words
	@Test
	public void testOneHugeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 273);
		assist.testClassifierCountWordsWithWordList(PATH_TO_LARGE_FILE, GB1, words);
	}

	@Test
	public void testOneSmallFileWithNotCaseSensitive () {
		List<String> words = Arrays.asList("Abcd", "aBcd", "abcd", "ABCD");
		assist.testClassifierCountWordsWithWordList(PATH_TO_SMALL_FILE, KB1, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterByWordSize () {
		List<String> words = Arrays.asList("abcd", "abc", "a", "dc");
		assist.testClassifierCountWordsWithWordList(PATH_TO_SMALL_FILE, KB1, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterBlancks () {
		List<String> words = Arrays.asList("abcd", " ", "  ");
		assist.testClassifierCountWordsWithWordList(PATH_TO_SMALL_FILE, KB1, words);
	}

	private List<String> readRandomWords(int size) {
		return IntStream.range(1, size).boxed().map(i->UUID.randomUUID().toString()).collect(Collectors.toList());
	}

}
