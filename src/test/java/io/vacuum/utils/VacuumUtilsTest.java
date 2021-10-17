/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Marcin Bukowiecki
 */
public class VacuumUtilsTest {

  @Test
  public void testDetectDelim_1() {
    final String result = VacuumUtils.INSTANCE.detectDelim("%v %v".split("%v"));
    Assertions.assertEquals(" ", result);
  }

  @Test
  public void testDetectDelim_2() {
    final String result = VacuumUtils.INSTANCE.detectDelim("%v  %v".split("%v"));
    Assertions.assertEquals("  ", result);
  }

  @Test
  public void testDetectDelim_3() {
    final String result = VacuumUtils.INSTANCE.detectDelim("%v%v".split("%v"));
    Assertions.assertEquals("", result);
  }

  @Test
  public void testDetectDelim_4() {
    final String result = VacuumUtils.INSTANCE.detectDelim(" %v%v".split("%v"));
    Assertions.assertEquals("", result);
  }

  @Test
  public void testDetectDelim_5() {
    final String result = VacuumUtils.INSTANCE.detectDelim(" %v  %v".split("%v"));
    Assertions.assertEquals("  ", result);
  }

  @Test
  public void testDetectDelim_6() {
    final String result = VacuumUtils.INSTANCE.detectDelim("%v  %v  %v".split("%v"));
    Assertions.assertEquals("  ", result);
  }

  @Test
  public void testDetectDelim_7() {
    final String result = VacuumUtils.INSTANCE.detectDelim("   %v  %v  %v".split("%v"));
    Assertions.assertEquals("  ", result);
  }

  @Test
  public void testDetectDelim_8() {
    final String result = VacuumUtils.INSTANCE.detectDelim("   %v   %v  %v     ".split("%v"));
    Assertions.assertNull(result);
  }

  @Test
  public void testDetectDelim_9() {
    final String result = VacuumUtils.INSTANCE.detectDelim("%v   %v  %v".split("%v"));
    Assertions.assertNull(result);
  }
}
