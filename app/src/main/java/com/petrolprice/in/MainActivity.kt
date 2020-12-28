package com.petrolprice.`in`

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.text.DecimalFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mYear: Int = 0;
    private var mMonth: Int = 0;
    private var mDay: Int = 0;
    private var mHour: Int = 0;
    private var mMinute: Int = 0;
    private var mSeconds: Int = 0;

    var posToPrint: String? = null
    var PACKAGE_NAME: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PACKAGE_NAME =
            applicationContext.packageName

        // access the items of the list
        val petrolPumps = resources.getStringArray(R.array.PetrolPumps)

        edt_Address.text =
            "Unnamed Road, Chikhalwadi, Bopodi, Pune, 411020"
        edt_Phone_No.text = "NA"
        //edt_Code.text = "No. 091123"

        // access the spinner
        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, petrolPumps
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // write code to perform some action
                }

                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {

                    /*Toast.makeText(
                        this@MainActivity, getString(R.string.selected_item) + " " +
                                "" + petrolPumps[position], Toast.LENGTH_SHORT
                    ).show()*/

                    var pos = petrolPumps[position]
                    posToPrint = pos

                    when (pos) {

                        "Mauli Petroleum" -> {
                            edt_Address.text =
                                "BANER MAIN ROAD, Pune, Maharashtra 411045"
                            edt_Phone_No.text = "098902 51393"
                            //edt_Code.text = "No. 091123"
                        }
                        "HPCL Pump" -> {
                            edt_Address.text = "Plot NO. 132 Pune Nasikh, NH 50, MIDC, Bhosari, Pune, 411026"
                            edt_Phone_No.text = "098500 26094"
                            //edt_Code.text = "No. 052145"
                        }
                        "Vardhaman Petrol Depot" -> {
                            edt_Address.text =
                                "Survey No. 130/2/1/1, Mumbai-Bangalore Highway, Warje, Pune, 411052"
                            edt_Phone_No.text = "020 2523 6001"
                            //edt_Code.text = "No. 021547"
                        }
                        "Gaikwad Pump" -> {
                            edt_Address.text =
                                "137+138 Near Telphone EXC. Road, Marutirao Gaikwad Nagar, Pune, 411007"
                            edt_Phone_No.text = "098223 92977"
                            //edt_Code.text = "No. 022856"
                        }
                        else -> { // Note the block
                            print("nothing selected")
                        }
                    }

                }

            }

        }


        txtDate.setOnClickListener {

            // Get Current Date
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                this,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    txtDate.text = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.show()

        }


        txtTime.setOnClickListener {

            // Get Current Time
            val c: Calendar = Calendar.getInstance()
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            mSeconds = c.get(Calendar.SECOND);

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { view, hourOfDay, minute ->
                    txtTime.text = "$hourOfDay:$minute"
                }, mHour, mMinute, true
            )
            timePickerDialog.show()
        }

        textPrint.setOnClickListener {

            InPrintReceiptUtil()

        }

        txtTotal.setOnClickListener {

           /* if(validate()){

            }*/

            val id = radioGroup.checkedRadioButtonId

            val radioButton = findViewById<RadioButton>(id)

            radioButton.text

            var rateNum = edt_Rate.text.toString()
            val rateNumFloat: Float = rateNum.toFloat()


            var qtyNum = edt_qty.text.toString()
            val qtyNumFloat: Float = qtyNum.toFloat()

            txtTotal.text = ("₹ " + (rateNumFloat * qtyNumFloat).toString())
            manageBlinkEffect()
        }

    }

    /*private fun validate(): Boolean {





    }*/

    private fun clearAllFields() {
        edt_Address.text = ""
        edt_Phone_No.text = ""
       // edt_Code.text = ""
        textPrint.visibility = View.GONE
        txtDate.text = ""
        txtTime.text = ""
    }

    @SuppressLint("WrongConstant")
    private fun manageBlinkEffect() {

        textPrint.visibility = View.VISIBLE

        val anim = ObjectAnimator.ofInt(
            textPrint, "textColor", Color.WHITE, Color.RED,
            Color.WHITE
        )
        anim.duration = 1500
        anim.setEvaluator(ArgbEvaluator())
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.start()
    }


    fun InPrintReceiptUtil(

        /*context: Context?, address: String?, phNo: String,
        No: String, date: String, isDuplicate: Boolean*/
    ) {
        val jsonFinalData = JSONObject()
        var jsonObject: JSONObject
        val jsonArrayInReceipt = JSONArray()
        try {
            jsonObject = JSONObject()
            jsonObject.put("text", posToPrint)
            jsonObject.put("font", GlobalConstant.fontEnum.LARGE)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", edt_Address.text.toString())
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "PH : ${edt_Phone_No.text.toString()}")
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "CASH/BILL")
            jsonObject.put("font", GlobalConstant.fontEnum.LARGE)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "No. ${edt_Code.text}             ${txtDate.text}")
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.LEFT)
            jsonObject.put("line", true)
            jsonArrayInReceipt.put(jsonObject)

            val line =
                String.format("%1$-1s %2$-10s %3$2s %4$5s %5$2s", "","DISCRIPTION","QTY","RATE","AMOUNT")
            jsonObject = JSONObject()
            jsonObject.put("text", line)
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.LEFT)
            jsonObject.put("line", true)
            jsonArrayInReceipt.put(jsonObject)

            var doubleQty : Double? = edt_qty.text.toString().toDouble()
           var qtyFormated=String.format("%.1f%n",doubleQty )

            val line2 =
                String.format("%1$-1s %2$-10s %3$3s %4$5s %5$6s", "","PETROL", "${edt_qty.text}","${edt_Rate.text}","500")

            /*val line2 =
                String.format("%1$-1s %2$-10s %3$2s %4$2s %5$2s", "","PETROL", "${edt_qty.text}","${edt_Rate.text}","500")*/

            jsonObject = JSONObject()
            jsonObject.put("text", line2)
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.LEFT)
            jsonObject.put("line", true)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "CASH : " +round(txtTotal.text.split(" ")[1],2))
            jsonObject.put("font", GlobalConstant.fontEnum.LARGE)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", true)
            jsonArrayInReceipt.put(jsonObject)

            val ramdom = (0..6).random()
            jsonObject = JSONObject()
            jsonObject.put("text", "${txtTime.text}                 M/C NO ${ramdom}")
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.LEFT)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)


            jsonObject = JSONObject()
            jsonObject.put("text", " ")
            jsonObject.put("font", GlobalConstant.fontEnum.SMALL)
            jsonObject.put("align", GlobalConstant.textAlign.LEFT)
            jsonObject.put("line", true)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "THANK YOU!!")
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)

            jsonObject = JSONObject()
            jsonObject.put("text", "PLEASE VISIT AGAIN!")
            jsonObject.put("font", GlobalConstant.fontEnum.MEDIUM)
            jsonObject.put("align", GlobalConstant.textAlign.CENTER)
            jsonObject.put("line", false)
            jsonArrayInReceipt.put(jsonObject)


            jsonFinalData.put("print_receipt_array", jsonArrayInReceipt)
            sendToPrintApp(jsonFinalData)
            clearAllFields()
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendToPrintApp(jsonObject: JSONObject) {
        val launchIntent = Intent(Intent.ACTION_MAIN)
        launchIntent.component = ComponentName(
            "com.apnapay.szzt",
            "com.apnapay.szzt" + ".PrintActivity"
        )
        if (launchIntent != null) {
            launchIntent.putExtra("package", PACKAGE_NAME)
            launchIntent.putExtra("utility_name", "print")
            launchIntent.putExtra("print_data", jsonObject.toString())
            launchIntent.putExtra(
                "activity_path",
                "com.petrolprice.`in`.MainActivity"
            )
            launchIntent.putExtra(
                "activity",
                "com.petrolprice.`in`.MainActivity"
            )
            startActivity(launchIntent)
        }
    }

    fun round(Rval: String?, Rpl: Int): String? {
        var df: DecimalFormat? = null //("#.00");
        var finalStr: String? = ""
        if (Rval != null) {
            if (Rval.contains(".")) {
                val inputArr = Rval.split("\\.").toTypedArray()
                df = DecimalFormat("#0.00")
                finalStr = df.format(Rval.toDouble())
            } else {
                finalStr = "$Rval.00"
            }
        } else {
            finalStr = "0.00"
        }
        return finalStr
    }

  /*public fun deleteCache( context: Context) {
    try {
         var dir : File = context.getCacheDir();
        deleteDir(dir);
    } catch ( e : Exception) { e.printStackTrace();}
}

    public fun deleteDir( dir : File) : Boolean {
    if (dir != null && dir.isDirectory()) {
        var children  = dir.list()
        for ( i :Int in children.length) {
            boolean success = deleteDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
        return dir.delete();
    } else if(dir!= null && dir.isFile()) {
        return dir.delete();
    } else {
        return false;
    }
}*/




}
