package kiteytys.email

import javax.mail.util.ByteArrayDataSource

import com.typesafe.scalalogging.LazyLogging
import kiteytys.conf.Conf
import kiteytys.Game
import org.apache.commons.mail.{EmailException, EmailAttachment, DefaultAuthenticator, MultiPartEmail, SimpleEmail, Email}

import scalaz.concurrent.Task

final class Mailer(conf: Conf.Smtp) extends LazyLogging {

  def sendPDF(recipient: String, data: Array[Byte]): Task[String] = {
    val email = new MultiPartEmail()

    val subject = "Kiteyttäjä"
    val message = """Moi!
      |
      |Toivottavasti pelihetki oli antoisa - tässä Kiteyttäjä. Huomaat Kiteyttäjän visuaalisen kauneusleikkauksen etenemisen tässä matkan varrella.
      |
      |Me uskomme palautteen voimaan. Haasta meidät palautteella niin tulet huomaamaan kehityksen seuraavaan pelisessioon mennessä.
      |
      |Risut, ruusut ja pajut voi ohjata esimerkiksi johonkin seuraavista:
      |
      |galla@galliwashere.com
      |0400246626
      |@jussigalla
      |
      |innolla,
      |gällit
      |
      |Play. Focus. Do. Repeat""".stripMargin

    val attachmentName = "Kiteyttaja.pdf"
    val attachmentDescription = "PDF"
    val attachmentMime = "application/pdf"

    setup(email, recipient, subject, message)

    email.attach(
      new ByteArrayDataSource(data, attachmentMime),
      attachmentName,
      attachmentDescription,
      EmailAttachment.ATTACHMENT
    )

    send(email, recipient)
  }

  def sendGame(game: Game): Task[String] = {
    val email = new SimpleEmail()
    val recipient = conf.admin

    val subject = "Uusi peli kiteyttäjässä"
    val message = "TODO: content"

    setup(email, recipient, subject, message)

    send(email, recipient)
  }


  private def setup(email: Email, recipient: String, subject: String, message: String): Unit = {
    email.setHostName(conf.host)
    email.setSmtpPort(conf.port)
    email.setAuthenticator(new DefaultAuthenticator(conf.user, conf.password))
    email.setSSLOnConnect(conf.ssl)

    email.setFrom(conf.from, conf.fromName)
    email.addTo(recipient)

    email.setSubject(subject)
    email.setMsg(message)
  }

  private def send(email: Email, recipient: String): Task[String] = {
    try {
      val result = email.send()
      logger.info(s"Sent email to ${recipient}. Message ID: $result")
      Task.now(result)
    } catch {
      case ex: EmailException => Task.fail(ex)
      case ex: IllegalStateException => Task.fail(ex)
    }
  }
}
