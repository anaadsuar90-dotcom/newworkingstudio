package com.example.nearbyeventradar.ui.i18n

import com.example.nearbyeventradar.data.model.RoleCategory
import com.example.nearbyeventradar.data.model.VisibilityStatus

interface AppStrings {
    // Top Bar & App Info
    val appTitle: String
    val appSubtitle: String
    val switchLanguagePrompt: String

    // Bottom Navigation Tabs
    val tabRadar: String
    val tabAttendees: String
    val tabSessions: String
    val tabConnections: String
    val tabBadge: String

    // Radar Screen
    val activeAttendees: String
    val tapBlipToConnect: String
    val statusScanning: String
    val statusPaused: String
    val sonarView: String
    val listView: String
    val zoomLabel: String
    val pauseRadar: String
    val resumeRadar: String
    val myPass: String
    val noAttendeesTitle: String
    val noAttendeesSubtitle: String
    val youMarker: String

    // Role Filter Chips
    val filterAll: String
    val filterTech: String
    val filterDesign: String
    val filterFounders: String
    val filterInvestors: String
    val filterSpeakers: String
    val filterBeacons: String

    // Attendees Screen
    val attendeesTitle: String
    val attendeesSubtitle: String
    val searchPlaceholder: String
    val filterSaved: String
    val filterUnconnected: String
    val filterMatches: String
    fun showingAttendeesCount(count: Int): String
    val emptyAttendeesTitle: String
    val emptyAttendeesSubtitle: String

    // Cards & Quick Actions
    val sayHi: String
    val waveSent: String
    val viewCard: String
    val viewProfile: String
    val chat: String
    val saveContact: String
    val close: String
    fun matchPercentage(score: Int): String
    fun distanceAway(meters: Float): String
    fun proximityWithDistance(meters: Float): String

    // Detail Sheet
    val sectionAbout: String
    val sectionLookingFor: String
    val sectionOffering: String
    val sectionInterests: String
    val sectionIcebreakers: String
    val sectionDeviceInfo: String
    val beaconIdLabel: String
    val protocolLabel: String
    val firstSeenLabel: String
    val exchangeBadge: String
    val connectedStatus: String

    // Chat Bottom Sheet
    fun directProximityHeader(distanceStr: String): String
    val chatIcebreakerPrompt: String
    fun emptyChatNotice(name: String): String
    val chatInputPlaceholder: String
    val sendButton: String

    // Digital Badge QR Dialog
    val qrPassHeader: String
    val qrBeaconIdPrefix: String
    val qrScanHint: String
    val doneButton: String

    // Incoming Wave Dialog
    val incomingWaveHeader: String
    fun wavedAtYouMessage(name: String): String
    fun waveDistanceAndMatch(distanceMeters: Float, matchScore: Int): String
    val laterButton: String
    val waveBackButton: String

    // Connections Screen
    val connectionsTitle: String
    fun connectionsCountSubtitle(count: Int): String
    fun contactsCount(count: Int): String
    val noConnectionsTitle: String
    val emptyConnectionsTitle: String
    val noConnectionsSubtitle: String
    val emptyConnectionsSubtitle: String

    // My Badge Screen
    val myBadgeTitle: String
    val badgeTitle: String
    val myBadgeSubtitle: String
    val badgeSubtitle: String
    val editProfile: String
    val saveProfile: String
    val bleActiveStatus: String
    val stealthStatus: String
    val bleStealthStatus: String
    val fullNameLabel: String
    val titleRoleLabel: String
    val companyOrgLabel: String
    val companyLabel: String
    val bioLabel: String
    val myTopicsSkillsHeader: String
    val topicsSkillsHeader: String
    val showEventPassButton: String
    val showQrPassButton: String
    val visibilityModesHeader: String
    val visibilityHeader: String

    // Sessions Screen
    val sessionsTitle: String
    val sessionsSubtitle: String
    fun nearbySessionAttendeesCount(count: Int): String
    fun nearbyCount(count: Int): String

    // Role & Visibility Display Helpers
    fun getRoleName(role: RoleCategory): String
    fun getVisibilityLabel(status: VisibilityStatus): String
    fun getVisibilityDescription(status: VisibilityStatus): String

