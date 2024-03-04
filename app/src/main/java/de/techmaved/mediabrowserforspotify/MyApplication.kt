package de.techmaved.mediabrowserforspotify

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import de.techmaved.mediabrowserforspotify.ui.components.ErrorDialog
import de.techmaved.mediabrowserforspotify.models.Model
import org.acra.config.dialog
import org.acra.data.StringFormat
import org.acra.ktx.initAcra

class MyApplication : Application() {
    lateinit var model: Model

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            dialog {
                reportDialogClass = ErrorDialog()::class.java
            }
        }
    }

    companion object {
        lateinit var context: Context
    }
}