package com.example.apptutorias.viewmodel


import androidx.lifecycle.ViewModel
import com.example.apptutorias.network.RetrofitClient
import com.example.apptutorias.tutoria.Tutoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TutoriaViewModel : ViewModel() {
    private val _tutorias = MutableStateFlow<List<Tutoria>>(emptyList())
    val tutorias: StateFlow<List<Tutoria>> = _tutorias

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun cargarTutorias() {
        _isLoading.value = true
        RetrofitClient.apiService.getTutorias().enqueue(object : Callback<List<Tutoria>> {
            override fun onResponse(call: Call<List<Tutoria>>, response: Response<List<Tutoria>>) {
                if (response.isSuccessful) {
                    _tutorias.value = response.body() ?: emptyList()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al cargar tutorías: ${response.code()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<List<Tutoria>>, t: Throwable) {
                _errorMessage.value = "Error al cargar tutorías: ${t.message}"
                _isLoading.value = false
            }
        })
    }

    fun crearTutoria(tutoria: Tutoria, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        RetrofitClient.apiService.crearTutoria(tutoria).enqueue(object : Callback<Tutoria> {
            override fun onResponse(call: Call<Tutoria>, response: Response<Tutoria>) {
                if (response.isSuccessful) {
                    cargarTutorias()
                    onComplete()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al crear tutoría: ${response.code()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<Tutoria>, t: Throwable) {
                _errorMessage.value = "Error al crear tutoría: ${t.message}"
                _isLoading.value = false
            }
        })
    }

    fun actualizarTutoria(id: Long, tutoria: Tutoria, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        RetrofitClient.apiService.actualizarTutoria(id, tutoria).enqueue(object : Callback<Tutoria> {
            override fun onResponse(call: Call<Tutoria>, response: Response<Tutoria>) {
                if (response.isSuccessful) {
                    cargarTutorias()
                    onComplete()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al actualizar tutoría: ${response.code()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<Tutoria>, t: Throwable) {
                _errorMessage.value = "Error al actualizar tutoría: ${t.message}"
                _isLoading.value = false
            }
        })
    }

    fun eliminarTutoria(id: Long, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        RetrofitClient.apiService.eliminarTutoria(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    cargarTutorias()
                    onComplete()
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Error al eliminar tutoría: ${response.code()}"
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _errorMessage.value = "Error al eliminar tutoría: ${t.message}"
                _isLoading.value = false
            }
        })
    }
}
