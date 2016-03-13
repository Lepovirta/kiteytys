package neljas.email

import org.apache.commons.mail._

import neljas.EmailData

object Mailer {

  val message = "Hello!"

  def sendPDF(ed: EmailData): Unit = {
    var attachment = new EmailAttachment()
    attachment.setPath(ed.attachment.path)
    attachment.setDisposition(EmailAttachment.ATTACHMENT)
    attachment.setDescription(ed.attachment.description)
    attachment.setName(ed.attachment.name)

    var email = new MultiPartEmail()
    email.setHostName(ed.host)
    email.setSmtpPort(ed.port)
    email.setAuthenticator(new DefaultAuthenticator(ed.user, ed.password))
    email.setSSLOnConnect(true)
    email.addTo(ed.to, ed.toName)
    email.setFrom(ed.from, ed.fromName)
    email.setSubject(ed.subject)
    email.setMsg(message)

    email.attach(attachment)
    email.send()
  }
}
