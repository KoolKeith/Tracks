# Tracks

##Screenshot

<img src="https://github.com/FabioSchiavo/Tracks/blob/master/Screenshot_2014-12-25-10-41-39.png" width="30%">
<img src="https://github.com/FabioSchiavo/Tracks/blob/master/Screenshot_2014-12-25-10-41-47.png" width="30%">

##API Key

In AndroidManifest.xml remembers to insert your Google Maps API key.

```xml
<meta-data
            android:name="com.google.android.maps.v2.API_KEY"
            android:value="your-google-maps-api-key-here" />
```

##Compatibility

```xml
<uses-sdk
        android:minSdkVersion="14"
        android:targetSdkVersion="17" />
```
This code must be updated to be runnable with Android API level 23 or higher.
It's necessary to add the Requesting Permissions at Run Time.

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="schiavo.tracks.permission.MAPS_RECEIVE" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```

##UML
<img src="https://github.com/FabioSchiavo/Tracks/blob/master/UML.PNG">
