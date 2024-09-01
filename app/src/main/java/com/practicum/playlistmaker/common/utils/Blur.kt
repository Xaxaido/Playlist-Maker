@file:Suppress("DEPRECATION")
package com.practicum.playlistmaker.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.ImageView
import androidx.annotation.RequiresApi

object Blur {

    private const val BLUR_RADIUS: Float = 25f
    private const val RENDER_EFFECT_BLUR_RADIUS: Float = 20f

    fun blur(context: Context, view: ImageView, originalBitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurWithRenderEffect(view, originalBitmap)
        } else {
            blurWithRenderScript(context, view, originalBitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurWithRenderEffect(view: ImageView, bitmap: Bitmap) {
        val renderEffect = RenderEffect.createBlurEffect(RENDER_EFFECT_BLUR_RADIUS, RENDER_EFFECT_BLUR_RADIUS, Shader.TileMode.CLAMP)
        val blurredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(blurredBitmap)

        canvas.drawBitmap(bitmap, 0f, 0f, null)
        view.setImageBitmap(blurredBitmap)
        view.setRenderEffect(renderEffect)
    }

    private fun blurWithRenderScript(context: Context, view: ImageView, originalBitmap: Bitmap) {
        val inputBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, originalBitmap)
        val output = Allocation.createTyped(rs, input.type)
        val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        try {
            blurScript.setRadius(BLUR_RADIUS)
            blurScript.setInput(input)
            blurScript.forEach(output)
            output.copyTo(inputBitmap)
        } finally {
            input.destroy()
            output.destroy()
            blurScript.destroy()
            rs.destroy()
        }

        view.setImageBitmap(inputBitmap)
    }
}
