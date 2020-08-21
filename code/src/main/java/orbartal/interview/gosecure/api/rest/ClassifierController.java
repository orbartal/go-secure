package orbartal.interview.gosecure.api.rest;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import orbartal.interview.gosecure.application.ClassifierApplication;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/classifier")
public class ClassifierController {
	
	private ClassifierApplication classifierApplication;

	public ClassifierController(ClassifierApplication classifierApplication) {
		this.classifierApplication = classifierApplication;
	}

	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public Publisher<ResponseEntity<String>> upload(@RequestPart("file") Mono<FilePart> upload) {     
		Mono<String> output = classifierApplication.analysis(upload);
		return output.map(p -> ResponseEntity.ok(p));
	}

}
