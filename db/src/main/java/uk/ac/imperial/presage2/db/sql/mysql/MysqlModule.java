package uk.ac.imperial.presage2.db.sql.mysql;

import java.util.Properties;

import com.google.inject.Singleton;

import uk.ac.imperial.presage2.db.sql.SQLModule;
import uk.ac.imperial.presage2.db.sql.SQLService;
import uk.ac.imperial.presage2.db.sql.SQLStorage;
import uk.ac.imperial.presage2.db.sql.sqlite.SQLiteStorage;

public class MysqlModule extends SQLModule {

	public MysqlModule(Properties props) {
		super(props);
	}
	
	@Override
	protected void configure() {
		super.configure();
		bind(SQLiteStorage.class).in(Singleton.class);
		bind(SQLStorage.class).to(MysqlStorage.class);
		bind(SQLService.class).to(MysqlStorage.class);
	}

}
