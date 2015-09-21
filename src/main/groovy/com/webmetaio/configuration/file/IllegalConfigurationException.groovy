package com.webmetaio.configuration.file

class IllegalConfigurationException extends RuntimeException {

  IllegalConfigurationException(String msg) {
    super(msg)
  }

  IllegalConfigurationException(String msg, Throwable cause) {
    super(msg, cause)
  }

  IllegalConfigurationException(Throwable cause) {
    super(cause)
  }

}

