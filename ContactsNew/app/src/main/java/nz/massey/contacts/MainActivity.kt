package nz.massey.contacts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.massey.contacts.theme.ui.AppTypography
import androidx.core.net.toUri

const val TAG="Contacts"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : ComponentActivity() {

    data class Contact (var id:Int, var name: String)


    @Composable
    fun SettingsSwitch(modifier:Modifier=Modifier, heading:String="Example Switch", description:String="Switch me", state: State<Boolean>, onChange:(Boolean)->Unit={}) {
        Row (horizontalArrangement = Arrangement.SpaceBetween, modifier = modifier.padding(8.dp)){
            Column(modifier = modifier.padding(8.dp).weight(0.9f)) {
                Text(text = heading, style = AppTypography.titleMedium)
                Text(text = description, style = AppTypography.bodyMedium)
            }
            Switch(
                modifier = Modifier.fillMaxWidth().align(CenterVertically).weight(0.2f).padding(8.dp),
                checked = state.value,
                onCheckedChange = {
                    onChange(it)
                })
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar( modifier:Modifier) {
        var showDropDownMenu by remember { mutableStateOf(false) }
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
                        text= {
                            SettingsSwitch(
                                heading = "Dial",
                                description = "Dial Directly?",
                                state = viewmodel.dial.collectAsStateWithLifecycle(),
                            ) { viewmodel.setDial(it) }
                        }, onClick = {})
                DropdownMenuItem(
                        text= {
                            SettingsSwitch(
                                heading = "Sort Reverse",
                                description = "Sort contacts in reverse order?",
                                state = viewmodel.sortRev.collectAsStateWithLifecycle(),
                            ) { viewmodel.setSortRev(it)
                            }
                        }, onClick = {})}

            }
        )
    }

    @Composable
    fun ContactList()  {
        Scaffold(
            topBar = { AppBar( modifier = Modifier) },
        ) { innerPadding ->
            val contacts = viewmodel.contacts.collectAsState().value
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                LazyColumn {
                    items(contacts.size) { i ->
                        Row {
                            Text(text = contacts[i].name, modifier = Modifier.weight(4f))
                            Button(onClick = { callphone(contacts[i].id) }, modifier = Modifier.weight(1f)) {
                                Text("Call")
                            }
                        }
                    }
                }
            }
        }
    }
    fun callphone(contactId:Int) {
        val selectionargs: Array<String?>? = arrayOf<String?>("" + contactId)
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
        val phoneNumber =
            phones.getString(index)
        phones.close()
        // call the number
        startActivity(
            Intent(
                if(viewmodel.dial.value) Intent.ACTION_CALL else Intent.ACTION_DIAL,
                ("tel:$phoneNumber").toUri())
        )
    }
    lateinit var viewmodel: ContactViewModel
    fun init() {
        CoroutineScope(Dispatchers.IO).launch {
            // get preference to see if contacts are displayed in reverse order
            val sortRev = viewmodel.sortRev.value
            // get contacts with a phone number
            var mCursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + (if (sortRev) " DESC" else " ASC")
            )

            val contacts = mutableListOf<Contact>()

            if (mCursor != null) {
                mCursor.moveToFirst()
                do {
                    var index = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    var name = ""
                    var id = 0
                    if (index >= 0) name = mCursor.getString(index)
                    index = mCursor.getColumnIndex(ContactsContract.Contacts._ID)
                    if (index >= 0) id = mCursor.getInt(index)
                    contacts.add(Contact(id, name))
                    mCursor.moveToNext()
                } while (!mCursor.isAfterLast)
            }
            viewmodel.setContacts(contacts)
            mCursor?.close()
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        viewmodel = ContactViewModel(application) { init() }
        super.onCreate(savedInstanceState)
        setContent {
            ContactList()
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
        else init()
    }
}
