package com.example.controlgastos

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GastosViewModel : ViewModel() {

    private val _gastos = MutableStateFlow<List<Gasto>>(emptyList())
    val gastos: StateFlow<List<Gasto>> = _gastos

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarGastos() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _cargando.value = true

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(uid)
            .collection("gastos")
            .get()
            .addOnSuccessListener { resultado ->
                _gastos.value = resultado.documents.map { doc ->
                    Gasto(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        monto = doc.getDouble("monto") ?: 0.0,
                        categoria = doc.getString("categoria") ?: "",
                        fecha = doc.getString("fecha") ?: "",
                        userId = doc.getString("userId") ?: ""
                    )
                }
                _cargando.value = false
            }
            .addOnFailureListener {
                _cargando.value = false
            }
    }

    fun calcularTotalMensual(gastos: List<Gasto>, mesAnio: String): Double {
        // mesAnio formato "yyyy-MM", fecha guardada como "yyyy-MM-dd"
        return gastos
            .filter { it.fecha.startsWith(mesAnio) }
            .sumOf { it.monto }
    }

    fun filtrarPorCategoria(gastos: List<Gasto>, categoria: String): List<Gasto> {
        return if (categoria == "Todas") gastos
        else gastos.filter { it.categoria == categoria }
    }
}
