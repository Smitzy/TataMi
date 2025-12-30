package at.tatami.club.presentation.components

import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.StringResource
import tatami.composeapp.generated.resources.*

data class TimezoneItem(
    val id: String, // IANA timezone ID
    val displayName: String, // "New York, USA"
    val region: String, // "Americas", "Europe", etc.
    val abbreviation: String, // "EST/EDT"
    val searchAliases: List<String> = emptyList() // Additional search terms
) {
    val timeZone: TimeZone get() = TimeZone.of(id)
}

object TimezoneData {
    
    val americas = listOf(
        TimezoneItem("America/New_York", "New York, USA", "Americas", "EST/EDT", listOf("EST", "Eastern", "NYC", "NY")),
        TimezoneItem("America/Chicago", "Chicago, USA", "Americas", "CST/CDT", listOf("CST", "Central")),
        TimezoneItem("America/Denver", "Denver, USA", "Americas", "MST/MDT", listOf("MST", "Mountain")),
        TimezoneItem("America/Los_Angeles", "Los Angeles, USA", "Americas", "PST/PDT", listOf("PST", "Pacific", "LA", "SF")),
        TimezoneItem("America/Phoenix", "Phoenix, USA", "Americas", "MST"),
        TimezoneItem("America/Anchorage", "Anchorage, USA", "Americas", "AKST/AKDT", listOf("Anchorage")),
        TimezoneItem("Pacific/Honolulu", "Honolulu, USA", "Americas", "HST"),
        TimezoneItem("America/Toronto", "Toronto, Canada", "Americas", "EST/EDT"),
        TimezoneItem("America/Vancouver", "Vancouver, Canada", "Americas", "PST/PDT"),
        TimezoneItem("America/Halifax", "Halifax, Canada", "Americas", "AST/ADT"),
        TimezoneItem("America/St_Johns", "St. John's, Canada", "Americas", "NST/NDT"),
        TimezoneItem("America/Mexico_City", "Mexico City, Mexico", "Americas", "CST/CDT"),
        TimezoneItem("America/Cancun", "Cancun, Mexico", "Americas", "EST"),
        TimezoneItem("America/Tijuana", "Tijuana, Mexico", "Americas", "PST/PDT"),
        TimezoneItem("America/Guatemala", "Guatemala City, Guatemala", "Americas", "CST"),
        TimezoneItem("America/Costa_Rica", "San José, Costa Rica", "Americas", "CST"),
        TimezoneItem("America/Panama", "Panama City, Panama", "Americas", "EST"),
        TimezoneItem("America/Havana", "Havana, Cuba", "Americas", "CST/CDT"),
        TimezoneItem("America/Santo_Domingo", "Santo Domingo, Dominican Republic", "Americas", "AST"),
        TimezoneItem("America/Puerto_Rico", "San Juan, Puerto Rico", "Americas", "AST"),
        TimezoneItem("America/Jamaica", "Kingston, Jamaica", "Americas", "EST"),
        TimezoneItem("America/Bogota", "Bogotá, Colombia", "Americas", "COT"),
        TimezoneItem("America/Lima", "Lima, Peru", "Americas", "PET"),
        TimezoneItem("America/La_Paz", "La Paz, Bolivia", "Americas", "BOT"),
        TimezoneItem("America/Caracas", "Caracas, Venezuela", "Americas", "VET"),
        TimezoneItem("America/Santiago", "Santiago, Chile", "Americas", "CLT/CLST"),
        TimezoneItem("America/Buenos_Aires", "Buenos Aires, Argentina", "Americas", "ART"),
        TimezoneItem("America/Sao_Paulo", "São Paulo, Brazil", "Americas", "BRT/BRST"),
        TimezoneItem("America/Rio_Branco", "Rio Branco, Brazil", "Americas", "ACT"),
        TimezoneItem("America/Manaus", "Manaus, Brazil", "Americas", "AMT"),
        TimezoneItem("America/Recife", "Recife, Brazil", "Americas", "BRT"),
        TimezoneItem("America/Montevideo", "Montevideo, Uruguay", "Americas", "UYT/UYST"),
        TimezoneItem("America/Asuncion", "Asunción, Paraguay", "Americas", "PYT/PYST"),
        TimezoneItem("America/Paramaribo", "Paramaribo, Suriname", "Americas", "SRT")
    )
    
