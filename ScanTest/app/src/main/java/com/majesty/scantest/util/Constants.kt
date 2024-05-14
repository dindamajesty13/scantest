package com.majesty.scantest.util

import android.view.KeyEvent

object Constants {
    const val PREF_NAME_SESSION = "session_pref"
    const val AUTH_KEY = "session_auth"

    fun isValidKey(keyCode: Int): Boolean {
        return (
                (keyCode == KeyEvent.KEYCODE_F9 || keyCode == 285 || keyCode == 286 || keyCode == 293 || keyCode == 142) ||
                (keyCode == KeyEvent.KEYCODE_F10 || keyCode == 289 || keyCode == 290) ||
                (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F5)
                )
    }
}