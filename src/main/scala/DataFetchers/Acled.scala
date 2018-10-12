package DataFetchers

import java.io.PrintWriter
import java.security.cert.X509Certificate

import javax.net.ssl._
import org.apache.spark.sql.{DataFrame, SparkSession}

//object Acled extends DataFetcher{
object Acled {
  /*
  NOTE - THIS IMPLENTATION IS NOT SECURE
  I've temporarily bypassed SSL verification due to an issue with certificate verfication caused by issues on my work
  machine. As long as this implementation is in place, the data is vulnerable to man in the middle attacks and should be
  handled with care.
   */
  val spark: SparkSession = SparkSession.builder.appName("Simple Application").master("local[2]").getOrCreate()

  def fetchData(): DataFrame = {
    println("Spark Initialized")

    val baseUrl = "https://api.acleddata.com/acled/read.csv"
    val filters = "?limit=10&country=Iran&fields=iso%7Cactor1%7Cactor2%7Cevent_date%7Cevent_type%7Cinteraction%7Cfatalities%7Clatitude%7Clongitude%7Cadmin1"
    val completeUrl = baseUrl + filters

    fetchUrl(completeUrl)
    val df = parseData()
    df.show()
  }

  // Bypasses both client and server validation.
  object TrustAll extends X509TrustManager {
    val getAcceptedIssuers: Null = null

    def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}

    def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}
  }

  // Verifies all host names by simply returning true.
  object VerifiesAllHostNames extends HostnameVerifier {
    def verify(s: String, sslSession: SSLSession) = true
  }


  def fetchUrl(completeUrl: String): Unit = {
    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, Array(TrustAll), new java.security.SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier(VerifiesAllHostNames)

    new PrintWriter("tempdata.csv") {
      write(scala.io.Source.fromURL(completeUrl).mkString);
      close()
    }

  }

  def parseData(): DataFrame = {

    spark.read.format("csv").option("header", "true").load("tempdata.csv")
    //    val list = rawData.split("\n").filter(_ != "")
  }
}
