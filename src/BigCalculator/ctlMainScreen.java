package BigCalculator;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Stack;

public class ctlMainScreen {
	@FXML private Button btnFactorial;
	@FXML private TextArea txDisplay;

	private Stack<String> stkOperands = new Stack<String>();
	String strCurrentEntry = "";

	private void updateDisplay() {
		String strDisplay = "TOP\n";

		for(String s : stkOperands) {
			strDisplay += s + "\n";
		}

		txDisplay.clear();
		txDisplay.setText(strDisplay);
	}

	//region BUTTONS
	@FXML private void btnDigit_Clicked(Event event) {
		Button btn = (Button)event.getSource();
		String strDigit = btn.getText();
		if (strDigit.isEmpty()) return;

		strCurrentEntry += (strDigit);
		txDisplay.setText(txDisplay.getText() + strDigit);
	}

	@FXML private void btnEnter_Clicked() {
		if (strCurrentEntry.isEmpty()) {	// Replicate current stack entry
			strCurrentEntry = stkOperands.pop(); stkOperands.push(strCurrentEntry);
		}
		stkOperands.push(CleanStr(strCurrentEntry));
		strCurrentEntry = "";

		updateDisplay();
	}

	@FXML private void btnClear_Clicked() {
		stkOperands.clear();
		strCurrentEntry = "";

		updateDisplay();
	}

	@FXML private void btnDel_Clicked() {
		if (strCurrentEntry.isEmpty() && !stkOperands.isEmpty()) stkOperands.pop();
		strCurrentEntry = "";

		updateDisplay();
	}

	@FXML private void btnFactorial_Action() {
		String strResult;

		if (! strCurrentEntry.isEmpty()) btnEnter_Clicked();

		if (stkOperands.isEmpty()) return;
		strCurrentEntry = stkOperands.pop();
		if (strCurrentEntry.isEmpty()) return;
		strResult = strFactorise(strCurrentEntry);
		strCurrentEntry = "";

		stkOperands.push(CleanStr(strResult));
		updateDisplay();
	}

	@FXML private void btnMultiply_Clicked() {
		String strResult, strOperand1;

		if (! strCurrentEntry.isEmpty()) btnEnter_Clicked();

		if (stkOperands.isEmpty()) return;
		strCurrentEntry = stkOperands.pop();
		if (strCurrentEntry.isEmpty()) return;
		if (stkOperands.isEmpty()) { stkOperands.push(strCurrentEntry); strCurrentEntry = ""; return; }
		strOperand1 = stkOperands.pop();
		if (strOperand1.isEmpty()) strOperand1 = "1";
		strResult = strMultiply(strOperand1, strCurrentEntry);
		strCurrentEntry = "";

		stkOperands.push(CleanStr(strResult));
		updateDisplay();
	}

	@FXML private void btnAdd_Clicked() {
		String strResult, strOperand1;

		if (! strCurrentEntry.isEmpty()) btnEnter_Clicked();

		if (stkOperands.isEmpty()) return;
		strCurrentEntry = stkOperands.pop();
		if (strCurrentEntry.isEmpty()) return;
		if (stkOperands.isEmpty()) { stkOperands.push(strCurrentEntry); strCurrentEntry = ""; return; }
		strOperand1 = stkOperands.pop();
		if (strOperand1.isEmpty()) strOperand1 = "0";
		strResult = strAdd(strOperand1, strCurrentEntry);
		strCurrentEntry = "";

		stkOperands.push(CleanStr(strResult));
		updateDisplay();
	}

	@FXML private void btnSubtract_Clicked() {
		String strResult, strOperand1;

		if (! strCurrentEntry.isEmpty()) btnEnter_Clicked();

		if (stkOperands.isEmpty()) return;
		strCurrentEntry = stkOperands.pop();
		if (strCurrentEntry.isEmpty()) return;
		if (stkOperands.isEmpty()) { stkOperands.push(strCurrentEntry); strCurrentEntry = ""; return; }
		strOperand1 = stkOperands.pop();
		if (strOperand1.isEmpty()) strOperand1 = "0";
		strResult = strSubtract(strOperand1, strCurrentEntry);
		strCurrentEntry = "";

		stkOperands.push(CleanStr(strResult));
		updateDisplay();
	}


