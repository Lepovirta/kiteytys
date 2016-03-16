package kiteytys

import kiteytys.FormParsing.FieldConverter

object Errors {

  trait UserVisibleError {
    def userVisibleMessage: String
  }

  class NoOwnerFound(ownerId: String) extends Exception with UserVisibleError {
    def toFinnish: String = s"Tunnistetta '$ownerId' ei löytynyt."
    def userVisibleMessage: String = toFinnish
  }

  class FormParsingError(error: FormParsing.Error, fieldConverter: FieldConverter)
    extends Exception with UserVisibleError {

    def toFinnish: String = error.toFinnish(fieldConverter)
    def userVisibleMessage: String = toFinnish
  }
}
