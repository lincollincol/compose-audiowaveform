package com.linc.audiowaveform.sample.model

sealed class NavDestination(val route: String) {

    object AudioList : NavDestination("audio-list")

    object AudioWaveform : NavDestination("audio-waveform/{id}") {
        const val ContentIdArg = "id"
        fun createRoute(id: String) = "audio-waveform/$id"
    }

}