package com.webmetaio.tools.time

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.LocalDate


interface Clock {

  DateTime getNow()

  LocalDate getToday()

  void setFixedInstant(DateTime instant)

  void setOffset(Duration offset, boolean intoThePast)

}
