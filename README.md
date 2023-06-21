# PythiaSQL Tutorial

PythiaSQL is a Java library that provides a convenient way to interact with a MySQL database. This tutorial will guide you through the steps to use PythiaSQL to connect to a MySQL database and perform various operations.

## Prerequisites
Before getting started, make sure you have the following:
- Java Development Kit (JDK) installed on your system.
- A MySQL database server running with the necessary credentials (host, port, database name, username, and password).

## Step 1: Setting up the PythiaSQL Library
- Download the PythiaSQL library from the official repository on GitHub.
- Add the PythiaSQL library to your Java project's classpath.

## Step 2: Enabling the Database Connection
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
