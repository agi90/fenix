/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.logins

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_login_site_info.*
import org.mozilla.fenix.R
import org.mozilla.fenix.components.FenixSnackbar
import org.mozilla.fenix.ext.components

class SavedLoginSiteInfoFragment : Fragment(R.layout.fragment_saved_login_site_info) {
    private val safeArguments get() = requireNotNull(arguments)

    private val savedLoginItem: SavedLoginsItem by lazy {
        SavedLoginSiteInfoFragmentArgs.fromBundle(
            safeArguments
        ).savedLoginItem
    }

    override fun onPause() {
        // If we pause this fragment, we want to pop users back to reauth
        findNavController().popBackStack(R.id.loginsFragment, false)
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        siteInfoText.text = savedLoginItem.url
        copySiteItem.setOnClickListener {
            val clipboard = view.context.components.clipboardHandler
            clipboard.text = savedLoginItem.url
            showCopiedSnackbar(getString(R.string.logins_site_copied))
        }

        usernameInfoText.text = savedLoginItem.userName
        copyUsernameItem.setOnClickListener {
            val clipboard = view.context.components.clipboardHandler
            clipboard.text = savedLoginItem.userName
            showCopiedSnackbar(getString(R.string.logins_username_copied))
        }

        passwordInfoText.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordInfoText.text = savedLoginItem.password
        revealPasswordItem.setOnClickListener {
            togglePasswordReveal()
        }
        copyPasswordItem.setOnClickListener {
            val clipboard = view.context.components.clipboardHandler
            clipboard.text = savedLoginItem.password
            showCopiedSnackbar(getString(R.string.logins_password_copied))
        }
    }

    private fun showCopiedSnackbar(copiedItem: String) {
        view?.let {
            FenixSnackbar.make(it, Snackbar.LENGTH_SHORT).setText(copiedItem).show()
        }
    }

    private fun togglePasswordReveal() {
        if (passwordInfoText.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT) {
            revealPasswordItem.setImageDrawable(context?.getDrawable(R.drawable.ic_password_hide))
            passwordInfoText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            revealPasswordItem.contentDescription =
                context?.getString(R.string.saved_login_hide_password)
        } else {
            revealPasswordItem.setImageDrawable(context?.getDrawable(R.drawable.ic_password_reveal))
            passwordInfoText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            revealPasswordItem.contentDescription =
                context?.getString(R.string.saved_login_reveal_password)
        }
        // For the new type to take effect you need to reset the text
        passwordInfoText.text = savedLoginItem.password
    }

    override fun onResume() {
        super.onResume()
        activity?.title = savedLoginItem.url
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}
