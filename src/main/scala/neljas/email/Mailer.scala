package neljas.email

import javax.mail.util.ByteArrayDataSource

import com.typesafe.scalalogging.LazyLogging
import neljas.User
import neljas.conf.Conf
import org.apache.commons.mail.{EmailException, EmailAttachment, DefaultAuthenticator, MultiPartEmail}

import scalaz.concurrent.Task

final class Mailer(conf: Conf.Smtp) extends LazyLogging {

  val message = "Hello!"
  val subject = "Subj"

  val attachmentName = "attachment.pdf"
  val attachmentDescription = "PDF"
  val attachmentMime = "application/pdf"

  def sendPDF(recipient: User, data: Array[Byte]): Task[String] = {
    val email = dataToEmail(recipient, data)
    try {
      val result = email.send()
      logger.info(s"Sent email to ${recipient.email}. Message ID: $result")
      Task.now(result)
    } catch {
      case ex: EmailException => Task.fail(ex)
      case ex: IllegalStateException => Task.fail(ex)
    }
  }

  private def dataToEmail(recipient: User, data: Array[Byte]) = {
    val email = new MultiPartEmail()

    email.setHostName(conf.host)
    email.setSmtpPort(conf.port)
    email.setAuthenticator(new DefaultAuthenticator(conf.user, conf.password))
    email.setSSLOnConnect(true)
    email.addTo(recipient.email, recipient.name)
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
