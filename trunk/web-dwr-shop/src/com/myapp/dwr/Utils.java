/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.myapp.dwr;

import java.text.*;

class Utils {

    private static NumberFormat format = new DecimalFormat("EUR #,##0.00");

    static String formatCurrency(int cents) {
        return format.format((double) (cents / 100));
    }
}
