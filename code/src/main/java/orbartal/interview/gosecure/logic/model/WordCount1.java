package orbartal.interview.gosecure.logic.model;

import reactor.core.publisher.Mono;

public class WordCount1 {

	private final String word;
	private final Mono<Long> count;

	public WordCount1(String word, Mono<Long> count) {
		this.word = word;
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public Mono<Long> getCount() {
		return count;
	}

}
