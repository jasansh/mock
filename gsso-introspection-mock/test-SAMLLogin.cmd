:tomcat
curl -v --data "userId=fake" -H "Content-Type:application/x-www-form-urlencoded" -X POST http://localhost:8080/gsso-introspection-mock-1.0-SNAPSHOT/SAML/Login