package ru.samtakoy.listtest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val navController by lazy {
        Navigation.findNavController(
            this@MainActivity,
            R.id.nav_host_fragment_container
        )
    }
    private val appBarConfiguration by lazy {
        AppBarConfiguration.Builder(R.id.listFragment, R.id.aboutFragment)
            .setFallbackOnNavigateUpListener { onNavigateUp() }
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        setupBottomNavigationBar()
    }


    private fun setupActionBar() {
        //setSupportActionBar(main_toolbar)
        getSupportActionBar()!!.setHomeButtonEnabled(true)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupBottomNavigationBar() {
        bottom_navigation.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}