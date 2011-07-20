/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

import uk.ac.imperial.presage2.db.StorageService;
import uk.ac.imperial.presage2.db.Table.TableBuilder;

public abstract class SQLStorage extends SQLService implements StorageService {

	long simulationID;

	protected SQLStorage(String driver, String connectionurl,
			Properties connectionProps) throws ClassNotFoundException {
		super(driver, connectionurl, connectionProps);
	}

	@Override
	public TableBuilder buildTable(String tableName) {
		return new SQLTable.SQLTableBuilder(tableName, simulationID, this);
	}

}
