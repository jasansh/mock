SAML Portal Application
It is a bank portal application to simulate SAML request

Prerequisites
To build this project, the development machine needs the following software installed:

JDK 1.8
Maven 3.5+

Building the project
To do a full build of the artifact, the following command can be executed in the root folder of the project:

mvn clean install

Deploying the application
WAS 9.x
Steps:
1. Deploy the WAR file
2. create a folder path "/BankPortal/config" and copy the content of ext-config in this path.
3. set JVM level property, key as com.ibm.ws.cdi.enableCDI and value as false.
4. Change the class loader order as  parent last.

Application URL: 
http://host-name:port/portal-app/