	@FXML private void btnDivide_Clicked() {
		String strResult = "0", strOperand1;

		if (! strCurrentEntry.isEmpty()) btnEnter_Clicked();

		if (stkOperands.isEmpty()) return;
		strCurrentEntry = stkOperands.pop();
		if (strCurrentEntry.isEmpty()) return;
		if (stkOperands.isEmpty()) { stkOperands.push(strCurrentEntry); strCurrentEntry = ""; return; }
		strOperand1 = stkOperands.pop();
		if (strOperand1.isEmpty()) strOperand1 = "0";
		strResult = strDivide(strOperand1, strCurrentEntry);
		strCurrentEntry = "";

		stkOperands.push(CleanStr(strResult));
		updateDisplay();
	}

	//endregion

	//region String Computations
	public String strFactorise(String strOperand) {

		String strResult = "1";
		String strIntermediate = "1";

		while (strNumericCompare(strOperand, strIntermediate) > 0) {
			strIntermediate = strAdd(strIntermediate, "1");
			strResult = strMultiply(strResult, strIntermediate);
		}
		return strResult;
	}

	public String strMultiply(String num1, String num2) {
		String n1 = new StringBuilder(num1).reverse().toString();
		String n2 = new StringBuilder(num2).reverse().toString();

		int[] d = new int[num1.length()+num2.length()];

		//multiply each digit and sum at the corresponding positions
		for(int i=0; i<n1.length(); i++){
			for(int j=0; j<n2.length(); j++){
				d[i+j] += (n1.charAt(i)-'0') * (n2.charAt(j)-'0');
			}
		}

		StringBuilder sb = new StringBuilder();

		//calculate each digit
		for(int i=0; i<d.length; i++){
			int mod = d[i]%10;
			int carry = d[i]/10;
			if(i+1<d.length){
				d[i+1] += carry;
			}
			sb.insert(0, mod);
		}

		//remove front 0's
		while(sb.charAt(0) == '0' && sb.length()> 1){
			sb.deleteCharAt(0);
		}

		return sb.toString();
	}

	public String strAdd(String num1, String num2) {
		int maxStrLen = (num1.length() > num2.length() ? num1.length() : num2.length()) + 1;
		int d1, d2;
		boolean n1Neg = (num1.charAt(0) == '-'), n2Neg = (num2.charAt(0) == '-');	// sign
		if (n1Neg && n2Neg) return "-" + strAdd(num1.substring(1), num2.substring(1));
		if (n2Neg) return strSubtract(num1, num2.substring(1));
		if (n1Neg) return strSubtract(num2, num1.substring(1));

		String n1 = new StringBuilder(num1).reverse().toString();
		String n2 = new StringBuilder(num2).reverse().toString();

		// Only positive values now...
		int[] d = new int[maxStrLen];

		//add each digit and sum at the corresponding positions
		for(int i=0; i < maxStrLen-1; i++){
			d1 = (i < n1.length() ? (n1.charAt(i)-'0') : 0);
			d2 = (i < n2.length() ? (n2.charAt(i)-'0') : 0);
			d[i] = d1 + d2;
		}

		StringBuilder sb = new StringBuilder();

		//calculate each digit
		for(int i=0; i<d.length; i++){
			int mod = d[i]%10;
			int carry = d[i]/10;
			if(i+1<d.length){
				d[i+1] += carry;
			}
			sb.insert(0, mod);
		}

		//remove front 0's
		while(sb.charAt(0) == '0' && sb.length()> 1){
			sb.deleteCharAt(0);
		}

		return sb.toString();
	}

	// Implements num1 - num2
	// Operand (parameters) ORDER IMPORTANT
	public String strSubtract(String num1, String num2) {

		// Trivial cases
		if (isZero(num2)) return num1;

		boolean negative = false;
		String n1, n2;
		int maxStrLen = (num1.length() > num2.length() ? num1.length() : num2.length()) + 1;
		int d1, d2;
		boolean n1Neg = (num1.charAt(0) == '-'), n2Neg = (num2.charAt(0) == '-');	// sign

		if (n2Neg) return strAdd(num1, num2.substring(1));
		if (n1Neg) return "-" + strAdd(num1.substring(1), num2);

		// Only positive values now...
		if (strNumericCompare(num1, num2) >= 0) {
			n1 = new StringBuilder(num1).reverse().toString();
			n2 = new StringBuilder(num2).reverse().toString();
		} else {
			n2 = new StringBuilder(num1).reverse().toString();
			n1 = new StringBuilder(num2).reverse().toString();
			negative = true;
		}

		int[] d = new int[maxStrLen];

		//add each digit and sum at the corresponding positions
		for(int i=0; i<n1.length(); i++){
			d1 = (i < n1.length() ? (n1.charAt(i)-'0') : 0);
			d2 = (i < n2.length() ? (n2.charAt(i)-'0') : 0);
			d[i] = d1 - d2;
		}

		StringBuilder sb = new StringBuilder();

		//calculate each digit
		for(int i=0; i<d.length; i++){
			int mod = (d[i] < 0 ? d[i]+10 : d[i]);
			int carry = (d[i] < 0 ? 1 : 0);
			if(i+1<d.length){
				d[i+1] -= carry;
			}
			sb.insert(0, mod);
		}

		//remove front 0's
		while(sb.charAt(0) == '0' && sb.length()> 1){
			sb.deleteCharAt(0);
		}

		return (negative ? "-" : "") + sb.toString();
	}

