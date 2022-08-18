Put cache in your local user name it solak and unzip all the contents in there

download java 1.8 from here https://www.oracle.com/uk/java/technologies/javase/javase8-archive-downloads.html

put that in program files in a new folder called java

in windows, search for "environment variables" and open environment variables

set up JAVA_HOME and Path as shown here https://gyazo.com/2f39132ff7f7e8a303a490e8a040a06c

open cmd and type java -version to double check

download intellij, either edition is fine. 

to open the project, open the lunite folder and select the pom file and click open as project

once project is open, maven will autmatically build the project

double check the project is using the right java build, open file -> project structure -> project and check SDK is 1.8 also set language level to 18

in this order navigate to azura-server/src/main/java/com/ruse/GameServer and run the server by clicking the green arrow on line 17

same principles when applying the java version to the client, boot the client by going solak-client/src/main/java/Client and right click the class rune client.main




