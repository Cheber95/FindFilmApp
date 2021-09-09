package ru.chebertests.findfilmapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        bindingGeneral.bottomNavigation.also { bottomNavigation ->
            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_menu_films_fragment -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container_general, ListFilmFragment.newInstance())
                            .commitNow()
                    }
                    R.id.bottom_menu_profile_settings -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container_general, SettingsFragment.newInstance())
                            .commitNow()
                    }
                }
                true
            }
        }


    }
}
