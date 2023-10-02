package com.example.safehome

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale

class Utils {

    companion object {
        var screenWidth = 0
        var screenHeight: Int = 0

        fun isNetworkAvailable(mContext: Context): Boolean {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }

        fun showToast(context: Context, message: String) {
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

        }


        fun saveStringPref(context: Context, key: String, value: String) {

            val sharedPreferences =
                context.getSharedPreferences("SafeHomePref", Context.MODE_PRIVATE)
            val edit = sharedPreferences.edit()
            edit.putString(key, value)
            edit.apply()
        }

        fun getStringPref(context: Context, key: String,  defaultValue: String?): String? {
            val sharedPreferences =
                context.getSharedPreferences("SafeHomePref", Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, defaultValue)
        }

        fun getBooleanPref(context: Context, key: String?, defaultValue: Boolean): Boolean {
            val sharedPref = context.getSharedPreferences("Login_Preferences", Context.MODE_PRIVATE)
            return sharedPref.getBoolean(key, defaultValue)
        }

        fun savebooleanPref(context: Context, key: String?, value: Boolean) {
            val sharedPref = context.getSharedPreferences("Login_Preferences", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(key, value)
            editor.commit()
        }

        fun convertStringToDateMillis(dateString: String, dateFormat: String): Long {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            val date = formatter.parse(dateString)
            return date?.time ?: 0L
        }

        fun changeDateFormat(inputDate: String): String{
            val myFormat = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
            val date = myFormat.parse(inputDate)
            val outPutFormat = SimpleDateFormat("dd-MM-yyy", Locale.getDefault())
            return outPutFormat.format(date)
        }
        fun changeDateFormatToMMDDYYYY(inputDate: String): String{
            val myFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = myFormat.parse(inputDate)
            val outPutFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
            return outPutFormat.format(date)
        }

        fun formatDateAndMonth(inputDate: String): String {
            // Define the input and output date formats
            val inputFormat = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

            try {
                // Parse the input date string to a Date object
                val date = inputFormat.parse(inputDate)

                // Format the parsed date to the desired output format
                return outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return inputDate // Return the input string if parsing fails
            }
        }
        fun formatDateMonthYear(inputDate: String): String {
            // Define the input and output date formats
            val inputFormat = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("DD/MM/yyyy", Locale.getDefault())

            try {
                // Parse the input date string to a Date object
                val date = inputFormat.parse(inputDate)

                // Format the parsed date to the desired output format
                return outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return inputDate // Return the input string if parsing fails
            }
        }
        fun getMonth(inputDate: String): String {
            // Define the input and output date formats
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM", Locale.getDefault())

            try {
                // Parse the input date string to a Date object
                val date = inputFormat.parse(inputDate)

                // Format the parsed date to the desired output format
                return outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return inputDate // Return the input string if parsing fails
            }
        }

        fun formatDateToMonth(inputDate: String): String {
            // Define the input and output date formats
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

            try {
                // Parse the input date string to a Date object
                val date = inputFormat.parse(inputDate)

                // Format the parsed date to the desired output format
                return outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return inputDate // Return the input string if parsing fails
            }
        }


    }


}