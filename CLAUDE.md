# agents.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
[Hazelcast](https://hazelcast.com/) is a unified real-time data platform that combines an in-memory data store with a stream processing engine. It is primarily designed to provide low-latency data access and high-speed processing for applications that need to act on data instantly. 
 
## Related Documentation
- Hazelcast reference manual is avaiable at https://docs.hazelcast.com/hazelcast/latest/what-is-hazelcast. Documentation is based on release Hazelcast versions and `latest` points to the current current latest release
- Hazelcast published JavaDoc can be found in https://docs.hazelcast.org/docs/5.6.0/javadoc/ (based on release version)
- Hazelcast Code Samples can be found in https://github.com/hazelcast/hazelcast-code-samples
- Platform version compatibility (ibcluding supported JDKs) can be found in https://docs.hazelcast.com/hazelcast/5.6/deploy/versioning-compatibility
- Getting started guide https://docs.hazelcast.com/hazelcast/latest/

## PR Conventions
- User must supply PR description based on template https://github.com/nishaatr/hazelcast/blob/master/.github/pull_request_template.md
- Ensure PR author is adhering to https://github.com/nishaatr/hazelcast/blob/master/CONTRIBUTING.md
- PR title descriptions must speak to the end-user about the positive impact, not about internal implementation details
- PR and git commits should be ideally in imperative Verb Form
- PRs must reference GitHub issue with "Fixes #NNN" at the top of the PR body and labels are added accordingly (as in PR template)
- PRs must pass account for testing
  - Write/update tests alongside implementation code to validate changes.
  - Do not skip or omit test code.
  - Do not delete or weaken tests just to make them pass.
  - For complex/large work, user should consider updating Hazelcast Code Samples

## Code Quality
- Match existing code style and conventions
- Sonar scan can be found at https://sonarcloud.io/project/overview?id=hazelcast-os

## Security
  - No secrets in code (API keys, tokens, passwords)
  - Adding or upgrading dependencies that introduce new transitive trees or licenses should be highlighted
  - upgrades or new depednecies must not have CVE aganist them
    
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
- `JAVA_HOME` and/or `PATH` environment variable set with the correct JDK
- Docker vXX (optional)

### Building

```bash
# Build JAR without tests (fastest)
./mvnw clean package -DskipTests
```

### Running Tests
Some tests require Docker to run. User can set `-Dhazelcast.disable.docker.tests` system property to ignore them.

Build and run quick/integration tests

```bash
./mvnw test
```

## Planning Mode (Automatic)

**Automatically enter planning mode** when ANY of these conditions apply:
- Multi-file changes (3+ files affected)
- Architectural decisions
- Unclear or evolving requirements
- Risk mitigation on core systems
- New feature implementation
- Refactoring existing functionality

**Do NOT ask** whether to enter planning mode - just enter it when conditions are met.

Planning mode flow: read-only exploration → create plan → get approval → execute.

**Skip planning mode** only for:
- Single-file bug fixes
- Typo corrections
- Simple config changes
- Tasks with explicit step-by-step instructions from user
- 
## Custom Skills
We don't have this  but I see its becoming common practice. I see either skills or commands or both being provided
Might be useful
