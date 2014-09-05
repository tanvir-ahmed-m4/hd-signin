hd-signin
=========

In order to run the program locally, your computer must have the following installed:

1. Java 7
2. Maven
3. MySQL

Once both are installed, you can run the program by doing the following:

1. Create a MySQL user for the program to log in as
2. Create a database, and give the created user full permissions on it
3. Run the script `create_database.sql` (located in the root of the repository) to populate the database
4. Set the MySQL username and password in the application.properties configuration file
5. Launch the program by running `mvn jetty:run` on the command line from the root of the project
6. The site can then be accessed by going to [http://localhost:8080/signin](http://localhost:8080/signin)


Notes
=========
1. You can access a live-generated API reference at [http://localhost:8080/signin/apireference.html](http://localhost:8080/signin/apireference.html) while the program is running
