/*
 * Copyright 2022 DroidconKE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android254.presentation.speakers.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android254.presentation.speakers.SpeakersScreenUiState
import com.android254.presentation.speakers.SpeakersScreenViewModel
import com.droidconke.chai.ChaiDCKE22Theme
import com.droidconke.chai.chaiColorsPalette
import com.droidconke.chai.components.ChaiBodyLarge
import com.droidconke.chai.components.ChaiBodyMediumBold
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import ke.droidcon.kotlin.presentation.R

@Composable
fun SpeakersRoute(
    speakersScreenViewModel: SpeakersScreenViewModel = hiltViewModel(),
    navigateToHomeScreen: () -> Unit = {},
    navigateToSpeaker: (String) -> Unit = {}
) {
    val uiState by speakersScreenViewModel.speakersScreenUiState.collectAsStateWithLifecycle()
    SpeakersScreen(
        uiState = uiState,
        navigateToHomeScreen = navigateToHomeScreen,
        navigateToSpeaker = navigateToSpeaker
    )
}

@Composable
private fun SpeakersScreen(
    uiState: SpeakersScreenUiState,
    navigateToHomeScreen: () -> Unit = {},
    navigateToSpeaker: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ChaiBodyLarge(
                        bodyText = stringResource(id = R.string.speakers_label),
                        textColor = MaterialTheme.chaiColorsPalette.textBoldColor
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = navigateToHomeScreen
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = stringResource(R.string.back_arrow_icon_description),
                            tint = MaterialTheme.chaiColorsPalette.textBoldColor
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.chaiColorsPalette.textBoldColor,
                    navigationIconContentColor = MaterialTheme.chaiColorsPalette.textBoldColor
                )
            )
        },
        containerColor = MaterialTheme.chaiColorsPalette.background
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = uiState is SpeakersScreenUiState.Loading),
            onRefresh = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is SpeakersScreenUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is SpeakersScreenUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ChaiBodyMediumBold(
                            modifier = Modifier.align(Alignment.Center),
                            bodyText = uiState.message,
                            textColor = MaterialTheme.chaiColorsPalette.textNormalColor
                        )
                    }
                }

                is SpeakersScreenUiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(items = uiState.speakers) { speaker ->
                            SpeakerComponent(
                                speaker = speaker,
                                onClick = {
                                    navigateToSpeaker.invoke(speaker.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SpeakersScreenPreview() {
    ChaiDCKE22Theme {
        SpeakersScreen(
            uiState = SpeakersScreenUiState.Success(speakers = listOf())
        )
    }
}