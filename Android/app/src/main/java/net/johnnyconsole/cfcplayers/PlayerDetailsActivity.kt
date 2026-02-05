package net.johnnyconsole.cfcplayers

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import net.johnnyconsole.cfcplayers.databinding.ActivityPlayerDetailsBinding
import net.johnnyconsole.cfcplayers.objects.Player
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PlayerDetailsActivity : AppCompatActivity() {
    private lateinit var player: Player
    private lateinit var binding: ActivityPlayerDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerDetailsBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)
            enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT))

            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
            player = intent.getSerializableExtra("profile") as Player
            title.text = getString(R.string.PlayerDetails, player.cfcId, player.name)

            cfcID.text = getString(R.string.placeholderInt, player.cfcId)
            name.text = getString(R.string.placeholderString, player.name)

            if (player.expiry.isEmpty() || player.expiry.isBlank()) {
                expiry.text = getString(R.string.noExpiry)
            } else if (player.expiry.isNotEmpty() && player.expiry.isNotBlank()) {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA)
                val expiryDate = LocalDate.parse(player.expiry, formatter)
                val today = LocalDate.now()

                if (expiryDate != null && today != null && expiryDate.year >= today.year + 5) {
                    expiry.text = getString(R.string.noExpiry)
                } else if (expiryDate != null && today != null && expiryDate <= today) {
                    expiry.text = getString(R.string.expired, player.expiry)
                    expiry.setTextColor(getColor(R.color.CFCRed))
                } else {
                    expiry.text = getString(R.string.placeholderString, player.expiry)
                }
            }

            cityProv.text = getString(R.string.placeholderString, player.cityProvince)

            if (player.fideID > 0) {
                fideID.text = getString(R.string.placeholderInt, player.fideID)
            } else {
                fideID.text = getString(R.string.unregistered)
                fideID.setTextColor(getColor(R.color.CFCRed))
            }

            if (player.regular > 0) {
                regular.text = getString(R.string.placeholderInt, player.regular)
            } else {
                regular.text = getString(R.string.unrated)
                regular.setTextColor(getColor(R.color.CFCRed))
            }

            if (player.quick > 0) {
                quick.text = getString(R.string.placeholderInt, player.quick)
            } else {
                quick.text = getString(R.string.unrated)
                quick.setTextColor(getColor(R.color.CFCRed))
            }

            updated.text = getString(R.string.placeholderString, player.updated)

            cfcID.setOnClickListener { _ ->
                val url = if (Locale.getDefault().language.contains("en")) {
                    "https://chess.ca/en/ratings/p/?id=${player.cfcId}"
                } else {
                    "https://chess.ca/fr/ratings/p/?id=${player.cfcId}"
                }

                val intent = Intent(this@PlayerDetailsActivity, ViewPlayerWebProfileActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("profile", getString(R.string.cfc))
                startActivity(intent)
            }

            fideID.setOnClickListener { _ ->
                if (player.fideID == 0) {
                    val msgbox = AlertDialog.Builder(this@PlayerDetailsActivity).setNeutralButton(R.string.dismiss, null)
                        .setTitle(R.string.notFideRegisteredTitle)
                        .setMessage(R.string.notFideRegisteredMessage)
                        .create()
                    msgbox.show()

                    (msgbox.getButton(AlertDialog.BUTTON_NEUTRAL).layoutParams as LinearLayout.LayoutParams).width =
                        LinearLayout.LayoutParams.MATCH_PARENT
                } else {
                    val url = "https://ratings.fide.com/profile/${player.fideID}"
                    val intent = Intent(this@PlayerDetailsActivity, ViewPlayerWebProfileActivity::class.java)
                    intent.putExtra("url", url)
                    intent.putExtra("profile", "FIDE")
                    startActivity(intent)
                }
            }
        }
    }
}