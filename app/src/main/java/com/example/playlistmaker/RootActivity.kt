package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class RootActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRootBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val hostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = hostFragment.navController

        appBarConfig = AppBarConfiguration(
            setOf(
                R.id.mediaLibraryFragment,
                R.id.searchFragment,
                R.id.settingsFragment
            )
        )

        val bottomNav: BottomNavigationView = binding.bottomNav
        val bottomDivider = binding.bottomDivider
        bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isPlayer = destination.id == R.id.playerFragment
            binding.bottomNav.visibility  = if (isPlayer) View.GONE else View.VISIBLE
            binding.bottomDivider.visibility = if (isPlayer) View.GONE else View.VISIBLE
            val navHostView = findViewById<View>(R.id.nav_host_fragment)
            val params = navHostView.layoutParams as CoordinatorLayout.LayoutParams
            val marginDp = if (isPlayer) 0 else 57
            params.bottomMargin = (marginDp * resources.displayMetrics.density).toInt()
            navHostView.layoutParams = params
        }
    }
}