package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_result;
    private String firstNum = "";
    private String operation = "";
    private String secondNum = "";
    private String currentResult = "";
    private String showText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        tv_result = findViewById(R.id.tv_result);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_divide).setOnClickListener(this);
        findViewById(R.id.btn_dot).setOnClickListener(this);
        findViewById(R.id.btn_eight).setOnClickListener(this);
        findViewById(R.id.btn_equal).setOnClickListener(this);
        findViewById(R.id.btn_five).setOnClickListener(this);
        findViewById(R.id.btn_four).setOnClickListener(this);
        findViewById(R.id.btn_minus).setOnClickListener(this);
        findViewById(R.id.btn_mul).setOnClickListener(this);
        findViewById(R.id.btn_nine).setOnClickListener(this);
        findViewById(R.id.btn_one).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_reciprocal).setOnClickListener(this);
        findViewById(R.id.btn_seven).setOnClickListener(this);
        findViewById(R.id.btn_six).setOnClickListener(this);
        findViewById(R.id.btn_sqrt).setOnClickListener(this);
        findViewById(R.id.btn_three).setOnClickListener(this);
        findViewById(R.id.btn_two).setOnClickListener(this);
        findViewById(R.id.btn_zero).setOnClickListener(this);
    }


    public void onClick(View view) {
        String inputText;
        inputText = ((TextView) view).getText().toString();
        switch (view.getId()) {
            case R.id.btn_clear:
                clear();
                break;
            case R.id.btn_cancel:
                break;
            case R.id.btn_divide:
            case R.id.btn_mul:
            case R.id.btn_minus:
            case R.id.btn_plus:
                operation = inputText;
                refreshText(showText + inputText);
                break;
            case R.id.btn_equal:
                double cal_res = cal_four();
                refreshOperate(String.valueOf(cal_res));
                refreshText(currentResult);
                break;
            default:
                if (currentResult.length() > 0 && operation.equals("")) {
                    clear();
                }
                if (operation.equals("")) firstNum += inputText;
                else secondNum += inputText;
                if (showText.equals("0") && !inputText.equals(".")) {
                    refreshText(inputText);
                } else {
                    refreshText(showText + inputText);
                }
                break;
        }

    }

    private void refreshText(String Text) {
        showText = Text;
        tv_result.setText(showText);
    }

    private void refreshOperate(String new_result) {
        currentResult = new_result;
        firstNum = currentResult;
        secondNum = "";
        operation = "";

    }

    private void clear() {
        refreshOperate("");
        refreshText("");
    }

    private double cal_four() {
        switch (operation) {
            case "+":
                return Double.parseDouble(firstNum) + Double.parseDouble(secondNum);
            case "-":
                return Double.parseDouble(firstNum) - Double.parseDouble(secondNum);
            case "×":
                return Double.parseDouble(firstNum) * Double.parseDouble(secondNum);
            default:
                return Double.parseDouble(firstNum) / Double.parseDouble(secondNum);

        }
    }
}