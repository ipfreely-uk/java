[![Java CI with Maven](https://github.com/ipfreely-uk/java/actions/workflows/maven.yml/badge.svg)](https://github.com/ipfreely-uk/java/actions/workflows/maven.yml)
[![JavaDoc](https://github.com/ipfreely-uk/java/actions/workflows/javadoc.yml/badge.svg)](https://github.com/ipfreely-uk/java/actions/workflows/javadoc.yml)

# IPFreely.uk

IP address manipulation library.

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

## Documentation

[Javadoc](https://ipfreely-uk.github.io/java/)/[![javadoc](https://javadoc.io/badge2/uk.ipfreely/addresses/javadoc.svg)](https://javadoc.io/doc/uk.ipfreely/addresses) 

## Releases

Libraries are published to [Maven Central](https://central.sonatype.com/artifact/uk.ipfreely/addresses/overview)

## Versions

Version numbers are three digits - the Java version, major version, minor version.

## Building

Requires [JDK17](https://whichjdk.com/)+.

```shell
 ./code/mvnw -f code/pom.xml install
```
