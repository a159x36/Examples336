package com.android.dzclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.View
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.min
import androidx.core.graphics.withRotation
import androidx.core.graphics.withSave

class PowerClock (private val mContext: Context, attrs: AttributeSet?) : View(mContext, attrs) {
    private val mClock = Clock.systemDefaultZone()
    private val mDialWidth = 400
    private val mDialHeight = 400
    private val mPaint = Paint()
    private val mHourPaint = Paint()
    private val mMinPaint = Paint()
    private var mCurrent = 0f
    private var mCurrentAv = 0f
    private var mVoltage = 0f
    private var mMinutes = 20f
    private var mHour = 9 + mMinutes / 60
    private var mChanged = false
    private var mLastCurrentTime: Long = 0
    private var mTemperature = 0f
    private var mStatus = 0

    init {
        mPaint.color = Color.BLACK
        mHourPaint.color = Color.BLACK
        mMinPaint.color = Color.BLACK
        mHourPaint.strokeWidth = 12f
        mMinPaint.strokeWidth = 6f
        mPaint.style = Paint.Style.FILL
        mPaint.textSize = 40f
        mPaint.strokeWidth = 4f
        mPaint.isAntiAlias = true
        mHourPaint.isAntiAlias = true
        mMinPaint.isAntiAlias = true
        mPaint.textAlign = Paint.Align.CENTER
    }

    fun setVoltage(mVoltage: Float) {
        this.mVoltage = mVoltage
    }

    fun setTemperature(temp: Float) {
        mTemperature = temp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var hScale = 1.0f
        var vScale = 1.0f

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = widthSize.toFloat() / mDialWidth.toFloat()
        }

        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = heightSize.toFloat() / mDialHeight.toFloat()
        }

        val scale = min(hScale.toDouble(), vScale.toDouble()).toFloat()

        setMeasuredDimension(
            resolveSizeAndState((mDialWidth * scale).toInt(), widthMeasureSpec, 0),
            resolveSizeAndState((mDialHeight * scale).toInt(), heightMeasureSpec, 0)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mChanged = true
    }

    private fun updateCurrent() {
        if ((mClock.millis() - mLastCurrentTime) < 1000) return
        mLastCurrentTime = mClock.millis()
        val batteryManager = mContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        mCurrent =
            (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000).toFloat()
        mCurrentAv =
            (batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) / 1000).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //    if(isInEditMode()) canvas.drawColor(Color.BLACK);
        val changed = mChanged
        if (changed) {
            mChanged = false
        }
        if (!isInEditMode) updateCurrent()
        val availableWidth = width
        val availableHeight = height - 80

        val x = availableWidth / 2
        val y = availableHeight / 2

        val w = mDialWidth
        val h = mDialHeight
        val s: String = if (mStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            if (mCurrent < 0) {
                "Charging: " + "%.2f".format(-mCurrent / 1000) +
                        "A · " + "%.2f".format(-mCurrent * mVoltage / 1000) + "W · ${mTemperature}°C"
            } else {
                "Charging"
            }
        } else "${mCurrent.toInt()}mA · Av ${mCurrentAv.toInt()}mA"

        canvas.drawText(s, x.toFloat(), (y + w / 2 + 30).toFloat(), mPaint)

        canvas.withSave {
            for (i in 0..59) {
                var len = 10
                if (i % 5 == 0) len = 25
                drawLine(
                    x.toFloat(),
                    (y - (h / 2) + 40).toFloat(),
                    x.toFloat(),
                    (y - (h / 2) + 40 + len).toFloat(),
                    mPaint
                )
                rotate(6f, x.toFloat(), y.toFloat())
            }
        }
        canvas.withRotation(mHour / 12.0f * 360.0f, x.toFloat(), y.toFloat()) {
            drawLine(
                x.toFloat(),
                (y - (h / 2) + 90).toFloat(),
                x.toFloat(),
                y.toFloat(),
                mHourPaint
            )

        }
        canvas.withRotation(mMinutes / 60.0f * 360.0f, x.toFloat(), y.toFloat()) {
            drawLine(
                x.toFloat(),
                (y - (h / 2) + 68).toFloat(),
                x.toFloat(),
                y.toFloat(),
                mMinPaint
            )
        }
        canvas.drawCircle(x.toFloat(), y.toFloat(), 10f, mMinPaint)
    }

    fun onTimeChanged() {
        val nowMillis = mClock.millis()
        val localDateTime: LocalDateTime = toLocalDateTime(nowMillis, mClock.zone)

        val hour = localDateTime.hour
        val minute = localDateTime.minute
        val second = localDateTime.second

        mMinutes = minute + second / 60.0f
        mHour = hour + mMinutes / 60.0f
        mChanged = true
        invalidate()
    }

    fun setStatus(status: Int) {
        mStatus = status
    }

    private fun toLocalDateTime(timeMillis: Long, zoneId: ZoneId?): LocalDateTime {
        val instant = Instant.ofEpochMilli(timeMillis)
        return LocalDateTime.ofInstant(instant, zoneId)
    }
}
