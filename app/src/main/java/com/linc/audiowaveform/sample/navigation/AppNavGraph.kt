package com.linc.audiowaveform.sample.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.linc.audiowaveform.sample.model.NavDestination
import com.linc.audiowaveform.sample.ui.screen.list.AudioListRoute
import com.linc.audiowaveform.sample.ui.screen.waveform.AudioWaveformRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    startDestination: NavDestination = NavDestination.AudioList,
    navHostController: NavHostController
) {
    NavHost(
        modifier = Modifier.then(modifier),
        navController = navHostController,
        startDestination = startDestination.route
    ) {
        composable(NavDestination.AudioList.route) {
            AudioListRoute(hiltViewModel(), navHostController)
        }
        composable(NavDestination.AudioWaveform.route) {
            val contentId = it.arguments
                ?.getString(NavDestination.AudioWaveform.ContentIdArg)
                ?.let(::requireNotNull)
                .orEmpty()
            AudioWaveformRoute(hiltViewModel(), contentId)
        }
    }

}