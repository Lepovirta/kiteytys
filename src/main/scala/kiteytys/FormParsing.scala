package kiteytys

import org.http4s.UrlForm

import scala.util.Try

object FormParsing {
  def stringToInt(s: String): Option[Int] = Try(s.toInt).toOption

  def stringField(form: UrlForm, field: String): Either[String, String] =
    form.getFirst(field).toRight(s"Missing field '$field'")

  def intField(form: UrlForm, field: String,
    min: Int = Int.MinValue, max: Int = Int.MaxValue): Either[String, Int] =
    form.getFirst(field)
      .flatMap(stringToInt)
      .toRight(s"Invalid format for field '$field'")
      .right.flatMap { i =>
      if (i < min) Left(s"Number $i should be greater or equal than $min")
      else Right(i)
    }
      .right.flatMap { i =>
      if (i > max) Left(s"Number $i should be less or equal than $max")
      else Right(i)
    }
}
