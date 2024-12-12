package ru.premiumbonus.cdp.logger

import org.slf4j.event.Level

interface CdpLogger {
    companion object {
        fun getLogger(name: String) = CdpLoggerImpl(name)
    }

    fun getName(): String

    fun trace(message: String)
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)

    fun traceC(message: String, additionalFields: Map<String, Any>)
    fun debugC(message: String, additionalFields: Map<String, Any>)
    fun infoC(message: String, additionalFields: Map<String, Any>)
    fun warnC(message: String, additionalFields: Map<String, Any>)
    fun errorC(message: String, additionalFields: Map<String, Any>)

    fun putContext(key: String, value: Any)
    fun removeContext(key: String)

    fun log(message: String, level: Level)
    fun logC(message: String, level: Level, additionalFields: Map<String, Any>)
}
