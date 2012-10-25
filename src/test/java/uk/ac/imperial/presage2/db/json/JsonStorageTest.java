package uk.ac.imperial.presage2.db.json;

import uk.ac.imperial.presage2.core.db.GenericStorageServiceTest;

public class JsonStorageTest extends GenericStorageServiceTest {

	@Override
	public void getDatabase() {
		JsonStorage json = new JsonStorage();
		this.db = json;
		this.sto = json;
	}

}
