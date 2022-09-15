package com.linc.audiowaveform.sample.ui.screen.list

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.linc.audiowaveform.sample.R
import com.linc.audiowaveform.sample.extension.getReadStoragePermission
import com.linc.audiowaveform.sample.model.PermissionsState
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
    AudioListScreen(
        uiState = viewModel.uiState,
        onPermissionsGranted = viewModel::updatePermissionsState,
        onQueryChange = viewModel::updateSearchQuery
    )
}

@Composable
fun AudioListScreen(
    uiState: AudioListUiState,
    onPermissionsGranted: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionsGranted
    )
    LaunchedEffect(Unit) {
        launcher.launch(getReadStoragePermission())
    }
    Box(modifier = Modifier.fillMaxSize()) {
        when(uiState.permissionsState) {
            PermissionsState.Unknown ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            PermissionsState.Granted ->
                AudioList(uiState = uiState, onQueryChange = onQueryChange)
            PermissionsState.Denied ->
                PermissionsRationale(onGrantClick = {
                    launcher.launch(getReadStoragePermission())
                })
        }
        AnimatedVisibility(visible = uiState.isLoadingAudios && uiState.audioFiles.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun PermissionsRationale(
    modifier: Modifier = Modifier,
    onGrantClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(0.8F),
            text = stringResource(id = R.string.read_permissions_rationale),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGrantClick) {
            Text(text = stringResource(id = R.string.grant_permissions))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AudioList(
    modifier: Modifier = Modifier,
    uiState: AudioListUiState,
    onQueryChange: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        stickyHeader {
            SearchField(value = uiState.searchQuery, onValueChange = onQueryChange)
        }
        items(items = uiState.audioFiles, key = { it.id }) {
            AudioItem(itemState = it)
        }
    }
}

@Composable
private fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = stringResource(id = R.string.search)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AudioItem(
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
            uiState = AudioListUiState(),
            onPermissionsGranted = {},
            onQueryChange = {}
        )
    }
}