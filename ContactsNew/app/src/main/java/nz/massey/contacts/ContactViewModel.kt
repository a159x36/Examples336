package nz.massey.contacts

import android.app.Application
import android.util.Log
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

    private object PreferenceKeys {
        val SORT_REV = booleanPreferencesKey("sort_rev")
        val DIAL = booleanPreferencesKey("dial")
    }

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow<List<Contact>>()
    private val _sortRev = MutableStateFlow<Boolean>(false)
    private val _dial = MutableStateFlow<Boolean>(false)
    val sortRev = _sortRev.asStateFlow<Boolean>()
    val dial = _dial.asStateFlow<Boolean>()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.data.collect { settings ->
                val newSortRev = settings[PreferenceKeys.SORT_REV] == true
                if(_sortRev.value!=newSortRev) {
                    _sortRev.value = newSortRev
                    init()
                }
                _dial.value = settings[PreferenceKeys.DIAL] == true
                Log.i(TAG, "settings $settings")
            }
        }
    }
    fun setContacts(contacts: List<Contact>) {
        _contacts.value=contacts
    }
    fun setSortRev(sortRev: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.edit { settings ->
                settings[PreferenceKeys.SORT_REV] = sortRev
            }
        }
    }
    fun setDial(dial: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.edit { settings ->
                settings[PreferenceKeys.DIAL] = dial
            }
        }
    }
}