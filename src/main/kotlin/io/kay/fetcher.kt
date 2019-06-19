package io.kay

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.File
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

class Fetcher {
  fun assemble(): String {
  var file = this::class.java.classLoader.getResource("template.html")!!.readText()

    val websites = mapOf(
      Pair("Holztisch", "http://www.yummyaki.at/index.php/Speisekarte.html"),
      Pair("Golden Harp", "https://www.goldenharp1070.at/images/menu1070.jpg"),
      Pair("Stadtkrems", "http://stadtkrems.steman.at/"),
      Pair("Kantine", "http://www.kantine.at/Speisen/Menu"),
      Pair("Gundis", "https://gundis.at/wp-content/uploads/%%year%%/%%io.kay.month%%/Mittagsmen%C3%BC-KW-%%week%%.pdf"), //generisch
      Pair("kaoo", "http://www.kaoo.at/wp-content/uploads/2017/10/kaoo-mittagsmen%C3%BC_online.pdf") //vielleicht generisch
    )

    //Holztisch
    val holztisch = getById(websites["Holztisch"], "table_13")
    file = file.replace("%%HOLZTISCH%%", holztisch.toString())

    //Stadtkrems
    val krems = getById(websites["Stadtkrems"], "wochenkarte")
    file = file.replace("%%KREMS%%", krems.toString())

    //Kantine
    val kantine = get(websites["Kantine"]).getElementsByClass("menu-list")[0]
    file = file.replace("%%KANTINE%%", kantine.toString())

    val date = LocalDate.now()

    //Gundis
    var gundis = websites["Gundis"]!!
      .replace("%%year%%", "" + date.year)
      .replace("%%io.kay.month%%", "" + month(date))
      .replace("%%week%%", "" + date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()))
    file = file.replace("%%GUNDIS%%", gundis)

    return file
  }

  fun month(date: LocalDate): String {
    val monthValue = date.monthValue
    return if(monthValue < 10) {
      "0$monthValue"
    } else {
      "$monthValue"
    }
  }

  fun get(url: String?): Element {
    return Jsoup.connect(url).get().body()
  }

  fun getById(url: String?, id: String): Element {
    return Jsoup.connect(url).get().body().getElementById(id)
  }
}