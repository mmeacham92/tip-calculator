package com.example.tipcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private const val INITIAL_SPLIT_BETWEEN = 1
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var tvTipPercent: TextView
    private lateinit var sbTipPercent: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var etSplitBetween: EditText
    private lateinit var cbRoundTotal: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBaseAmount = findViewById(R.id.etBaseAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        sbTipPercent = findViewById(R.id.sbTipPercent)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        etSplitBetween = findViewById(R.id.etSplitBetween)
        cbRoundTotal = findViewById(R.id.cbRoundTotal)



        sbTipPercent.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        sbTipPercent.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                tvTipPercent.text = "$progress%"
                updateViews(cbRoundTotal.isChecked)
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        etBaseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(value: Editable?) {
                updateViews(cbRoundTotal.isChecked)
            }
        })

        etSplitBetween.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(value: Editable?) {
                updateViews(cbRoundTotal.isChecked)
            }
        })

        cbRoundTotal.setOnCheckedChangeListener {buttonView, isChecked ->
            updateViews(isChecked)
        }
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
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int

        tvTipDescription.setTextColor(color)
    }

    private fun updateViews(isRoundedUp: Boolean) {
        if (etBaseAmount.text.isEmpty() || etSplitBetween.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        val baseAmount = etBaseAmount.text.toString().toDouble()
        var tipPercent = sbTipPercent.progress
        var tipAmount = baseAmount *  tipPercent / 100
        var totalAmount = baseAmount + tipAmount
        val splitBetween = etSplitBetween.text.toString().toInt()

        // super rough!!!
        if (isRoundedUp) {
            // update tip amount
            // calculate a new tipPercent
            //   tipPercent = tipAmount / baseAmount
            // update SeekBar progress to be the new tipPercent / 100 to two decimals
            //   sbTipPercent.progress = "%.2f".format(tipPercent / 100)
            // update tip description based on new tipPercent
            // update total amount

            val roundedTotal = Math.ceil(totalAmount)
            tipAmount += roundedTotal - totalAmount
            totalAmount = roundedTotal
            tipPercent = (tipAmount / baseAmount * 100).toInt()
            sbTipPercent.progress = tipPercent

            // question: how to handle if user checks CheckBox before putting in a balance?
            // possible solution: whenever the user interacts with the baseAmount EditText or the SeekBar, we could set the CheckBox to be unchecked
        }

        tvTipAmount.text = "%.2f".format(tipAmount / splitBetween)
        tvTotalAmount.text = "%.2f".format(totalAmount / splitBetween)
    }
}