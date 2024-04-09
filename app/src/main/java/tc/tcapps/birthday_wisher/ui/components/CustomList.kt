package tc.tcapps.birthday_wisher.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import tc.tcapps.birthday_wisher.viewModles.ContactsViewModel

// This is the CustomListItem composable that is used in the app to display a list of contacts.
// it take a list of contacts, a function that is called when a contact is clicked, and a ContactsViewModel object as parameters
@SuppressLint("SuspiciousIndentation")
@Composable
fun CustomListItem(contacts: List<Map<String, Any>>, onClick: () -> Unit, contactsViewModel: ContactsViewModel ) {
    val scrollState = rememberScrollState();
    Column(modifier = Modifier.verticalScroll(scrollState).padding(bottom = 150.dp) ) {
            for (contact in contacts) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .padding(8.dp) // Provide some padding around the Card
                ) {
                    ListItem(
                        headlineContent = { Text(contact.get("name").toString()) },
                        supportingContent = { Text(DOBFormatter(contact.get("DOB").toString())) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Localized description",
                                tint = Color.Black,
                            )
                        },
                        trailingContent = {
                            Icon(
                                Icons.Filled.Edit, // This icon represents a right-pointing arrow
                                contentDescription = "Go to details",
                                modifier = Modifier.clickable {
                                    contactsViewModel.setContactTobeUpdated(contact);
                                    onClick();
                                }
                            )
                        },
                        colors = customListItemColors()
                    )
                }
            }
    }

}

// This is the customListItemColors function that is used to set the colors of the CustomListItem
@Composable
fun customListItemColors(): ListItemColors {
    val textColor = Color.Black
    val disabledColor = textColor.copy(alpha = ContentAlpha.disabled)
    val iconColor = LocalContentColor.current

    return ListItemColors(
        containerColor = Color.Transparent,
        headlineColor = textColor,
        overlineColor = textColor,
        supportingTextColor = textColor,
        leadingIconColor = iconColor,
        trailingIconColor = iconColor,
        disabledHeadlineColor = disabledColor,
        disabledLeadingIconColor = disabledColor,
        disabledTrailingIconColor = disabledColor
    )
}

// This is the DOBFormatter function that is used to format the date of birth
private fun DOBFormatter(DOB: String): String{
//    Split the DOB string into an array of strings
    val date = DOB.split("-");
//    Check the month and return the month name
    when(date[1]){
        "1" -> return date[0] + " January";
        "2" -> return date[0] + " February";
        "3" -> return date[0] + " March";
        "4" -> return date[0] + " April";
        "5" -> return date[0] + " May";
        "6" -> return date[0] + " June";
        "7" -> return date[0] + " July";
        "8" -> return date[0] + " August";
        "9" -> return date[0] + " September";
        "10" -> return date[0] + " October";
        "11" -> return date[0] + " November";
        "12" -> return date[0] + " December";
        else -> return "Invalid Date";
    }
}