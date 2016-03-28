package kiteytys

import org.http4s.UrlForm

import scala.util.Try

object FormParsing {
  type FieldConverter = String => String

  sealed trait Error {
    def toFinnish(converField: FieldConverter): String
  }

  case class MissingField(field: String) extends Error {
    def toFinnish(convertField: FieldConverter): String =
      s"Kentän '${convertField(field)}' tiedot puuttuvat."
  }

  case class InvalidFormat(field: String) extends Error {
    def toFinnish(convertField: FieldConverter): String =
      s"Kentän '${convertField(field)}' tiedot ovat väärää muotoa."
  }

  case class InvalidRange(field: String, min: Int, max: Int) extends Error {
    def toFinnish(converField: FieldConverter): String =
      s"Kentän '${converField(field)}' arvojen on oltava väliltä $min - $max."
  }

  def stringToInt(s: String): Option[Int] = Try(s.toInt).toOption

  def nonEmptyString(s: String): Option[String] =
    if (s.isEmpty) None
    else Some(s)

  def stringField(form: UrlForm, field: String): Either[Error, String] =
    form.getFirst(field).toRight(MissingField(field))

  def optionalStringField(form: UrlForm, field: String): Either[Error, Option[String]] =
    Right(form.getFirst(field))

  def intField(form: UrlForm, field: String, min: Int, max: Int): Either[Error, Int] =
    stringField(form, field).right
      .flatMap(s => stringToValidInt(field, s, min, max))

  def optionalIntField(form: UrlForm, field: String, min: Int, max: Int): Either[Error, Option[Int]] =
    form.getFirst(field) match {
      case Some(s) => stringToValidInt(field, s, min, max).right.map(Some(_))
      case None => Right(None)
    }

  def stringToValidInt(field: String, s: String, min: Int, max: Int): Either[Error, Int] =
    stringToInt(s)
      .toRight(InvalidFormat(field)).right
      .flatMap(i => validRange(field, i, min, max))

  private def validRange(field: String, i: Int, min: Int, max: Int) =
    if (i >= min && i <= max) Right(i)
    else Left(InvalidRange(field, min, max))

  def gameFieldToFinnish(field: String): String = field match {
    case "owner" => "pelitunniste"
    case "email" => "sähköpostiosoite"
    case "strongCard" => "vahvin kortti"
    case "strongNum" => "vahvin arvosana"
    case "weakCard" => "heikoin kortti"
    case "weakNum" => "heikoin arvosana"
    case "importantCard" => "tärkein kortti"
    case "importantNum" => "tärkein arvosana"
    case "hardCard" => "vaikein kortti"
    case "hardNum" => "vaikein arvosana"
    case "tediousCard" => "ikävin kortti"
    case "tediousNum" => "ikävin arvosana"
    case "inspiringCard" => "innostavin kortti"
    case "inspiringNum" => "innostavin arvosana"
    case "topaasia" => "topaasia"
    case "topaasiaAnswer" => "avoin vastaus"
    case "rating" => "loppu arvio"
    case s => s"[$s]"
  }
}
