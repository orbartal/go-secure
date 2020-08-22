package orbartal.interview.gosecure.logic.model;

import java.util.HashMap;


@SuppressWarnings("serial")
public class CountByKey  extends HashMap<String, Long> {

	public CountByKey increase (String key) {
		Long value = this.get(key);
		value = (value!=null)? value : 0L;
		this.put(key, value + 1L);
		return this;
	}

}
