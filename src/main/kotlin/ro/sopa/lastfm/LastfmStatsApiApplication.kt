package ro.sopa.lastfm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LastfmStatsApiApplication

fun main(args: Array<String>) {
	runApplication<LastfmStatsApiApplication>(*args)
}