    // Notification / Snackbar messages
    fun snackbarWaveSent(name: String): String
    fun snackbarWaveReturned(name: String): String
    fun snackbarContactExchanged(name: String): String
    fun snackbarAttendeeSaved(name: String): String
    fun snackbarAttendeeRemoved(name: String): String
    val snackbarScannerResumed: String
    val snackbarScannerPaused: String
    val snackbarProfileUpdated: String
    val snackbarLanguageChanged: String

    // Icebreaker recommendations
    fun getIcebreakers(attendeeName: String, role: RoleCategory): List<String>

    companion object {
        val Spanish: AppStrings = SpanishStrings
        val English: AppStrings = EnglishStrings
    }
}

object SpanishStrings : AppStrings {
    override val appTitle = "Radar de Eventos Cercanos"
    override val appSubtitle = "Detección BLE de asistentes por proximidad"
    override val switchLanguagePrompt = "Cambiar a Inglés"

    override val tabRadar = "Radar"
    override val tabAttendees = "Gente"
    override val tabSessions = "Agenda"
    override val tabConnections = "Contactos"
    override val tabBadge = "Mi Pase"

    override val activeAttendees = "Asistentes Activos"
    override val tapBlipToConnect = "Toca un punto para conectar"
    override val statusScanning = "ESCANEANDO"
    override val statusPaused = "PAUSADO"
    override val sonarView = "Sonar"
    override val listView = "Lista"
    override val zoomLabel = "Zoom"
    override val pauseRadar = "Pausar Radar"
    override val resumeRadar = "Reanudar Radar"
    override val myPass = "Mi Pase"
    override val noAttendeesTitle = "No se encontraron asistentes cerca"
    override val noAttendeesSubtitle = "Prueba a ampliar el zoom del radar o cambiar los filtros"
    override val youMarker = "TÚ"

    override val filterAll = "Todos"
    override val filterTech = "Tecnología"
    override val filterDesign = "Diseño"
    override val filterFounders = "Fundadores"
    override val filterInvestors = "Inversores"
    override val filterSpeakers = "Ponentes"
    override val filterBeacons = "Balizas"

    override val attendeesTitle = "Asistentes Cercanos"
    override val attendeesSubtitle = "Descubrimiento en vivo por proximidad BLE"
    override val searchPlaceholder = "Buscar por nombre, cargo, tecnología…"
    override val filterSaved = "Guardados"
    override val filterUnconnected = "Por Conectar"
    override val filterMatches = "Coincidencias"
    override fun showingAttendeesCount(count: Int) = "Mostrando $count asistentes"
    override val emptyAttendeesTitle = "No se encontraron asistentes"
    override val emptyAttendeesSubtitle = "Prueba a ajustar tu búsqueda o los filtros."

    override val sayHi = "Saludar 👋"
    override val waveSent = "Onda enviada 👋"
    override val viewCard = "Ver Tarjeta"
    override val viewProfile = "Ver Perfil"
    override val chat = "Chat"
    override val saveContact = "Guardar Contacto"
    override val close = "Cerrar"
    override fun matchPercentage(score: Int) = "$score% Coincidencia"
    override fun distanceAway(meters: Float) = String.format("a %.1f m", meters)
    override fun proximityWithDistance(meters: Float) = String.format("%.1f m de distancia", meters)

    override val sectionAbout = "ACERCA DE"
    override val sectionLookingFor = "BUSCANDO"
    override val sectionOffering = "OFRECIENDO"
    override val sectionInterests = "INTERESES Y ESPECIALIDADES"
    override val sectionIcebreakers = "TEMAS PARA ROMPER EL HIELO"
    override val sectionDeviceInfo = "INFORMACIÓN DE BALIZA Y DISPOSITIVO"
    override val beaconIdLabel = "ID de Baliza"
    override val protocolLabel = "Protocolo"
    override val firstSeenLabel = "Detectado por 1ª vez"
    override val exchangeBadge = "Intercambiar Pase"
    override val connectedStatus = "Conectado 🤝"

