package orbartal.interview.gosecure.logic;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import orbartal.interview.gosecure.logic.model.CountByKey;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WordReader {

	public Mono<CountByKey> countWords(Flux<String> lines) {
		Flux<String> words = lines.map(s -> splitLine(s)).flatMapIterable(s -> s);
		Flux<String> words4 = words.filter(w->w.length()>3).map(s->s.toLowerCase());
		return words4.reduce(new CountByKey(), (a, s)->a.increase(s));
	}

	private List<String> splitLine(String s) {
		return Arrays.asList(s.split(" "));
	}

}
