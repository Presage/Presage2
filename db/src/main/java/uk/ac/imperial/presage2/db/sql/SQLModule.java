package uk.ac.imperial.presage2.db.sql;

import java.sql.Connection;
import java.util.Properties;

import uk.ac.imperial.presage2.db.DatabaseService;
import uk.ac.imperial.presage2.db.StorageService;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class SQLModule extends AbstractModule {

	final Properties jdbcProps;
	
	public SQLModule(Properties props) {
		this.jdbcProps = props;
	}
	
	@Override
	protected void configure() {
		bind(Connection.class).toProvider(SQLService.class);
		bind(DatabaseService.class).to(SQLService.class).in(Singleton.class);
		bind(StorageService.class).to(SQLStorage.class).in(Singleton.class);
		
		bind(Properties.class).annotatedWith(JDBCProperties.class).toInstance(jdbcProps);
		bind(String.class).annotatedWith(JDBCUrl.class).toInstance(jdbcProps.getProperty("url"));
	}

}
