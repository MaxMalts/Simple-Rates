package com.example.simplerates

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var source_field: EditText? = null
    private var target_field: EditText? = null
    private var amount_field: EditText? = null

    private var main_btn: Button? = null
    private var result_info: TextView? = null
    private var favourites_link: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        source_field = findViewById(R.id.source_field)
        target_field = findViewById(R.id.target_field)
        amount_field = findViewById(R.id.amount_field)
        main_btn = findViewById(R.id.main_btn)
        result_info = findViewById(R.id.result_info)
        favourites_link = findViewById(R.id.favourites_link)

        main_btn?.setOnClickListener {
            if (arrayOf(source_field, target_field, amount_field).all {
                    it?.text?.toString()?.trim()?.equals("")!!
            }) {
                Toast.makeText(this, getString(R.string.empty_field_error), Toast.LENGTH_LONG).show();
            } else {


                GlobalScope.launch {
                    var result = fetchRate()
                    Log.d(null, result)
                }
            }
        }
    }

    private suspend fun fetchRate(): String {
        var from: String = source_field?.text.toString()
        var to: String = target_field?.text.toString()
        var amount: String = amount_field?.text.toString()
        var key: String = "tGyBprkYuHRlGNGlNZ144Fiy9f9rh2Qn"
        var url: String = "https://api.apilayer.com/exchangerates_data/convert?to=${to}&from=${from}&amount=${amount}"

        var result = withContext(Dispatchers.IO) {

            var inputStream: InputStream
            var url: URL = URL(url)

            var conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("apikey", key)
            conn.connect()

            inputStream = conn.inputStream
            if (inputStream != null) {
                convertInputStreamToString(inputStream)
            } else {
                "failed to fetch rate"
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