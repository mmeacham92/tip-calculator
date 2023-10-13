package com.example.tipcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import kotlin.math.ceil

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
    private lateinit var tvTipTotalWhenSplit: TextView
    private lateinit var tvTotalAmountWhenSplit: TextView

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
        tvTipTotalWhenSplit = findViewById(R.id.tvTipTotalWhenSplit)
        tvTotalAmountWhenSplit = findViewById(R.id.tvTotalAmountWhenSplit)

        sbTipPercent.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        cbRoundTotal.visibility = View.INVISIBLE
        tvTipTotalWhenSplit.visibility = View.INVISIBLE
        tvTotalAmountWhenSplit.visibility = View.INVISIBLE
        etSplitBetween.setText(INITIAL_SPLIT_BETWEEN.toString())

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
                if (value.toString().isEmpty()) {
                    cbRoundTotal.visibility = View.INVISIBLE
                    cbRoundTotal.isChecked = false
                } else cbRoundTotal.visibility = View.VISIBLE
                updateViews(cbRoundTotal.isChecked)
            }
        })

        etSplitBetween.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(value: Editable?) {
                if (value.toString().isEmpty()) {
                    tvTipTotalWhenSplit.visibility = View.INVISIBLE
                    tvTotalAmountWhenSplit.visibility = View.INVISIBLE
                    return
                }

                if (value.toString().toInt() > 1) {
                    tvTipTotalWhenSplit.visibility = View.VISIBLE
                    tvTotalAmountWhenSplit.visibility = View.VISIBLE
                }
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
        val color = ArgbEvaluator().evaluate(
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

        val splitBetween = etSplitBetween.text.toString().toInt()
        val baseAmount = etBaseAmount.text.toString().toDouble() / splitBetween
        var tipPercent = sbTipPercent.progress
        var tipAmount = baseAmount *  tipPercent / 100
        var totalAmount = baseAmount + tipAmount

        // TODO: implement roundup functionality when splitBetween > 1
        if (isRoundedUp) {
            val roundedTotal = Math.ceil(totalAmount)
            tipAmount += roundedTotal - totalAmount
            totalAmount = roundedTotal
            tipPercent = (tipAmount / baseAmount * 100).toInt()
            sbTipPercent.progress = tipPercent
        }

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
        val splitTipTotalText = "%.2f".format(tvTipAmount.text.toString().toDouble() * splitBetween)
        val splitTotalAmountText = "%.2f".format(tvTotalAmount.text.toString().toDouble() * splitBetween)
        tvTipTotalWhenSplit.text = "(${splitTipTotalText})"
        tvTotalAmountWhenSplit.text = "(${splitTotalAmountText})"
    }
}