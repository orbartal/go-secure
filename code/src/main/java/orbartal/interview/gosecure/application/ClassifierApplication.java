package orbartal.interview.gosecure.application;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import orbartal.interview.gosecure.logic.WordReader;
import orbartal.interview.gosecure.logic.model.WordCount1;
import orbartal.interview.gosecure.tools.FileReader;
import orbartal.interview.gosecure.tools.JsonMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClassifierApplication {

	private FileReader fileReader;
	private WordReader wordReader;
	private JsonMapper jsonMapper;

	public ClassifierApplication(FileReader fileReader, WordReader wordReader, JsonMapper jsonMapper) {
		this.fileReader = fileReader;
		this.wordReader = wordReader;
		this.jsonMapper = jsonMapper;
	}

	public Mono<String> analysis(Mono<FilePart> input) {
		Flux<String> lines = this.fileReader.readLines(input);
		Flux<WordCount1> countWords = this.wordReader.countWords(lines);
		return this.jsonMapper.readJson(countWords);
	}

}
