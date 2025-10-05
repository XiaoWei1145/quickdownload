package com.multicloud.downloader.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberDirectoryPickerLauncher(
    onDirectorySelected: (Uri?) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val context = LocalContext.current
    
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val uri = result.data?.data
            onDirectorySelected(uri)
        } else {
            onDirectorySelected(null)
        }
    }
}

fun createDirectoryPickerIntent(context: Context): Intent {
    return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
        // Optionally, you can set the initial directory
        // putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
    }
}