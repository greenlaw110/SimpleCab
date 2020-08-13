# SimpleCab

Assumptions/Preconditions:

* The application is running on Linux
* JDK8+ is properly installed
* MySQL 8/MariaDB 10 is properly installed
* maven 3.6+ is property installed
* The `ny_cab_data_cab_trip_data_full.sql` file are downloaded and put into `/tmp` dir

### Initialize Database

**Create database and user**

```
# connect to MySQL service with `root` user
mysql -u root -p
# run the following scripts in mysql shell to create simplecab database and users
CREATE DATABASE simplecab;
CREATE USER 'simplecab-user'@'localhost' IDENTIFIED BY 'simplecab';
GRANT ALL ON simplecab.* TO 'simplecab-user'@'localhost';
exit
```

**Load `ny_cab_data_cab_trip_data_full.sql` data into `simplecab` database** using `simplecab-user` login

```sql
mysql -u simplecab-user -p simplecab < /tmp/ny_cab_data_cab_trip_data_full.sql
```

**Create `cab_trip_view` view**

```sql
# connect to mysql shell with `simplecab-user` login and default to `simplecab` database
mysql -u simplecab-user -p simplecab 
# run the following script in mysql shell to creat the `cab_trip_view`
CREATE VIEW cab_trip_view AS SELECT row_number() over(order by pickup_datetime) row_num, medallion, pickup_datetime FROM cab_trip_data;
```

**note** The reason we need the `cab_trip_view` is the original `cab_trip_data` does not have the primary key. Hence we will use the `row_number()` function to generate the `Id` in the view to adapt `cab_trip_data` to JPA entity `CabTrip` in our service implementation.

### API implementation

I provide two API implementations for the tech challenge:

* The [main solution](https://github.com/greenlaw110/SimpleCab/tree/master/simplecab-api-springboot) to the challenge is created based on SpringBoot. This solution strictly comply with requirement specified. The solution comes up with implementation, testing and API documentation.
* [A secondary solution](https://github.com/greenlaw110/SimpleCab/tree/master/simplecab-api-act) is created based on [ActFramework](https://github.com/actframework/actframework). The solution implements most of the requirement specified but caching is implemented in a different way than the specification. The secondary solution is provided to demonstrate the simplicity, API documentation and automate test support on ActFramework.

#### SpringBoot solution

**Run SpringBoot solution**

1. Go to the `simplecab-api-springboot` dir
2. type `mvn spring-boot:run`

You should be able to see screen like:

![image](https://user-images.githubusercontent.com/216930/90088409-368d8800-dd62-11ea-9e8e-b6dddc31cc2f.png)

**API Document**

Once you have started the solution, navigate to [http://localhost:8080/swagger-ui/index.html#/cab-trip-endpoint](http://localhost:8080/swagger-ui/index.html#/cab-trip-endpoint) to view API document:

![image](https://user-images.githubusercontent.com/216930/90088635-c03d5580-dd62-11ea-82fc-5601fc56bf66.png)

**Test SpringBoot solution**

1. Go to the `simplecab-api-springboot` dir
2. type `mvn test`

You should be able to see screen like:

![image](https://user-images.githubusercontent.com/216930/90088491-69378080-dd62-11ea-9d13-cb6f46c3e64f.png)

**Deploy SpringBoot solution**

1. Go to the `simplecab-api-springboot` dir
2. type `mvn package`

Once it's done, a runnable Jar file will get created in `target/` dir. the file can be copied to the product environment and run via `java -jar simplecab-api-0.0.1-SNAPSHOT.jar`.

#### Act solution

**Run Act solution**

1. Go to the `simplecab-api-act` dir
2. type `mvn compile act:run`

You should be able to see screen like:

![image](https://user-images.githubusercontent.com/216930/90088753-05fa1e00-dd63-11ea-99bd-ac3b9e36527b.png)

**API Document**

Once you have started solution, navigate to [http://localhost:5460/~/api](http://localhost:5460/~/api) to view API document

![image](https://user-images.githubusercontent.com/216930/90088841-4d80aa00-dd63-11ea-8978-4d5282363e3c.png)

**Test Act solution**

There are two different ways to run Act solution test. 

The first approach is to get Act solution running up in dev mode via `mvn compile act:run` and then navigate to [http://localhost:5460/~/test](http://localhost:5460/~/test):

![image](https://user-images.githubusercontent.com/216930/90088974-97699000-dd63-11ea-8d10-facc270fe65c.png)

This approach is preferred when developer is developing/debugging the project.

The second approach is more preferred to be used in a CI environment - run `mvn test`:

![image](https://user-images.githubusercontent.com/216930/90089070-ddbeef00-dd63-11ea-8ada-4d3a956f36f4.png)

**Deploy act solution**

1. Go to the `simplecab-api-act` dir
2. type `mvn package`

Once it's done, a tar.gz file will be generated at `target/dist` dir. The file can be copied to the prod environment and then run `tar xzf xxx.tar.gz` to unpackage. Then run the `run` script to start the app.

Act also provide a docker build command to support deploy app via docker container:

1. Go to the `simplecab-api-act` dir
2. type `mvn -Pdocker package`

Once it's done it will generate a docker image:

![image](https://user-images.githubusercontent.com/216930/90089566-098ea480-dd65-11ea-8181-4fbef9f56d1a.png)


### Client implementation

A Java based client implementation is provided in [simplecab-client](https://github.com/greenlaw110/SimpleCab/tree/master/simplecab-client).

**Build client**

1. Go to the `simplecab-client` dir
2. type `mvn package`

Once it's done a runnable jar file `simplecab-client-1.0-SNAPSHOT-jar-with-dependencies.jar` will be generated in the `target` dir:

![image](https://user-images.githubusercontent.com/216930/90089695-5c685c00-dd65-11ea-8e9a-61e7563667b3.png)

**Run client**

Once client has been built, then it can run the jar file directly. **Note** please make sure the api server is running up (either springboot solution or act solution).

To run the client type

```
java -jar target/simplecab-client-1.0-SNAPSHOT-jar-with-dependencies.jar [--options]
```

Where options can be:

```
--host <host>        - specifies the api server host. Default value: localhost
--port <port>        - specifies the api server port. Default value: 8080
--secure             - specifies whether use secure http channel (i.e. https). Default value: false
--context <context>  - specifies the API path context. Default value: /api/v1/cab_trips
-m <medallion(s)     - specifies one or more cab medallion, use comma to separate multiple medallions.
--pickup-date <date> - specifies pickup date in format `yyyy-MM-dd`, e.g. `2013-12-31`
```

**Note** 

1. you don't need to specify all options as most of them has built-in default value.
2. When talk to act solution, use `--port 5460`.
3. medallion(s) must be specified.
3. When pickup date is presented then it will only use the first medallion in the list if multiple medallions are specified.

There are a few examples in the [test.sh](https://github.com/greenlaw110/SimpleCab/blob/master/simplecab-client/test.sh).
