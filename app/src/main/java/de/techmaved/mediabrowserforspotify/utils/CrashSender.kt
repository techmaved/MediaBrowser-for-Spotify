package de.techmaved.mediabrowserforspotify.utils

import android.content.Context
import com.google.auto.service.AutoService
import org.acra.config.CoreConfiguration
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

class LocalSender : ReportSender {
    override fun send(context: Context, errorContent: CrashReportData) {}
}

@AutoService(ReportSenderFactory::class)
class SenderFactory : ReportSenderFactory {
    override fun create(context: Context, config: CoreConfiguration): ReportSender {
        return LocalSender()
    }
}