:tomcat
curl --data "token=fake" -H "Content-Type:application/x-www-form-urlencoded" -X POST http://localhost:8080/gsso-introspection-mock-1.0-SNAPSHOT/introspect/oauth2

