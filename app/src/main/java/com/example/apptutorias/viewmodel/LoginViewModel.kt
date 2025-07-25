package com.example.apptutorias.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptutorias.network.RetrofitClient
import com.example.apptutorias.network.TokenProvider
import com.example.apptutorias.network.model.LoginRequest
import com.example.apptutorias.network.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val role: String) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<String>>() // token o error
    val loginResult: LiveData<Result<String>> = _loginResult

    fun login(username: String, password: String) {
        val request = LoginRequest(
            username = username,
            password = password
            // Si quieres enviar role al backend, agrega aquí: , role = role
        )

        println("Intentando login como: $role")

        RetrofitClient.apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token ?: ""
                    TokenProvider.token = token
                    _loginResult.value = Result.success(token)
                } else {
                    _loginResult.value = Result.failure(Exception("Error de login: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = Result.failure(t)
            }
        })
    }
}
