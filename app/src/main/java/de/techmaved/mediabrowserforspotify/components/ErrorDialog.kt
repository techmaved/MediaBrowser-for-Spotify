package de.techmaved.mediabrowserforspotify.components

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.documentfile.provider.DocumentFile
import de.techmaved.mediabrowserforspotify.R
import de.techmaved.mediabrowserforspotify.ui.theme.MediaBrowserForSpotifyTheme
import org.acra.dialog.CrashReportDialogHelper
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ErrorDialog: ComponentActivity() {
    private lateinit var helper: CrashReportDialogHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper = CrashReportDialogHelper(this, intent)

        setContent {
            MediaBrowserForSpotifyTheme {
                Dialog()
            }
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            val directory = uri?.let { DocumentFile.fromTreeUri(applicationContext, it) }
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val file = directory?.createFile("application/json", "mediabrowser-for-spotify-$currentDate")
            val pfd: ParcelFileDescriptor? = applicationContext.contentResolver.openFileDescriptor(file!!.uri, "w")
            val fos = FileOutputStream(pfd?.fileDescriptor)

            if (pfd != null) {
                fos.write(helper.reportData.toJSON().toByteArray())
                fos.close()
            }

            finish()

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_issues_link)))
            startActivity(intent)
        }
    }

    @Composable
    private fun Dialog() {
        AlertDialog(
            onDismissRequest = { finish() },
            title = { Text("Media Browser for Spotify just crashed") },
            text = {
                Column() {
                    Text("It would be helpful when you open an issue on GitHub.")
                    Text("To do so please choose the location where the error logs should be saved to.")
                    Text("GitHub will be opened for you.")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    resultLauncher.launch(intent)
                }) {
                    Text("Save error logs and open GitHub")
                }
            },
            dismissButton = {
                TextButton(onClick = { finish() }) {
                    Text("Cancel")
                }
            },
        )
    }
}