    val europe = listOf(
        TimezoneItem("Europe/London", "London, UK", "Europe", "GMT/BST", listOf("GMT", "UTC", "London")),
        TimezoneItem("Europe/Dublin", "Dublin, Ireland", "Europe", "GMT/IST", listOf("IST")),
        TimezoneItem("Europe/Lisbon", "Lisbon, Portugal", "Europe", "WET/WEST"),
        TimezoneItem("Europe/Madrid", "Madrid, Spain", "Europe", "CET/CEST"),
        TimezoneItem("Europe/Paris", "Paris, France", "Europe", "CET/CEST", listOf("CET", "Paris")),
        TimezoneItem("Europe/Brussels", "Brussels, Belgium", "Europe", "CET/CEST"),
        TimezoneItem("Europe/Amsterdam", "Amsterdam, Netherlands", "Europe", "CET/CEST"),
        TimezoneItem("Europe/Berlin", "Berlin, Germany", "Europe", "CET/CEST", listOf("Germany", "Berlin")),
        TimezoneItem("Europe/Zurich", "Zurich, Switzerland", "Europe", "CET/CEST", listOf("Switzerland")),
        TimezoneItem("Europe/Rome", "Rome, Italy", "Europe", "CET/CEST", listOf("Italy")),
        TimezoneItem("Europe/Vienna", "Vienna, Austria", "Europe", "CET/CEST", listOf("Austria", "Wien")),
        TimezoneItem("Europe/Prague", "Prague, Czech Republic", "Europe", "CET/CEST"),
        TimezoneItem("Europe/Warsaw", "Warsaw, Poland", "Europe", "CET/CEST", listOf("Poland")),
        TimezoneItem("Europe/Budapest", "Budapest, Hungary", "Europe", "CET/CEST"),
        TimezoneItem("Europe/Copenhagen", "Copenhagen, Denmark", "Europe", "CET/CEST", listOf("Denmark")),
        TimezoneItem("Europe/Stockholm", "Stockholm, Sweden", "Europe", "CET/CEST", listOf("Sweden")),
        TimezoneItem("Europe/Oslo", "Oslo, Norway", "Europe", "CET/CEST", listOf("Norway")),
        TimezoneItem("Europe/Helsinki", "Helsinki, Finland", "Europe", "EET/EEST", listOf("Finland")),
        TimezoneItem("Europe/Athens", "Athens, Greece", "Europe", "EET/EEST", listOf("Greece")),
        TimezoneItem("Europe/Istanbul", "Istanbul, Turkey", "Europe", "TRT", listOf("Turkey")),
        TimezoneItem("Europe/Kiev", "Kyiv, Ukraine", "Europe", "EET/EEST", listOf("Ukraine", "Kiev")),
        TimezoneItem("Europe/Bucharest", "Bucharest, Romania", "Europe", "EET/EEST", listOf("Romania")),
        TimezoneItem("Europe/Sofia", "Sofia, Bulgaria", "Europe", "EET/EEST", listOf("Bulgaria")),
        TimezoneItem("Europe/Belgrade", "Belgrade, Serbia", "Europe", "CET/CEST", listOf("Serbia")),
        TimezoneItem("Europe/Zagreb", "Zagreb, Croatia", "Europe", "CET/CEST", listOf("Croatia")),
        TimezoneItem("Europe/Moscow", "Moscow, Russia", "Europe", "MSK", listOf("Russia", "Moscow")),
        TimezoneItem("Europe/Minsk", "Minsk, Belarus", "Europe", "MSK", listOf("Belarus")),
        TimezoneItem("Europe/Kaliningrad", "Kaliningrad, Russia", "Europe", "EET"),
        TimezoneItem("Europe/Samara", "Samara, Russia", "Europe", "SAMT"),
        TimezoneItem("Europe/Volgograd", "Volgograd, Russia", "Europe", "MSK")
    )
    
