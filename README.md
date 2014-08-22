hd-signin
=========

In order to run the program locally, your computer must have the following installed:

1. Maven
2. MySQL

Once both are installed, you can run the program by doing the following:

1. Create a MySQL user for the program
2. Create a database using the script `create_database.sql`, in the root of the repository
3. Give the MySQL user you created full access to this database
4. Set the MySQL username and password in the application.properties configuration file
5. Run the program by typing `mvn jetty:run` on the command line from the root of the project
6. The site can then be accessed by going to http://localhost:8080/signin
