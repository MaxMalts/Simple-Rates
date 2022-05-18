package com.example.simplerates

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL


class FetchRate {
    companion object {
        suspend fun fetchRate(from: String?, to: String?, amount: String?): String? {
            var key: String = "tGyBprkYuHRlGNGlNZ144Fiy9f9rh2Qn"
            var url: String = "https://api.apilayer.com/exchangerates_data/convert?to=${to}&from=${from}&amount=${amount}"

            var result = withContext(Dispatchers.IO) {
                try {
                    var conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                    conn.setRequestProperty("apikey", key)
                    conn.connect()

                    val status: Int = conn.responseCode
                    if (status < 200 || status > 299) {
                        null
                    } else {
                        var input_stream = conn.inputStream
                        var input_json = JSONObject(convertInputStreamToString(input_stream))
                        var res_val = input_json.getDouble("result").toString()
                        res_val
                    }

                } catch (err: Throwable) {
                    null
                }
            }

            return result
        }

        private fun convertInputStreamToString(inputStream: InputStream): String {
            var bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))

            var line: String? = bufferedReader?.readLine()
            var result: String = ""

            while (line != null) {
                result += line
                line = bufferedReader?.readLine()
            }

            inputStream.close()
            return result
        }
    }
}