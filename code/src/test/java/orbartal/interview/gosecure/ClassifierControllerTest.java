
package orbartal.interview.gosecure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import orbartal.interview.gosecure.tools.WordWriter;

//All the tests work but please run them one by one manually.
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
	public void testSmallFile() {
		String path = Configuration.get().getPathToSmallFile();
		int sizeInBytes = 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testMediumFile() {
		String path = Configuration.get().getPathToMediumFile();
		int sizeInBytes = 1000 * 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testLargeFile() {
		String path = Configuration.get().getPathToLargeFile();
		int sizeInBytes = 1000 * 1000 * 100;
		testClassifierCountWords(path, sizeInBytes);
	}

	@Test
	public void testHugeFile() throws FileNotFoundException, UnsupportedEncodingException {
		String path = Configuration.get().getPathToHugeFile();
		int sizeInBytes = 1000 * 1000 * 1000;
		testClassifierCountWords(path, sizeInBytes);
	}

	private void testClassifierCountWords(String path, int sizeInBytes) {
		File file = new File(path);
		Assert.assertTrue(file.exists());

		Map<String, Long> expected = wordWriter.writeWordsIntoFile(file, sizeInBytes, WEEK_DAYS);
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
	}

}