    override fun directProximityHeader(distanceStr: String) = "Proximidad Directa ($distanceStr)"
    override val chatIcebreakerPrompt = "💬 «¡Hola! ¿Tomamos un café rápido en la zona de descanso?»"
    override fun emptyChatNotice(name: String) =
        "¡Inicia una conversación amistosa con $name!\nLos mensajes se transmiten punto a punto vía BLE."
    override val chatInputPlaceholder = "Escribe un mensaje…"
    override val sendButton = "Enviar"

    override val qrPassHeader = "PASE RADAR DEL EVENTO ✨"
    override val qrBeaconIdPrefix = "ID DE BALIZA: "
    override val qrScanHint = "Escanea este pase o acerca los dispositivos para intercambiar contactos al instante."
    override val doneButton = "Listo ✨"

    override val incomingWaveHeader = "✨ ONDA ENTRANTE"
    override fun wavedAtYouMessage(name: String) = "¡$name te ha saludado! 👋"
    override fun waveDistanceAndMatch(distanceMeters: Float, matchScore: Int) =
        String.format("a %.1f m • %d%% Coincidencia", distanceMeters, matchScore)
    override val laterButton = "Más tarde"
    override val waveBackButton = "Devolver saludo 👋"

    override val connectionsTitle = "Contactos"
    override fun connectionsCountSubtitle(count: Int) = "$count contactos guardados en este evento"
    override fun contactsCount(count: Int) = "$count contactos"
    override val noConnectionsTitle = "Aún no tienes contactos guardados"
    override val emptyConnectionsTitle = "Aún no tienes contactos guardados"
    override val noConnectionsSubtitle =
        "Toca a cualquier asistente en el Radar para saludar o intercambiar pases digitales del evento."
    override val emptyConnectionsSubtitle =
        "Toca a cualquier asistente en el Radar para saludar o intercambiar pases digitales del evento."

    override val myBadgeTitle = "Mi Identificación Digital"
    override val badgeTitle = "Mi Identificación Digital"
    override val myBadgeSubtitle = "Configura lo que descubrirán los asistentes cercanos"
    override val badgeSubtitle = "Configura lo que descubrirán los asistentes cercanos"
    override val editProfile = "Editar perfil"
    override val saveProfile = "Guardar perfil"
    override val bleActiveStatus = "BLE ACTIVO"
    override val stealthStatus = "INVISIBLE"
    override val bleStealthStatus = "INVISIBLE"
    override val fullNameLabel = "Nombre Completo"
    override val titleRoleLabel = "Título / Cargo"
    override val companyOrgLabel = "Empresa / Organización"
    override val companyLabel = "Empresa / Organización"
    override val bioLabel = "Biografía"
    override val myTopicsSkillsHeader = "MIS TEMAS Y HABILIDADES"
    override val topicsSkillsHeader = "MIS TEMAS Y HABILIDADES"
    override val showEventPassButton = "Ver Pase de Evento e ID de Baliza"
    override val showQrPassButton = "Ver Pase de Evento e ID de Baliza"
    override val visibilityModesHeader = "MODOS DE VISIBILIDAD POR PROXIMIDAD"
    override val visibilityHeader = "MODOS DE VISIBILIDAD POR PROXIMIDAD"

    override val sessionsTitle = "Agenda del Evento y Zonas"
    override val sessionsSubtitle = "Seguimiento de sesiones, ponentes y afluencia en cada zona"
    override fun nearbySessionAttendeesCount(count: Int) = "$count asistentes cerca"
    override fun nearbyCount(count: Int) = "$count cerca"

    override fun getRoleName(role: RoleCategory): String {
        return when (role) {
            RoleCategory.DEVELOPER -> "Desarrollador Galáctico"
            RoleCategory.DESIGNER -> "Diseño Hula / UX"
            RoleCategory.FOUNDER -> "Fundador Ohana"
            RoleCategory.INVESTOR -> "Inversor Cósmico (VC)"
            RoleCategory.SPEAKER -> "Ponente Principal"
            RoleCategory.ORGANIZER -> "Organizador del Evento"
            RoleCategory.SPONSOR -> "Patrocinador Galáctico"
            RoleCategory.VENUE_BEACON -> "Baliza Aloha"
        }
    }

