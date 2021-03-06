import Main.{airports, parseAirport, parseCountry, parseRunway, runways}
import model.Airport.countryFromCode
import scala.util.Try

object Report {
  val countriesData = parseCountry()
  val airportsData = parseAirport()
  val runwaysData = parseRunway()
  def highest(): String = {
    val AirportsCount: List[(String, Int)] = airportsData.map(_.split(",")(8)).groupBy(identity).mapValues { _.size }.toList
    /*** List of 10 countries with highest number of airports
     * MapReduce => * 1) Map : retrieves tuples from countries (iso_country) and from airports (code)
     * => compare the two and increment the number of airports => we get at the end a list
     * of tuples containing the (name of the country, number of airports)
     * 2) Reduce : we sort the list in descending order to keep the first first tuples and return them */
    val reverseSortedCountries: List[(String, Int)] = AirportsCount.sortWith(-_._2 < -_._2)
    val topTenAirportsCountries: List[(String, Int)] = reverseSortedCountries.take(10)
    topTenAirportsCountries.foreach(x=>println(x._1,x._2))
    "highest() checked"
  }

  def lowest(): String ={
    /*** List of 10 countries with lowest number of airports ***/
    val AirportsCount: List[(String, Int)] = airportsData.map(_.split(",")(8)).groupBy(identity).mapValues { _.size }.toList
    val sortedCountries: List[(String, Int)] = AirportsCount.sortWith(_._2 < _._2)
    val bottomTenAirportsCountries: List[(String, Int)] = sortedCountries.take(10)
    bottomTenAirportsCountries.foreach(x=>println(x._1,x._2))
    "lowest() checked"
  }

  def runwayTypes(): String = {
    /** * Type of Runways ("surface") per Country ** */
    val SurfaceType = runways.map { x => (x.airport_ident, x.surface) }.toMap
    val listCountryType = airports.map(x => (countryFromCode(x.iso_country), Try {SurfaceType(x.ident)}.getOrElse(""))).toList
    val runwaysSurfacePerCountry = listCountryType groupBy (_._1) mapValues { x => x.filterNot {_._2 == ""}.map(_._2)}
    runwaysSurfacePerCountry.toList.foreach(x=>println(x))
    "runwayTypes() checked"

  }

  def commonIdentity(): String = {
    /** ** The 10 most common runway identity ("le_ident") *** */
    // runways : table
    // select * from runwys
    //  on veut common identity => select identity from runways group by
    val mostCommonRunways: List[String] = runways.map(x => Try {x.le_ident}.getOrElse("")).toList.groupBy(identity).mapValues {_.size
    }.toList.sortWith(-_._2 < -_._2).take(10).map { case (id, count) => id }
    mostCommonRunways.foreach(x=>println(x))
    "commonIdentity() checked"

  }

  def InitReport(): Any = scala.io.StdIn.readLine("Please enter your choice > ") match {

    case "1" => highest()

    case "2" => lowest()

    case "3" => runwayTypes()

    case "4" => commonIdentity()
    case _ => println("Wrong input : chose between 1 and 4")
  }

}
