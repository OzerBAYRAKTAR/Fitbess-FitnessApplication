package com.bayraktar.healthybackandneck.ui.FirstActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bayraktar.healthybackandneck.data.JetpackDataStore.DataStoreManage
import com.bayraktar.healthybackandneck.databinding.ActivityMainBinding
import com.bayraktar.healthybackandneck.ui.HomeActivity
import com.bayraktar.healthybackandneck.utils.fitContentToSystemBars
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(com.bayraktar.healthybackandneck.utils.LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fitContentToSystemBars(binding.root)
        binding.beginTraining.setOnClickListener {
            binding.beginTraining.isEnabled = false
            lifecycleScope.launch {
                DataStoreManage.getInstance(applicationContext).saveLog(true)
                startActivity(
                    Intent(this@MainActivity, HomeActivity::class.java)
                        .putExtra(HomeActivity.EXTRA_SHOW_FIRST_RUN_AD_FREE_OFFER, true)
                )
                finish()
            }
        }
    }
}
