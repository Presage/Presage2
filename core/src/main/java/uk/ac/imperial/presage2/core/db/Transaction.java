package uk.ac.imperial.presage2.core.db;

public interface Transaction {

	void failure();

	void finish();

	void success();

}
