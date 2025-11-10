# Masking Logger
A flexible and configurable logging solution that provides data masking capabilities for sensitive information in application logs.

## Getting Started
### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Installation
Add the dependency to your Maven project:
``` xml
<dependency>
    <groupId>me.maxhub</groupId>
    <artifactId>masking-logger-spring-boot-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```
### Configuration
#### Basic Configuration
1. Create or modify your file to use the masking encoder: `logback.xml`
```xml
<configuration>
    <include resource="masking-logback.xml"/>

    <property name="PROPS_PROVIDER" value="spring"/>
    <property name="LOGGER_TO_INCLUDE" value="me.maxhub.include1, me.maxhub"/>
    <property name="LOGGER_TO_IGNORE" value="me.maxhub.ignore1"/>

    <root level="debug">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="rollingFile"/>
    </root>
</configuration>
```
#### Configuring Masking Patterns
Create a file to define masking patterns: `wlogger.properties`
```properties
# Example masking patterns
wlogger.mask.enabled=true
wlogger.mask.default-masker=json
wlogger.mask.masker-version=v2
wlogger.mask.fields=\
  /string,\
  /testData/string,\
  /testDataList/#/bigDecimal,\
  /testDataList/#/testDataList/#/integer,\
  /stringList/#/
```

Or via `application.yml/properties` (if `${PROPS_PROVIDER} == spring` in `logback.xml`):
```yml
wlogger:
  request:
    enabled: true
  mask:
    enabled: true
    fields:
      - /stringArray/#
      - /string
      - /testData/integer
      - /testDataList/#/bigDecimal
      - /testDataList/#/testDataList/#/integer
      - /stringList/#/
      - /password
    default-masker: json
    masker-version: v2
```
## Usage Examples
### Basic Logging with Masking
```java
public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);
    
    public void processRequest(String data) {
        logger.info("Processing: {}", data);
    }
}
```
### Using WLogger utility class
```java
public class ContextExample {
    
    public void processRequest(String requestId) {
        WLogger.info("opName", null /* your message body */, "Message [{}] [{}]", "TEST", Instant.now());
        WLogger.level(level)
            .message("Hello WLogger [{}]", level.name())
            .messageBody(null /* your message body */)
            .operationName("opName")
            .status("SUCCESS")
            .log();
        WLogger.error().message("yes yes yes [{}]", "no").log();
    }
}
```
See other examples in [masking-logger-example](masking-logger-example) module.
