package net.johnnyconsole.cfcplayers

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.johnnyconsole.cfcplayers.databinding.ActivityMainBinding
import net.johnnyconsole.cfcplayers.objects.Player
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var players = ArrayList<Player>()

    inner class PlayerListAdapter() : ArrayAdapter<String>(
        this,
        android.R.layout.simple_list_item_1
    ) {

        override fun getCount(): Int {
            return if (!binding.btSearch.isEnabled) 0
            else if (players.isEmpty()) 1
            else players.size
        }

        override fun getItem(position: Int): String {
            return if (binding.btSearch.isEnabled && players.isEmpty()) getString(R.string.noPlayers)
            else "${players[position].cfcId}: ${players[position].name}"

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))

        with(binding) {
            setContentView(root)
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }

            tlSearchField.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    etSearchText.inputType = if (tab!!.position == 0) {
                        InputType.TYPE_CLASS_NUMBER
                    } else {
                        InputType.TYPE_CLASS_TEXT
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    // Intentionally Blank
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // Intentionally Blank
                }

            })

            lvPlayerList.adapter = PlayerListAdapter()
            lvPlayerList.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    if (players.isNotEmpty()) {
                        val intent = Intent(this@MainActivity, PlayerDetailsActivity::class.java)
                        intent.putExtra("profile", players[position])
                        startActivity(intent)
                    }
                }

            btSearch.setOnClickListener { _ ->
                lifecycleScope.launch {
                    with(binding) {

                        if (etSearchText.text.isEmpty()) {
                            val msgbox =
                                AlertDialog.Builder(this@MainActivity)
                                    .setNeutralButton(R.string.dismiss, null)
                                    .setTitle(R.string.searchErrorTitle)
                                    .setMessage(R.string.searchError)
                                    .create()
                            msgbox.show()

                            (msgbox.getButton(AlertDialog.BUTTON_NEUTRAL).layoutParams as LinearLayout.LayoutParams).width =
                                LinearLayout.LayoutParams.MATCH_PARENT
                        } else {
                            indicator.visibility = VISIBLE
                            btSearch.isEnabled = false
                            players.clear()
                            val url = when (tlSearchField.selectedTabPosition) {
                                0 -> {
                                    "https://server.chess.ca/api/player/v1/${etSearchText.text}"
                                }

                                2 -> {
                                    "https://server.chess.ca/api/player/v1/find?first=${etSearchText.text}*&last="
                                }

                                else -> {
                                    "https://server.chess.ca/api/player/v1/find?first=&last=${etSearchText.text}*"
                                }

                            }
                            val json = playerSearch(URL(url))

                            if (json.has("player")) {
                                val player = json.getJSONObject("player")
                                if (player.has("name_first")) {
                                    players.add(
                                        Player(
                                            player.getInt("cfc_id"),
                                            player.getString("name_last"),
                                            player.getString("name_first"),
                                            player.getString("cfc_expiry"),
                                            player.getString("addr_city"),
                                            player.getString("addr_province"),
                                            player.getInt("fide_id"),
                                            player.getInt("regular_rating"),
                                            player.getInt("quick_rating"),
                                            json.getString("updated")
                                        )
                                    )
                                }
                            } else {
                                val playersArray = json.getJSONArray("players")
                                for (i in 0 until playersArray.length()) {
                                    val player = playersArray.getJSONObject(i)
                                    players.add(
                                        Player(
                                            player.getInt("cfc_id"),
                                            player.getString("name_last"),
                                            player.getString("name_first"),
                                            player.getString("cfc_expiry"),
                                            player.getString("addr_city"),
                                            player.getString("addr_province"),
                                            player.getInt("fide_id"),
                                            player.getInt("regular_rating"),
                                            player.getInt("quick_rating"),
                                            json.getString("updated")
                                        )
                                    )
                                }
                            }
                        }

                        (lvPlayerList.adapter as PlayerListAdapter).notifyDataSetChanged()
                        indicator.visibility = GONE
                        btSearch.isEnabled = true
                    }
                }
            }
        }
    }

    private suspend fun playerSearch(url: URL): JSONObject = withContext(Dispatchers.IO) {
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.requestMethod = "GET"
        urlConnection.doInput = true
        urlConnection.connect()

        val br = BufferedReader(InputStreamReader(url.openStream()))
        val sb = StringBuilder()
        var line: String?
        while (br.readLine().also { line = it } != null) {
            sb.append(line)
        }
        br.close()
        JSONObject(sb.toString())
    }
}