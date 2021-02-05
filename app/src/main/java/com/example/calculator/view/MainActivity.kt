package com.example.calculator.view

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.R
import com.example.calculator.common.*
import com.example.calculator.common.Utils.format
import com.example.calculator.viewmodel.ViewModelCalculator
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var viewModel: ViewModelCalculator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getButtonIds().forEach {
            it.setOnClickListener(this)
        }
        sharedPreferences = getSharedPreferences("result", MODE_PRIVATE)
        editor = sharedPreferences.edit()
        tvFormula.text = sharedPreferences.getString("count", "") + sharedPreferences.getString("result", "")
        if(tvFormula.text != ""){
            btnClear.text = "AC"
        }
        viewModel = ViewModelCalculator()
        setActionBar()
        registerObserve()
    }

    private fun getButtonIds() =
        arrayOf(
            btn_0,
            btn_1,
            btn_2,
            btn_3,
            btn_4,
            btn_5,
            btn_6,
            btn_7,
            btn_8,
            btn_9,
            btnDelete,
            btnClear,
            btnPow,
            btnSqrt,
            btnPercent,
            btnMinus,
            btnMultiply,
            btnPlus,
            btnDivide,
            btnDecimal,
            btnBracketsOpen,
            btnBracketsClose,
            btnEquals
        )

    private fun setActionBar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDecimal -> viewModel.dotClick()
            R.id.btn_0 -> viewModel.zeroClicked()
            R.id.btn_1 -> add("1")
            R.id.btn_2 -> add("2")
            R.id.btn_3 -> add("3")
            R.id.btn_4 -> add("4")
            R.id.btn_5 -> add("5")
            R.id.btn_6 -> add("6")
            R.id.btn_7 -> add("7")
            R.id.btn_8 -> add("8")
            R.id.btn_9 -> add("9")
            R.id.btnBracketsOpen -> add("(")
            R.id.btnBracketsClose -> add(")")
            R.id.btnDelete -> viewModel.delete()
            R.id.btnClear -> clear()
            R.id.btnPercent -> operation(PERCENT)
            R.id.btnPow -> operation(POWER)
            R.id.btnSqrt -> operation(SQRT)
            R.id.btnPlus -> operation(PLUS)
            R.id.btnMinus -> operation(MINUS)
            R.id.btnMultiply -> operation(MULTIPLY)
            R.id.btnDivide -> operation(DIVIDE)
            R.id.btnEquals -> viewModel.result()
        }
    }

    fun add(number: String){
        viewModel.showNewFormula(sharedPreferences.getString("count", "") + sharedPreferences.getString("result", ""))
        viewModel.add(number)
    }

    fun operation(key: String){
        viewModel.showNewFormula(sharedPreferences.getString("count", "") + sharedPreferences.getString("result", ""))
        viewModel.operation(key)
    }

    private fun clear() {
        if (btnClear.text == "AC") {
            editor.clear()
            editor.apply()
            btnClear.text = "C"
            tvFormula.text = ""
        } else {
            viewModel.clear("0","")
            tvCount.text = "0"
            btnClear.text = "AC"
            tvResult.text = ""
        }
    }

    private fun registerObserve(){
        registerCount()
        registerFormula()
        registerResult()
        registerNameClear()
        registerEditorCount()
        registerEditorResult()
    }

    private fun registerCount() {
        viewModel.count.observe(this) {
            tvCount.text = it
        }
    }

    private fun registerFormula() {
        viewModel.formula.observe(this) {
            tvFormula.text = it
        }
    }

    private fun registerResult() {
        viewModel.result.observe(this) {
            tvResult.text = it
        }
    }

    private fun registerNameClear() {
        viewModel.clear.observe(this) {
            btnClear.text = it
        }
    }

    private fun registerEditorCount() {
        viewModel.editorCount.observe(this) {
            editor.putString("count", it)
            editor.apply()
        }
    }

    private fun registerEditorResult() {
        viewModel.editorResult.observe(this) {
            editor.putString("result", it)
            editor.apply()
        }
    }



}