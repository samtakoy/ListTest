package ru.samtakoy.listtest.presentation.settings

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.ktx.moxyPresenter
import ru.samtakoy.listtest.R
import ru.samtakoy.listtest.app.Di
import javax.inject.Inject
import javax.inject.Provider

private const val TAG = "SettingsFragment"

class SettingsFragment : MvpPreferenceFragmentCompat(), SettingsView{

    @Inject
    lateinit var presenterProvider: Provider<SettingsPresenter>
    private val presenter by moxyPresenter {
        presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Di.appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)

        findPreference("pref_key_invalidate_db_cache").onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference: Preference? ->
                presenter.onUiInvalidateDbCache()
                true
            }

        findPreference("pref_key_clear_db_cache").onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference: Preference? ->
                presenter.onUiClearDbCache()
                true
            }

        findPreference("pref_key_clear_glide_cache").onPreferenceClickListener =
            Preference.OnPreferenceClickListener { preference: Preference? ->
                presenter.onUiClearGlideCaches()
                true
            }
    }

    override fun showMessage(messageId: Int) {
        Toast.makeText(requireContext().applicationContext, messageId, Toast.LENGTH_SHORT).show()
    }

    override fun clearGlideCaches() {

        val appContext = requireContext().applicationContext

        Glide.get(appContext).clearMemory();

        clearGlideDiskCache()

    }

    private fun clearGlideDiskCache() {

        val appContext = requireContext().applicationContext

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Log.e(TAG, "Disk clearing exception", throwable)
            showMessage(R.string.msg_settings_apply_error)
        }

        lifecycleScope.launch(coroutineExceptionHandler) {
            withContext(Dispatchers.IO) {
                Glide.get(appContext).clearDiskCache()
            }
            showMessage(R.string.msg_settings_apply_success)
        }
    }
}