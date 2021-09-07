# Customization VAM RBS Confirmation Payee Mock Bank Portal

This is the PCM Customization VAM RBS CoP Mock Bank Portal module.

# Getting started

This application is maintained in bitbucket repository.
https://bitbucket.shared.int.tds.tieto.com/projects/FINANCIALSERVICES/repos/testbankapps/browse

## Prerequisites

To build this project, the development machine needs the following software installed:

* JDK 1.8
* Maven 3.5+


## Building the project

To do a full build of all artifacts, the following command can be executed in the root folder of the project:

```bash
mvn clean install
```

## Request/Response handling
There are two API implemented in CopBankPortal application:
$[URL_PROTOCOL]://$[HOSTNAME]$[COLON]$[COP_MOCK_PORT_NUMBER]/cop-mock/api/payee
The following responses can be received depends on "accountNumber" value received in request:
- To receive error due to timeout in response, in request "accountNumber" value must include "55", example "accountNumber": "12345655"
- To receive "responseResult":"Partial" in response, in request "accountNumber" value must include "77", example "accountNumber": "12345677"
- To receive error in response, in request "accountNumber" value must include "88", example "accountNumber": "12345688"
- To receive "responseResult":"No" in response, in request "accountNumber" value must include "99", example "accountNumber": "12345699"
- To receive "uniqueReference":"11111111-2222-3333-4444-55555555555" in response, in request "accountNumber" value must include "66", example "accountNumber": "12345666"

$[URL_PROTOCOL]://$[HOSTNAME]$[COLON]$[COP_MOCK_PORT_NUMBER]/cop-mock/api/payee/{copId}/accept
- To receive "requestStatus":"Failure" in response, in request "uniqueReference" value must be "11111111-2222-3333-4444-55555555555"





