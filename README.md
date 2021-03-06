# Android Typesafe Prefs
An Android library to access Android SharedPreferences in a TypeSafe manner.

Example:
```
SharedPrefs.init(context, new Gson()); // Initialize one-tine in your Application.onCreate() method
private static final TypedKey<String> COUNTRY_CODE = new TypedKey("countryCode", String.class);
SharedPrefs.put(COUNTRY_CODE, "US"); // bind the key COUNTRY_CODE to the value "US"

.... // Anywhere in app
String country = SharedPrefs.get(COUNTRY_CODE)  ==> Returns "US"
```

The type of the key can be any arbitrary type that the Gson instance can serialize/deserialize.
If the type is a primitive or a String, the values are stored as is. For complex objects, the class is serialized as JSON and stored as String.


# Use with Gradle
add to your repositories

```
repositories {
    maven { url "https://jitpack.io" }
}
```

In your app build.gradle, add:  `compile "com.github.PeelTechnologies:android-typesafe-prefs:1.1.9"`

# User Guide
TypedKey can take arbitrarily complex Java object that Gson can serialize/deserialize. For example, `TypedKey<Customer>` may represent a class with nested fields for `Address`, name, phone numbers, etc.

`SharedPrefs` class uses the default preferences file for the context to store properties. If you want to use a a different file, specify it during initialization:
```
SharedPrefs.init(context, gson, "my-custom-preferences-file-name", 25);
```
Note that 25 is the size of the in-memory cache for faster access of prefs.

`SharedPrefs` is a static singleton class for `Prefs` with convenience methods named `put` and `get`. For non-static access, use the `Prefs` class directly:
```
Prefs prefs = new Prefs(context, gson, prefsFileName, 25);
TypedKey<CountryCode> key = new TypedKey<>("countryCode", CountryCode.class);
prefs.put(key, CountryCode.US);
CountryCode country = prefs.get(key);
```
Since `Prefs` class is non-static, you can create multiple instances to manage different preferences files.
```
Prefs prefs1 = new Prefs(context, gson, "user-preferences", 10);
Prefs prefs2 = new Prefs(context, gson, "app-configuration", 5);
```
