package com.example.kitremap

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hoho.android.usbserial.driver.UsbSerialProider
import com.hoho.android.usbserial.driver.UsbSerialProider.getDefaultProider
import com.hoho.android.usbserial.driver.UsbSerialPort

class MainActivity : AppCompatActivity() {

    private var port: UsbSerialPort? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // ตรวจสอบการเชื่อมต่อ USB OTG สายจูน K-Line
        initUsbConnection()
    }

    private fun initUsbConnection() {
        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val availableDrivers = getDefaultProider().findAllDrivers(manager)
        
        if (availableDrivers.isEmpty()) {
            Toast.makeText(this, "ไม่พบสายจูน USB OTG", Toast.LENGTH_SHORT).show()
            return
        }

        val driver = availableDrivers[0]
        val connection = manager.openDevice(driver.device) ?: run {
            Toast.makeText(this, "ขอสิทธิ์เชื่อมต่อ USB ไม่สำเร็จ", Toast.LENGTH_SHORT).show()
            return
        }

        port = driver.ports[0]
        try {
            port?.open(connection)
            // ตั้งค่า Baud Rate 10400 สำหรับ Honda K-Line หรือ 9600 ตามชนิดชิปสายจูน
            port?.setParameters(10400, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
            Toast.makeText(this, "เชื่อมต่อสายจูน KITREMAP สำเร็จ!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "เชื่อมต่อล้มเหลว: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            port?.close()
        } catch (e: Exception) {
            // ปิดพอร์ตเมื่อออกจากแอป
        }
    }
}