    val asia = listOf(
        TimezoneItem("Asia/Dubai", "Dubai, UAE", "Asia", "GST", listOf("UAE", "Dubai")),
        TimezoneItem("Asia/Jerusalem", "Jerusalem, Israel", "Asia", "IST/IDT", listOf("Israel", "IST")),
        TimezoneItem("Asia/Beirut", "Beirut, Lebanon", "Asia", "EET/EEST", listOf("Lebanon")),
        TimezoneItem("Asia/Damascus", "Damascus, Syria", "Asia", "EET/EEST", listOf("Syria")),
        TimezoneItem("Asia/Tehran", "Tehran, Iran", "Asia", "IRST/IRDT", listOf("Iran")),
        TimezoneItem("Asia/Baghdad", "Baghdad, Iraq", "Asia", "AST", listOf("Iraq", "AST")),
        TimezoneItem("Asia/Riyadh", "Riyadh, Saudi Arabia", "Asia", "AST", listOf("Saudi")),
        TimezoneItem("Asia/Kuwait", "Kuwait City, Kuwait", "Asia", "AST", listOf("Kuwait")),
        TimezoneItem("Asia/Qatar", "Doha, Qatar", "Asia", "AST", listOf("Qatar")),
        TimezoneItem("Asia/Karachi", "Karachi, Pakistan", "Asia", "PKT", listOf("Pakistan")),
        TimezoneItem("Asia/Kabul", "Kabul, Afghanistan", "Asia", "AFT", listOf("Afghanistan")),
        TimezoneItem("Asia/Tashkent", "Tashkent, Uzbekistan", "Asia", "UZT", listOf("Uzbekistan")),
        TimezoneItem("Asia/Almaty", "Almaty, Kazakhstan", "Asia", "ALMT", listOf("Kazakhstan")),
        TimezoneItem("Asia/Dhaka", "Dhaka, Bangladesh", "Asia", "BST", listOf("Bangladesh")),
        TimezoneItem("Asia/Kolkata", "Kolkata, India", "Asia", "IST", listOf("India", "IST", "Calcutta")),
        TimezoneItem("Asia/Colombo", "Colombo, Sri Lanka", "Asia", "IST", listOf("Sri Lanka")),
        TimezoneItem("Asia/Kathmandu", "Kathmandu, Nepal", "Asia", "NPT", listOf("Nepal")),
        TimezoneItem("Asia/Yangon", "Yangon, Myanmar", "Asia", "MMT", listOf("Myanmar", "Burma")),
        TimezoneItem("Asia/Bangkok", "Bangkok, Thailand", "Asia", "ICT", listOf("Thailand")),
        TimezoneItem("Asia/Ho_Chi_Minh", "Ho Chi Minh City, Vietnam", "Asia", "ICT", listOf("Vietnam", "Saigon")),
        TimezoneItem("Asia/Jakarta", "Jakarta, Indonesia", "Asia", "WIB", listOf("Indonesia")),
        TimezoneItem("Asia/Singapore", "Singapore", "Asia", "SGT", listOf("Singapore")),
        TimezoneItem("Asia/Kuala_Lumpur", "Kuala Lumpur, Malaysia", "Asia", "MYT", listOf("Malaysia")),
        TimezoneItem("Asia/Manila", "Manila, Philippines", "Asia", "PHT", listOf("Philippines")),
        TimezoneItem("Asia/Hong_Kong", "Hong Kong", "Asia", "HKT", listOf("Hong Kong", "HK")),
        TimezoneItem("Asia/Shanghai", "Shanghai, China", "Asia", "CST", listOf("China", "CST", "Beijing")),
        TimezoneItem("Asia/Taipei", "Taipei, Taiwan", "Asia", "CST", listOf("Taiwan")),
        TimezoneItem("Asia/Seoul", "Seoul, South Korea", "Asia", "KST", listOf("Korea", "Seoul")),
        TimezoneItem("Asia/Tokyo", "Tokyo, Japan", "Asia", "JST", listOf("Japan", "JST", "Tokyo")),
        TimezoneItem("Asia/Vladivostok", "Vladivostok, Russia", "Asia", "VLAT", listOf("Vladivostok"))
    )
    
