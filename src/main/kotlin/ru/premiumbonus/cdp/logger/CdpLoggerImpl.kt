package ru.premiumbonus.cdp.logger

import ch.qos.logback.classic.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.DefaultLoggingEvent
import org.slf4j.event.Level
import java.util.concurrent.ConcurrentHashMap

class CdpLoggerImpl(name: String) : CdpLogger {
    private val subLogger: Logger

    private val contextFields: MutableMap<String, Any> = ConcurrentHashMap()

    init {
        val logger = LoggerFactory.getLogger(name)
        if (logger is Logger) {
            subLogger = logger
        } else {
            throw Exception("cant find ch.qos.logback slf4j implementation")
        }
    }

    override fun getName(): String = subLogger.name

    override fun trace(message: String) = log(message, Level.TRACE)
    override fun debug(message: String) = log(message, Level.DEBUG)
    override fun info(message: String) = log(message, Level.INFO)
    override fun warn(message: String) = log(message, Level.WARN)
    override fun error(message: String) = log(message, Level.ERROR)

    override fun traceC(message: String, additionalFields: Map<String, Any>) = logC(message, Level.TRACE, additionalFields)
    override fun debugC(message: String, additionalFields: Map<String, Any>) = logC(message, Level.DEBUG, additionalFields)
    override fun infoC(message: String, additionalFields: Map<String, Any>) = logC(message, Level.INFO, additionalFields)
    override fun warnC(message: String, additionalFields: Map<String, Any>) = logC(message, Level.WARN, additionalFields)
    override fun errorC(message: String, additionalFields: Map<String, Any>) = logC(message, Level.ERROR, additionalFields)

    override fun putContext(key: String, value: Any) {
        contextFields[key] = value
    }

    override fun removeContext(key: String) {
        contextFields -= key
    }

    override fun log(message: String, level: Level) = logC(message, level, emptyMap())

    override fun logC(message: String, level: Level, additionalFields: Map<String, Any>) {
        val le = DefaultLoggingEvent(level, subLogger)
        le.message = message
        contextFields.forEach { le.addKeyValue(it.key, it.value) }
        additionalFields.forEach { le.addKeyValue(it.key, it.value) }
        subLogger.log(le)
    }
}