    override fun getVisibilityLabel(status: VisibilityStatus): String {
        return when (status) {
            VisibilityStatus.BROADCASTING -> "🌺 Modo Aloha (Abierto)"
            VisibilityStatus.BUSY -> "🛸 Concentración Total"
            VisibilityStatus.IN_TALK -> "🥥 En Conferencia"
            VisibilityStatus.STEALTH -> "🤫 Modo Invisible 626"
        }
    }

    override fun getVisibilityDescription(status: VisibilityStatus): String {
        return when (status) {
            VisibilityStatus.BROADCASTING -> "Transmite la insignia BLE a amigos y asistentes cercanos"
            VisibilityStatus.BUSY -> "Visible en el radar pero ocupado programando o en reunión"
            VisibilityStatus.IN_TALK -> "Escuchando una charla en modo No Molestar"
            VisibilityStatus.STEALTH -> "Escáner activo, pero tu insignia permanece oculta en el espacio"
        }
    }

    override fun snackbarWaveSent(name: String) = "¡Onda enviada a $name! 👋"
    override fun snackbarWaveReturned(name: String) = "¡Has devuelto el saludo a $name! 👋"
    override fun snackbarContactExchanged(name: String) = "¡Contacto intercambiado con $name! 🤝"
    override fun snackbarAttendeeSaved(name: String) = "¡$name guardado en tus contactos!"
    override fun snackbarAttendeeRemoved(name: String) = "¡$name eliminado de tus contactos!"
    override val snackbarScannerResumed = "Escáner de proximidad BLE activado"
    override val snackbarScannerPaused = "Escáner de proximidad BLE pausado"
    override val snackbarProfileUpdated = "¡Perfil digital actualizado con éxito! ✨"
    override val snackbarLanguageChanged = "Idioma cambiado a Español 🇪🇸"

    override fun getIcebreakers(attendeeName: String, role: RoleCategory): List<String> {
        val firstName = attendeeName.split(" ").firstOrNull() ?: attendeeName
        return when (role) {
            RoleCategory.DESIGNER -> listOf(
                "💬 «¡Hola $firstName! Me encantaron tus ideas de micro-interacciones en Compose.»",
                "🎨 «¿Qué opinas del nuevo sistema de tokens y temas fluidos?»"
            )
            RoleCategory.DEVELOPER -> listOf(
                "⚡ «¡Hola $firstName! ¿Estás trabajando con IA en dispositivo o mallas BLE?»",
                "🚀 «¿Qué proyectos interesantes estás desarrollando últimamente?»"
            )
            RoleCategory.FOUNDER -> listOf(
                "💡 «¡Hola $firstName! Cuéntame más sobre la visión y desafíos de tu proyecto.»",
                "🤝 «¿Qué tipo de talento o colaboradores estás buscando en el evento?»"
            )
            RoleCategory.INVESTOR -> listOf(
                "📈 «¡Hola $firstName! ¿Qué tendencias en deep tech y edge AI te resultan más prometedoras?»",
                "☕ «¿Tendrías 5 minutos para un café rápido en el lounge?»"
            )
            RoleCategory.SPEAKER -> listOf(
                "🎤 «¡Excelente ponencia magistral, $firstName! La arquitectura decentralizada fue fascinante.»",
                "❓ «Tenía una duda sobre la latencia en mallas locales, ¿podemos comentarlo?»"
            )
            RoleCategory.VENUE_BEACON -> listOf(
                "🌺 «¡Tomemos un descanso con hielo raspado tropical!»",
                "🔋 «Punto de recarga y Wi-Fi de alta velocidad disponible.»"
            )
            else -> listOf(
                "👋 «¡Hola $firstName! ¿Qué tal estás viviendo el evento?»",
                "☕ «¿Tomamos un café rápido y compartimos contactos?»"
            )
        }
    }
}

object EnglishStrings : AppStrings {
    override val appTitle = "Nearby Event Radar"
    override val appSubtitle = "Proximity BLE attendee & beacon discovery"
    override val switchLanguagePrompt = "Cambiar a Español"

    override val tabRadar = "Radar"
    override val tabAttendees = "People"
    override val tabSessions = "Sessions"
    override val tabConnections = "Connections"
    override val tabBadge = "Badge"

