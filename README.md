[![Java CI with Maven](https://github.com/ipfreely-uk/java/actions/workflows/maven.yml/badge.svg)](https://github.com/ipfreely-uk/java/actions/workflows/maven.yml)
[![JavaDoc](https://github.com/ipfreely-uk/java/actions/workflows/javadoc.yml/badge.svg)](https://github.com/ipfreely-uk/java/actions/workflows/javadoc.yml)

# IPFreely.uk - Java Implementation

IP address manipulation library.

Treats IP addresses as arithmetic types.
Supports IPv4 `32-bit unsigned int` & IPv6 `128-bit unsigned int`.
Includes methods for arithmetic operations & bitwise operations,
and special collections for discrete sets.

## Example

```java
// EXAMPLE
// Define 192.168.0.0/24
int maskBits = 24;
V4 networkAddress = Family.v4().parse("192.168.0.0");
// 255.255.255.0
V4 mask = Family.v4().subnets().masks().get(maskBits);
// 0.0.0.255
V4 maskComplement = mask.not();
// 192.168.0.255
V4 lastAddress = maskComplement.or(networkAddress);
```

## API Documentation

[Javadoc](https://ipfreely-uk.github.io/java/)/[![javadoc](https://javadoc.io/badge2/uk.ipfreely/addresses/javadoc.svg)](https://javadoc.io/doc/uk.ipfreely/addresses) 

## Releases

Libraries are published to [Maven Central](https://central.sonatype.com/artifact/uk.ipfreely/addresses/overview)

### Maven

```xml
<dependency>
    <groupId>uk.ipfreely</groupId>
    <artifactId>addresses</artifactId>
    <version>${version}</version>
</dependency>
```

### Gradle

```
implementation("uk.ipfreely:addresses:${version}")
```

## Versions

Version numbers are three digits - the Java version, major version, minor version.

## Building

Requires [JDK17](https://adoptium.net)+.

```shell
 ./code/mvnw -f code/pom.xml install
```
