
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

	private final ClassifierTesterAssist assist = new ClassifierTesterAssist();

	public ClassifierControllerTest() {
		RestAssured.baseURI = Configuration.get().getServerBaseUrl();
	}

	@Test
	public void testOneSmallFile() {
		String path = Configuration.get().getPathToSmallFile();
		assist.testClassifierCountWordsOnWeekDaysAsWords(path, KB1);
	}

	@Test
	public void testOneMediumFile() {
		String path = Configuration.get().getPathToMediumFile();
		assist.testClassifierCountWordsOnWeekDaysAsWords(path, MB1);
	}

	@Test
	public void testOneLargeFile() {
		String path = Configuration.get().getPathToLargeFile();
		assist.testClassifierCountWordsOnWeekDaysAsWords(path, MB100);
	}

	@Test
	public void testOneHugeFile() throws FileNotFoundException, UnsupportedEncodingException {
		String path = Configuration.get().getPathToHugeFile();
		assist.testClassifierCountWordsOnWeekDaysAsWords(path, GB1);
	}
	
	@Test
	public void testManySmallFilesInSequence() {
		String basePath = Configuration.get().getPathToSmallFile();
		for (int i=0; i<100; i++) {
			assist.testOneFile(basePath, i);
		}
	}

	@Test
	public void testManySmallFilesInParallel () throws Exception {
		String basePath = Configuration.get().getPathToSmallFile();
		ForkJoinPool myPool = new ForkJoinPool(20);
		myPool.submit(() ->
			IntStream.range(1, 100).boxed().parallel().forEach(i->assist.testOneFile(basePath, i))
		).get();
	}
	
	@Test
	public void testOneSmallFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(100);
		String path = Configuration.get().getPathToSmallFile();
		assist.testClassifierCountWordsWithWordList(path, KB1, words);
	}
	
	@Test
	public void testOneLargeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 10);
		String path = Configuration.get().getPathToLargeFile();
		assist.testClassifierCountWordsWithWordList(path, MB100, words);
	}

	//Oxford Dictionary define 273,000 words
	@Test
	public void testOneHugeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 273);
		String path = Configuration.get().getPathToLargeFile();
		assist.testClassifierCountWordsWithWordList(path, GB1, words);
	}

	@Test
	public void testOneSmallFileWithNotCaseSensitive () {
		List<String> words = Arrays.asList("Abcd", "aBcd", "abcd", "ABCD");
		String path = Configuration.get().getPathToSmallFile();
		assist.testClassifierCountWordsWithWordList(path, KB1, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterByWordSize () {
		List<String> words = Arrays.asList("abcd", "abc", "a", "dc");
		String path = Configuration.get().getPathToSmallFile();
		assist.testClassifierCountWordsWithWordList(path, KB1, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterBlancks () {
		List<String> words = Arrays.asList("abcd", " ", "  ");
		String path = Configuration.get().getPathToSmallFile();
		assist.testClassifierCountWordsWithWordList(path, KB1, words);
	}

	private List<String> readRandomWords(int size) {
		return IntStream.range(1, size).boxed().map(i->UUID.randomUUID().toString()).collect(Collectors.toList());
	}

}
