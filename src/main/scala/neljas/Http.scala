package neljas

import org.http4s.headers.{`Content-Type`}
import org.http4s.MediaType._
import org.http4s.{UrlForm, HttpService}
import org.http4s.dsl._
import org.http4s.twirl.TwirlInstances
import org.http4s.server.staticcontent
import org.http4s.server.staticcontent.ResourceService.Config

import neljas.pdf.PDF
import neljas.conf.Settings
import neljas.util.Util
import neljas.email.Mailer

final case class Http(conf: Settings) extends TwirlInstances {

  private val static = cachedResource(Config("/static", "/static"))

  val route = HttpService {
    case r @ GET -> "static" /: _ => static(r)

    case GET -> Root / "conf" =>
      Ok(conf.smtpPort.toString)

    case GET -> Root =>
      Ok(html.index.render())

    case req @ POST -> Root / "submit" =>
      req.decode[UrlForm] { form =>
        User.fromForm(form) match {
          case Left(errors) => BadRequest(errors)
          case Right(user) =>
            val page = html.pdf.render(user).toString()
            val pdf  = PDF.generate(page)
            val path = Util.savePDF(conf.pdfPath, pdf)
            val ed   = EmailData(conf, user.email, path)
            Mailer.sendPDF(ed)
            Ok(pdf).withContentType(Some(`Content-Type`(`application/pdf`)))
        }
      }
  }

  private def cachedResource(config: Config): HttpService = {
    val cachedConfig = config.copy(cacheStartegy = staticcontent.MemoryCache())
    staticcontent.resourceService(cachedConfig)
  }
}
