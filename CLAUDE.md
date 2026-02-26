# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
[Hazelcast](https://hazelcast.com/) is a unified real-time data platform that combines an in-memory data store with a stream processing engine. It is primarily designed to provide low-latency data access and high-speed processing for applications that need to act on data instantly. 
 
## Documentation
- Hazelcast reference manual is avaiable at https://docs.hazelcast.com/hazelcast/latest/what-is-hazelcast. Documentation is based on release Hazelcast versions and `latest` points to the current current latest release
- Hazelcast published JavaDoc can be found in https://docs.hazelcast.org/docs/5.6.0/javadoc/ (based on release version)
- Hazelcast Code Samples can be found in https://github.com/hazelcast/hazelcast-code-samples
- Platform version compatibility (ibcluding supported JDKs) can be found in https://docs.hazelcast.com/hazelcast/5.6/deploy/versioning-compatibility

## PR Conventions
- User must supply PR description based on template https://github.com/nishaatr/hazelcast/blob/master/.github/pull_request_template.md
- Ensure PR author is adhering to https://github.com/nishaatr/hazelcast/blob/master/CONTRIBUTING.md
- PRs must pass account for testing
  - Write/update tests alongside implementation code to validate changes.
  - Do not skip or omit test code.
  - Do not delete or weaken tests just to make them pass.
- PRs should
- sonar

→ 



## Key Modules
- `hazelcast/` — core library
- `hazelcast-spring/` — Spring integration
- `extensions/` — optional connectors
## Key Modules
- `hazelcast/` — core library
- `hazelcast-spring/` — Spring integration
- `extensions/` — optional connectors
  
## Build Commands

### Prerequisites

- Maven 3
- Java 17+ (64-bit)
- `JAVA_HOME` and/or PATH environment variable set with the correct JDK.

### Building

```bash
# Build JAR without tests (fastest)
./mvnw clean package -DskipTests
```

### Running Tests
Some tests require Docker to run. Set `-Dhazelcast.disable.docker.tests` system property to ignore them.

Build and run quick/integration tests

```bash
./mvnw test
```
