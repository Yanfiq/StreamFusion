package com.yanfiq.streamfusion.ui.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanfiq.streamfusion.R

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<AppCompatButton>(R.id.button_ryan).setOnClickListener{
            val intent = Intent(this, AboutDetailsActivity::class.java)
            intent.putExtra("DATA_NAME", "Muhammad Ryan Fikri Fakhrezi")
            intent.putExtra("DATA_NIM", "L0122114")
            intent.putExtra("DATA_MOTTO", "Ajinomotto")
            intent.putExtra("DATA_INSTAGRAM_USERNAME", "12yan.fikri_")
            intent.putExtra("DATA_WHATSAPP_NUM", "6287734341945")
            intent.putExtra("DATA_IMG", R.drawable.wak)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.button_udin).setOnClickListener{
            val intent = Intent(this, AboutDetailsActivity::class.java)
            intent.putExtra("DATA_NAME", "Muhammad Saifuddin Eka Nugraha")
            intent.putExtra("DATA_NIM", "L0122115")
            intent.putExtra("DATA_MOTTO", "Gitar dipetik bass ku betot, hai nona cantik bass ku betot")
            intent.putExtra("DATA_INSTAGRAM_USERNAME", "m_saifuddin711")
            intent.putExtra("DATA_WHATSAPP_NUM", "6281909561200")
            intent.putExtra("DATA_IMG", R.drawable.udin)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.button_danish).setOnClickListener{
            val intent = Intent(this, AboutDetailsActivity::class.java)
            intent.putExtra("DATA_NAME", "Muhammad Danish Dhiyaulhaq")
            intent.putExtra("DATA_NIM", "L0122107")
            intent.putExtra("DATA_MOTTO", "Meskipun mukaku kayak titid, tapi cintaku padamu unlimited")
            intent.putExtra("DATA_INSTAGRAM_USERNAME", "danshd5")
            intent.putExtra("DATA_WHATSAPP_NUM", "6281358328255")
            intent.putExtra("DATA_IMG", R.drawable.danis)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.button_naila).setOnClickListener{
            val intent = Intent(this, AboutDetailsActivity::class.java)
            intent.putExtra("DATA_NAME", "Naila Iffah Aulia")
            intent.putExtra("DATA_NIM", "L0122119")
            intent.putExtra("DATA_MOTTO", "Hidup emang kadang kiding.")
            intent.putExtra("DATA_INSTAGRAM_USERNAME", "")
            intent.putExtra("DATA_WHATSAPP_NUM", "")
            intent.putExtra("DATA_IMG", R.drawable.naila)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.button_arva).setOnClickListener{
            val intent = Intent(this, AboutDetailsActivity::class.java)
            intent.putExtra("DATA_NAME", "Muhammad Arva Zaky W P")
            intent.putExtra("DATA_NIM", "L0122103")
            intent.putExtra("DATA_MOTTO", "Nangka Buruk Nangka Biyek, Ngarampuyuk Hayang Diewe")
            intent.putExtra("DATA_INSTAGRAM_USERNAME", "")
            intent.putExtra("DATA_WHATSAPP_NUM", "")
            intent.putExtra("DATA_IMG", R.drawable.arva)
            startActivity(intent)
        }

        findViewById<AppCompatButton>(R.id.aboutProject).setOnClickListener{
            val intent= Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yanfiq/StreamFusion"))
            startActivity(intent)
        }
    }
}