package neljas.email

import org.apache.commons.mail._

import neljas.EmailData

object Mailer {

  val message = "Hello!"
  val subject = "Subj"

  val attName = "attachment"
  val attDesc = "description"

  def sendPDF(ed: EmailData): Unit = {
    var attachment = new EmailAttachment()
    attachment.setPath(ed.filePath)
    attachment.setDisposition(EmailAttachment.ATTACHMENT)
    attachment.setDescription(attDesc)
    attachment.setName(attName)

    var email = new MultiPartEmail()
    email.setHostName(ed.host)
    email.setSmtpPort(ed.port)
    email.setAuthenticator(new DefaultAuthenticator(ed.user, ed.password))
    email.setSSLOnConnect(true)
    email.addTo(ed.to, ed.toName)
    email.setFrom(ed.from, ed.fromName)
    email.setSubject(subject)
    email.setMsg(message)

    email.attach(attachment)
    email.send()
  }
}
