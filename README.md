dancingdrone
============

Android app for the Parrot AR Drone.

Connect to your drone, select an audio file from your device's Music folder and watch the drone dance to the music.


### How it works

The project uses the [Visualiser](http://developer.android.com/reference/android/media/audiofx/Visualizer.html) class to get information about the currently playing media file. It then averages amplitude of the base, mid and pitch frequencies over a frame or 1000ms. Based on these it commands the drone to move. Base controlls vertical movement, mid controlls side movements and pitch controlls spin.

### Problems/solutions

1. The Visualiser send information about the currently playing frame. This means that the movement will be delayed by at least a few seconds to the song. To solve this we should preprocess the song a few frames in advance. An attempt was made with the [musicg](https://code.google.com/p/musicg/) library but getting the spectrogram for an entire song consumes way to much memory. A [FFT](http://en.wikipedia.org/wiki/Fast_Fourier_transform) over a chunk of the song should do the trick.
2. The movement based on this mapping is not entirely recognisable. A better approach would be to extract the beat and make the movements longer or shorted based on the tempo.
3. Every movement currently has an opposite movement to try keept the drone in certain boundries. However the oposite movement needs to be more powerful to counterbalance the innertia. This works for a while but once the drone starts to drift the effect snowballs. A potential solution would be to use the bottom camera to watch a marker on the floor and modify every movement so that the drone always drifts to this spot.
