package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("macOS Launcher", appName)
  }

  @Test
  fun `test lunar calendar conversion`() {
    // Test conversion of a known date
    val lunar = com.example.util.LunarCalendarHelper.convertSolar2Lunar(1, 1, 2026)
    org.junit.Assert.assertNotNull(lunar)
    org.junit.Assert.assertTrue(lunar.day in 1..30)
    org.junit.Assert.assertTrue(lunar.month in 1..12)
    org.junit.Assert.assertNotNull(lunar.canChiYear)
  }
}
