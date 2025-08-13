package nz.massey.contacts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.massey.contacts.theme.ui.AppTheme
import nz.massey.contacts.theme.ui.AppTypography

//const val TAG="Contacts"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {

    data class Contact (var id:Int, var name: String)

    val viewmodel:ContactViewModel by viewModels {
        ContactViewModelFactory(dataStore) { init() }
    }

    @Composable
    fun SettingsSwitch(modifier:Modifier=Modifier, heading:String, description:String, state: Boolean, onChange:(Boolean)->Unit={}) {
        Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(8.dp)){
            Column(modifier = modifier.padding(8.dp).weight(0.9f)) {
                Text(text = heading, style = AppTypography.titleMedium)
                Text(text = description, style = AppTypography.bodyMedium)
            }
            Switch(
                modifier = Modifier.fillMaxWidth().align(CenterVertically).weight(0.2f).padding(8.dp),
                checked = state,
                onCheckedChange = {
                    onChange(it)
                })
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(viewmodel: ContactViewModel, modifier:Modifier) {
        var showDropDownMenu by remember { mutableStateOf(false) }

        val sortRev=viewmodel.sortRev.collectAsState().value
        val dial=viewmodel.dial.collectAsState().value

        TopAppBar(
            modifier = modifier,
            title = { Text(text = "Contacts") },
            actions = {
                IconButton(onClick = { showDropDownMenu = true }) {
                    Icon(Icons.Filled.MoreVert, null)
                }
                DropdownMenu(
                    expanded = showDropDownMenu,
                    onDismissRequest = { showDropDownMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            SettingsSwitch(
                                heading = "Dial",
                                description = "Dial Directly?",
                                state = dial,
                            ) { viewmodel.updateDial(it) }
                        }, onClick = {})
                    DropdownMenuItem(
                        text = {
                            SettingsSwitch(
                                heading = "Sort Reverse",
                                description = "Sort contacts in reverse order?",
                                state = sortRev,
                            ) { viewmodel.updateSortRev(it) }
                        }, onClick = {})
                }

            }
        )
    }

    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun AppPreview() {
       val viewmodel:ContactViewModel =viewModel(factory = ContactViewModelFactory(LocalContext.current.dataStore) { init() })
        viewmodel.setContacts(listOf(Contact(1, "Bob"), Contact(2, "Alice")))
        AppTheme {
            ContactList(viewmodel)
        }
    }
    
    @Composable
    fun ContactList(viewmodel: ContactViewModel)  {
        Scaffold(
            topBar = { AppBar(viewmodel, modifier = Modifier) },
        ) { innerPadding ->
            val contacts = viewmodel.contacts.collectAsState().value
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                LazyColumn {
                    items(contacts.size, key = { contacts[it].id }) { i ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .animateItem(),
                            shape = RoundedCornerShape(4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(Modifier.padding(8.dp)) {
                                Text(
                                    text = contacts[i].name,
                                    style = AppTypography.displaySmall,
                                    modifier = Modifier.weight(4f)
                                )
                                Button(
                                    onClick = { callphone(contacts[i].id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(4.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(90,160,90)
                                    )
                                ) {
                                    Text("Call")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun callphone( contactId:Int) {
        val selectionargs: Array<String> = arrayOf("" + contactId)
        // contacts may have more than one phone number so the numbers are stored in a separate table
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", selectionargs, null
        )
        if (phones == null)  // shouldn't happen because we asked for contacts with phone numbers
            return
        // get the first phone number
        phones.moveToFirst()
        val index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val phoneNumber = phones.getString(index)
        phones.close()
        // call the number
        startActivity(
            Intent(
                if(viewmodel.dial.value) Intent.ACTION_CALL else Intent.ACTION_DIAL,
                ("tel:$phoneNumber").toUri())
        )
    }

    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            // get preference to see if contacts are displayed in reverse order
            val sortRev = viewmodel.sortRev.value
            // get contacts with a phone number
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + (if (sortRev) " DESC" else " ASC")
            )

            val contacts = mutableListOf<Contact>()
            if (cursor != null) {
                with(cursor) {
                    val nameindex = getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val idindex = getColumnIndex(ContactsContract.Contacts._ID)
                    if (nameindex < 0 || idindex < 0 ) return@launch
                    if(!moveToFirst()) return@launch
                    do {
                        val name = getString(nameindex)
                        val id = getInt(idindex)
                        contacts.add(Contact(id, name))
                        moveToNext()
                    } while (!isAfterLast)
                    close()
                }
            }
            viewmodel.setContacts(contacts)
        }
    }
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ContactList(viewmodel)
            }
        }
        val canReadContacts=checkSelfPermission(android.Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED
        val canCall=checkSelfPermission(android.Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED
        val requestPermissionLauncher = registerForActivityResult(RequestMultiplePermissions()) {
                isGranted: Map<String,Boolean> -> if(isGranted[android.Manifest.permission.READ_CONTACTS] == true)
            init()
        }
        // ask for permissions
        if (!canReadContacts || !canCall)
            requestPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_CONTACTS ,
                android.Manifest.permission.CALL_PHONE))
    }

    override fun onResume() {
        super.onResume()
        if(checkSelfPermission(android.Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED)
            init()
    }
}
