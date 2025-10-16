
package com.example.gesttrack

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {
    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = "https://uhdgqwzjywdbjtbmglbp.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVoZGdxd3pqeXdkYmp0Ym1nbGJwIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTk5NTAwMDYsImV4cCI6MjA3NTUyNjAwNn0.Bohwg320D7qrekjt8iLv_AikzUGP9nmxL_IbLzTch0c"
    ) {
        install(Auth)
        install(Postgrest)
    }
}
