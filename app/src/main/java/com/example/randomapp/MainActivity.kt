package com.example.randomapp

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.randomapp.AppAdapter.OnSwitchClickListener
import com.google.android.material.button.MaterialButton
import java.text.Normalizer
import java.util.Locale
import java.util.Random

class MainActivity : AppCompatActivity() {
    private var apps: MutableList<App>? = null
    private var packageManager: PackageManager? = null
    private var adapter: AppAdapter? = null
    private var sharePreference: SharePreference? = null

    override fun onDestroy() {
        apps!!.clear()
        apps = null
        packageManager = null
        adapter = null
        sharePreference = null
        super.onDestroy()
    }

    override fun onStop() {
        val i = apps!!.size.toShort()
        val nameApps: MutableList<String?> = ArrayList()
        for (j in 0 until i) {
            if (apps!![j].isChecked) {
                nameApps.add(apps!![j].info.packageName)
            }
        }
        sharePreference!!.putSwitchStatement("state", nameApps)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        packageManager = getPackageManager()
        val btnRandom = findViewById<MaterialButton>(R.id.btnRandom)
        val search = findViewById<EditText>(R.id.ed_search)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        sharePreference = SharePreference(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppAdapter(this, object : OnSwitchClickListener {
            override fun onChangeChecked(name: String?, state: Boolean) {
                for (x in apps!!) {
                    if (packageManager?.getApplicationLabel(x.info).toString() == name) {
                        x.isChecked = state
                        break
                    }
                }
            }
        })
        if (sharePreference!!.getBoolean("first")) {
            getAllApp2(sharePreference!!.getSwitchStatement("state"))
        } else {
            sharePreference!!.putBoonleanValue("first", true)
            allApp
        }
        adapter!!.setList(apps)
        recyclerView.adapter = adapter
        btnRandom.setOnClickListener { openApp() }
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                filterApp(charSequence.toString().trim { it <= ' ' })
            }

            override fun afterTextChanged(editable: Editable) {}
        })
    }

    private fun openApp() {
        val packageName: MutableList<String?> = ArrayList()
        val i = apps!!.size.toShort()
        for (j in 0 until i) {
            if (apps!![j].isChecked) {
                packageName.add(apps!![j].info.packageName)
            }
        }
        val size = packageName.size.toShort()
        if (size > 0) {
            val random = Random()
            val ran = random.nextInt(size.toInt()).toShort()
            val namePack = packageName[ran.toInt()]
            val intent = packageManager!!.getLaunchIntentForPackage(namePack!!)
            if (intent != null) {
                sharePreference!!.putSwitchStatement("state", packageName)
                Toast.makeText(this, namePack, Toast.LENGTH_SHORT).show()
                finishAndRemoveTask()
                startActivity(intent)
            } else {
                Toast.makeText(this, "Can't open app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterApp(txt: String) {
        if (txt.isEmpty()) {
            adapter!!.setList(apps)
        } else {
            val data: MutableList<App> = ArrayList()
            for (x in apps!!) {
                if (normalizeString(packageManager!!.getApplicationLabel(x.info).toString().lowercase(Locale.getDefault()).trim { it <= ' ' }).contains(normalizeString(txt.lowercase(Locale.getDefault()).trim { it <= ' ' }))) {
                    data.add(x)
                }
            }
            adapter!!.setList(data)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllApp2(state: MutableList<String>?) {
        if (state == null) {
            allApp
            return
        }
        apps = ArrayList()
        val applicationInfos = packageManager!!.getInstalledApplications(PackageManager.GET_META_DATA)
        var check = false
        for (x in applicationInfos) {
            if (x.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                for (i in state.indices) {
                    if (state[i] == x.packageName) {
                        apps?.add(App(x, true))
                        check = true
                        state.removeAt(i)
                        break
                    }
                }
                if (!check) {
                    apps?.add(App(x, false))
                }
                check = false
            }
        }
        state.clear()
        applicationInfos.clear()
    }

    @get:SuppressLint("QueryPermissionsNeeded")
    private val allApp: Unit
        get() {
            apps = ArrayList()
            val applicationInfos = packageManager!!.getInstalledApplications(PackageManager.GET_META_DATA)
            for (x in applicationInfos) {
                if (x.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    apps?.add(App(x, false))
                }
            }
            applicationInfos.clear()
        }

    private fun normalizeString(input: String): String {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replace("\\p{M}".toRegex(), "")
    }
}