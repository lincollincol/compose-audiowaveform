# Compose AudioWaveform<img align="right" src="https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/icon.png" width="25%">  

![GitHub release (latest by date)](https://img.shields.io/github/v/release/lincollincol/compose-audiowaveform)
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

## Usage
#### Example
``` kotlin
var waveformProgress by remember {
  mutableStateOf(0F)
}
AudioWaveform(
  amplitudes = samples,
  progress = waveformProgress,
  onProgressChange = { progress = it }
)
``` 

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

## Sample app
You could also try [sample app](https://github.com/lincollincol/compose-audiowaveform/tree/develop/app), which demonstrateі all AudioWaveform library features. 
Wavefrom from sample app also synchronized with [Media3](https://developer.android.com/jetpack/androidx/releases/media3).
<p align="center">
  <img src="https://github.com/lincollincol/compose-audiowaveform/blob/develop/readme/sample_app.gif" width="25%"/>
</p>


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
