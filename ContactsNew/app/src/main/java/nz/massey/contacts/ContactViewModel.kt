package nz.massey.contacts

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nz.massey.contacts.MainActivity.Contact

@Suppress("UNCHECKED_CAST")
class ContactViewModelFactory(val app:Application, val init: () -> Unit) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ContactViewModel(app, init) as T
    }
}

class ContactViewModel(val app:Application, val init: () -> Unit): ViewModel() {

    object PreferenceKeys {
        val SORT_REV = booleanPreferencesKey("sort_rev")
        val DIAL = booleanPreferencesKey("dial")
    }

    private val _contacts = MutableStateFlow(emptyList<Contact>())
    val contacts = _contacts.asStateFlow()


    private val _sortRev = MutableStateFlow(false)
    private val _dial = MutableStateFlow(false)
    val sortRev =_sortRev.asStateFlow()
    val dial = _dial.asStateFlow()

    init{
        CoroutineScope(Dispatchers.IO).launch {
            app.dataStore.data.collect { settings ->
                val newSortRev = settings[PreferenceKeys.SORT_REV]?:false
                if(_sortRev.value!=newSortRev) {
                    _sortRev.value = newSortRev
                    init()
                }
                _dial.value = settings[PreferenceKeys.DIAL]?:false
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