OpenTok Android SDK
===================

The OpenTok Android SDK 2.3.1 lets you use OpenTok-powered video sessions in apps you build
for Android devices.

Apps written with the OpenTok Android SDK 2.3.1 can interoperates with OpenTok apps written with the following OpenTok SDKs:

* <a href="http://tokbox.com/opentok/libraries/client/js/">OpenTok.js 2.2</a>
* <a href="http://tokbox.com/opentok/libraries/client/ios/">OpenTok iOS SDK 2.2+</a>

Installation
------------

The library binaries are included in the OpenTok/libs subdirectory of the SDK.

Developer and client requirements
---------------------------------

The OpenTok Android SDK supports one published audio-video stream, one subscribed audio-video
gstream, and up to five additional subscribed audio-only streams simultaneously. (This is the baseline support on a Samsung Galaxy S3.) To connect more than two clients in a session using the
OpenTok Android SDK, create a session that uses the OpenTok Media Router (a session with the
media mode set to routed). See [The OpenTok Media Router and media modes](http://tokbox.com/opentok/tutorials/create-session/#media-mode).

The SDK is supported on high-speed Wi-Fi and 4G LTE networks.

For a list of supported devices, see the [Developer and client
requirements](http://tokbox.com/opentok/libraries/client/android/#developerandclientrequirements)
section of the OpenTok Android SDK page at the TokBox website.

Using the sample apps
---------------------

The samples directory of the SDK contains the OpenTokSamples app. This app shows the most basic
functionality of the OpenTok Android SDK: connecting to sessions, publishing streams, and subscribing to streams. It also shows how to add UI controls to publisher and subscriber views,
how to use custom video capturer and renderers, how to send and receive messages in the session,
and how to respond to archiving events.

The samples are also available at the [opentok-android-sdk-samples repo at
github](https://github.com/opentok/opentok-android-sdk-samples).

For more information, see the README file in the samples directory.

Creating your own app using the OpenTok Android SDK
---------------------------------------------------

To target ARM, add the following libraries to your project's build path:

* opentok-android-sdk-2.3.jar
* armeabi/libopentok.so

To target x86, add the following libraries to your project's build path:

* opentok-android-sdk-2.3.jar
* x86/libopentok.so

To target ARM and x86, add the following libraries to your project's build path:

* opentok-android-sdk-2.3.jar
* armeabi/libopentok.so
* x86/libopentok.so

These are included in the OpenTok/libs subdirectory of the SDK. From the desktop, drag the
opentok-android-sdk-2.3.jar file into the libs directory of your project in the ADT package
explorer. Then drag either the armeabi directory, the x86/libopentok.so, or both into the libs 
directory of your project in the ADT package explorer.

Also, you need to add the following permissions and features to your app manifest:

* android.permission.CAMERA
* android.permission.INTERNET
* android.permission.RECORD_AUDIO
* android.permission.WAKE_LOCK
* android.permission.MODIFY\_AUDIO_SETTINGS
* android.hardware.camera
* android.hardware.camera.autofocus

Your app needs to use a session ID and token generated with your OpenTok API key, which you can get
at [the OpenTok developer dashboard](https://dashboard.tokbox.com).

For test purposes, you can generate a session ID and token on
[the projects page](https://dashboard.tokbox.com/projects) of the OpenTok developer dashboard.
For a production app, generate unique tokens (and session IDs, if you need to support multiple
sessions) using the [OpenTok server-side libraries](http://tokbox.com/opentok/libraries/server/).

Documentation
-------------

Reference documentation is available in the docs subdirectory of the SDK and at <http://www.tokbox.com/opentok/libraries/client/android/reference/index.html>.

More information
-----------------

For a list of new features and known issues, see the [release notes](release-notes.md).
