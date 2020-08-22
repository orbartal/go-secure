
package orbartal.interview.gosecure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import orbartal.interview.gosecure.tools.WordWriter;

//All the tests pass but please only run them manually and one at a time.
@Ignore
public class ClassifierControllerTest {

	private static final String API_PATH = "/v1/classifier/upload";
	private static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
	private static final String[] DAYS = {"Sunday",  "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",  "Saturday"};
	private static final List<String> WEEK_DAYS = Arrays.asList(DAYS).stream().collect(Collectors.toList());

	private WordWriter wordWriter = new WordWriter();

	public ClassifierControllerTest() {
		RestAssured.baseURI = Configuration.get().getServerBaseUrl();
	}

	@Test
	public void testOneSmallFile() {
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testOneMediumFile() {
		String path = Configuration.get().getPathToMediumFile();
		int sizeInBytes = 1000 * 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testOneLargeFile() {
		String path = Configuration.get().getPathToLargeFile();
		int sizeInBytes = 1000 * 1000 * 100;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testOneHugeFile() throws FileNotFoundException, UnsupportedEncodingException {
		String path = Configuration.get().getPathToHugeFile();
		int sizeInBytes = 1000 * 1000 * 1000;
		testClassifierCountWords(path, sizeInBytes);
	}
	
	@Test
	public void testManySmallFilesInSequence() {
		String basePath = Configuration.get().getPathToSmallFile();
		for (int i=0; i<100; i++) {
			testOneFile(basePath, i);
		}
	}

	@Test
	public void testManySmallFilesInParallel () throws Exception {
		String basePath = Configuration.get().getPathToSmallFile();
		ForkJoinPool myPool = new ForkJoinPool(20);
		myPool.submit(() ->
			IntStream.range(1, 100).boxed().parallel().forEach(i->testOneFile(basePath, i))
		).get();
	}
	
	@Test
	public void testOneSmallFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(100);
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes, words);
	}
	
	@Test
	public void testOneLargeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 10);
		String path = Configuration.get().getPathToLargeFile();
		int sizeInBytes = 1000 * 1000 * 100;
		testClassifierCountWords(path, sizeInBytes, words);
	}

	//Oxford Dictionary define 273,000 words
	@Test
	public void testOneHugeFileWithManyWords () throws Exception {
		List<String> words = readRandomWords(1000 * 273);
		String path = Configuration.get().getPathToLargeFile();
		int sizeInBytes = 1000 * 1000 * 1000;
		testClassifierCountWords(path, sizeInBytes, words);
	}

	@Test
	public void testOneSmallFileWithNotCaseSensitive () {
		List<String> words = Arrays.asList("Abcd", "aBcd", "abcd", "ABCD");
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterByWordSize () {
		List<String> words = Arrays.asList("abcd", "abc", "a", "dc");
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes, words);
	}
	
	@Test
	public void testOneSmallFileWithFilterBlancks () {
		List<String> words = Arrays.asList("abcd", " ", "  ");
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes, words);
	}

	private List<String> readRandomWords(int size) {
		return IntStream.range(1, size).boxed().map(i->UUID.randomUUID().toString()).collect(Collectors.toList());
	}

	private void testOneFile(String basePath, int i) {
		String path = basePath + i;
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	private void testClassifierCountWords(String path, int sizeInBytes) {
		testClassifierCountWords(path, sizeInBytes, WEEK_DAYS);
	}

	private void testClassifierCountWords(String path, int sizeInBytes, List<String> words) {
		long start = System.currentTimeMillis();
		File file = new File(path);
		if (!file.exists()) {
			try {file.createNewFile();} catch (IOException e) {}
		}
		Assert.assertTrue(file.exists());

		Map<String, Long> expected = wordWriter.writeWordsIntoFile(file, sizeInBytes, words);
		Response response = RestAssured.given()
				.contentType(MULTIPART_FORM_DATA_VALUE)
				.multiPart("file",file)
				.request(Method.POST, API_PATH);

		Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
		Assert.assertNotNull(response.getBody());

		String body = response.getBody().asString();
		@SuppressWarnings("unchecked")
		Map<String, Double> m = new Gson().fromJson(body, Map.class);
		Map<String, Long> actaul = m.entrySet().stream().collect(Collectors.toConcurrentMap(p->p.getKey(), p->p.getValue().longValue()));
		Assert.assertEquals(expected, actaul);
		
		long end = System.currentTimeMillis();
		long timeDiff = end - start;
		long second = timeDiff / 1000;
		System.out.println("Running test for "+ second + " second");
	}

}
