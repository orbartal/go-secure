package orbartal.interview.gosecure.tools;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import orbartal.interview.gosecure.logic.model.WordCount;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class JsonMapper {

	public Mono<String> readJson(Flux<Mono<WordCount>> input) {
		Flux<WordCount> counts = input.map(wc->wc.flux()).flatMap(s->s);
		Mono<Aggregator> mono = counts.reduce(new Aggregator(), (a, wc)->a.add(wc));
		return mono.map(x->x.toString());
	}

	private static class Aggregator {

		private JsonObject json = new JsonObject();
		
		public Aggregator add (WordCount wc) {
			json.addProperty(wc.getWord(), wc.getCount());
			return this;
		}
		
		public String toString() {
			return json.toString();
		}
	}

}
