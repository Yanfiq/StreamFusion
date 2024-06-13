package com.yanfiq.streamfusion.ui.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yanfiq.streamfusion.R
import de.hdodenhof.circleimageview.CircleImageView

class AboutDetailsActivity : AppCompatActivity() {

    private lateinit var profilePicture: CircleImageView
    private lateinit var name: TextView
    private lateinit var nim: TextView
    private lateinit var motto: TextView
    private lateinit var instagram_btn: ImageView
    private lateinit var whatsapp_btn: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_details)

        profilePicture = findViewById(R.id.profile_picture)
        name = findViewById(R.id.name)
        nim = findViewById(R.id.nim)
        motto = findViewById(R.id.motto)
        instagram_btn = findViewById(R.id.instagram)
        whatsapp_btn = findViewById(R.id.whatsapp)

        name.text = intent.getStringExtra("DATA_NAME")
        nim.text = intent.getStringExtra("DATA_NIM")
        motto.text = intent.getStringExtra("DATA_MOTTO")

        profilePicture.setImageResource(intent.getIntExtra("DATA_IMG", -1))
        instagram_btn.setOnClickListener{
            intent= Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/${intent.getStringExtra("DATA_INSTAGRAM_USERNAME")}"))
            startActivity(intent)
        }
        whatsapp_btn.setOnClickListener{
            intent= Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${intent.getStringExtra("DATA_WHATSAPP_NUM")}"))
            startActivity(intent)
        }
    }
}