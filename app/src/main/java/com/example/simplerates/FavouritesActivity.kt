package com.example.simplerates

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FavouritesActivity : AppCompatActivity() {
    private var fav_list: ListView? = null
    private var fav_list_items: ArrayList<String> = ArrayList()
    private var fav_list_adapter: MyAdapter? = null

    private var source_field: EditText? = null
    private var target_field: EditText? = null
    private var add_btn: Button? = null

    private var shared_prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        initAddingFields()
        initBackBtn()
        initFavList()
    }

    private fun initFavList() {
        shared_prefs = getPreferences(MODE_PRIVATE)

        fav_list = findViewById(R.id.rates_list)
        fav_list_adapter = MyAdapter(this, fav_list_items)
        fav_list?.adapter = fav_list_adapter

        var rates = shared_prefs?.all
        rates?.forEach { (from, to) ->
            fetchNewRate(from, to as String)
        }
    }

    private fun initAddingFields() {
        source_field = findViewById(R.id.source_field)
        target_field = findViewById(R.id.target_field)

        add_btn = findViewById(R.id.fav_add_btn)
        add_btn?.setOnClickListener {
            if (arrayOf(source_field, target_field).any {
                    it?.text?.toString()?.trim()?.equals("")!!
                }) {
                Toast.makeText(this, getString(R.string.empty_field_error), Toast.LENGTH_LONG).show();

            } else {
                var from = source_field?.text?.toString()?.trim()?.uppercase()
                var to = target_field?.text?.toString()?.trim()?.uppercase()
                fetchNewRate(from, to)

                shared_prefs?.edit()?.putString(from, to)?.apply()
            }
        }
    }

    private fun initBackBtn() {
        var callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                var intent = Intent(this@FavouritesActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun fetchNewRate(from: String?, to: String?) {
        lifecycleScope.launch {
            var cur_res = FetchRate.fetchRate(from, to, "1")

            withContext(Dispatchers.Main) {
                var res_str: String
                if (cur_res == null) {
                    res_str = "${getString(R.string.fetch_error)}: ${from} -> ${to}"
                } else {
                    res_str = "1 ${from} = ${cur_res} ${to}"
                }
                fav_list_items.add(res_str)
                fav_list_adapter?.notifyDataSetChanged()
            }
        }
    }
}


class MyAdapter(private val context: Context, private val arrayList: ArrayList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
        var res_view = LayoutInflater.from(context).inflate(R.layout.fav_item, parent, false)
        var item_text: TextView? = res_view?.findViewById(R.id.fav_item_text)
        item_text?.text = arrayList[position]

        return res_view
    }
}