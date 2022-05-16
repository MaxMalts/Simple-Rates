package com.example.simplerates

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {
    private var source_field: EditText? = null
    private var target_field: EditText? = null
    private var amount_field: EditText? = null

    private var main_btn: Button? = null
    private var result_label: TextView? = null
    private var favourites_link: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        source_field = findViewById(R.id.source_field)
        target_field = findViewById(R.id.target_field)
        amount_field = findViewById(R.id.amount_field)
        main_btn = findViewById(R.id.main_btn)
        result_label = findViewById(R.id.result_info)
        favourites_link = findViewById(R.id.favourites_link)

        main_btn?.setOnClickListener {
            if (arrayOf(source_field, target_field, amount_field).any {
                    it?.text?.toString()?.trim()?.equals("")!!
            }) {
                Toast.makeText(this, getString(R.string.empty_field_error), Toast.LENGTH_LONG).show();
            } else {
                GlobalScope.launch {
                    var result = fetchRate()
                    withContext(Dispatchers.Main) {
                        if (result == null) {
                            result_label?.text = getString(R.string.fetch_error)
                        } else if (result_label != null) {
                            var sourceCurrency = source_field?.text?.toString()?.trim()?.uppercase()
                            var targetCurrency = target_field?.text?.toString()?.trim()?.uppercase()
                            var res_label =
                                "${amount_field?.text?.toString()} ${sourceCurrency} = ${result} ${targetCurrency}"
                            result_label?.text = res_label
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchRate(): String? {
        var from: String = source_field?.text.toString()
        var to: String = target_field?.text.toString()
        var amount: String = amount_field?.text.toString()
        var key: String = "tGyBprkYuHRlGNGlNZ144Fiy9f9rh2Qn"
        var url: String = "https://api.apilayer.com/exchangerates_data/convert?to=${to}&from=${from}&amount=${amount}"

        var result = withContext(Dispatchers.IO) {
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