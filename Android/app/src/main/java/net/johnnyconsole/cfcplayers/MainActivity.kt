package net.johnnyconsole.cfcplayers

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.tabs.TabLayout
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
    var searched: Boolean = false

    inner class PlayerListAdapter(ctx: Context): ArrayAdapter<String>(ctx,
        android.R.layout.simple_list_item_1) {

        override fun getCount(): Int {
            return if(!searched) 0
                   else if(players.size == 0) 1
                   else players.size
        }

        override fun getItem(position: Int): String {
            return if(searched && players.size == 0) getString(R.string.noPlayers)
                   else "${players[position].cfcId}: ${players[position].name}"

        }
    }

    inner class PlayerSearchTask: AsyncTask<String, Void, JSONObject>() {

        override fun doInBackground(vararg args: String?): JSONObject {
            val url = URL(args[0])
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
            return JSONObject(sb.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tlSearchField.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.etSearchText.inputType = if (tab!!.position == 0) {
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

        binding.lvPlayerList.adapter = PlayerListAdapter(this)
        binding.lvPlayerList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                if(players.isNotEmpty()) {
                    val intent = Intent(this, PlayerDetailsActivity::class.java)
                    intent.putExtra("profile", players[position])
                    startActivity(intent)
                }
            }
    }

    fun onSearchClicked(view: View) {
        if(binding.etSearchText.text.isEmpty()) {
            val msgbox = AlertDialog.Builder(this).setNeutralButton(R.string.dismiss, null)
                .setTitle(R.string.searchErrorTitle)
                .setMessage(R.string.searchError)
                .create()
            msgbox.show()

            (msgbox.getButton(AlertDialog.BUTTON_NEUTRAL).layoutParams as LinearLayout.LayoutParams).width = LinearLayout.LayoutParams.MATCH_PARENT
        }
        else {
            searched = true
            players.clear()
            val url = when(binding.tlSearchField.selectedTabPosition) {
                0 -> {
                    "https://server.chess.ca/api/player/v1/${binding.etSearchText.text}"
                }
                2 -> {
                    "https://server.chess.ca/api/player/v1/find?first=${binding.etSearchText.text}*&last="
                }
                else -> {
                    "https://server.chess.ca/api/player/v1/find?first=&last=${binding.etSearchText.text}*"
                }

            }
            val json = PlayerSearchTask().execute(url).get()
            if(json.has("player")) {
                val player = json.getJSONObject("player")
                if(player.has("name_first")) {
                    players.add(
                        Player(player.getInt("cfc_id"),
                            player.getString("name_last"),
                            player.getString("name_first"),
                            player.getString("cfc_expiry"),
                            player.getString("addr_city"),
                            player.getString("addr_province"),
                            player.getInt("fide_id"),
                            player.getInt("regular_rating"),
                            player.getInt("quick_rating"),
                            json.getString("updated")
                        ))
                }
            } else {
                val playersArray = json.getJSONArray("players")
                for (i in 0 until playersArray.length()) {
                    val player = playersArray.getJSONObject(i)
                    players.add(
                        Player(player.getInt("cfc_id"),
                            player.getString("name_last"),
                            player.getString("name_first"),
                            player.getString("cfc_expiry"),
                            player.getString("addr_city"),
                            player.getString("addr_province"),
                            player.getInt("fide_id"),
                            player.getInt("regular_rating"),
                            player.getInt("quick_rating"),
                            json.getString("updated")
                        ))
                }
            }
        }

        (binding.lvPlayerList.adapter as PlayerListAdapter).notifyDataSetChanged()
    }
}