package com.privacy.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import android.content.*;

public class MainActivity extends Activity {
    
    private TextView tvDisplay;
    private StringBuilder input = new StringBuilder();
    private StringBuilder secretInput = new StringBuilder();
    private double num1 = 0;
    private String operator = "";
    private boolean isNewInput = true;
    private final String PASSWORD = "1984";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tvDisplay = findViewById(R.id.tvDisplay);
        
        // number buttons 0-9
        int[] numIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                        R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int i = 0; i < numIds.length; i++) {
            int finalI = i;
            findViewById(numIds[i]).setOnClickListener(v -> appendNumber(String.valueOf(finalI)));
        }
        
        // operator buttons
        findViewById(R.id.btnAdd).setOnClickListener(v -> setOperator("+"));
        findViewById(R.id.btnSub).setOnClickListener(v -> setOperator("-"));
        findViewById(R.id.btnMul).setOnClickListener(v -> setOperator("*"));
        findViewById(R.id.btnDiv).setOnClickListener(v -> setOperator("/"));
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculate());
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
        findViewById(R.id.btnDot).setOnClickListener(v -> appendDot());
        findViewById(R.id.btnBack).setOnClickListener(v -> backspace());
        
        // long press operator to enter vault
        View.OnLongClickListener longClick = v -> { showVault(); return true; };
        findViewById(R.id.btnAdd).setOnLongClickListener(longClick);
        findViewById(R.id.btnSub).setOnLongClickListener(longClick);
        findViewById(R.id.btnMul).setOnLongClickListener(longClick);
        findViewById(R.id.btnDiv).setOnLongClickListener(longClick);
    }
    
    private void appendNumber(String num) {
        if (isNewInput) {
            input = new StringBuilder();
            isNewInput = false;
        }
        input.append(num);
        secretInput.append(num);
        tvDisplay.setText(input.toString());
        
        // check password
        if (secretInput.toString().equals(PASSWORD)) {
            showVault();
            secretInput = new StringBuilder();
        }
    }
    
    private void appendDot() {
        if (isNewInput) {
            input = new StringBuilder("0.");
            isNewInput = false;
        } else if (!input.toString().contains(".")) {
            input.append(".");
        }
        tvDisplay.setText(input.toString());
    }
    
    private void setOperator(String op) {
        if (input.length() > 0) {
            try {
                num1 = Double.parseDouble(input.toString());
            } catch (Exception e) {}
        }
        operator = op;
        isNewInput = true;
    }
    
    private void calculate() {
        if (operator.isEmpty() || input.length() == 0) return;
        
        double num2 = 0;
        try {
            num2 = Double.parseDouble(input.toString());
        } catch (Exception e) { return; }
        
        double result = 0;
        switch (operator) {
            case "+": result = num1 + num2; break;
            case "-": result = num1 - num2; break;
            case "*": result = num1 * num2; break;
            case "/": 
                if (num2 != 0) result = num1 / num2; 
                else { tvDisplay.setText("Error"); isNewInput = true; return; }
                break;
        }
        
        String resultStr;
        if (result == (long) result) {
            resultStr = String.valueOf((long) result);
        } else {
            resultStr = String.valueOf(result);
        }
        
        tvDisplay.setText(resultStr);
        input = new StringBuilder(resultStr);
        operator = "";
        isNewInput = true;
    }
    
    private void clear() {
        input = new StringBuilder();
        secretInput = new StringBuilder();
        num1 = 0;
        operator = "";
        isNewInput = true;
        tvDisplay.setText("0");
    }
    
    private void backspace() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
            if (secretInput.length() > 0) {
                secretInput.deleteCharAt(secretInput.length() - 1);
            }
            tvDisplay.setText(input.length() > 0 ? input.toString() : "0");
        }
    }
    
    private void showVault() {
        Intent intent = new Intent(this, VaultActivity.class);
        startActivity(intent);
    }
}
