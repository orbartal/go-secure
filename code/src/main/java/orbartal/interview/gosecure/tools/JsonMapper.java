package orbartal.interview.gosecure.tools;

import java.util.Map.Entry;

import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import orbartal.interview.gosecure.logic.model.CountByKey;
import reactor.core.publisher.Mono;

@Component
public class JsonMapper {

	public Mono<String> readJson(Mono<CountByKey> input) {
		return input.map(m->toJson(m)).map(x->x.toString());
	}

	private JsonObject toJson(CountByKey m) {
		JsonObject r = new JsonObject();
		for (Entry<String, Long> e : m.entrySet()) {
			r.addProperty(e.getKey(), e.getValue());
		}
		return r;
	}

}
