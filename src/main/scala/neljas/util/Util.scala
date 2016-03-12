package neljas.util

import java.io.{BufferedOutputStream, FileOutputStream}
import java.util.Date
import java.text.SimpleDateFormat

object Util {

  def savePDF(path: String, pdf: Array[Byte]): String = {
    val fname    = new SimpleDateFormat("yyyyMMdd-hh:mm:ss'.pdf'").format(new Date());
    val fullpath = path + fname
    val bos      = new BufferedOutputStream(new FileOutputStream(fullpath))

    Stream.continually(bos.write(pdf))
    bos.close()
    fullpath
  }

}
