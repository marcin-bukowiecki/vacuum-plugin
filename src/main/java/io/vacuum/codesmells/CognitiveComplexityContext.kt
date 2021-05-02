/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.codesmells

/**
 * @author Marcin Bukowiecki
 */
class CognitiveComplexityContext {

    private var cognitiveComplexityCounter = 0

    fun incrCognitiveComplexityCounter(by: Int) {
        cognitiveComplexityCounter+=by
    }

    fun getCognitiveComplexityCounter(): Int {
        return cognitiveComplexityCounter
    }
}
