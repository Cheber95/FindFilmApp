package ru.chebertests.findfilmapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import kotlinx.android.synthetic.main.general_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import ru.chebertests.findfilmapp.R
import ru.chebertests.findfilmapp.databinding.GeneralFragmentBinding
import ru.chebertests.findfilmapp.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var bindingGeneral: GeneralFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        bindingGeneral = GeneralFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(bindingGeneral.containerGeneral.id, ListFilmFragment.newInstance())
                .commit()
            binding.container.addView(bindingGeneral.root)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_menu_films_fragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_general, ListFilmFragment.newInstance())
                        .commit()
                }
                R.id.bottom_menu_profile_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_general, SettingsFragment.newInstance())
                        .commit()
                }
            }
            true
        }

    }
}
