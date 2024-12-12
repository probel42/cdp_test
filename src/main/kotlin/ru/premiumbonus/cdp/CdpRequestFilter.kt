package ru.premiumbonus.cdp

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.event.Level
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import ru.premiumbonus.cdp.logger.CdpLogger
import kotlin.random.Random

@Component
class CdpRequestFilter : OncePerRequestFilter() {
    companion object {
        val LOGGER: CdpLogger = CdpLogger.getLogger(CdpRequestFilter::class.java.name)

        private const val BODY_SIZE_LIMIT = 10000

        private const val LOG_MARKER_LENGTH: Long = 8
        private val LOG_MARKER_CHARS = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        private fun genRandomMarker(): String = (1..LOG_MARKER_LENGTH)
            .map { Random.nextInt(0, LOG_MARKER_CHARS.size).let { LOG_MARKER_CHARS[it] } }
            .joinToString("")

        private fun buildFullMessage(request: HttpServletRequest, response: HttpServletResponse): String {
            val sb = StringBuilder()

            // todo body первая сложность (двойное чтение input stream): нужно городить большой костыль,
            //  чтобы перегонять HttpServletRequest.inputStream в byte[] для многократного чтения и обратно для отправки в filter chain

            sb.append(request.method + " " + request.requestURI)
            sb.append("\n\nRESPONSE STATUS: " + response.status)
            return sb.toString()
        }

        private fun getLogLevel(status: HttpStatus): Level {
            return when (status.series()) {
                HttpStatus.Series.INFORMATIONAL -> Level.INFO
                HttpStatus.Series.SUCCESSFUL -> Level.INFO
                HttpStatus.Series.REDIRECTION -> Level.INFO
                HttpStatus.Series.CLIENT_ERROR -> Level.WARN
                HttpStatus.Series.SERVER_ERROR -> Level.ERROR
            }
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val startedAt = System.currentTimeMillis()
        try {
            filterChain.doFilter(request, response)
        } finally {
            val message = buildFullMessage(request, response)
            val marker = genRandomMarker()
            val params = mutableMapOf(
                "category" to LOGGER.getName(),
                "duration" to (System.currentTimeMillis() - startedAt).toDouble() / 1000,
                "logger" to marker,
                "logger2" to marker,
                "route" to "-", // todo вторая сложность: вытаскивание имени контроллера и метода
                "status" to response.status
            )
            val level = getLogLevel(HttpStatus.valueOf(response.status))
            LOGGER.logC(message, level, params)
        }
    }
}
