package com.example.gesttrack

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object DatabaseHelper {

    const val SUPABASE_URL = "https://uhdgqwzjywdbjtbmglbp.supabase.co"
    const val SUPABASE_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVoZGdxd3pqeXdkYmp0Ym1nbGJwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5NTAwMDYsImV4cCI6MjA3NTUyNjAwNn0.Bohwg320D7qrekjt8iLv_AikzUGP9nmxL_IbLzTch0c"

    val client = OkHttpClient()

    // ------------------ PACIENTE ------------------

    fun inserirPaciente(
        nome: String,
        cpf: String,
        rg: String,
        dataNascimento: String,
        telefone: String,
        email: String,
        senha: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val json = JSONObject().apply {
            put("nome", nome)
            put("cpf", cpf)
            put("rg", rg)
            put("data_nascimento", dataNascimento)
            put("telefone", telefone)
            put("email", email)
            put("senha", senha)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/pacientes")
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val success = response.isSuccessful
                val responseBody = response.body?.string()
                callback(success, responseBody)
            }
        })
    }

    fun verificarLogin(email: String, senha: String, callback: (Boolean, String?) -> Unit) {
        val url =
            "$SUPABASE_URL/rest/v1/pacientes?email=eq.${email}&senha=eq.${senha}&select=id_paciente"

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val jsonArray = JSONArray(body)
                    callback(jsonArray.length() > 0, null)
                } else {
                    callback(false, "Erro no servidor ou credenciais inválidas")
                }
            }
        })
    }

    fun obterPacientePorEmailESenha(email: String, senha: String, callback: (JSONObject?) -> Unit) {
        val url =
            "$SUPABASE_URL/rest/v1/pacientes?email=eq.${email}&senha=eq.${senha}&select=*"

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val jsonArray = JSONArray(body)
                        if (jsonArray.length() > 0) {
                            callback(jsonArray.getJSONObject(0))
                        } else {
                            callback(null)
                        }
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
    }

    // ------------------ MÉDICO ------------------

    fun inserirMedico(
        nome: String,
        crm: String,
        senha: String,
        telefone: String,
        email: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val json = JSONObject().apply {
            put("nome", nome)
            put("crm", crm)
            put("senha", senha)
            put("telefone", telefone)
            put("email", email)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$SUPABASE_URL/rest/v1/medicos")
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                println("🔹 Código HTTP: ${response.code}")
                println("🔹 Resposta Supabase: $responseBody")
                callback(response.isSuccessful, responseBody)
            }
        })
    }


    fun verificarLoginMedico(email: String, senha: String, callback: (Boolean, String?) -> Unit) {
        val url =
            "$SUPABASE_URL/rest/v1/medicos?email=eq.${email}&senha=eq.${senha}&select=id_medico"

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val jsonArray = JSONArray(body)
                    callback(jsonArray.length() > 0, null)
                } else {
                    callback(false, "Erro no servidor ou credenciais inválidas")
                }
            }
        })
    }

    fun obterMedicoPorEmailESenha(email: String, senha: String, callback: (JSONObject?) -> Unit) {
        val url =
            "$SUPABASE_URL/rest/v1/medicos?email=eq.${email}&senha=eq.${senha}&select=*"

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val jsonArray = JSONArray(body)
                        if (jsonArray.length() > 0) {
                            callback(jsonArray.getJSONObject(0))
                        } else {
                            callback(null)
                        }
                    } catch (e: Exception) {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
    }

    fun obterPacientePorId(
        pacienteId: String,
        callback: (JSONObject?) -> Unit
    ) { // 🔥 String ao invés de Int
        val url = "$SUPABASE_URL/rest/v1/pacientes?id_paciente=eq.$pacienteId&select=*"

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", SUPABASE_KEY)
            .addHeader("Authorization", "Bearer $SUPABASE_KEY")
            .addHeader("Content-Type", "application/json")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val jsonArray = JSONArray(body)
                        if (jsonArray.length() > 0) {
                            callback(jsonArray.getJSONObject(0))
                        } else {
                            callback(null)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
        })
    }
}
