package tc.tcapps.birthday_wisher.ui.components

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


// This is the BottomAppBar composable that is used in the app
@Composable
fun BottomAppBar(
    onHomeClick: () -> Unit, // This is a function that is called when the home button is clicked
    onAddClick: () -> Unit // This is a function that is called when the add button is clicked
) {
//    This is the BottomAppBar composable that is used in the app
            BottomAppBar(
//                Set the actions of the BottomAppBar to a IconButton with the home icon
                actions = {
                    IconButton(onClick = onHomeClick) {
                        Icon(Icons.Filled.Home, contentDescription = "Localized description")
                    }
                },
//                Set the height of the BottomAppBar to 60.dp
                Modifier.height(60.dp),
//                Set the floatingActionButton to a FloatingActionButton with the add icon
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddClick, // Set the onClick listener of the FloatingActionButton to the onAddClick function
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        modifier = Modifier.size(width = 40.dp, height = 40.dp)
                    ) {
                        Icon(Icons.Filled.Add, "Localized description")
                    }
                }
            )
}
