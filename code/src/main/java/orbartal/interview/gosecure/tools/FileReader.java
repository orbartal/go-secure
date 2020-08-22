package orbartal.interview.gosecure.tools;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

@Component
public class FileReader {

	private static final String EOL = "\n";
	private static final String CR = "\\r";
	private static final String CR_EOL = "\\r\\n";
	private static final String SPACE_OR_EOL = "\\r?\\n";

	public Flux<String> readLines(Mono<FilePart> input) {
		return input.flatMapMany(fp -> readLinesFromFile(fp));
	}

	private Flux<String> readLinesFromFile(FilePart fp) {
		Flux<String> fluxString = fp.content().map(db -> buildString(db)).map(s->eol(s));
		Flux<Tuple3<String, String, String>> fluxTuple3 = readTuple3(fluxString);
		return fluxTuple3.map(s -> splitLines(s)).flatMapIterable(s -> s);
	}

	private String eol(String s) {
		return s.replaceAll(CR_EOL, EOL).replaceAll(CR, EOL);
	}

	private String buildString(DataBuffer dataBuffer) {
		byte[] bytes = new byte[dataBuffer.readableByteCount()];
		dataBuffer.read(bytes);
		DataBufferUtils.release(dataBuffer);
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	private Flux<Tuple3<String, String, String>> readTuple3(Flux<String> input) {
		Flux<String> start = Flux.just("");
		Flux<String> end = Flux.just("");
		Flux<String> first = Flux.concat(start, input, end);
		Flux<String> second = first.skip(1L);
		Flux<String> third = first.skip(2L);
		return Flux.zip(first, second, third);
	}

	private List<String> splitLines(Tuple3<String, String, String> t) {
		String s1 = t.getT1();
		String s2 = t.getT2();
		String s3 = t.getT3();

		List<String> lines = new ArrayList<>();
		lines.addAll(Arrays.asList(s2.split(SPACE_OR_EOL)));

		if (!s1.isEmpty() && !s1.endsWith(EOL)) {
			//The first word is part of previous line last word!
			lines.remove(0);
		}

		if (!s2.isEmpty() && !s2.endsWith(EOL) && !s3.isEmpty()) {
			//The last word is part of next line first word!
			String s2Last = lines.get(lines.size()-1);
			String s3First =  s3.split(SPACE_OR_EOL)[0];
			String fullWord = s2Last + s3First;
			lines.remove(lines.size()-1); 
			lines.add(fullWord);
		}
		return lines;
	}

}
