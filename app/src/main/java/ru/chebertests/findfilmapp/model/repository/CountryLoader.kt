package ru.chebertests.findfilmapp.model.repository

import ru.chebertests.findfilmapp.model.Callback
import ru.chebertests.findfilmapp.model.dto.CountriesDTO

interface CountryLoader {
    fun getCountry(callback: Callback<CountriesDTO>)
}