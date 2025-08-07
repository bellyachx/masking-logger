# Masking Logger
A flexible and configurable logging solution that provides data masking capabilities for sensitive information in application logs.
## Features
- **JSON Logging Format**: Structured logging with JSON format for better log analysis
- **Spring Boot Integration**: Seamless integration with Spring Boot applications
- **Custom Filters**: Configurable filtering based on logger names
- **Flexible Configuration**: Configure masking patterns via properties files
- **Index-Based Rolling Policy**: Custom rolling policy for log file management

## Project Structure
The project is organized into several modules:
- : Core API interfaces and contracts **masking-logger-api**
- : Auto-configuration for standard Java applications **masking-logger-autoconfig**
- : Main implementation of the masking functionality **masking-logger-core**
- : Example application demonstrating usage **masking-logger-example**
- : Spring Boot starter for easy integration **masking-logger-spring-boot-starter**

## Getting Started
### Prerequisites
- Java 21 or higher
- Maven 3.6+ (for building)

### Installation
Add the dependency to your Maven project:
``` xml
<dependency>
    <groupId>me.maxhub</groupId>
    <artifactId>masking-logger-spring-boot-starter</artifactId>
    <version>1.0</version>
</dependency>
```
### Configuration
#### Basic Configuration
1. Create or modify your file to use the masking encoder: `logback.xml`
```xml
<configuration>
    <include resource="masking-logback.xml"/>

    <property name="ENABLE_MASKER" value="true"/>
    <property name="DEFAULT_MASKER" value="json"/>
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
Create a file to define masking patterns: `logging.mask.properties`
```properties
# Example masking patterns
wlogger.mask.enabled=true
wlogger.mask.fields=\
  /string,\
  /testData/string,\
  /testDataList/#/bigDecimal,\
  /testDataList/#/testDataList/#/integer,\
  /stringList/#/
```

Or through `application.yml/properties` if `${PROPS_PROVIDER} == spring` in `logback.xml`
```yml
wlogger:
  request:
    enabled: true
  mask:
    enabled: true
    fields:
      - /string
      - /testData/integer
      - /testDataList/#/bigDecimal
      - /testDataList/#/testDataList/#/integer
      - /stringList/#/
      - /password
```
## Usage Examples
### Basic Logging with Masking
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Example {
    private static final Logger logger = LoggerFactory.getLogger(Example.class);
    
    public void processRequest(String data) {
        logger.info("Processing: {}", data);
    }
}
```
### Using WLogger utility class
```java
import me.maxhub.logger.LoggingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
See other examples in `masking-logger-example` module.
## Core Components
### MaskingJsonEncoder
The is the central component responsible for formatting log events as JSON and applying masking rules to sensitive data before writing logs. `MaskingJsonEncoder`
### LoggerNameFilter
Allows filtering log events based on logger names, providing a way to control which loggers have masking applied.
### IndexBasedRollingPolicy
Custom implementation of a rolling policy that manages log files based on index numbering rather than dates, useful for specific log rotation requirements.
