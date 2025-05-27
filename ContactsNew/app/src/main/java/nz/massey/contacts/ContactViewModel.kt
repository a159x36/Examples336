package nz.massey.contacts

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.massey.contacts.MainActivity.Contact

class ContactViewModel(val app:Application, init: () -> Unit): ViewModel() {

    object PreferenceKeys {
        val SORT_REV = booleanPreferencesKey("sort_rev")
        val DIAL = booleanPreferencesKey("dial")
    }

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow<List<Contact>>()

    var sortRev = false
    var dial = false

/*
    private val _sortRev = MutableStateFlow<Boolean>(false)
    private val _dial = MutableStateFlow<Boolean>(false)
    val sortRev = _sortRev.asStateFlow<Boolean>()
    val dial = _dial.asStateFlow<Boolean>()

 */

    init{
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.data.collect { settings ->
                val newSortRev = settings[PreferenceKeys.SORT_REV]?:false
                if(sortRev!=newSortRev) {
                    sortRev = newSortRev
                    init()
                }
                dial = settings[PreferenceKeys.DIAL]?:false
            }
        }
    }

    fun setContacts(contacts: List<Contact>) {
        _contacts.value=contacts
    }

    // generic function to update preferences
    fun <T>setPref(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.edit { settings ->
                settings[key] = value
            }
        }
    }
    fun updateSortRev(sortRev: Boolean) {setPref(PreferenceKeys.SORT_REV, sortRev) }
    fun updateDial(dial: Boolean) { setPref(PreferenceKeys.DIAL, dial) }

}