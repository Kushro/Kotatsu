# Kotatsu Android Manga Reader

Kotatsu is a free and open-source Android manga reader app built with Kotlin. Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Working Effectively

### Prerequisites
Kotatsu is an Android application that requires:
- Java JDK 17+ (OpenJDK 17.0.16+ confirmed working)
- Android SDK with platforms android-34, android-35, android-36 
- Android SDK Build Tools 35.0.0
- Gradle 9.0.0 (managed by wrapper)

### Bootstrap and Build Commands
**CRITICAL BUILD LIMITATION**: The current build configuration uses Android Gradle Plugin 8.13.0 which may not be available in restricted network environments. This is a known limitation.

**If you have full network access**, execute these commands:
```bash
cd /home/runner/work/Kotatsu/Kotatsu
./gradlew clean
./gradlew build --stacktrace
```

**If AGP resolution fails** (common in sandboxed environments):
- Error message: "Plugin [id: 'com.android.application', version: '8.13.0'] was not found"
- Root cause: Network restrictions preventing Google repository access
- **DO NOT** repeatedly try different AGP versions - document this limitation
- Focus on static code analysis, documentation review, and architecture understanding

**Build Timing Expectations** (when network allows):
- `./gradlew clean` - 30 seconds. NEVER CANCEL.
- `./gradlew build` - 10-15 minutes first time, 5-7 minutes subsequent builds. NEVER CANCEL. Set timeout to 20+ minutes.
- `./gradlew test` - 3-5 minutes. NEVER CANCEL. Set timeout to 10+ minutes.

**Verified Working Commands** (no network dependencies):
- `./gradlew --version` - Shows Gradle 9.0.0, Kotlin 2.2.0
- Static file analysis and code review
- Manual validation of configuration files

### Testing Commands
**NOTE**: Testing requires successful build completion. If build fails due to AGP resolution:

```bash
# Run unit tests (JUnit-based, 3 test files) - requires build success
./gradlew test --continue

# Run Android instrumentation tests (6 test files) - requires build success + emulator  
./gradlew connectedAndroidTest
```

**Test Coverage**:
- Unit tests: `VersionIdTest`, `MultiMutexTest`, `ChapterPagesTest`
- Android tests: Database, backup, shortcut manager functionality
- Test runner: `HiltTestRunner` for dependency injection

**Manual Test File Validation** (when build unavailable):
- Review test files in `app/src/test/kotlin/` for logic validation
- Check test file syntax and imports
- Verify test coverage matches feature requirements

### Linting and Code Quality
**NOTE**: Lint commands require successful build. Alternative validation methods:

```bash
# Run Android lint checks (requires build success)
./gradlew lint

# Check Kotlin code style - ktlint configured in .idea/ktlint.xml (requires build success)
./gradlew ktlintCheck

# Format code (requires build success)
./gradlew ktlintFormat
```

**Manual Code Quality Validation** (when build unavailable):
- Review code style against Kotlin conventions
- Check indentation and formatting manually
- Verify imports are organized and unused imports removed
- Validate naming conventions match project standards

**Always attempt lint and format before committing** - the project enforces strict code quality standards when build environment allows.

## Validation

### Build Verification
After making changes, always validate:
1. `./gradlew clean build` - Full build succeeds 
2. `./gradlew test` - All unit tests pass
3. `./gradlew lint` - No lint violations
4. `./gradlew ktlintCheck` - Code formatting correct

### Manual Testing Scenarios
**CRITICAL**: If build succeeds, you cannot run the Android app in this environment as it requires device/emulator. Focus on:
- Code compilation without errors
- Unit test execution and results
- Lint checks passing
- Static analysis validation

### Code Change Impact Analysis
When modifying files, understand these key areas:
- **Core**: `app/src/main/kotlin/org/koitharu/kotatsu/core/` - Application foundation, DI, database
- **UI Modules**: Features like `reader/`, `main/`, `settings/`, `search/`
- **Data Layer**: `local/`, `network/`, `database/` components
- **Tests**: `app/src/test/` and `app/src/androidTest/`

## Project Architecture

### Key Technologies
- **Language**: Kotlin with coroutines
- **DI**: Hilt (Dagger)
- **Database**: Room with SQLite
- **UI**: Android Views + Jetpack components
- **Network**: OkHttp + custom parsers
- **Image Loading**: Coil3
- **Build**: Gradle with Android Gradle Plugin 8.13.0

### Main Source Directories
```
app/src/main/kotlin/org/koitharu/kotatsu/
├── core/           # App foundation, DI, utils
├── main/           # Main activity and navigation  
├── reader/         # Manga reading functionality
├── search/         # Search and discovery
├── settings/       # App configuration
├── local/          # Local storage management
├── download/       # Download management
├── favourites/     # User favorites
├── history/        # Reading history
└── [other modules] # Feature-specific modules
```

### Configuration Files
- `app/build.gradle` - Android app configuration, dependencies
- `gradle/libs.versions.toml` - Version catalog for dependencies
- `app/proguard-rules.pro` - R8/ProGuard rules for release builds
- `.idea/ktlint.xml` - Kotlin code style configuration

### Database Schema
Room database with entities in `core/db/entity/`. Migration files in `app/schemas/`.

## Common Tasks Reference

### Build Configuration Quick Reference
```groovy
// Target Android API levels
compileSdk = 36
targetSdk = 36  
minSdk = 23

// Key dependencies (from libs.versions.toml)
kotlin = "2.2.10"
hilt = "2.57.1" 
room = "2.7.2"
okhttp = "5.1.0"
coil = "3.3.0"
```

### Troubleshooting Build Issues
1. **AGP Resolution Failure** (Primary Issue):
   - Error: "Plugin [id: 'com.android.application', version: '8.13.0'] was not found"
   - Root Cause: Network restrictions blocking Google repository access
   - Solution: Work around limitation with static analysis
   - **DO NOT**: Try multiple AGP versions - wastes time

2. **Alternative Validation Approaches**:
   - Manual code review and syntax checking
   - File structure and architecture analysis  
   - Configuration file validation
   - Import and dependency analysis

3. **Dependency Conflicts**: Check `gradle/libs.versions.toml` for version alignment
4. **Kotlin Compilation**: Verify JDK 17+ and Kotlin 2.2+ compatibility  
5. **Room Schema**: Database code in `app/schemas/` for reference
6. **ProGuard Issues**: Check `app/proguard-rules.pro` for keep rules

### Important Notes
- **Performance Priority**: Choose performance over code elegance per CONTRIBUTING.md
- **APK Size Matters**: Avoid unnecessary dependencies
- **Material You**: UI follows Material Design 3 principles
- **Multi-platform**: Code supports phones, tablets, Android 6.0+
- **Offline-first**: App works without internet connectivity
- **Parser Integration**: Manga sources handled via separate kotatsu-parsers library

## Working with External Dependencies
- Main parser library: `com.github.KotatsuApp:kotatsu-parsers`
- Custom fork of subsampling image view: `com.github.KotatsuApp:subsampling-scale-image-view`
- All versions managed in `gradle/libs.versions.toml`

**Always validate these steps work before updating documentation.**