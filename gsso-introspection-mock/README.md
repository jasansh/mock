GSSO Oauth 2.0 Introspection endpoint mock
==========================================
is a very simple mock service for OAuth 2.0 Introspection endpoint. 

Mock service will try to parse incoming token if parsing is successful response will return following

```
{
	"iss": "https://mock.gsso.com",
	"active": true,
	"token_type": "Bearer",
	"exp": 9999999999,
	"client_id": "BankSystem",
	"jti": "wsx2m",
	"sub": "**<this is picked up from incoming token attribute preferred_username>**"
}
```
Note that GSSO picks up the userid from "**preferred_username**"-attribute if parsing is successful.

See test.cmd for curl examples.


In case of any error during token parsing a canned response is returned

```
{
	"iss": "https://mock.gsso.com",
	"active": true,
	"token_type": "Bearer",
	"exp": 9999999999,
	"client_id": "BankSystem",
	"jti": "wsx2m",
	"sub": "banksystem"
}
```

GSSO will always return '**banksystem**' as user if token parsing fails.

Prerequisites
-------------
To build this project, the development machine needs the following software installed:
* JDK 1.8
* Maven 3.5+

Building the project
--------------------
To do a full build of the artifact, the following command can be executed in the root folder of the project:

mvn clean install

Deploying the application on WAS 9.x
------------------------------------
Steps:
1. Deploy the WAR file
3. set JVM level property, key as com.ibm.ws.cdi.enableCDI and value as false.
4. Change the class loader order as  parent last.

OAuth 2.0 Introspection endpoint URL: 
-------------------------------------
* WAS, http://host-name:port/gsso/introspect.oauth2
* Spring-boot,  http://host-name:8080/introspect.oauth2
* Tomcat, http://localhost:8080/gsso-introspection-mock-1.0-SNAPSHOT/introspect.oauth2


