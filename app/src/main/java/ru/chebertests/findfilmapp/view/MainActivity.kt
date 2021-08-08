package ru.chebertests.findfilmapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListFilmFragment.newInstance())
                .commitNow()
        }

        val bottomNav = binding.bottomNavigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_films_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, ListFilmFragment.newInstance())
                        .commitNow()
                }
                R.id.bottom_menu_profile_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance())
                        .commitNow()
                }
            }
            true
        }

    }
}
