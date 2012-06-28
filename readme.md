## presage2-sqldb

This is a MySQL implementation of the Presage2 storage api. It is designed for high performance insertion of data during the execution of a simulation. *Note*: This is not a complete implementation, and behaviour when retrieving data using the storage api may not conform to the api spec beyond expected using during the running of a simulation. 

### Features and limitations

 * Define custom data tables, and choose which data to store.
 * Asynchronous query execution and batch transactions to prevent simulation exeuction blocking for IO and allow optimised data insertion by the dbms.
 * Manual garbage collection of stored data - Data stored via [StorageService] is cached into a Map which must be manually cleared when the data is pushed to the database, otherwise it will sit in memory until the end of the simulation.

## Usage

### pom.xml

Include the presage2-sqldb artifact in your `pom.xml` dependencies as follows:

```xml
	<dependency>
		<groupId>uk.ac.imperial.presage2</groupId>
		<artifactId>presage2-sqldb</artifactId>
		<version>${presage.version}</version>
	</dependency>
```

### db.properties

The following parameters can be used in the db.properties file to specify the db driver to load and database connection details.

 * `module` - This is the module used to load the presage2 db driver. It should be set to `uk.ac.imperial.presage2.db.sql.SqlModule`
 * `driver` (optional) - The JDBC driver to use for the database connection. Default is `com.mysql.jdbc.Driver`
 * `url` - JDBC connection url, including database name. This will depend in the JDBC driver being used, for mysql it will be in the format `jdbc:mysql://host[:port]/dbname`
 * `user` - Database username.
 * `password` - Database user's password.
 * `implementation` (optional) - A class extending `uk.ac.imperial.presage2.db.sql.SqlStorage` which should be used as the StorageService implementation. This allows you to add your custom sql into this class.

### Extending SqlStorage

In order to provide your custom sql you will need to override `uk.ac.imperial.presage2.db.sql.SqlStorage`. This class provides various points to plugin custom queries. The jdbc connection is accessable from the `conn` member variable.

#### Constructor

Your constructor should call the `SqlStorage` super constructor with the jdbc connection properties. These are injected with the `sql.info` named annotation:

```java
@Inject
public CustomStorage(@Named(value = "sql.info") Properties jdbcInfo) {
	super(jdbcInfo);
}
```

#### Initialising tables

Your class should be able to create the tables it will use in the database automatically. This is largely a convenience factor so you don't have to manually create tables before running simulations. Override the `initTables()` method to add queries you need here. In general you should just need `CREATE TABLE IF NOT EXISTS` queries. *NB*: You must call the overrided `super.initTables();` method in addition to your queries.

#### Inserting data

The following method are available to override in order to insert cached data into the database:

 * `updateEnvironment()` - `environmentQ` contains the environment cache if a global environment property has been updated.
 * `updateTransientEnvironment()` - `environmentTransientQ` contains the environment cache if a transient environment property has been updated. You should clear data out of the environment's `transientProperties` map if the data is no longer needed.
 * `updateAgents()` - `agentQ` contains agents whose global properies have changed.
 * `updateTransientAgents()` - `agentTransientQ` contains agents whose transient properies have changed. The agent's `transientProperties` map should be manually cleared of data which is no longer needed.
 
  [StorageService]: http://dev.presage2.info/jenkins/job/presage2-develop/javadoc/reference/uk/ac/imperial/presage2/core/db/StorageService.html
