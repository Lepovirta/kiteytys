package kiteytys.email

import javax.mail.util.ByteArrayDataSource

import com.typesafe.scalalogging.LazyLogging
import kiteytys.Game
import kiteytys.conf.Conf
import org.apache.commons.mail.{DefaultAuthenticator, Email, EmailAttachment, EmailException, MultiPartEmail, SimpleEmail}

import scalaz.concurrent.Task

object Mailer {
  val attachmentMime = "application/pdf"
}

final class Mailer(conf: Conf.Email) extends LazyLogging {
  import Mailer._

  def sendPDF(recipient: String, data: Array[Byte]): Task[String] = {
    val email = new MultiPartEmail()

    setup(email, recipient, conf.responseSubject, conf.responseMessage)

    email.attach(
      new ByteArrayDataSource(data, attachmentMime),
      conf.attachmentName,
      conf.attachmentDescription,
      EmailAttachment.ATTACHMENT
    )

    send(email, recipient)
  }

  def sendGame(game: Game): Task[String] = {
    val email = new SimpleEmail()
    val recipient = conf.adminEmail

    val subject = "Uusi peli Kiteyttäjässä"
    val message = s"""Pelin tiedot:
      |
      | Pelitunniste: ${game.owner.render}
      | Sähköposti: ${game.email}
      |
      | Vahvin: ${game.strong.map(_.render).getOrElse("-")}
      | Heikoin: ${game.weak.map(_.render).getOrElse("-")}
      | Tärkein: ${game.important.map(_.render).getOrElse("-")}
      | Vaikein: ${game.hard.map(_.render).getOrElse("-")}
      | Ikävin: ${game.tedious.map(_.render).getOrElse("-")}
      | Innostavin: ${game.inspiring.map(_.render).getOrElse("-")}
      |
      | Topaasia: ${game.topaasia.map(_.card.subject).getOrElse("-")}
      | Perustelu: ${game.topaasia.map(_.answer).getOrElse("-")}
      |
      | Hyöty: ${game.rating}
      |""".stripMargin

    setup(email, recipient, subject, message)

    send(email, recipient)
  }


  private def setup(email: Email, recipient: String, subject: String, message: String): Unit = {
    email.setCharset("utf-8")
    email.setHostName(conf.smtp.host)
    email.setSmtpPort(conf.smtp.port)
    email.setAuthenticator(new DefaultAuthenticator(conf.smtp.user, conf.smtp.password))
    email.setSSLOnConnect(conf.smtp.ssl)

    email.setFrom(conf.fromEmail, conf.fromName)
    email.addTo(recipient)

    email.setSubject(subject)
    val _ = email.setMsg(message)
  }

  private def send(email: Email, recipient: String): Task[String] =
    try {
      val result = email.send()
      logger.info(s"Sent email to $recipient. Message ID: $result")
      Task.now(result)
    } catch {
      case ex: EmailException => Task.fail(ex)
      case ex: IllegalStateException => Task.fail(ex)
    }
}
