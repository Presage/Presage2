package uk.ac.imperial.presage2.db.sql.sqlite;

import java.util.Properties;

import com.google.inject.Singleton;

import uk.ac.imperial.presage2.db.sql.SQLModule;
import uk.ac.imperial.presage2.db.sql.SQLService;
import uk.ac.imperial.presage2.db.sql.SQLStorage;

public class SQLiteModule extends SQLModule {

	public SQLiteModule(Properties props) {
		super(props);
	}

	@Override
	protected void configure() {
		bind(SQLiteStorage.class).in(Singleton.class);
		bind(SQLStorage.class).to(SQLiteStorage.class);
		bind(SQLService.class).to(SQLiteStorage.class);
	}

}
