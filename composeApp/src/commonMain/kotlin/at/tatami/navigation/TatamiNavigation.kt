package at.tatami.navigation

import kotlinx.serialization.Serializable

sealed interface TatamiRoute {
    @Serializable
    data object Auth : TatamiRoute {
        @Serializable
        data object Login : TatamiRoute
        
        @Serializable
        data object Register : TatamiRoute
        
        @Serializable
        data object ForgotPassword : TatamiRoute
        
        @Serializable
        data object EmailVerification : TatamiRoute
    }
    
    @Serializable
    data object Main : TatamiRoute {
        
        @Serializable
        data object PersonList : TatamiRoute
        
        @Serializable
        data object PersonListRoot : TatamiRoute
        
        @Serializable
        data class PersonDetail(val personId: String) : TatamiRoute
        
        
        @Serializable
        data object CreatePerson : TatamiRoute

        @Serializable
        data class PersonPhotoUpload(val personId: String) : TatamiRoute

        @Serializable
        data class EditPerson(val personId: String) : TatamiRoute

        @Serializable
        data object ClubList : TatamiRoute
        
        @Serializable
        data class ClubDetail(val clubId: String) : TatamiRoute


        @Serializable
        data object CreateClub : TatamiRoute

        @Serializable
        data class ClubPhotoUpload(val clubId: String) : TatamiRoute

        @Serializable
        data object JoinOrCreateClub : TatamiRoute
        
        @Serializable
        data object JoinClub : TatamiRoute

        @Serializable
        data object Dashboard : TatamiRoute
        
        @Serializable
        data object DashboardHome : TatamiRoute
        
        @Serializable
        data object Event : TatamiRoute

        @Serializable
        data object EventCreate : TatamiRoute

        @Serializable
        data class EventDetail(val eventId: String) : TatamiRoute

        @Serializable
        data object Group : TatamiRoute

        @Serializable
        data object GroupCreate : TatamiRoute

        @Serializable
        data class GroupDetail(val groupId: String) : TatamiRoute

        @Serializable
        data class TrainingList(val groupId: String) : TatamiRoute

        @Serializable
        data class TrainingDetail(val groupId: String, val trainingId: String) : TatamiRoute

        @Serializable
        data object Settings : TatamiRoute

        @Serializable
        data object SystemSettings : TatamiRoute

        @Serializable
        data object AccountSettings : TatamiRoute

        @Serializable
        data object ClubSettings : TatamiRoute

        @Serializable
        data object TimezonePicker : TatamiRoute

        @Serializable
        data object ComponentPlayground : TatamiRoute

    }
}