	private boolean isZero(String strValue) {
		boolean isZero = true;

		for (int pos = 0; pos < strValue.length(); pos++) {
			if(strValue.charAt(0) != '0' && strValue.charAt(0) != '-' && strValue.charAt(0) != '.') {
				isZero = false;
				break;
			}
		}

		return isZero;
	}

	public String strDivide(String strNumerator, String strDenominator) {

		// check for zeroes
		if (isZero(strDenominator)) return "ERR";
		if (isZero(strNumerator)) return "0"; // Trivial case

		int d1, d2;
		boolean n1Neg = (strNumerator.charAt(0) == '-'), n2Neg = (strDenominator.charAt(0) == '-');    // sign

		if (n1Neg && n2Neg) return strDivide(strNumerator.substring(1), strDenominator.substring(1));
		if (n2Neg) return "-" + strDivide(strNumerator, strDenominator.substring(1));
		if (n1Neg) return "-" + strDivide(strNumerator.substring(1), strDenominator);

		// Only positive values past this point...

		// Handle trivial cases
		if (strNumericCompare(strNumerator, strDenominator) < 0) return "0";
		if (strNumericCompare(strNumerator, strDenominator) == 0) return "1";

		String strOp1;
		int cnt = 0, pos1 = 0, posDigit = 0;

		int[] d = new int[strNumerator.length()];
		pos1 = strDenominator.length() - 1;
		strOp1 = strNumerator.substring(0, pos1);

		while (true) {
			pos1++;
			strOp1 += strNumerator.substring(pos1 - 1, pos1);

			cnt = 0;
			while (strNumericCompare(strOp1, strDenominator) >= 0) {
				strOp1 = strSubtract(strOp1, strDenominator);
				cnt++;
			}

			d[posDigit++] = cnt;
			if (pos1 >= strNumerator.length()) break;
		}

		StringBuilder sb = new StringBuilder();

		//calculate each digit
		for(int i=0; i<posDigit; i++){
			int mod = d[i];
			sb.append(mod);
		}

		//remove front 0's
		while(sb.charAt(0) == '0' && sb.length()> 1){
			sb.deleteCharAt(0);
		}

		return sb.toString();
	}

	public static int strNumericCompare(String num1, String num2) {
		int maxStrLen = num1.length() > num2.length() ? num1.length() : num2.length();
		int d1, d2;
		if (num1.isEmpty() && num2.isEmpty()) return 0;
		if (num1.isEmpty()) return -1;
		if (num2.isEmpty()) return 1;

		boolean n1Neg = (num1.charAt(0) == '-'), n2Neg = (num2.charAt(0) == '-');    // sign

		if (n1Neg && n2Neg) return strNumericCompare(num2.substring(1), num1.substring(1));
		if (n2Neg) return 1;
		if (n1Neg) return -1;

		// Only positive values past this point...
		String n1 = new StringBuilder(num1).reverse().toString();
		String n2 = new StringBuilder(num2).reverse().toString();

		int[] d = new int[ maxStrLen ];

		//add each digit and sum at the corresponding positions
		for(int i=0; i<maxStrLen; i++){
			d1 = (i < n1.length() ? (n1.charAt(i)-'0') : 0);
			d2 = (i < n2.length() ? (n2.charAt(i)-'0') : 0);
			d[i] = d1 - d2;
		}

		// First non-zero value indicates who is bigger!
		for (int i = maxStrLen-1; i >= 0; i--) {
			if (d[i] < 0) return -1;
			if (d[i] > 0) return 1;
		}
		return 0;
	}

	//endregion

	//region Helper Functions
	private static String CleanStr(String strValue) {
		StringBuilder sb = new StringBuilder(strValue.trim());
		while (sb.charAt(0) == '0' && sb.length() > 0) {
			sb.deleteCharAt(0);
		}

		return sb.toString();
	}

	//endregion
	@FXML private void initialize() {

	}
}
