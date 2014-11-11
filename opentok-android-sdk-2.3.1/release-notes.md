OpenTok Android SDK release notes
=================================

New features and changes
------------------------

Version 2.3.1

* Fix for the bitrate control to improve video quality on 1:1 mobile calls.

Version 2.3.0

* The reason parameter has been added to the
  `SubscriberKit.VideoListener.onVideoDisabled(SubscriberKit subscriber, String reason)`
  and `SubscriberKit.onVideoDisabled(SubscriberKit subscriber, String reason)`
  methods. This parameter describes the reason why the subscriber video is being
  disabled. In the previous version, this method was only called when the video
  was disabled due to changes in the stream quality (in a session that uses the
  OpenTok Media Router). In version 2.3.0, the method is also called if the
  publisher stops sending a video stream or the subscriber stops subscribing to
  it (and the `reason` parameter value will be set accordingly).

* New methods indicate when a subscriber's video stream starts (when there
  previously was no video) or resumes (after video was disabled):
  `SubscriberKit.VideoListener.onVideoEnabled(SubscriberKit subscriber, String reason)`
  and `SubscriberKit.onVideoEnabled(SubscriberKit subscriber, String reason)`.

* Use the new PublisherKit.AudioLevelListener and SubscriberKit.AudioLevelListener
  classes to monitor changes in the audio levels of publishers and subscribers.
  To use these, call the `setAudioLevelListener(listener)` method of a PublisherKit
  or SubscriberKit object.

* The new `SubscriberKit.VideoListener.onVideoDisabledWarning(subscriber)` method
  is called when the OpenTok Media Router determines that the stream quality has
  degraded and the video will be disabled if the quality degrades more. The new
  `SubscriberKit.VideoListener.onVideoDisabledWarningLifted(subscriber)` method
  is called when the stream quality improves. This feature is only available in
  sessions that use the OpenTok Media Router (sessions with the
  [media mode](http://tokbox.com/opentok/tutorials/create-session/#media-mode)
  set to routed), not in sessions with the media mode set to relayed. The
  SubscriberKit class also has protected `onVideoDisabledWarning()` and
  `onVideoDisabledWarningLifted()` methods that you can implement (instead of
  the VideoListener methods) when you subclass SubscriberKit.

* This version includes a new custom audio driver API. This lets you use
  custom audio streams and define the audio device used to capture and render
  audio data. The following new classes support the custom audio driver API:

  * AudioDeviceManager -- Use this class to set the app to specify a custom
  audio device for use in the app.

  * BaseAudioDevice -- Defines an audio device for use in a session.

  * BaseAudioDevice.AudioBus -- The audio bus marshals audio data between the network and
  the audio device.

  * BaseAudioDevice.AudioSettings -- Defines the format of the audio.

* You can optimize the speaker usage for voice-only calls by calling
  `AudioDeviceManager.getAudioDevice().setOutputMode(BaseAudioDevice.VOICE_COMMUNICATION)`.
  This sets the app to use the headset speaker (and the loudspeaker is disabled), which is
  preferable in voice-only apps.

* This build improves the video quality under poor network conditions.

* The Session.PublisherListener and the `Session.onPublisherAdded(publisher)`
  and `Session.onPublisherRemoved(publisher)` methods have been removed. You
  can monitor the `PublisherKit.PublisherListener.onStreamCreated(stream)` and
  `PublisherKit.PublisherListener.onStreamDestroyed(stream)` methods to determine
  when a publisher's stream is created and destroyed.


Version 2.2.1

* Updated to use version 1.0.1h of OpenSSL.
* The `Session.unpublish()` method now takes a PublisherKit parameter, instead of a Publisher
  parameter. (The Publisher class extends PublisherKit.)

Known issues
------------

* The SDK is not supported on the Android Emulator. Build and deploy to a supported device.

* In a session with the [media mode](http://tokbox.com/opentok/tutorials/create-session/#media-mode)
  set to relayed, only one client can subscribe to a stream published by an Android device.
