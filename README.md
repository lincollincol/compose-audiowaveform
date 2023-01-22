# Compose AudioWaveform<img align="right" src="https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/icon.png" width="25%">  

![GitHub release (latest by date)](https://img.shields.io/github/v/release/lincollincol/compose-audiowaveform)
[![](https://jitpack.io/v/lincollincol/compose-audiowaveform/month.svg)](https://jitpack.io/#lincollincol/compose-audiowaveform)
![GitHub](https://img.shields.io/github/license/lincollincol/compose-audiowaveform)

![GitHub followers](https://img.shields.io/github/followers/lincollincol?style=social)
![GitHub stars](https://img.shields.io/github/stars/lincollincol/compose-audiowaveform?style=social)
![GitHub forks](https://img.shields.io/github/forks/lincollincol/compose-audiowaveform?style=social)

## Description
AudioWaveform is a lightweight Jetpack Compose library which draws waveform of audio.   
Library uses compose `Canvas API` under the hood. It helps us to create customizable and flexible waveforms. 
This library was inspired by the [WaveformSeekBar](https://github.com/massoudss/waveformSeekBar) library (`xml` implementation).   
AudioWaveform is fully compatible with [Amplituda](https://github.com/lincollincol/Amplituda) library.

## Waveforms created with AudioWaveform
<p align="center">
  <img src="https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/waveforms.png" width="100%"/>
</p>

## Download
``` groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
``` groovy
dependencies {
  implementation 'com.github.lincollincol:compose-audiowaveform:x.y.z'
}
```

## Usage
#### Common way
``` kotlin
var waveformProgress by remember { mutableStateOf(0F) }
AudioWaveform(
  amplitudes = amplitudes,
  progress = waveformProgress,
  onProgressChange = { waveformProgress = it }
)
``` 

#### Custom color
* `SolidColor()` - single color `Brush`.
* `Brush.horizontalGradient()`, `Brush.verticalGradient()` (and more) - default `Brush` static gradient implementations.
* `Brush.infinite*Gradient()` (where * is one of `linear`, `horizontal` or `vertical`)  - infinite animated gradient. This is AudioWaveform library extension functions created with [this article](https://medium.com/androiddevelopers/animating-brush-text-coloring-in-compose-%EF%B8%8F-26ae99d9b402).
``` kotlin
var waveformProgress by remember { mutableStateOf(0F) }

val colorBrush = SolidColor(Color.Magenta)

val staticGradientBrush = Brush.linearGradient(colors = listOf(Color(0xff22c1c3), Color(0xfffdbb2d)))

val animatedGradientBrush = Brush.infiniteLinearGradient(
  colors = listOf(Color(0xff22c1c3), Color(0xfffdbb2d)),
  animation = tween(durationMillis = 6000, easing = LinearEasing),
  width = 128F
)

AudioWaveform(
  progress = waveformProgress,
  progressBrush = brush,
  amplitudes = amplitudes,
  onProgressChange = { waveformProgress = it }
)
``` 

#### All parameters
``` kotlin
var waveformProgress by remember { mutableStateOf(0F) }
AudioWaveform(
  modifier = Modifier.fillMaxWidth(),
  // Spike DrawStyle: Fill or Stroke 
  style = Fill,
  waveformAlignment = WaveformAlignment.Center, 
  amplitudeType = AmplitudeType.Avg,
  // Colors could be updated with Brush API
  progressBrush = SolidColor(Color.Magenta),
  waveformBrush = SolidColor(Color.LightGray),
  spikeWidth = 4.dp,
  spikePadding = 2.dp,
  spikeRadius = 4.dp,
  progress = waveformProgress,
  amplitudes = amplitudes,
  onProgressChange = { waveformProgress = it },
  onProgressChangeFinished = {}
)
```

## Sample app
You could also try [sample app](https://github.com/lincollincol/compose-audiowaveform/tree/develop/app), which demonstrates all AudioWaveform library features. 
Waveform from sample app is also synchronized with [Media3](https://developer.android.com/jetpack/androidx/releases/media3).  
Download sample app apk [here](https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/audiowaveform_sample.apk).
<p align="center">
  <img src="https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/sample_app.gif" width="25%"/>
</p>

## Amplituda compatibility
AudioWaveform requires `amplitudes` to draw waveform. This parameter is a list of integers, which represents audio data.
You can process the audio file by yourself or use already existing library [Amplituda](https://github.com/lincollincol/Amplituda).  
[Amplituda](https://github.com/lincollincol/Amplituda) is a fast audio processing library, which provides you data for drawing waveforms.
Library has [caching](https://github.com/lincollincol/Amplituda#-cache-output-data) and [compressing](https://github.com/lincollincol/Amplituda#-compress-output-data) processed data features out of the box.  
Here is [Amplituda library usage](https://github.com/lincollincol/compose-audiowaveform/blob/develop/app/src/main/java/com/linc/audiowaveform/sample/data/AudioManager.kt) in a sample app.

## License
```
   Copyright 2022-present lincollincol

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
