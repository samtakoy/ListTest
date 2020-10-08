package ru.samtakoy.listtest.presentation.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import moxy.MvpDelegate
import moxy.MvpDelegateHolder

//
//
open abstract class MvpPreferenceFragmentCompat : PreferenceFragmentCompat(), MvpDelegateHolder {

    private var _isStateSaved = false
    private var mvpDelegate: MvpDelegate<MvpPreferenceFragmentCompat?>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMvpDelegate()!!.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        _isStateSaved = false
        getMvpDelegate()!!.onAttach()
    }

    override fun onResume() {
        super.onResume()
        _isStateSaved = false
        getMvpDelegate()!!.onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _isStateSaved = true
        getMvpDelegate()!!.onSaveInstanceState(outState)
        getMvpDelegate()!!.onDetach()
    }

    override fun onStop() {
        super.onStop()
        getMvpDelegate()!!.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMvpDelegate()!!.onDetach()
        getMvpDelegate()!!.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.requireActivity().isFinishing) {
            getMvpDelegate()!!.onDestroy()
        } else if (_isStateSaved) {
            _isStateSaved = false
        } else {
            var anyParentIsRemoving = false
            var parent = this.parentFragment
            while (!anyParentIsRemoving && parent != null) {
                anyParentIsRemoving = parent.isRemoving
                parent = parent.parentFragment
            }
            if (this.isRemoving || anyParentIsRemoving) {
                getMvpDelegate()!!.onDestroy()
            }
        }
    }

    override fun getMvpDelegate(): MvpDelegate<*>? {
        if (mvpDelegate == null) {
            mvpDelegate = MvpDelegate<MvpPreferenceFragmentCompat?>(this)
        }
        return mvpDelegate
    }
}