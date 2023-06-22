# PythiaSQL Tutorial

PythiaSQL is a Java library that provides a convenient way to interact with a MySQL database. This tutorial will guide you through the steps to use PythiaSQL to connect to a MySQL database and perform various operations.

## Prerequisites
Before getting started, make sure you have the following:
- Java Development Kit (JDK) installed on your system.
- A MySQL database server running with the necessary credentials (host, port, database name, username, and password).
- A Maven or Gradle Projekt with configuration.
- The PythiaSQL Library imported from https://jitpack.io/#FlxwDNS/PythiaSQL/4419f6bc18

## Enabling the Database Connection
To enable the database connection, you need to call the `enable` method from the `PythiaSQL` class. This method takes the following parameters:
- `host` (String): The host address or name of the database server.
- `port` (int): The port number used for the database connection.
- `database` (String): The name of the database to connect to.
- `user` (String): The username for authentication.
- `password` (String): The password for authentication.

Example:
```java
import de.flxwdns.pythiasql.PythiaSQL;

public class Main {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 3306;
        String database = "mydatabase";
        String user = "myuser";
        String password = "mypassword";
        
        PythiaSQL.enable(host, port, database, user, password);
    }
}
```
### Inserting Data into a Table
To create a new entry in a table, you can use the `createEntry` method of the `DatabaseTable` object. Provide the column names and corresponding values as parameters. Here's an example:

```java
String tableName = "mytable";
DatabaseTable table = PythiaSQL.getTable(tableName);

// Inserting a new row
table.createEntry(Map.of(
        "columnOne", "valueOne",
        "columnTwo", "valueTwo"
));
```

### Remove Data from a Table
To create a new entry in a table, you can use the `removeEntry` method of the `DatabaseTable` object. Provide the column names and corresponding values as parameters. Here's an example:

```java
String tableName = "mytable";
DatabaseTable table = PythiaSQL.getTable(tableName);

// Removing a existing row
table.removeEntry(Map.of(
        "columnOne", "valueOne",
        "columnTwo", "valueTwo"
));
```

### Retrieving a DatabaseTable Object
To retrieve a DatabaseTable object representing a specific table, use the getTable method in the PythiaSQL class. Provide the table name as the parameter. This method assumes that the connection has been established using the enable method before calling it.

```java
String tableName = "mytable";
DatabaseTable table = PythiaSQL.getTable(tableName);
```
