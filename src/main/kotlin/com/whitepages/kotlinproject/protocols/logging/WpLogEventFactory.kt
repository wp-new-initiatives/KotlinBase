package com.whitepages.kotlinproject.protocols.logging

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.Property
import org.apache.logging.log4j.core.impl.Log4jLogEvent
import org.apache.logging.log4j.core.impl.LogEventFactory
import org.apache.logging.log4j.message.Message
import org.apache.logging.log4j.message.ObjectMessage
import org.apache.logging.log4j.message.ParameterizedMessage
import org.json.JSONObject

class WpLogEventFactory : LogEventFactory {
    override fun createEvent(loggerName: String?, marker: Marker?, fqcn: String?, level: Level?, data: Message?, properties: MutableList<Property>?, t: Throwable?): LogEvent {
        var throwable = t
        var msg = data
        if (t == null && data is Message) {
            throwable = data.throwable
        }

        if (data is ObjectMessage) {
            msg = ObjectMessage(JSONObject(data.parameter).toMap())
            if (msg.hashCode() == 0) {
                msg = data
            }
        } else if (data is ParameterizedMessage) {
            val obj = JSONObject()
            for (params in data.parameters) {
                if (params is Map<*, *>) {
                    for (param in params.entries) {
                        obj.put(param.key as String, param.value)
                    }
                }
            }
            obj.put("message", data.format)
            msg = ObjectMessage(obj.toMap())
            if (msg.hashCode() == 0) {
                msg = data
            }
        }

        // XXX do your adjustments here
        return Log4jLogEvent(loggerName, marker, null, level, msg, properties, throwable)
    }
}
