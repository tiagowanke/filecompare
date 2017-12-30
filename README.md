# How to run the application.
    Dependencies:
        - Java 8
        - Maven
        - Git

## Using the command line

First clone the project: git clone https://github.com/tiagowanke/filecompare.git
![Clonning the project](https://github.com/tiagowanke/filecompare/blob/master/src/main/resources/static/image/clone-project.png)

Now lets package the project: mvn package -DskipTests=true
![Package](https://github.com/tiagowanke/filecompare/blob/master/src/main/resources/static/image/package.png)

All we have to do now is to run the jar file: java -jar target\filecompare.jar
**Make sure that port 8080 is free**
![Start app](https://github.com/tiagowanke/filecompare/blob/master/src/main/resources/static/image/start-app.png)

## Using your IDE
If your using an IDE to run the project you can simply run class ApplicationConfiguration.java like java application:
![IDE run](https://github.com/tiagowanke/filecompare/blob/master/src/main/resources/static/image/ide-run.png)

# This project provide the following services:

## /v1/diff/<ID>/left
Post service that accepts a JSON base64 encoded binary data and will store in memory on the left position of the given <ID>
Returns:
  HttpStatus: OK

## /v1/diff/<ID>/right
Post service that accepts a JSON base64 encoded binary data and will store in memory on the left position of the given <ID>
Returns:
  HttpStatus: 200 OK

## /v1/diff/<ID>
Get service that returns a JSON information about the difference of left and right file of a the given <ID>
Returns:
  HttpStatus: 404 Not Found. If given <ID> doesn't exists
  HttpStatus: 202 Ok. With the possible JSON returns:
    If left and right have different sizes:
      { "message": "Files have different size." }
      
    If left and right have are equals:
      { "message": "Files are the same." }
      
    If left and right are from same size but differents:
       A JSON representation of each offset and its length
      { 
        <offset1> : <length>,
        <offset2> : <length>
        ...
      }
      
      
# The code of this project is made of 
##  Controller layer:
    Holds the rest services mentioned above.
    
##   Model layer:
    Holds the pojo classes that represent the necessary model of the project
   
##   Service layer:
    Holds the services that make available Model's information with business rules to the controller layer.
    
# The code has unit test:
![alt text](https://github.com/tiagowanke/filecompare/blob/master/src/main/resources/static/image/coverage.png)

The code also have java doc for further details.
