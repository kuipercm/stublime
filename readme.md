[![Build Status](https://img.shields.io/travis/kuipercm/stublime.svg?style=plastic)](https://travis-ci.org/kuipercm/stublime)
[![Coveralls](https://img.shields.io/coveralls/kuipercm/stublime.svg?style=plastic)](https://coveralls.io/r/kuipercm/stublime)


# Stublime

A Spring Boot app that allows stubbing / mocking of REST and SOAP services. [In development]

## How to

### Basic REST example

1. Start the app like any other [Spring Boot](https://projects.spring.io/spring-boot/) app: ```java -jar stublime-1.0.0-SNAPSHOT.jar```
2. Without any additional parameters, the app will launch at port 8080
3. To set a response in Stublime, execute a POST to ```http://localhost:8080/rest/response```
```$json
{
    "key": {
        "resource": "sales/id/123",
        "httpMethod": "GET"
    },
    "response": {
        "responseContent": "hello world"
    } 
}
```
4. This stub response definition will get matched on ```GET``` calls to ```http://localhost:8080/rest/stub/sales/id/123```.
5. You can also set a response HTTP status code (default: 200) by adding the ```"responseStatusCode": 400``` line to the
response, like so:
```$json
{
    "key": {
        "resource": "sales/id/123",
        "httpMethod": "GET"
    },
    "response": {
        "responseContent": "",
        "responseStatusCode": 400`
    } 
}
```
6. Additionally, it's possible to set the response content type, for example
```$json
{
    "key": {
        "resource": "sales/id/123",
        "httpMethod": "GET"
    },
    "response": {
        "responseContent": "{\"id\": 123}",
        "responseContentType": "application/json"
    } 
}
```

### REST example using pattern matching

A slightly more complex version of using the stub is by applying pattern matching on the incoming requests through the resource
definition. 

1. Execute a POST to ```http://localhost:8080/rest/response```
```$json
{
    "key": {
        "resource": "sales/id/[0-9]+",
        "httpMethod": "GET"
    },
    "response": {
        "responseContent": "hello world"
    } 
}
```
2. This stub response definition will get matched on ```GET``` calls to ```http://localhost:8080/rest/stub/sales/id/...```,
where the ... can be replaced by any number of digits. It will however not respond to ```http://localhost:8080/rest/stub/sales/id/john```
since that call doesn't match the pattern.


### REST example using POST body matching

A specific feature of this stub is that you can set multiple responses for similar requests but can use the request body
to differentiate between requests.

1. Execute a POST to ```http://localhost:8080/rest/response```
```$json
{
    "key": {
        "resource": "sales/id/[0-9]+",
        "httpMethod": "POST",
        "bodyType": "JSON",
        "expectedBodySignature": "123#ABC",
        "signatureElementsJoiner": "#",
        "bodySignatureExpressions": [
            "$.id",
            "$.code"
        ]
    },
    "response": {
        "responseContent": "hello world"
    } 
}
```
2. There are a few new elements in this key:
   ```bodyType``` represents the type of body to expect. For now, the only option is "JSON".
   ```bodySignatureExpressions``` and the ```signatureElementsJoiner```. The signature expressions are a list of, in the
case of a JSON body, [JsonPath](https://github.com/json-path/JsonPath) expressions that, when evaluated against the
incoming request body, and joined together using the joiner, form a "body signature".
   ```expectedBodySignature``` represents the expected value that should match the body signature to return this response.
3. The above response will match incoming requests like a POST to ```http://localhost:8080/rest/stub/sales/id/1``` with
the (application/json) body
```$json
{
    "id": "123",
    "name": "Some other sales item",
    "orderNumber": 5546,
    "code": "ABC" 
}
```
but not with the body
```$json
{
    "id": "123",
    "name": "Some other sales item",
    "orderNumber": 5546
}
```
since the code is missing and the calculated body signature doesn't match the set version.

## Delayed responses

It's possible to configure the application to delay responses to mimic real response times of remote services.

1. To set a delay time, POST the following to ```http://localhost:8080/rest/response```
```$json
{
    "key": {
        "resource": "sales/id/123",
        "httpMethod": "GET"
    },
    "response": {
        "responseContent": "hello world"
    },
    "responseTiming": {
        "minimumDelay": 200,
        "maximumDelay": 200
    }
}
```
2. This will delay the response by 200 milliseconds. The execution time of the stub itself is included in this number,
meaning that if it would take 100 milliseconds to compute the response from the stub, the response is delayed for another
100 milliseconds to make the total 200 milliseconds.

**Note** it's a requirement that the maximumDelay should always be greater or equal to the minimumDelay.

**Note** all delay values are in milliseconds.

### Default delay behavior

The default behavior of the stub is to delay for the minimum delay duration for all calls that match the configured key.

### Gaussian random delay behavior

By starting the app with the commandline property ```--stublime.delay.type=gaussian```, the response delay will
behave with a randomized delay following a Gaussian (normal) distribution between the minimum and maximum delays for all
calls that match the configured key.