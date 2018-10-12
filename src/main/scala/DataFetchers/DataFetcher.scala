package DataFetchers

import org.apache.spark.sql.DataFrame


trait DataFetcher {
  abstract def fetchData(): DataFrame
}

