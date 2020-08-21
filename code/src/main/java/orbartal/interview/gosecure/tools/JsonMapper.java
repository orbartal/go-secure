package orbartal.interview.gosecure.tools;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import orbartal.interview.gosecure.logic.model.WordCount1;
import orbartal.interview.gosecure.logic.model.WordCount2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JsonMapper {

	public Mono<String> readJson(Flux<WordCount1> input) {
		Flux<WordCount2> counts = input.map(wc->toWordCount2(wc).flux()).flatMap(s->s);
		Mono<Aggregator> mono = counts.reduce(new Aggregator(), (a, wc)->a.add(wc));
		return mono.map(x->x.toString());
	}

	private Mono<WordCount2> toWordCount2(WordCount1 wc) {
		return wc.getCount().map(c->new WordCount2(wc.getWord(), c));
	}
	
	private static class Aggregator {

		private JsonObject json = new JsonObject();
		
		public Aggregator add (WordCount2 wc) {
			json.addProperty(wc.getWord(), wc.getCount());
			return this;
		}
		
		public String toString() {
			return json.toString();
		}
	}

}
