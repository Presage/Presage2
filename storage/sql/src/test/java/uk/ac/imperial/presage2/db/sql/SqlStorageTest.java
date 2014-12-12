/**
 * 	Copyright (C) 2011-2014 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.db.sql;

import java.util.Properties;

import uk.ac.imperial.presage2.core.db.GenericStorageServiceTest;

public class SqlStorageTest extends GenericStorageServiceTest {

	SqlStorage sqlSto;

	@Override
	public void getDatabase() {
		Properties jdbcInfo = new Properties();
		jdbcInfo.put("driver", "com.mysql.jdbc.Driver");
		jdbcInfo.put("url", "jdbc:mysql://127.0.0.1/presage_test");
		jdbcInfo.put("user", "travis");
		jdbcInfo.put("password", "");
		this.sqlSto = new SqlStorage(jdbcInfo);
		this.db = sqlSto;
		this.sto = sqlSto;
	}

}
