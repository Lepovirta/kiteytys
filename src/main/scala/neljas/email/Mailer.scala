package neljas.email

import javax.mail.util.ByteArrayDataSource

import com.typesafe.scalalogging.LazyLogging
import neljas.conf.Conf
import org.apache.commons.mail.{EmailException, EmailAttachment, DefaultAuthenticator, MultiPartEmail}

import scalaz.concurrent.Task

final class Mailer(conf: Conf.Smtp) extends LazyLogging {

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

  def sendPDF(recipient: String, data: Array[Byte]): Task[String] = {
    val email = dataToEmail(recipient, data)
    try {
      val result = email.send()
      logger.info(s"Sent email to ${recipient}. Message ID: $result")
      Task.now(result)
    } catch {
      case ex: EmailException => Task.fail(ex)
      case ex: IllegalStateException => Task.fail(ex)
    }
  }

  private def dataToEmail(recipient: String, data: Array[Byte]) = {
    val email = new MultiPartEmail()

    email.setHostName(conf.host)
    email.setSmtpPort(conf.port)
    email.setAuthenticator(new DefaultAuthenticator(conf.user, conf.password))
    email.setSSLOnConnect(conf.ssl)
    email.addTo(recipient, recipient)
    email.setFrom(conf.from, conf.fromName)
    email.setSubject(subject)
    email.setMsg(message)

    email.attach(
      new ByteArrayDataSource(data, attachmentMime),
      attachmentName,
      attachmentDescription,
      EmailAttachment.ATTACHMENT)

    email
  }
}
