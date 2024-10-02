package io.axept.samplekotlin.screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.axept.android.library.AxeptioService
import io.axept.samplekotlin.repository.COOKIE_FIELDS
import io.axept.samplekotlin.repository.SharedPreferencesRepositoryImpl
import io.axept.samplekotlin.repository.TCFFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel private constructor(
    private val prefsRepo: io.axept.samplekotlin.repository.SharedPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PreferenceStateUI(fields = mapOf()))
    val state = _state.asStateFlow()

    private val _adState = MutableStateFlow(AddStateUI())
    val adState = _adState.asStateFlow()

    init {
        prefsRepo.listen()
    }

    fun setTargetService(service: AxeptioService) {
        viewModelScope.launch {
            observePreferences(service)

            when (service) {
                AxeptioService.PUBLISHERS_TCF -> {
                    TCFFields.entries.map { tcfField ->
                        val value = prefsRepo.get(tcfField.key, tcfField.type)
                        updatePreferenceFieldValue(tcfField.key, value)
                    }
                }

                AxeptioService.BRANDS -> {
                    COOKIE_FIELDS.entries.map { cookie ->
                        val value = prefsRepo.get(cookie.key, String)
                        updatePreferenceFieldValue(cookie.key, value)
                    }
                }
            }
        }
    }

    private suspend fun observePreferences(service: AxeptioService) {
        prefsRepo.hasUpdated.collect { key ->
            key ?: return@collect
            if (service == AxeptioService.PUBLISHERS_TCF
                && TCFFields.entries.map { it.key }.contains(key)
            ) {
                TCFFields.entries.firstOrNull { it.key == key }?.let { field ->
                    val value = prefsRepo.get(key, field.type)
                    updatePreferenceFieldValue(key, value)
                }
            } else {
                if (service == AxeptioService.BRANDS
                    && COOKIE_FIELDS.entries.map { it.key }.contains(key)
                ) {
                    COOKIE_FIELDS.entries.firstOrNull { it.key == key }?.let { _ ->
                        val value = prefsRepo.get(key, String)
                        updatePreferenceFieldValue(key, value)
                    }
                }
            }
        }
    }

    private fun updatePreferenceFieldValue(key: String, value: String) {
        var trimmedValue = value
        trimmedValue.apply {
            if (this.length > 500) trimmedValue = substring(0, 300) // Trim value for performance
        }
        val map = state.value.fields.toMutableMap()
        if (trimmedValue.isEmpty()) {
            map.remove(key)
        } else {
            map[key] = trimmedValue
        }
        _state.update {
            it.copy(fields = map)
        }
    }

    fun setAdStatus(status: AddStateUI.Status) {
        _adState.update { AddStateUI(status = status) }
    }

    override fun onCleared() {
        prefsRepo.clearListener()
        super.onCleared()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        class Factory(private val application: Application) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val prefsRepo = SharedPreferencesRepositoryImpl(application)
                return MainViewModel(prefsRepo) as T
            }
        }
    }

    data class PreferenceStateUI(
        val fields: Map<String, String>
    )

    data class AddStateUI(
        val status: Status = Status.LOADING
    ) {
        enum class Status { LOADING, LOADED, FAILURE }
    }
}