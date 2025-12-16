package com.example.exequiel_gutierrez

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.exequiel_gutierrez.restaurant.CuentaMesa
import com.example.exequiel_gutierrez.restaurant.ItemMenu
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var cuentaMesa: CuentaMesa

    // Formato de pesos chilenos
    private val formatoCLP: NumberFormat =
        NumberFormat.getCurrencyInstance(Locale("es", "CL")).apply {
            maximumFractionDigits = 0     // sin decimales
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // EditTexts
        val edtPastel = findViewById<EditText>(R.id.edtCantPastel)
        val edtCazuela = findViewById<EditText>(R.id.edtCantCazuela)

        // Switch
        val switchPropina = findViewById<Switch>(R.id.switchPropina)

        // TextViews subtotales
        val txtPastelSubtotal = findViewById<TextView>(R.id.txtPastelSubtotal)
        val txtCazuelaSubtotal = findViewById<TextView>(R.id.txtCazuelaSubtotal)

        // TextViews totales
        val txtTotalComida = findViewById<TextView>(R.id.txtTotalComida)
        val txtTotalPropina = findViewById<TextView>(R.id.txtTotalPropina)
        val txtTotalFinal = findViewById<TextView>(R.id.txtTotalFinal)

        // Menú
        val pastel = ItemMenu("Pastel de Choclo", 12000)
        val cazuela = ItemMenu("Cazuela", 10000)

        cuentaMesa = CuentaMesa()

        // TextWatcher para ambos EditText
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarTotales(
                    edtPastel,
                    edtCazuela,
                    pastel,
                    cazuela,
                    switchPropina.isChecked,
                    txtPastelSubtotal,
                    txtCazuelaSubtotal,
                    txtTotalComida,
                    txtTotalPropina,
                    txtTotalFinal
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtPastel.addTextChangedListener(listener)
        edtCazuela.addTextChangedListener(listener)

        // Listener del switch de propina
        switchPropina.setOnCheckedChangeListener { _, isChecked ->
            cuentaMesa.aceptaPropina = isChecked
            actualizarTotales(
                edtPastel,
                edtCazuela,
                pastel,
                cazuela,
                isChecked,
                txtPastelSubtotal,
                txtCazuelaSubtotal,
                txtTotalComida,
                txtTotalPropina,
                txtTotalFinal
            )
        }

        // Calcular una vez al iniciar
        actualizarTotales(
            edtPastel,
            edtCazuela,
            pastel,
            cazuela,
            switchPropina.isChecked,
            txtPastelSubtotal,
            txtCazuelaSubtotal,
            txtTotalComida,
            txtTotalPropina,
            txtTotalFinal
        )
    }

    private fun actualizarTotales(
        edtPastel: EditText,
        edtCazuela: EditText,
        pastel: ItemMenu,
        cazuela: ItemMenu,
        aceptaPropina: Boolean,
        txtPastelSubtotal: TextView,
        txtCazuelaSubtotal: TextView,
        txtTotalComida: TextView,
        txtTotalPropina: TextView,
        txtTotalFinal: TextView
    ) {
        // Limpiar items y mantener estado de propina
        cuentaMesa.limpiarItems()
        cuentaMesa.aceptaPropina = aceptaPropina

        val cantPastel = edtPastel.text.toString().toIntOrNull() ?: 0
        val cantCazuela = edtCazuela.text.toString().toIntOrNull() ?: 0

        if (cantPastel > 0) cuentaMesa.agregarItem(pastel, cantPastel)
        if (cantCazuela > 0) cuentaMesa.agregarItem(cazuela, cantCazuela)

        // Subtotales por plato
        val subtotalPastel = pastel.precio * cantPastel
        val subtotalCazuela = cazuela.precio * cantCazuela

        txtPastelSubtotal.text = formatoCLP.format(subtotalPastel.toLong())
        txtCazuelaSubtotal.text = formatoCLP.format(subtotalCazuela.toLong())

        // Totales
        val totalSinPropina = cuentaMesa.calcularTotalSinPropina()
        val propina = cuentaMesa.calcularPropina()
        val totalConPropina = cuentaMesa.calcularTotalConPropina()

        txtTotalComida.text = formatoCLP.format(totalSinPropina.toLong())
        txtTotalPropina.text = formatoCLP.format(propina.toLong())
        txtTotalFinal.text = formatoCLP.format(totalConPropina.toLong())
    }
}
