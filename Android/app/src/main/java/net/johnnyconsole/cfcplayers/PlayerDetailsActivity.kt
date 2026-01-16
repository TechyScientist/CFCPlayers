package net.johnnyconsole.cfcplayers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.johnnyconsole.cfcplayers.databinding.ActivityPlayerDetailsBinding
import net.johnnyconsole.cfcplayers.objects.Player
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerDetailsActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityPlayerDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        player = intent.getSerializableExtra("profile") as Player
        binding.title.text = getString(R.string.PlayerDetails, player.cfcId, player.name)

        binding.cfcID.text = getString(R.string.placeholderInt, player.cfcId)
        binding.name.text = getString(R.string.placeholderString, player.name)

        if(player.expiry.isEmpty() || player.expiry.isBlank()) {
            binding.expiry.text = getString(R.string.noExpiry)
        }
        else if(player.expiry.isNotEmpty() && player.expiry.isNotBlank()) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            val expiry = formatter.parse(player.expiry)
            val today = formatter.parse(formatter.format(Date()))

            if(expiry != null && today != null && expiry.year >= today.year + 5)  {
                binding.expiry.text = getString(R.string.noExpiry)
            }
            else if(expiry != null && today != null && expiry <= today) {
                binding.expiry.text = getString(R.string.expired, player.expiry)
                binding.expiry.setTextColor(getColor(R.color.CFCRed))
            }
            else {
                binding.expiry.text = getString(R.string.placeholderString, player.expiry)
            }
        }

        binding.cityProv.text = getString(R.string.placeholderString, player.cityProvince)

        if(player.fideID > 0) {
            binding.fideID.text = getString(R.string.placeholderInt, player.fideID)
        }
        else {
            binding.fideID.text = getString(R.string.unregistered)
            binding.fideID.setTextColor(getColor(R.color.CFCRed))
        }

        if(player.regular > 0) {
             binding.regular.text = getString(R.string.placeholderInt, player.regular)
        }
        else {
             binding.regular.text = getString(R.string.unrated)
             binding.regular.setTextColor(getColor(R.color.CFCRed))
        }

        if(player.quick > 0) {
            binding.quick.text = getString(R.string.placeholderInt, player.quick)
        }
        else {
            binding.quick.text = getString(R.string.unrated)
            binding.quick.setTextColor(getColor(R.color.CFCRed))
        }

        binding.updated.text = getString(R.string.placeholderString, player.updated)

        binding.cfcID.setOnClickListener {_ ->
           val url = if(Locale.getDefault().language.contains("en")) {
                "https://chess.ca/en/ratings/p/?id=${player.cfcId}"
            }
            else {
                "https://chess.ca/fr/ratings/p/?id=${player.cfcId}"
            }

            val intent = Intent(this, ViewPlayerWebProfileActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("profile", getString(R.string.cfc))
            startActivity(intent)
        }

        binding.fideID.setOnClickListener {_ ->
            if(player.fideID == 0) {
                val msgbox = AlertDialog.Builder(this).setNeutralButton(R.string.dismiss, null)
                    .setTitle(R.string.notFideRegisteredTitle)
                    .setMessage(R.string.notFideRegisteredMessage)
                    .create()
                msgbox.show()

                (msgbox.getButton(AlertDialog.BUTTON_NEUTRAL).layoutParams as LinearLayout.LayoutParams).width = LinearLayout.LayoutParams.MATCH_PARENT
            }
            else {
                val url = "https://ratings.fide.com/profile/${player.fideID}"
                val intent = Intent(this, ViewPlayerWebProfileActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("profile", "FIDE")
                startActivity(intent)
            }
        }
    }

}