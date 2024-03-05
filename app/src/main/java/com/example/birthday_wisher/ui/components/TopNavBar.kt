package com.example.birthday_wisher.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String,
    logoutClick: () -> Unit = {},
    isLoggedIn: Boolean = false,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    CenterAlignedTopAppBar(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        actions =
        {
            if (isLoggedIn) {
                IconButton(onClick = logoutClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}
