package com.example.tipcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var tvTipPercent: TextView
    private lateinit var sbTipPercent: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        sbTipPercent = findViewById(R.id.sbTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)

        sbTipPercent.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        sbTipPercent.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                tvTipPercent.text = "$progress%"
                updateViews()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(value: Editable?) {
                updateViews()
            }

        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tvTipDescription.text = tipDescription

        // update the color of the text here as well
        // color interpolation: https://developer.android.com/reference/kotlin/android/animation/ArgbEvaluator?authuser=1

        var color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / sbTipPercent.max,
            ContextCompat.getColor(this, R.color.best_tip),
            ContextCompat.getColor(this, R.color.worst_tip)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    private fun updateViews() {
        if (etBaseAmount.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        val tipPercent = sbTipPercent.progress
        val tipAmount = baseAmount *  tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }
}