    override val activeAttendees = "Active Attendees"
    override val tapBlipToConnect = "Tap any blip to connect"
    override val statusScanning = "SCANNING"
    override val statusPaused = "PAUSED"
    override val sonarView = "Sonar"
    override val listView = "List"
    override val zoomLabel = "Zoom"
    override val pauseRadar = "Pause Radar"
    override val resumeRadar = "Resume Radar"
    override val myPass = "My Pass"
    override val noAttendeesTitle = "No attendees found nearby"
    override val noAttendeesSubtitle = "Try zooming out or changing filter"
    override val youMarker = "YOU"

    override val filterAll = "All"
    override val filterTech = "Tech / Dev"
    override val filterDesign = "Design / UX"
    override val filterFounders = "Founders"
    override val filterInvestors = "Investors"
    override val filterSpeakers = "Speakers"
    override val filterBeacons = "Beacons"

    override val attendeesTitle = "Nearby Attendees"
    override val attendeesSubtitle = "Live BLE proximity discovery"
    override val searchPlaceholder = "Search by name, role, tech…"
    override val filterSaved = "Saved"
    override val filterUnconnected = "Unconnected"
    override val filterMatches = "Matches"
    override fun showingAttendeesCount(count: Int) = "Showing $count attendees"
    override val emptyAttendeesTitle = "No attendees found"
    override val emptyAttendeesSubtitle = "Try adjusting your search query or filter tags."

    override val sayHi = "Say Hi 👋"
    override val waveSent = "Wave Sent 👋"
    override val viewCard = "View Card"
    override val viewProfile = "View Profile"
    override val chat = "Chat"
    override val saveContact = "Save Contact"
    override val close = "Close"
    override fun matchPercentage(score: Int) = "$score% Match"
    override fun distanceAway(meters: Float) = String.format("%.1f m away", meters)
    override fun proximityWithDistance(meters: Float) = String.format("%.1f m away", meters)

    override val sectionAbout = "ABOUT"
    override val sectionLookingFor = "LOOKING FOR"
    override val sectionOffering = "OFFERING"
    override val sectionInterests = "INTERESTS & PASSIONS"
    override val sectionIcebreakers = "QUICK ICEBREAKERS"
    override val sectionDeviceInfo = "BEACON & DEVICE INFO"
    override val beaconIdLabel = "Beacon ID"
    override val protocolLabel = "Protocol"
    override val firstSeenLabel = "First Seen"
    override val exchangeBadge = "Exchange Badge"
    override val connectedStatus = "Connected 🤝"

    override fun directProximityHeader(distanceStr: String) = "Direct Proximity ($distanceStr)"
    override val chatIcebreakerPrompt = "💬 \"Hi! Want to grab a quick coffee at the community hub?\""
    override fun emptyChatNotice(name: String) =
        "Start a friendly conversation with $name!\nMessages exchange peer-to-peer over BLE."
    override val chatInputPlaceholder = "Type a message…"
    override val sendButton = "Send"

    override val qrPassHeader = "EVENT RADAR PASS ✨"
    override val qrBeaconIdPrefix = "BEACON ID: "
    override val qrScanHint = "Scan this pass or bring devices close to exchange contacts instantly."
    override val doneButton = "Done ✨"

    override val incomingWaveHeader = "✨ INCOMING WAVE"
    override fun wavedAtYouMessage(name: String) = "$name waved at you! 👋"
    override fun waveDistanceAndMatch(distanceMeters: Float, matchScore: Int) =
        String.format("%.1f m away • %d%% Match", distanceMeters, matchScore)
    override val laterButton = "Later"
    override val waveBackButton = "Wave Back 👋"

    override val connectionsTitle = "Connections"
    override fun connectionsCountSubtitle(count: Int) = "$count contacts saved at this event"
    override fun contactsCount(count: Int) = "$count contacts"
    override val noConnectionsTitle = "No Connections Yet"
    override val emptyConnectionsTitle = "No Connections Yet"
    override val noConnectionsSubtitle =
        "Tap on any attendee in the Radar to send a wave or exchange digital event contact passes."
    override val emptyConnectionsSubtitle =
        "Tap on any attendee in the Radar to send a wave or exchange digital event contact passes."

