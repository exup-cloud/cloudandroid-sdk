package com.yjkj.chainup.new_version.bean

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.yjkj.chainup.bean.QuotesData

/**
 * @Author: Bertking
 * @Date：2019-07-22-11:18
 * @Description:
 */
class QuotesBeanTypeAdapter : TypeAdapter<QuotesData>() {
    override fun write(writer: JsonWriter?, value: QuotesData?) {
        writer?.beginObject()
        writer?.name("event_rep")?.value(value?.event_rep)
        writer?.name("channel")?.value(value?.channel)
        writer?.name("data")?.value(value?.data.toString())
        writer?.beginObject()
        writer?.name("open")?.value(value?.tick?.open)
        writer?.name("amount")?.value(value?.tick?.amount)
        writer?.name("close")?.value(value?.tick?.close)
        writer?.name("high")?.value(value?.tick?.high)
        writer?.name("low")?.value(value?.tick?.low)
        writer?.name("rose")?.value(value?.tick?.rose)
        writer?.name("vol")?.value(value?.tick?.vol)
        writer?.endObject()

        writer?.name("ts")?.value(value?.ts)
        writer?.name("status")?.value(value?.status)
        writer?.endObject()
    }

    override fun read(reader: JsonReader?): QuotesData {
        val bean = QuotesData()
        reader?.beginObject()
        while (reader?.hasNext() == true) {
            when (reader?.nextName()) {
                "event_rep" -> {
                    bean?.event_rep = reader.nextString()
                }

                "channel" -> {
                    bean?.channel = reader.nextString()
                }

                "data" -> {
                    bean?.data = reader.nextNull()
                }

                "tick" -> {
                    val tick = QuotesData.Tick()
                    reader.beginObject()
                    while (reader.hasNext()) {
                        when (reader.nextName()) {
                            "open" -> {
                                tick.open = reader.nextString()
                            }

                            "amount" -> {
                                tick.amount = reader.nextString()

                            }

                            "close" -> {
                                tick.close = reader.nextString()

                            }

                            "high" -> {
                                tick.high = reader.nextString()

                            }

                            "low" -> {
                                tick.low = reader.nextString()

                            }

                            "rose" -> {
                                tick.rose = reader.nextDouble()

                            }

                            "vol" -> {
                                tick.vol = reader.nextString()

                            }
                        }
                    }
                    bean.tick = tick
                    reader.endObject()
                }

                "ts" -> {
                    bean.ts = reader.nextLong()
                }

                "status" -> {
                    bean.status = reader.nextString()
                }
            }
        }
        reader?.endObject()
        return bean
    }
}
