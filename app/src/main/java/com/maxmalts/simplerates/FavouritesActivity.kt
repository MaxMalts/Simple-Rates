package com.maxmalts.simplerates

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


data class Rate(val source_currency: String?, val target_currency: String?, val target_val: String?)

class FavouritesActivity : AppCompatActivity() {
    private var fav_list: ListView? = null
    private var fav_list_items: ArrayList<Rate> = ArrayList()
    private var fav_list_adapter: FavListAdapter? = null

    private var source_field: EditText? = null
    private var target_field: EditText? = null
    private var add_btn: Button? = null

    private var shared_prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        initAddingFields()
        initFavList()
    }

    private fun initFavList() {
        shared_prefs = getPreferences(MODE_PRIVATE)

        fav_list = findViewById(R.id.rates_list)
        fav_list_adapter = FavListAdapter(this, fav_list_items, ::onDelete)
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

                if (fav_list_items.any { it.source_currency == from && it.target_currency == to}) {
                    Toast.makeText(this, getString(R.string.fav_rate_already_exists), Toast.LENGTH_SHORT).show()
                } else {
                    fetchNewRate(from, to)
                    shared_prefs?.edit()?.putString(from, to)?.apply()
                }
            }
        }
    }

    private fun fetchNewRate(from: String?, to: String?) {
        lifecycleScope.launch {
            var cur_res = FetchRate.fetchRate(from, to, "1")

            withContext(Dispatchers.Main) {
                fav_list_items.add(Rate(from, to, cur_res))
                fav_list_adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun onDelete(ind: Int) {
        var source_currency = fav_list_items[ind].source_currency
        shared_prefs?.edit()?.remove(source_currency)?.apply()
        fav_list_items.removeAt(ind)
        fav_list_adapter?.notifyDataSetChanged()
    }
}


class FavListAdapter(
    private val context: Context,
    private val rates: ArrayList<Rate>,
    private val deleteCallback: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int {
        return rates.size
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

        var rate = rates[position]
        var res_str: String
        if (rate.target_val == null) {
            res_str = "${context.getString(R.string.fetch_error)}: ${rate.source_currency} -> ${rate.target_currency}"
        } else {
            res_str = "1 ${rate.source_currency} = ${rate.target_val} ${rate.target_currency}"
        }
        item_text?.text = res_str

        var delete_btn: ImageButton? = res_view?.findViewById(R.id.delete_btn)
        delete_btn?.setOnClickListener {
            deleteCallback(position)
        }

        return res_view
    }
}