package uk.ac.imperial.presage2.db.sql;

import java.util.Properties;

import org.junit.Ignore;

import uk.ac.imperial.presage2.core.db.GenericStorageServiceTest;

@Ignore
public class PostGreSqlStorageTest extends GenericStorageServiceTest {

	SqlStorage sqlSto;

	@Override
	public void getDatabase() {
		Properties jdbcInfo = new Properties();
		jdbcInfo.put("driver", "org.postgresql.Driver");
		jdbcInfo.put("url", "jdbc:postgresql://localhost/presage_test");
		jdbcInfo.put("user", "presage");
		jdbcInfo.put("password", "");
		this.sqlSto = new SqlStorage(jdbcInfo);
		this.db = sqlSto;
		this.sto = sqlSto;
	}

}
