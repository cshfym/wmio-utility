package com.webmetaio.tools.time

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductionClockConfiguration {

  @Bean
  Clock clock() {
    new ProductionClock()
  }

}
