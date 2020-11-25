## comScore Kit Integration

This repository contains the [comScore](https://www.comscore.com/) integration for the [mParticle Android SDK](https://github.com/mParticle/mparticle-android-sdk).

### Adding the integration

1. The comScore Kit requires that you add comScore's Maven server to your buildscript:

    ```
    repositories {
        maven { url "https://comscore.bintray.com/Analytics"}
        ...
    }
    ```

2. Add the kit dependency to your app's build.gradle:

    ```groovy
    dependencies {
        implementation 'com.mparticle:android-comscore-kit:5+'
    }
    ```

3. Follow the mParticle Android SDK [quick-start](https://github.com/mParticle/mparticle-android-sdk), then rebuild and launch your app, and verify that you see `"comScore detected"` in the output of `adb logcat`.
4. Reference mParticle's integration docs below to enable the integration.

### Documentation

[comScore integration](http://docs.mparticle.com/?java#comscore)

### License

[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)