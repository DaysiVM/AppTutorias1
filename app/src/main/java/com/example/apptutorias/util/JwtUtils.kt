package com.example.apptutorias.util

import android.util.Base64
import org.json.JSONObject

fun getRolesFromJWT(token: String): List<String> {
    val parts = token.split(".")
    if (parts.size != 3) return emptyList()

    val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
    val json = JSONObject(payload)
    val rolesArray = json.optJSONArray("roles") ?: return emptyList()

    return List(rolesArray.length()) { i -> rolesArray.getString(i) }
}