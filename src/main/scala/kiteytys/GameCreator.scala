package kiteytys

import java.time.LocalDateTime

import com.typesafe.scalalogging.LazyLogging
import kiteytys.db.Repositories
import kiteytys.email.Mailer
import kiteytys.pdf.PDF

import scalaz.concurrent.Task

final class GameCreator(repos: Repositories, mailer: Mailer, pdf: PDF) extends LazyLogging {

  /**
    * Persist given game, generate a PDF, and send it by email
    *
    * @param gameInput game to create
    * @return PDF bytes
    */
  def create(gameInput: GameInput): Task[Array[Byte]] = for {
    time <- currentTime
    owner <- repos.owner.fetchById(gameInput.owner.toLowerCase)
    cards <- fetchCards(gameInput)
    game = Game.fromInput(gameInput, owner, time, cards)
    pdfBytes <- generatePdf(game)
    _ <- persistAndEmitGame(game, pdfBytes)
  } yield pdfBytes

  private def currentTime: Task[LocalDateTime] = Task.now(LocalDateTime.now())

  private def fetchCards(game: GameInput) =
    repos.card
      .fetchByCodes(game.codes)
      .map(Card.collectionFromIterable)

  private def generatePdf(game: Game) = {
    val content = html.pdf.render(game)
    pdf.generate(content.toString)
  }

  private def persistAndEmitGame(game: Game, pdfBytes: Array[Byte]): Task[Unit] = for {
    _ <- repos.game.create(game)
    _ <- pdf.save(pdfBytes)
    _ <- mailer.sendPDF(game.email, pdfBytes)
    _ <- mailer.sendGame(game)
  } yield {}
}
