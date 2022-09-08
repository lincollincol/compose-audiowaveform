package com.linc.audiowaveform.sample.ui.screen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.linc.audiowaveform.sample.R
import com.linc.audiowaveform.sample.ui.screen.list.model.AudioListUiState
import com.linc.audiowaveform.sample.ui.screen.list.model.SingleAudioUiState
import com.linc.audiowaveform.sample.ui.theme.ComposeaudiowaveformTheme

@Composable
fun AudioListRoute(
    viewModel: AudioListViewModel,
    navController: NavController
) {
    LaunchedEffect(viewModel.navDestination) {
        viewModel.navDestination?.let(navController::navigate)
        viewModel.clearNavDestination()
    }
    AudioListScreen(viewModel.uiState)
}

@Composable
fun AudioListScreen(
    uiState: AudioListUiState
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(items = uiState.audioFiles, key = { it.id }) {
                AudioItem(itemState = it)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AudioItem(
    modifier: Modifier = Modifier,
    itemState: SingleAudioUiState
) {
    Surface(
        elevation = 2.dp,
        onClick = itemState.onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .then(modifier),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 4.dp),
                painter = painterResource(id = R.drawable.ic_audiotrack),
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = itemState.displayName,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = itemState.size,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun AudioItemPreview() {
    ComposeaudiowaveformTheme {
        AudioItem(
            itemState = SingleAudioUiState(
                id = "1",
                displayName = "Track #1",
                size = "0",
                onClick = {}
            )
        )
    }
}

@Preview
@Composable
private fun AudioListScreenPreview() {
    ComposeaudiowaveformTheme {
        AudioListScreen(
            uiState = AudioListUiState()
        )
    }
}