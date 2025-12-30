package at.tatami.common.domain

/**
 * Represents validation errors that can occur in the domain layer
 */
sealed class ValidationError : Exception() {
    data object InvalidEmail : ValidationError()
    data object WeakPassword : ValidationError()
    data object PasswordsDoNotMatch : ValidationError()
    data object EmptyField : ValidationError()
    data class FieldTooShort(val minLength: Int) : ValidationError()
    data class FieldTooLong(val maxLength: Int) : ValidationError()
}

/**
 * Common authentication errors
 */
sealed class AuthError : Exception() {
    data object InvalidCredentials : AuthError()
    data object UserNotFound : AuthError()
    data object EmailAlreadyInUse : AuthError()
    data object NetworkError : AuthError()
    data object UnknownError : AuthError()
}

/**
 * Club-related errors for operations like joining, creating, or managing clubs
 */
sealed class ClubError : Exception() {
    /** The invite code doesn't exist or has invalid format */
    data object InvalidInviteCode : ClubError()
    /** The invite code has expired */
    data object InviteCodeExpired : ClubError()
    /** The person is already a member of this club */
    data object AlreadyMember : ClubError()
    /** The club was not found */
    data object ClubNotFound : ClubError()
    /** User is not authenticated */
    data object Unauthenticated : ClubError()
    /** User doesn't have permission for this operation */
    data object PermissionDenied : ClubError()
    /** Network error occurred */
    data object NetworkError : ClubError()
    /** An unknown error occurred */
    data object UnknownError : ClubError()
}
