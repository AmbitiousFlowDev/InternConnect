# Upgrade Plan: intern-connect (20260428140332)

- **Generated**: 2026-04-28T14:03:32
- **HEAD Branch**: feature/sprint-1/authentification/validation
- **HEAD Commit ID**: HEAD

## Available Tools

**JDKs**
- JDK 25.0.2: C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot (target Java runtime, available)

**Build Tools**
- Maven Wrapper: 3.9.14 (compatible with Java 25, available)

## Guidelines

- Minimize disruption to the development workflow on feature/sprint-1/authentification/validation branch
- Ensure all tests pass with Java 25 runtime

## Options

- Working branch: appmod/java-upgrade-20260428140332
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade Java runtime to Java 25 LTS (latest LTS version)

### Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
| --------------------- | ------- | -------------- | ---------------- |
| Java | 25 (configured) | 25 | - |
| Spring Boot | 4.0.5 | 4.0.5 | - |
| Spring Framework | 7.x (via Boot) | 7.x | - |
| Spring Data JPA | 4.0.5 | 4.0.5 | - |
| Spring Security | 6.x (via Boot) | 6.x | - |
| Spring OAuth2 | 6.x (via Boot) | 6.x | - |
| Thymeleaf | 3.x (via Boot) | 3.x | - |
| Thymeleaf Security | 6.x | 6.x | - |
| Lombok | Latest | Latest | - |
| Oracle JDBC (ojdbc11) | 11.x | 11.x | - |
| Jakarta Servlet | 6.x (via Boot) | 6.x | - |
| Maven Wrapper | 3.9.14 | 3.9.14 | - |

## Derived Upgrades

**No additional upgrades required**. The project is already configured with:
- Java 25 runtime target
- Spring Boot 4.0.5 (latest stable, fully compatible with Java 25)
- All dependencies are compatible with Java 25

The upgrade path focuses on verifying that the installed Java 25 runtime works correctly with the application and resolving any potential runtime issues.

## Upgrade Steps

- **Step 1: Setup Environment**
  - **Rationale**: Verify Java 25 and Maven Wrapper are properly configured and accessible.
  - **Changes to Make**:
    - [ ] Verify Java 25.0.2 installation and JAVA_HOME configuration
    - [ ] Verify Maven Wrapper (mvnw) is executable
    - [ ] Confirm Maven Wrapper uses compatible version (3.9.14+)
  - **Verification**:
    - Command: `mvnw --version` and `java -version`
    - JDK: Java 25.0.2 (C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot)
    - Expected: Maven 3.9.14 and Java 25.0.2 both available and compatible

---

- **Step 2: Setup Baseline**
  - **Rationale**: Establish pre-upgrade compilation and test results as baseline for success criteria.
  - **Changes to Make**:
    - [ ] Stash any uncommitted changes on feature/sprint-1/authentification/validation
    - [ ] Run baseline compilation with current configuration
    - [ ] Run baseline test suite with current configuration
  - **Verification**:
    - Command: `mvnw clean compile test-compile` then `mvnw clean test`
    - JDK: Java 25 (current)
    - Expected: Document compilation SUCCESS/FAILURE and test pass rate (baseline for comparison)

---

- **Step 3: Verify Java 25 Runtime Compatibility**
  - **Rationale**: Ensure the application runs correctly with Java 25 runtime, check for module system warnings or reflection issues.
  - **Changes to Make**:
    - [ ] Clean rebuild with Java 25
    - [ ] Run full test suite with Java 25
    - [ ] Document any module system warnings or reflection access issues
  - **Verification**:
    - Command: `mvnw clean compile`
    - JDK: Java 25.0.2
    - Expected: Compilation SUCCESS with no errors

---

- **Step 4: Final Validation**
  - **Rationale**: Verify all upgrade goals met, application compiles successfully, and all tests pass with Java 25 runtime.
  - **Changes to Make**:
    - [ ] Verify pom.xml has java.version=25
    - [ ] Resolve any compilation errors with Java 25
    - [ ] Run full test suite and fix all test failures (iterative loop)
    - [ ] Verify no illegal reflective access warnings
  - **Verification**:
    - Command: `mvnw clean verify`
    - JDK: Java 25.0.2
    - Expected: Compilation SUCCESS + 100% tests pass

## Key Challenges

- **Oracle JDBC Driver Compatibility**
  - **Challenge**: ojdbc11 is designed for Oracle 21c; verify it works correctly with Java 25's module system.
  - **Strategy**: Monitor for any reflection warnings or runtime errors during test execution. If issues arise, check Oracle documentation for Java 25 compatibility or consider updating to the latest ojdbc version.

- **Java 25 Module System Strictness**
  - **Challenge**: Java 25 has stricter module system enforcement; illegal reflective access may be flagged.
  - **Strategy**: Monitor build and test output for warnings. If warnings appear, identify the source library and either update it or add JVM flags (--add-opens/--add-exports) as temporary workarounds.

- **Spring Boot 4.0.5 with Java 25**
  - **Challenge**: Ensure Spring Boot 4.0.5 and all included dependencies work correctly with Java 25 runtime.
  - **Strategy**: The project is already configured for Java 25; this step verifies the runtime behavior matches the build-time configuration.
