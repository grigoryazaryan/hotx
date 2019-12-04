package app.hotx.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.hotx.R
import kotlinx.android.synthetic.main.activity_ad.*

class AdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        title = ""

        web_view.settings.javaScriptEnabled = true
        web_view.loadUrl("file:///android_asset/juicyads.html")
    }
}
