package orbartal.interview.gosecure;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.junit.Assert;

import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import orbartal.interview.gosecure.tools.WordWriter;

public class ClassifierTesterHelper {
	
	private static final String API_PATH = "/v1/classifier/upload";
	private static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
	
	private WordWriter wordWriter = new WordWriter();
	
	public void testClassifierCountWords(String path, int sizeInBytes, List<String> words) {
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
