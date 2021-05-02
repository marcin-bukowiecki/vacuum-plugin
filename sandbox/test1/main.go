/*
 * Copyright 2021 Marcin Bukowiecki.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package main

import (
	"time"
)

type Person struct {
	StartDate time.Time `json:"startDate"`
	EndDate   time.Time `json:"endDate"`
}

type Test struct {
	StartDate string
	EndDate   string
}

type Test2 struct {
	name string
	arr []struct {
		i *int
		s *time.Time
	}
}
