package com.example.tipcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import org.w3c.dom.Text

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var tvTipPercent: TextView
    private lateinit var sbTipPercent: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        sbTipPercent = findViewById(R.id.sbTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)

        sbTipPercent.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"

        sbTipPercent.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                if (etBaseAmount.text.toString() != "") updateViews(etBaseAmount.text.toString().toDouble())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(value: Editable?) {
                Log.i(TAG, "afterTextChanged $value")
                // value represents the amount entered
                // update tip amount to reflect value * seekbar.progress / 100
                updateViews(value.toString().toDouble())
            }

        })
    }

    private fun updateViews(value: Double) {
        tvTipAmount.text = "${value * sbTipPercent.progress / 100}"
        tvTotalAmount.text = "${value + tvTipAmount.text.toString().toDouble()}"
    }
}