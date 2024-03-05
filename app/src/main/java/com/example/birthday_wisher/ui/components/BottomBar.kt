package com.example.birthday_wisher.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun BottomAppBar(
    onHomeClick: () -> Unit,
    onAddClick: () -> Unit
) {
            BottomAppBar(
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Filled.Home, contentDescription = "Localized description")
                    }
                },
                Modifier.height(60.dp),
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddClick,
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        modifier = Modifier.size(width = 40.dp, height = 40.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
}
