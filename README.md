# appscope
An Android library implementing Service Locator pattern

Examples:
```
Prefs.init(context, new Gson()); // Initialize one-tine in your Application.onCreate() method
private static final PrefsKey<String> COUNTRY_CODE = new PrefsKey("countryCode", String.class);
Prefs.put(COUNTRY_CODE, "US"); // bind the key COUNTRY_CODE to the value "US"

.... // Anywhere in app
String country = Prefs.get(COUNTRY_CODE)  ==> Returns "US"
```

In the example above, since the last constructor parameter (persist) of PrefsKey COUNTRY_CODE is true, the COUNTRY_CODE is persisted (in prefs) and usable across sessions.

The type of the key can be any arbitrary type that the Gson instance can serialize/deserialize.

# Use with Gradle
add to your repositories

```
repositories {
    maven { url "https://jitpack.io" }
}
```

In your app build.gradle, add:  `compile "com.github.PeelTechnologies:android-typesafe-prefs:1.0.0"`

# User Guide
PrefsKey can take arbitrarily complex Java object that Gson can serialize/deserialize. For example, `PrefsKey<Customer>` may represent a class with nested fields for `Address`, name, phone numbers, etc.

