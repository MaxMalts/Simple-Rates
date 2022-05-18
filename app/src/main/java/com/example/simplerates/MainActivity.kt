package com.example.simplerates

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
                lifecycleScope.launch {
                    var result = FetchRate.fetchRate(
                        source_field?.text?.toString(),
                        target_field?.text?.toString(),
                        amount_field?.text?.toString()
                    )

                    withContext(Dispatchers.Main) {
                        if (result == null) {
                            result_label?.text = getString(R.string.fetch_error)
                        } else if (result_label != null) {
                            var source_currency = source_field?.text?.toString()?.trim()?.uppercase()
                            var target_currency = target_field?.text?.toString()?.trim()?.uppercase()
                            var res_label =
                                "${amount_field?.text?.toString()} ${source_currency} = ${result} ${target_currency}"
                            result_label?.text = res_label
                        }
                    }
                }
            }
        }

        favourites_link?.setOnClickListener {
            var intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }
    }
}