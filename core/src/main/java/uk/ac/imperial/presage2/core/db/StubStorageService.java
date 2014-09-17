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
package uk.ac.imperial.presage2.core.db;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.inject.Singleton;

/**
 * This a stub implementation of the StorageService interface. It is intended to
 * be the fallback implementation when a database is not defined such that code
 * that worked with a database does not throw NullPointerExceptions when run
 * without.
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
class StubStorageService extends TupleStorageService {

	@Override
	public List<Long> getSimulations() {
		return Collections.emptyList();
	}

	@Override
	protected long getNextId() {
		return 0;
	}

	@Override
	protected void storeParameter(long id, String key, String value) {

	}

	@Override
	protected void storeTuple(long id, String key, String value) {

	}

	@Override
	protected void storeTuple(long id, String key, int value) {

	}

	@Override
	protected void storeTuple(long id, String key, double value) {

	}

	@Override
	protected void storeTuple(long id, String key, int t, String value) {

	}

	@Override
	protected void storeTuple(long id, String key, int t, int value) {

	}

	@Override
	protected void storeTuple(long id, String key, int t, double value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, String value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, double value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			String value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t, int value) {

	}

	@Override
	protected void storeTuple(long id, String key, UUID agent, int t,
			double value) {

	}

	@Override
	protected Set<String> fetchParameterKeys(long id) {
		return null;
	}

	@Override
	protected String fetchParameter(long id, String key) {
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, Class<T> type) {
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, int t, Class<T> type) {
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, Class<T> type) {
		return null;
	}

	@Override
	protected <T> T fetchTuple(long id, String key, UUID agent, int t,
			Class<T> type) {
		return null;
	}

}
