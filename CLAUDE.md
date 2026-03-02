# agents.md

This file provides rules for AI coding agents e.g. Claude Code when working with code in this repository.

## Project Overview
[Hazelcast](https://hazelcast.com/) is a unified real-time data platform that combines an in-memory data store with a stream processing engine. It is primarily designed to provide low-latency data access and high-speed processing for applications that need to act on data instantly. 
 
## Related Documentation
- Hazelcast reference manual is avaiable at [What is Hazelcast Platform?](https://docs.hazelcast.com/hazelcast/latest/what-is-hazelcast)
  - Documentation is based on release Hazelcast versions and `latest` points to the current current latest release
- Hazelcast published JavaDoc can be found in [here](https://docs.hazelcast.org/docs/latest/javadoc/)
  - this is latest JavaDocs
  - release specific docs URL format `https://docs.hazelcast.org/docs/<release version here>/javadoc/`
- Hazelcast Code Samples can be found in GitHub repository [hazelcast-code-samples](https://github.com/hazelcast/hazelcast-code-samples)
- Platform version compatibility (including supported JDKs) can be found in [Supported versions](https://docs.hazelcast.com/hazelcast/5.6/deploy/versioning-compatibility)
- Other useful pages
  - [Get started with Community Edition](https://docs.hazelcast.com/hazelcast/latest/getting-started/get-started-docker)
  - [Install Hazelcast Community Edition](https://docs.hazelcast.com/hazelcast/latest/getting-started/install-hazelcast)
  - [Best practices](https://docs.hazelcast.com/hazelcast/latest/cluster-performance/best-practices)

## PR Conventions
- User must supply PR description based on template [pull_request_template.md](https://github.com/nishaatr/hazelcast/blob/master/.github/pull_request_template.md)
- Ensure PR author is adhering to [CONTRIBUTING.md](https://github.com/nishaatr/hazelcast/blob/master/CONTRIBUTING.md)
- PR title descriptions must speak to the end-user about the positive impact, not about internal implementation details
- PR and git commits should be ideally in imperative Verb Form
- PRs must reference GitHub issue with "Fixes #NNN" at the top of the PR body and labels are added accordingly (as in PR template)
- Ensure changes do not introduce any `SNAPSHOT` dependencies
- PRs must pass account for testing
  - Write/update tests alongside implementation code to validate changes
  - Do not skip or omit test code
  - Do not delete or weaken tests just to make them pass
  - For complex/large work, user should consider updating Hazelcast Code Samples

## Code Quality
- Match existing code style and conventions
- Sonar scan can be found at https://sonarcloud.io/project/overview?id=hazelcast-os
- You should account for [CheckStyle](https://github.com/hazelcast/hazelcast/tree/master/checkstyle) formatting 

## Security
  - No secrets in code (API keys, tokens, passwords)
  - Adding or upgrading dependencies that introduce new transitive trees or licenses should be highlighted
  - upgrades or new depednecies must not have CVE aganist them
    
## Key Modules
- `hazelcast/` — core library
- `hazelcast-spring/` — Spring integration
- `extensions/` — optional connectors
  
## Build Commands

### Prerequisites

- Maven 3
- Java 17+ (64-bit)
- `JAVA_HOME` and/or `PATH` environment variable set with the correct JDK
- Docker (optional)

### Building
More details in [Building From Source](https://github.com/hazelcast/hazelcast?tab=readme-ov-file#building-from-source)

```bash
# Build JAR without tests (fastest)
./mvnw clean package -DskipTests
```

### Running Tests
More details in [Testing](https://github.com/hazelcast/hazelcast?tab=readme-ov-file#testing)
Some tests require Docker to run. User can set `-Dhazelcast.disable.docker.tests` system property to ignore them.

Build and run quick/integration tests

```bash
./mvnw test
```

## Planning Mode (Automatic)
I have seen some repos provide rules for planning phase:

**Automatically enter planning mode** when ANY of these conditions apply:
- Multi-file changes (3+ files affected)
- Architectural decisions
- Unclear or evolving requirements

**Skip planning mode** only for:
- Single-file bug fixes
- Typo corrections
- Simple config changes
- Tasks with explicit step-by-step instructions from user
- 
## Custom commands/skills
We could provide custom commands/skills to help the user:
- /build-hz
- /build-test-hz
- /commit-hz
- /launch-hz
