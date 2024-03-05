package io.axept.samplekotlin.screen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

        TCFFields.entries.map {
            updatePreferenceFieldValue(it)
        }

        viewModelScope.launch {
            prefsRepo.hasUpdated.collect { key ->
                val field = TCFFields.entries.firstOrNull { it.key == key }
                field ?: return@collect
                updatePreferenceFieldValue(field)
            }
        }

    }

    private fun updatePreferenceFieldValue(field: TCFFields) {
        var value = prefsRepo.get(field.key, field.type)
        value.apply {
            if (this.length > 500) value = substring(0, 300) // Trim value for performance
        }
        val map = state.value.fields.toMutableMap()
        map[field] = value
        _state.update {
            it.copy(
                fields = map
            )
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
        val fields: Map<TCFFields, String>
    )

    data class AddStateUI(
        val status: Status = Status.LOADING
    ) {
        enum class Status { LOADING, LOADED, FAILURE }
    }
}