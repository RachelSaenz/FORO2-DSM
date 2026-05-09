package com.example.controlgastos

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

data class Gasto(
    val id: String = "",
    val nombre: String = "",
    val monto: Double = 0.0,
    val categoria: String = "",
    val fecha: String = "",
    val userId: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GastosScreen(
    onBackToHome: () -> Unit = {}
) {
    var pantallaActual by remember { mutableStateOf("registro") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de gastos") },
                actions = {
                    TextButton(onClick = onBackToHome) {
                        Text("Inicio")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = pantallaActual == "registro",
                    onClick = { pantallaActual = "registro" },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Registrar") },
                    label = { Text("Registrar") }
                )

                NavigationBarItem(
                    selected = pantallaActual == "historial",
                    onClick = { pantallaActual = "historial" },
                    icon = { Icon(Icons.Default.List, contentDescription = "Historial") },
                    label = { Text("Historial") }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (pantallaActual) {
                "registro" -> RegistroGastoScreen()
                "historial" -> HistorialGastosScreen()
            }
        }
    }
}

@Composable
fun RegistroGastoScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var nombre by remember { mutableStateOf("") }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorMonto by remember { mutableStateOf<String?>(null) }
    var errorCategoria by remember { mutableStateOf<String?>(null) }
    var errorFecha by remember { mutableStateOf<String?>(null) }

    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val mes = month + 1
            fecha = "$year-${mes.toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
            errorFecha = null
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Registrar nuevo gasto",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                errorNombre = null
            },
            label = { Text("Nombre del gasto") },
            placeholder = { Text("Ejemplo: Almuerzo") },
            isError = errorNombre != null,
            supportingText = {
                if (errorNombre != null) Text(errorNombre!!)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = monto,
            onValueChange = {
                monto = it
                errorMonto = null
            },
            label = { Text("Monto") },
            placeholder = { Text("Ejemplo: 3.50") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = errorMonto != null,
            supportingText = {
                if (errorMonto != null) Text(errorMonto!!)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = categoria,
            onValueChange = {
                categoria = it
                errorCategoria = null
            },
            label = { Text("Categoría") },
            placeholder = { Text("Ejemplo: Comida") },
            isError = errorCategoria != null,
            supportingText = {
                if (errorCategoria != null) Text(errorCategoria!!)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fecha,
            onValueChange = {},
            label = { Text("Fecha") },
            placeholder = { Text("Seleccione una fecha") },
            readOnly = true,
            isError = errorFecha != null,
            supportingText = {
                if (errorFecha != null) Text(errorFecha!!)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                datePickerDialog.show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar fecha")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val montoDouble = monto.toDoubleOrNull()
                val usuarioActual = auth.currentUser

                var formularioValido = true

                if (nombre.isBlank()) {
                    errorNombre = "Ingrese el nombre del gasto"
                    formularioValido = false
                }

                if (monto.isBlank()) {
                    errorMonto = "Ingrese el monto"
                    formularioValido = false
                } else if (montoDouble == null || montoDouble <= 0) {
                    errorMonto = "Ingrese un monto válido"
                    formularioValido = false
                }

                if (categoria.isBlank()) {
                    errorCategoria = "Ingrese la categoría"
                    formularioValido = false
                }

                if (fecha.isBlank()) {
                    errorFecha = "Seleccione la fecha"
                    formularioValido = false
                }

                if (usuarioActual == null) {
                    Toast.makeText(
                        context,
                        "Debe iniciar sesión para guardar gastos",
                        Toast.LENGTH_SHORT
                    ).show()
                    formularioValido = false
                }

                if (formularioValido && usuarioActual != null && montoDouble != null) {
                    val uid = usuarioActual.uid

                    val gasto = hashMapOf(
                        "nombre" to nombre.trim(),
                        "monto" to montoDouble,
                        "categoria" to categoria.trim(),
                        "fecha" to fecha,
                        "userId" to uid
                    )

                    db.collection("usuarios")
                        .document(uid)
                        .collection("gastos")
                        .add(gasto)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Gasto guardado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            nombre = ""
                            monto = ""
                            categoria = ""
                            fecha = ""
                        }
                        .addOnFailureListener { error ->
                            Toast.makeText(
                                context,
                                "Error al guardar: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar gasto")
        }
    }
}

@Composable
fun HistorialGastosScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var gastos by remember { mutableStateOf<List<Gasto>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val usuarioActual = auth.currentUser

        if (usuarioActual == null) {
            Toast.makeText(context, "Debe iniciar sesión", Toast.LENGTH_SHORT).show()
            cargando = false
            return@LaunchedEffect
        }

        val uid = usuarioActual.uid

        db.collection("usuarios")
            .document(uid)
            .collection("gastos")
            .get()
            .addOnSuccessListener { resultado ->
                gastos = resultado.documents.map { documento ->
                    Gasto(
                        id = documento.id,
                        nombre = documento.getString("nombre") ?: "",
                        monto = documento.getDouble("monto") ?: 0.0,
                        categoria = documento.getString("categoria") ?: "",
                        fecha = documento.getString("fecha") ?: "",
                        userId = documento.getString("userId") ?: ""
                    )
                }

                cargando = false
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    context,
                    "Error al cargar gastos: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
                cargando = false
            }
    }

    val totalMensual = gastos.sumOf { it.monto }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Historial de gastos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Total mensual",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "$${String.format("%.2f", totalMensual)}",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (cargando) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (gastos.isEmpty()) {
            Text("No hay gastos registrados.")
        } else {
            LazyColumn {
                items(gastos) { gasto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = gasto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text("Monto: $${String.format("%.2f", gasto.monto)}")
                            Text("Categoría: ${gasto.categoria}")
                            Text("Fecha: ${gasto.fecha}")
                        }
                    }
                }
            }
        }
    }
}