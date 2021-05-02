/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package io.vacuum.lint

/**
 * @author Marcin Bukowiecki
 */
class GoLintOutputParser {

    fun parseLine(line: String): GoLintMessage? {
        if (line.trim().isEmpty()) {
            return null
        }

        var acc = StringBuilder()
        var pointer = 0

        while (true) { //get file path
            val ch = line[pointer]
            if (ch == ':') {
                pointer+=1
                if (line[pointer] == '/') {
                    acc.append(':')
                    acc.append('/')
                } else {
                    break
                }
            } else {
                acc.append(ch)
            }
            pointer+=1
        }
        val filePath = acc.toString()
        acc = StringBuilder()

        while (true) { //get row
            val ch = line[pointer]
            if (ch == ':') {
                pointer+=1
                break
            } else {
                acc.append(ch)
            }
            pointer+=1
        }
        val rowStr = acc.toString()
        acc = StringBuilder()

        while (true) { //get col
            val ch = line[pointer]
            if (ch == ':') {
                pointer+=1
                break
            } else {
                acc.append(ch)
            }
            pointer+=1
        }
        val colStr = acc.toString()
        acc = StringBuilder()

        while (true) { //get message
            if (pointer >= line.length) {
                break
            }
            val ch = line[pointer]
            if (ch == '\n') {
                pointer+=1
                break
            } else {
                acc.append(ch)
            }
            pointer+=1
        }

        val messageText = acc.toString()

        return GoLintMessage(filePath,
            Integer.parseInt(rowStr),
            Integer.parseInt(colStr),
            messageText.trim())
    }
}
