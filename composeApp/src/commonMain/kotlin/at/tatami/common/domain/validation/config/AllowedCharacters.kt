package at.tatami.common.domain.validation.config

/**
 * Character sets for name validation and filtering.
 * Single source of truth for all allowed characters in names across the application.
 */
object AllowedCharacters {
    
    // Basic Latin letters (a-z, A-Z)
    val BASIC_LETTERS = setOf(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    )
    
    // German umlauts and eszett
    val GERMAN_LETTERS = setOf('ä', 'ö', 'ü', 'Ä', 'Ö', 'Ü', 'ß')
    
    // French accented letters
    val FRENCH_LETTERS = setOf(
        'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 
        'ì', 'í', 'î', 'ï', 'ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø',
        'ù', 'ú', 'û', 'ü', 'ý', 'þ', 'ÿ',
        'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë',
        'Ì', 'Í', 'Î', 'Ï', 'Ð', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ø',
        'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Þ', 'Ÿ'
    )
    
    // Eastern European letters (Czech, Polish, Slovak, Hungarian, etc.)
    val EASTERN_EUROPEAN_LETTERS = setOf(
        'ā', 'ă', 'ą', 'ć', 'ĉ', 'ċ', 'č', 'ď', 'đ', 'ē', 'ĕ', 'ė', 'ę', 'ě',
        'ĝ', 'ğ', 'ġ', 'ģ', 'ĥ', 'ħ', 'ĩ', 'ī', 'ĭ', 'į', 'ı', 'ĳ', 'ĵ', 'ķ',
        'ĸ', 'ĺ', 'ļ', 'ľ', 'ŀ', 'ł', 'ń', 'ņ', 'ň', 'ŉ', 'ŋ', 'ō', 'ŏ', 'ő',
        'œ', 'ŕ', 'ŗ', 'ř', 'ś', 'ŝ', 'ş', 'š', 'ţ', 'ť', 'ŧ', 'ũ', 'ū', 'ŭ',
        'ů', 'ű', 'ų', 'ŵ', 'ŷ', 'ź', 'ż', 'ž', 'ſ',
        'Ā', 'Ă', 'Ą', 'Ć', 'Ĉ', 'Ċ', 'Č', 'Ď', 'Đ', 'Ē', 'Ĕ', 'Ė', 'Ę', 'Ě',
        'Ĝ', 'Ğ', 'Ġ', 'Ģ', 'Ĥ', 'Ħ', 'Ĩ', 'Ī', 'Ĭ', 'Į', 'İ', 'Ĳ', 'Ĵ', 'Ķ',
        'Ĺ', 'Ļ', 'Ľ', 'Ŀ', 'Ł', 'Ń', 'Ņ', 'Ň', 'Ŋ', 'Ō', 'Ŏ', 'Ő', 'Œ', 'Ŕ',
        'Ŗ', 'Ř', 'Ś', 'Ŝ', 'Ş', 'Š', 'Ţ', 'Ť', 'Ŧ', 'Ũ', 'Ū', 'Ŭ', 'Ů', 'Ű',
        'Ų', 'Ŵ', 'Ŷ', 'Ÿ', 'Ź', 'Ż', 'Ž'
    )
    
    // Vietnamese letters (common in Vietnamese names)
    val VIETNAMESE_LETTERS = setOf(
        'ị', 'ẹ', 'ụ', 'ư', 'ọ', 'ơ', 'ậ', 'ầ', 'ấ', 'ẫ', 'ẩ', 'ắ', 'ằ', 'ẳ', 'ẵ',
        'ệ', 'ề', 'ế', 'ễ', 'ể', 'ị', 'ì', 'í', 'ĩ', 'ỉ', 'ộ', 'ồ', 'ố', 'ỗ', 'ổ',
        'ợ', 'ờ', 'ớ', 'ỡ', 'ở', 'ụ', 'ù', 'ú', 'ũ', 'ủ', 'ự', 'ừ', 'ứ', 'ữ', 'ử',
        'ỵ', 'ỳ', 'ý', 'ỹ', 'ỷ', 'ṭ', 'ṅ',
        'Ị', 'Ẹ', 'Ụ', 'Ư', 'Ọ', 'Ơ', 'Ậ', 'Ầ', 'Ấ', 'Ẫ', 'Ẩ', 'Ắ', 'Ằ', 'Ẳ', 'Ẵ',
        'Ệ', 'Ề', 'Ế', 'Ễ', 'Ể', 'Ị', 'Ì', 'Í', 'Ĩ', 'Ỉ', 'Ộ', 'Ồ', 'Ố', 'Ỗ', 'Ổ',
        'Ợ', 'Ờ', 'Ớ', 'Ỡ', 'Ở', 'Ụ', 'Ù', 'Ú', 'Ũ', 'Ủ', 'Ự', 'Ừ', 'Ứ', 'Ữ', 'Ử',
        'Ỵ', 'Ỳ', 'Ý', 'Ỹ', 'Ỷ', 'Ṭ', 'Ṅ'
    )
    
    // Welsh letters (used in Welsh names)
    val WELSH_LETTERS = setOf(
        'ẁ', 'ẃ', 'ẅ', 'ỳ', 'ƴ',
        'Ẁ', 'Ẃ', 'Ẅ', 'Ỳ', 'Ƴ'
    )
    
    // African and IPA letters (used in African names)
    val AFRICAN_LETTERS = setOf(
        'ə', 'ɛ', 'ɔ', 'ɗ', 'ḍ', 'ƙ', 'ɓ', 'ɲ', 'ṣ', 'ț', 'ș',
        'Ə', 'Ɛ', 'Ɔ', 'Ɗ', 'Ḍ', 'Ƙ', 'Ɓ', 'Ɲ', 'Ṣ', 'Ț', 'Ș'
    )
    
    // Combine all letter sets
    val ALL_LETTERS = BASIC_LETTERS + GERMAN_LETTERS + FRENCH_LETTERS + EASTERN_EUROPEAN_LETTERS + 
                      VIETNAMESE_LETTERS + WELSH_LETTERS + AFRICAN_LETTERS
    
    // Special characters allowed in names
    val SPECIAL_CHARS = setOf(' ', '-', '\'')
    
    // All allowed characters for person names
    val NAME_CHARS = ALL_LETTERS + SPECIAL_CHARS
    
    // Numbers (for club descriptions and other contexts)
    val NUMBERS = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    
    // Additional punctuation for club names and descriptions
    val CLUB_PUNCTUATION = setOf('.', '&')
    val CLUB_DESCRIPTION_PUNCTUATION = setOf('.', ',', '!', '?', '"', '(', ')', '&', ':', ';', '/')
}