    override val myBadgeTitle = "My Digital Badge"
    override val badgeTitle = "My Digital Badge"
    override val myBadgeSubtitle = "Configure what nearby attendees discover"
    override val badgeSubtitle = "Configure what nearby attendees discover"
    override val editProfile = "Edit profile"
    override val saveProfile = "Save profile"
    override val bleActiveStatus = "BLE ACTIVE"
    override val stealthStatus = "STEALTH"
    override val bleStealthStatus = "STEALTH"
    override val fullNameLabel = "Full Name"
    override val titleRoleLabel = "Title / Role"
    override val companyOrgLabel = "Company / Organization"
    override val companyLabel = "Company / Organization"
    override val bioLabel = "Bio"
    override val myTopicsSkillsHeader = "MY TOPICS & SKILLS"
    override val topicsSkillsHeader = "MY TOPICS & SKILLS"
    override val showEventPassButton = "Show Event Pass & Beacon ID"
    override val showQrPassButton = "Show Event Pass & Beacon ID"
    override val visibilityModesHeader = "PROXIMITY VISIBILITY MODES"
    override val visibilityHeader = "PROXIMITY VISIBILITY MODES"

    override val sessionsTitle = "Event Agenda & Zones"
    override val sessionsSubtitle = "Track sessions, speakers, and attendee density per venue zone"
    override fun nearbySessionAttendeesCount(count: Int) = "$count nearby"
    override fun nearbyCount(count: Int) = "$count nearby"

    override fun getRoleName(role: RoleCategory): String = role.displayName

    override fun getVisibilityLabel(status: VisibilityStatus): String = status.label

    override fun getVisibilityDescription(status: VisibilityStatus): String = status.description

    override fun snackbarWaveSent(name: String) = "Wave sent to $name! 👋"
    override fun snackbarWaveReturned(name: String) = "Waved back to $name! 👋"
    override fun snackbarContactExchanged(name: String) = "Contact exchanged with $name! 🤝"
    override fun snackbarAttendeeSaved(name: String) = "$name saved to your contacts!"
    override fun snackbarAttendeeRemoved(name: String) = "$name removed from your contacts!"
    override val snackbarScannerResumed = "Proximity BLE scanner resumed"
    override val snackbarScannerPaused = "Proximity BLE scanner paused"
    override val snackbarProfileUpdated = "Digital profile updated successfully! ✨"
    override val snackbarLanguageChanged = "Language switched to English 🇺🇸"

    override fun getIcebreakers(attendeeName: String, role: RoleCategory): List<String> {
        val firstName = attendeeName.split(" ").firstOrNull() ?: attendeeName
        return when (role) {
            RoleCategory.DESIGNER -> listOf(
                "💬 \"Hi $firstName! Loved your ideas on fluid micro-interactions in Compose.\"",
                "🎨 \"What's your take on the latest dynamic color tokens?\""
            )
            RoleCategory.DEVELOPER -> listOf(
                "⚡ \"Hey $firstName! Are you exploring edge neural models or BLE mesh?\"",
                "🚀 \"What cool projects have you been hacking on lately?\""
            )
            RoleCategory.FOUNDER -> listOf(
                "💡 \"Hi $firstName! Would love to hear the vision behind your product.\"",
                "🤝 \"What key roles or partnerships are you looking for at the summit?\""
            )
            RoleCategory.INVESTOR -> listOf(
                "📈 \"Hi $firstName! What emerging trends in mobile AI are catching your eye?\"",
                "☕ \"Would love to grab 5 minutes over coffee at the lounge.\""
            )
            RoleCategory.SPEAKER -> listOf(
                "🎤 \"Great keynote session, $firstName! The decentralization breakdown was super insightful.\"",
                "❓ \"Quick question on edge latency if you have a moment!\""
            )
            RoleCategory.VENUE_BEACON -> listOf(
                "🌺 \"Take a quick Aloha break with fresh shave ice!\"",
                "🔋 \"High-speed Wi-Fi and rapid charging ports available.\""
            )
            else -> listOf(
                "👋 \"Hi $firstName! How are you enjoying the summit so far?\"",
                "☕ \"Want to grab a quick coffee and exchange contact cards?\""
            )
        }
    }
}
