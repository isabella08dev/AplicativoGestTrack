package com.example.gesttrack.paciente

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ChatPacienteActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val apiKey = "AIzaSyCJcL9GebaG9u9tB7vm8f97B-aThVJBEJg"

    private lateinit var sendButton: ImageButton
    private lateinit var promptInput: EditText
    private lateinit var chatRecyclerView: RecyclerView

    private val mensagens = mutableListOf<Mensagem>()
    private lateinit var chatAdapter: ChatAdapter

    private var contextoEnviado = false

    private val PREFS = "memoriaAura"
    private val PREFS_KEY_RESUMO = "resumo"
    private val PREFS_KEY_HISTORICO = "historico"

    private var chatId: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_chat)

        // 🔹 identifica qual aba de chat está aberta
        chatId = intent.getStringExtra("CHAT_ID") ?: "default"

        sendButton = findViewById(R.id.sendButton)
        promptInput = findViewById(R.id.messageEditText)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        chatAdapter = ChatAdapter(mensagens)
        chatRecyclerView.adapter = chatAdapter

        carregarHistorico()

        sendButton.setOnClickListener {
            val pergunta = promptInput.text.toString().trim()
            if (pergunta.isNotEmpty()) {
                addMensagem(Mensagem(pergunta, true))
                promptInput.text.clear()
                addMensagem(Mensagem("Pensando...", false))

                enviarPerguntaGemini { resposta ->
                    runOnUiThread {
                        updateUltimaMensagem(Mensagem(resposta, false))
                        salvarHistorico()
                        salvarResumoAutomatico()
                    }
                }
            } else {
                Toast.makeText(this, "Digite uma pergunta.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMensagem(msg: Mensagem) {
        mensagens.add(msg)
        chatAdapter.notifyItemInserted(mensagens.size - 1)
        chatRecyclerView.scrollToPosition(mensagens.size - 1)
    }

    private fun updateUltimaMensagem(msg: Mensagem) {
        val idx = mensagens.lastIndex
        if (idx >= 0) {
            mensagens[idx] = msg
            chatAdapter.notifyItemChanged(idx)
            chatRecyclerView.scrollToPosition(idx)
        }
    }

    // 🔹 Salva histórico por aba
    private fun salvarHistorico() {
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val arr = JSONArray()
        for (m in mensagens) {
            val obj = JSONObject()
            obj.put("texto", m.texto)
            obj.put("isUser", m.isUser)
            arr.put(obj)
        }
        prefs.edit().putString("${PREFS_KEY_HISTORICO}_$chatId", arr.toString()).apply()
    }

    // 🔹 Carrega histórico por aba
    private fun carregarHistorico() {
        val prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val salvo = prefs.getString("${PREFS_KEY_HISTORICO}_$chatId", null) ?: return
        val arr = JSONArray(salvo)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            mensagens.add(Mensagem(obj.getString("texto"), obj.getBoolean("isUser")))
        }
        chatAdapter.notifyDataSetChanged()
        chatRecyclerView.scrollToPosition(mensagens.size - 1)
    }

    // 🔹 Salva resumo por aba
    private fun salvarResumoAutomatico() {
        val resumoPrompt =
            "Resuma em poucas linhas apenas as informações importantes da paciente (nome, semanas de gestação, principais preocupações)."
        val endpoint =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val contents = JSONArray()
        contents.put(JSONObject().put("role", "user")
            .put("parts", JSONArray().put(JSONObject().put("text", resumoPrompt)))
        )
        for (m in mensagens) {
            if (!m.isUser && m.texto == "Pensando...") continue
            contents.put(JSONObject()
                .put("role", if (m.isUser) "user" else "model")
                .put("parts", JSONArray().put(JSONObject().put("text", m.texto)))
            )
        }
        val body = JSONObject().apply { put("contents", contents) }
        val requestBody = body.toString().toRequestBody(mediaType)
        val req = Request.Builder().url(endpoint).post(requestBody).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {
                val raw = response.body?.string()
                if (!response.isSuccessful || raw.isNullOrBlank()) return
                try {
                    val json = JSONObject(raw)
                    val resumo = json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        .edit().putString("${PREFS_KEY_RESUMO}_$chatId", resumo).apply()
                } catch (_: Exception) {}
            }
        })
    }

    private fun enviarPerguntaGemini(callback: (String) -> Unit) {
        val endpoint =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val contents = JSONArray()

        if (!contextoEnviado) {
            val resumoSalvo = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString("${PREFS_KEY_RESUMO}_$chatId", null)
            var textoContexto =
                "Você irá se apresentar como AURA (Assistente Unificado de Recomendações e Apoio) apenas na primeira mensagem. " +
                        "Você é um assistente focado em ajudar gestantes no período gestacional (suporte emocional, autocuidado, dilatação, cuidados com o feto)."
            if (!resumoSalvo.isNullOrEmpty()) {
                textoContexto += "\nResumo anterior desta paciente: $resumoSalvo"
            }
            contents.put(JSONObject().put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", textoContexto)))
            )
            contextoEnviado = true
        }

        for (m in mensagens) {
            if (!m.isUser && m.texto == "Pensando...") continue
            contents.put(JSONObject()
                .put("role", if (m.isUser) "user" else "model")
                .put("parts", JSONArray().put(JSONObject().put("text", m.texto)))
            )
        }

        val body = JSONObject().apply { put("contents", contents) }
        val requestBody = body.toString().toRequestBody(mediaType)
        val req = Request.Builder().url(endpoint).post(requestBody).build()

        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("Falha de rede: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                val raw = response.body?.string()
                if (!response.isSuccessful || raw.isNullOrBlank()) {
                    callback("Erro: ${response.code}")
                    return
                }
                try {
                    val json = JSONObject(raw)
                    val text = json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                    callback(text)
                } catch (e: Exception) {
                    callback("Erro parsing JSON: ${e.message}")
                }
            }
        })
    }

    // ---------------- MODELO ----------------
    data class Mensagem(val texto: String, val isUser: Boolean)

    // ---------------- ADAPTER ----------------
    class ChatAdapter(private val mensagens: List<Mensagem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            const val TYPE_USER = 1
            const val TYPE_BOT = 2
        }

        private val boldRegex = Regex("\\*\\*(.+?)\\*\\*")
        private val italicRegex = Regex("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)")

        override fun getItemViewType(position: Int): Int {
            return if (mensagens[position].isUser) TYPE_USER else TYPE_BOT
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_USER) {
                val v = inflater.inflate(R.layout.item_message_user, parent, false)
                UserVH(v)
            } else {
                val v = inflater.inflate(R.layout.item_message_bot, parent, false)
                BotVH(v)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val msg = mensagens[position]
            var html = msg.texto
            html = boldRegex.replace(html) { m -> "<b>${m.groupValues[1]}</b>" }
            html = italicRegex.replace(html) { m -> "<i>${m.groupValues[1]}</i>" }
            val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)

            if (holder is UserVH) holder.tv.text = spanned
            if (holder is BotVH) holder.tv.text = spanned
        }

        override fun getItemCount(): Int = mensagens.size

        class UserVH(view: View) : RecyclerView.ViewHolder(view) {
            val tv: TextView = view.findViewById(R.id.tvMessage)
        }

        class BotVH(view: View) : RecyclerView.ViewHolder(view) {
            val tv: TextView = view.findViewById(R.id.tvMessage)
        }
    }
}
