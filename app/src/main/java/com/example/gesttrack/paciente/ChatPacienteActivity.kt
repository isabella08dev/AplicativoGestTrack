package com.example.gesttrack.paciente

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gesttrack.R // Removido o import do BuildConfig
//import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Message(val text: String, val isFromUser: Boolean)

class ChatPacienteActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var messageAdapter: MessageAdapter

    private val messageList = mutableListOf<Message>()

    // --- DIAGNÓSTICO TEMPORÁRIO E INSEGURO ---
    // 1. COLE A SUA CHAVE DE API REAL AQUI DENTRO DAS ASPAS.
    // O objetivo é ver se os 22 erros de compilação desaparecem.
    private val apiKey = "AIzaSyCOdwVzsPURCbgxIKi9jZxcgNxrb8JsUoI"

    private val generativeModel by lazy {
        // Verifica se a chave foi colada
       if (apiKey == "AIzaSyCOdwVzsPURCbgxIKi9jZxcgNxrb8JsUoI" || apiKey.isBlank()) {
            // Apenas um aviso, não impede a compilação
        }
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey // Usando a variável local em vez do BuildConfig
        )
    }
    private val chat by lazy { generativeModel.startChat() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paciente_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        messageAdapter = MessageAdapter(messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        chatRecyclerView.adapter = messageAdapter

        sendButton.setOnClickListener {
            val userMessage = messageEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                addMessage(userMessage, true)
                messageEditText.text.clear()
                sendMessageToGemini(userMessage)
            }
        }

        if (apiKey == "COLE_SUA_CHAVE_API_AQUI" || apiKey.isBlank()) {
             addMessage("ERRO: A chave de API não foi configurada. Cole a chave na variável 'apiKey' do código.", false)
        }
    }

    private fun sendMessageToGemini(userMessage: String) {
        if (apiKey == "COLE_SUA_CHAVE_API_AQUI" || apiKey.isBlank()) return

        addMessage("Digitando...", false)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    chat.sendMessage(userMessage)
                }
                removeTyping()
                val text = response.text?.takeIf { it.isNotBlank() } ?: "Não obtive uma resposta."
                addMessage(text, false)

            } catch (e: Exception) {
                removeTyping()
                Log.e("GeminiError", "API Error: ${e.localizedMessage}", e)
                addMessage("Erro de conexão. Tente novamente.", false)
            }
        }
    }

    private fun addMessage(text: String, isFromUser: Boolean) {
        messageList.add(Message(text, isFromUser))
        messageAdapter.notifyItemInserted(messageList.size - 1)
        chatRecyclerView.scrollToPosition(messageList.size - 1)
    }

    private fun removeTyping() {
        if (messageList.isNotEmpty() && messageList.last().text == "Digitando...") {
            val idx = messageList.lastIndex
            messageList.removeAt(idx)
            messageAdapter.notifyItemRemoved(idx)
        }
    }

    class MessageAdapter(private val messages: List<Message>) :
        RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

        companion object { private const val VIEW_TYPE_USER = 1; private const val VIEW_TYPE_IA = 2; }

        override fun getItemViewType(position: Int): Int = if (messages[position].isFromUser) VIEW_TYPE_USER else VIEW_TYPE_IA

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == VIEW_TYPE_USER) {
                UserMessageViewHolder(inflater.inflate(R.layout.item_user_message, parent, false))
            } else {
                IAMessageViewHolder(inflater.inflate(R.layout.item_ia_message, parent, false))
            }
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) = holder.bind(messages[position])

        override fun getItemCount() = messages.size

        abstract class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) { abstract fun bind(message: Message) }

        class UserMessageViewHolder(view: View) : MessageViewHolder(view) {
            private val textView: TextView = view.findViewById(R.id.userMessageTextView)
            override fun bind(message: Message) { textView.text = message.text }
        }

        class IAMessageViewHolder(view: View) : MessageViewHolder(view) {
            private val textView: TextView = view.findViewById(R.id.iaMessageTextView)
            override fun bind(message: Message) { textView.text = message.text }
        }
    }
}