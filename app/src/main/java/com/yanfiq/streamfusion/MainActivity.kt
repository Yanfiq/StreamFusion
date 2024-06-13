package com.yanfiq.streamfusion

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.yanfiq.streamfusion.data.retrofit.audius.AudiusEndpointUtil
import com.yanfiq.streamfusion.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.yanfiq.streamfusion.data.retrofit.spotify.SpotifyApi
import com.yanfiq.streamfusion.ui.home.HomeFragment
import com.yanfiq.streamfusion.ui.settings.ThemeUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        AudiusEndpointUtil.initialize(this)

        SpotifyApi.initialize(this)

        val themePref = ThemeUtils.getThemePreference(this)
        ThemeUtils.applyTheme(themePref)

        lifecycleScope.launch {
            AudiusEndpointUtil.fetchEndpoints(this@MainActivity)
            AudiusEndpointUtil.setUsedEndpoint(this@MainActivity)
        }

//        lifecycleScope.launch {
//            AudiusEndpointUtil.fetchEndpoints(this@MainActivity)
//            AudiusEndpointUtil.setUsedEndpoint(this@MainActivity)
//        }
    }
}