    val africa = listOf(
        TimezoneItem("Africa/Cairo", "Cairo, Egypt", "Africa", "EET", listOf("Egypt")),
        TimezoneItem("Africa/Johannesburg", "Johannesburg, South Africa", "Africa", "SAST", listOf("South Africa")),
        TimezoneItem("Africa/Lagos", "Lagos, Nigeria", "Africa", "WAT", listOf("Nigeria")),
        TimezoneItem("Africa/Nairobi", "Nairobi, Kenya", "Africa", "EAT", listOf("Kenya")),
        TimezoneItem("Africa/Addis_Ababa", "Addis Ababa, Ethiopia", "Africa", "EAT", listOf("Ethiopia")),
        TimezoneItem("Africa/Khartoum", "Khartoum, Sudan", "Africa", "CAT", listOf("Sudan")),
        TimezoneItem("Africa/Algiers", "Algiers, Algeria", "Africa", "CET", listOf("Algeria")),
        TimezoneItem("Africa/Casablanca", "Casablanca, Morocco", "Africa", "WET/WEST", listOf("Morocco")),
        TimezoneItem("Africa/Tunis", "Tunis, Tunisia", "Africa", "CET", listOf("Tunisia")),
        TimezoneItem("Africa/Tripoli", "Tripoli, Libya", "Africa", "EET", listOf("Libya")),
        TimezoneItem("Africa/Accra", "Accra, Ghana", "Africa", "GMT", listOf("Ghana")),
        TimezoneItem("Africa/Dakar", "Dakar, Senegal", "Africa", "GMT", listOf("Senegal")),
        TimezoneItem("Africa/Abidjan", "Abidjan, Ivory Coast", "Africa", "GMT", listOf("Ivory Coast")),
        TimezoneItem("Africa/Kampala", "Kampala, Uganda", "Africa", "EAT", listOf("Uganda")),
        TimezoneItem("Africa/Dar_es_Salaam", "Dar es Salaam, Tanzania", "Africa", "EAT", listOf("Tanzania"))
    )
    
    val oceania = listOf(
        TimezoneItem("Pacific/Auckland", "Auckland, New Zealand", "Oceania", "NZST/NZDT", listOf("New Zealand", "Auckland")),
        TimezoneItem("Pacific/Fiji", "Suva, Fiji", "Oceania", "FJT/FJST", listOf("Fiji")),
        TimezoneItem("Pacific/Port_Moresby", "Port Moresby, Papua New Guinea", "Oceania", "PGT", listOf("Papua New Guinea")),
        TimezoneItem("Pacific/Noumea", "Nouméa, New Caledonia", "Oceania", "NCT", listOf("New Caledonia")),
        TimezoneItem("Australia/Sydney", "Sydney, Australia", "Oceania", "AEST/AEDT", listOf("Australia", "Sydney", "AEST")),
        TimezoneItem("Australia/Melbourne", "Melbourne, Australia", "Oceania", "AEST/AEDT", listOf("Melbourne")),
        TimezoneItem("Australia/Brisbane", "Brisbane, Australia", "Oceania", "AEST", listOf("Brisbane")),
        TimezoneItem("Australia/Adelaide", "Adelaide, Australia", "Oceania", "ACST/ACDT", listOf("Adelaide")),
        TimezoneItem("Australia/Perth", "Perth, Australia", "Oceania", "AWST", listOf("Perth")),
        TimezoneItem("Australia/Darwin", "Darwin, Australia", "Oceania", "ACST", listOf("Darwin"))
    )
    
    val allTimezones = americas + europe + asia + africa + oceania
    
    // Create search index for fast lookups
    val searchIndex: Map<String, List<TimezoneItem>> by lazy {
        val index = mutableMapOf<String, MutableList<TimezoneItem>>()
        
        allTimezones.forEach { tz ->
            // Index by display name parts
            tz.displayName.lowercase().split(", ", " ").forEach { part ->
                if (part.isNotBlank()) {
                    index.getOrPut(part) { mutableListOf() }.add(tz)
                }
            }
            
            // Index by abbreviation
            tz.abbreviation.lowercase().split("/").forEach { abbr ->
                index.getOrPut(abbr) { mutableListOf() }.add(tz)
            }
            
            // Index by search aliases
            tz.searchAliases.forEach { alias ->
                index.getOrPut(alias.lowercase()) { mutableListOf() }.add(tz)
            }
            
            // Index by IANA ID parts
            tz.id.split("/").forEach { part ->
                index.getOrPut(part.lowercase()) { mutableListOf() }.add(tz)
            }
        }
        
        index.mapValues { it.value.distinct() }
    }
    
    fun search(query: String): List<TimezoneItem> {
        if (query.isBlank()) return allTimezones
        
        val lowercaseQuery = query.lowercase().trim()
        
        // Direct matches first
        val directMatches = searchIndex[lowercaseQuery] ?: emptyList()
        
        // Partial matches
        val partialMatches = searchIndex.entries
            .filter { it.key.contains(lowercaseQuery) }
            .flatMap { it.value }
            .distinct()
        
        // Combine and deduplicate, keeping direct matches first
        return (directMatches + partialMatches).distinct()
    }
}