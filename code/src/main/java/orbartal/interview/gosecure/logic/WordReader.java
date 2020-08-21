package orbartal.interview.gosecure.logic;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import orbartal.interview.gosecure.logic.model.WordCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

@Component
public class WordReader {

	public Flux<Mono<WordCount>> countWords(Flux<String> lines) {
		Flux<String> words = lines.map(s -> splitLine(s)).flatMapIterable(s -> s);
		Flux<String> words4 = words.filter(w->w.length()>3);
		Flux<GroupedFlux<String, String>> groupes = words4.groupBy(s1->s1);
		return groupes.map(g->getWordCount(g));

	}

	private Mono<WordCount> getWordCount(GroupedFlux<String, String> g) {
		return g.count().map(c->new WordCount(g.key(), c));
	}

	private List<String> splitLine(String s) {
		return Arrays.asList(s.split(" "));
	}

}
