package tc.tcapps.birthday_wisher.ui.components

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


// This is the MyAppBar composable that is used in the app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    title: String, // This is the title of the top bar that is displayed
    logoutClick: () -> Unit = {}, // This is a function that is called when the logout button is clicked
    isLoggedIn: Boolean = false, // This is a boolean that determines whether the user is logged in or not
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    CenterAlignedTopAppBar(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        actions =
        {
//            If the user is logged in, display a logout button
            if (isLoggedIn) {
                IconButton(onClick = logoutClick) { // Set the onClick listener of the IconButton to the logoutClick function
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
//        Set the scrollBehavior of the top bar to the scrollBehavior
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )
}
