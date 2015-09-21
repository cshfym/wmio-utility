package com.webmetaio.tools.time

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.LocalDate

import javax.naming.OperationNotSupportedException

class ProductionClock implements Clock {

  static final DateTimeZone UTC = DateTimeZone.forID('UTC')

  @Override
  DateTime getNow() {
    new DateTime(UTC)
  }

  @Override
  LocalDate getToday() {
    now.toLocalDate()
  }

  @Override
  void setFixedInstant(DateTime instant) {
    throw new OperationNotSupportedException('Changing the system time in production is not allowed!')
  }

  @Override
  void setOffset(Duration offset, boolean intoThePast) {
    throw new OperationNotSupportedException('Changing the system time in production is not allowed!')
  }

}
