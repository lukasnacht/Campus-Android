package de.tum.`in`.tumcampusapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.tumonline.TUMOnlineClient
import de.tum.`in`.tumcampusapp.component.other.locations.TumLocationManager
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarCardsProvider
import de.tum.`in`.tumcampusapp.component.tumui.calendar.CalendarController
import de.tum.`in`.tumcampusapp.component.tumui.tutionfees.TuitionFeeManager
import de.tum.`in`.tumcampusapp.component.tumui.tutionfees.TuitionFeesCardsProvider
import de.tum.`in`.tumcampusapp.component.tumui.tutionfees.TuitionFeesNotificationProvider
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.CafeteriaNotificationProvider
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaCardsProvider
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaManager
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.controller.CafeteriaMenuManager
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.interactors.FetchBestMatchMensaInteractor
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.repository.CafeteriaLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.cafeteria.repository.CafeteriaRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomCardsProvider
import de.tum.`in`.tumcampusapp.component.ui.chat.ChatRoomController
import de.tum.`in`.tumcampusapp.component.ui.eduroam.EduroamController
import de.tum.`in`.tumcampusapp.component.ui.news.NewsCardsProvider
import de.tum.`in`.tumcampusapp.component.ui.news.NewsController
import de.tum.`in`.tumcampusapp.component.ui.news.repository.NewsLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.news.repository.NewsRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.EventsLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.EventsRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.TicketsLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.ticket.repository.TicketsRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.transportation.TransportCardsProvider
import de.tum.`in`.tumcampusapp.component.ui.transportation.TransportController
import de.tum.`in`.tumcampusapp.component.ui.transportation.TransportNotificationProvider
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.MvvApiService
import de.tum.`in`.tumcampusapp.component.ui.transportation.api.MvvClient
import de.tum.`in`.tumcampusapp.component.ui.transportation.repository.TransportLocalRepository
import de.tum.`in`.tumcampusapp.component.ui.transportation.repository.TransportRemoteRepository
import de.tum.`in`.tumcampusapp.component.ui.transportation.widget.MVVWidgetController
import de.tum.`in`.tumcampusapp.database.TcaDb
import de.tum.`in`.tumcampusapp.utils.sync.SyncManager
import org.jetbrains.anko.defaultSharedPreferences
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideSharedPreferences(): SharedPreferences {
        return context.defaultSharedPreferences
    }

    @Singleton
    @Provides
    fun provideDatabase(): TcaDb {
        return TcaDb.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesTumCabeClient(): TUMCabeClient {
        return TUMCabeClient.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesTumOnlineClient(): TUMOnlineClient {
        return TUMOnlineClient.getInstance(context)
    }

    @Singleton
    @Provides
    fun providesCafeteriaLocalRepository(db: TcaDb): CafeteriaLocalRepository {
        return CafeteriaLocalRepository(db)
    }

    @Singleton
    @Provides
    fun providesCafeteriaRemoteRepository(client: TUMCabeClient): CafeteriaRemoteRepository {
        return CafeteriaRemoteRepository(client)
    }

    @Singleton
    @Provides
    fun provideEventsLocalRepository(
            database: TcaDb
    ): EventsLocalRepository {
        return EventsLocalRepository(database)
    }

    @Singleton
    @Provides
    fun provideEventsRemoteRepository(
            tumCabeClient: TUMCabeClient,
            eventsLocalRepository: EventsLocalRepository,
            ticketsLocalRepository: TicketsLocalRepository,
            ticketsRemoteRepository: TicketsRemoteRepository
    ): EventsRemoteRepository {
        return EventsRemoteRepository(context, tumCabeClient,
                eventsLocalRepository, ticketsLocalRepository, ticketsRemoteRepository)
    }

    @Singleton
    @Provides
    fun provideTicketsLocalRepository(
            database: TcaDb
    ): TicketsLocalRepository {
        return TicketsLocalRepository(database)
    }

    @Singleton
    @Provides
    fun provideTumLocationManager(): TumLocationManager {
        return TumLocationManager(context)
    }

    @Singleton
    @Provides
    fun provideCafeteriaManager(
            tumLocationManager: TumLocationManager,
            localRepository: CafeteriaLocalRepository,
            remoteRepository: CafeteriaRemoteRepository
    ): CafeteriaManager {
        return CafeteriaManager(context, tumLocationManager, localRepository, remoteRepository)
    }

    @Singleton
    @Provides
    fun provideCafeteriaMenuManager(): CafeteriaMenuManager {
        return CafeteriaMenuManager(context)
    }

    @Singleton
    @Provides
    fun provideSyncManager(): SyncManager {
        return SyncManager(context)
    }

    @Singleton
    @Provides
    fun provideNewsController(): NewsController {
        return NewsController(context)
    }

    @Singleton
    @Provides
    fun provideNewsLocalRepository(
            database: TcaDb
    ): NewsLocalRepository {
        return NewsLocalRepository(context, database)
    }

    @Singleton
    @Provides
    fun provideNewsRemoteRepository(
            localRepository: NewsLocalRepository,
            syncManager: SyncManager,
            tumCabeClient: TUMCabeClient,
            newsController: NewsController
    ): NewsRemoteRepository {
        return NewsRemoteRepository(localRepository, syncManager, tumCabeClient, newsController)
    }

    @Singleton
    @Provides
    fun provideNewsCardsProvider(
            database: TcaDb,
            newsLocalRepository: NewsLocalRepository
    ): NewsCardsProvider {
        return NewsCardsProvider(context, database, newsLocalRepository)
    }

    @Singleton
    @Provides
    fun provideCalendarController(): CalendarController {
        return CalendarController(context)
    }

    @Singleton
    @Provides
    fun provideCalendarCardsProvider(
            database: TcaDb
    ): CalendarCardsProvider {
        return CalendarCardsProvider(context, database)
    }

    @Singleton
    @Provides
    fun provideMvvApiService(): MvvApiService {
        return MvvClient.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideTransportController(): TransportController {
        return TransportController(context)
    }

    @Singleton
    @Provides
    fun provideTransportLocalRepository(
            database: TcaDb
    ): TransportLocalRepository {
        return TransportLocalRepository(database)
    }

    @Singleton
    @Provides
    fun provideTransportRemoteRepository(
            mvvService: MvvApiService
    ): TransportRemoteRepository {
        return TransportRemoteRepository(mvvService)
    }

    @Singleton
    @Provides
    fun provideTransportCardsProvider(
            transportRemoteRepository: TransportRemoteRepository
    ): TransportCardsProvider {
        return TransportCardsProvider(context, transportRemoteRepository)
    }

    @Singleton
    @Provides
    fun provideMVVWidgetController(
            localRepository: TransportLocalRepository
    ): MVVWidgetController {
        return MVVWidgetController(localRepository)
    }

    @Singleton
    @Provides
    fun provideCafeteriaCardsProvider(
            cafeteriaManager: CafeteriaManager,
            localRepository: CafeteriaLocalRepository,
            tumLocationManager: TumLocationManager
    ) : CafeteriaCardsProvider {
        return CafeteriaCardsProvider(context, cafeteriaManager, localRepository, tumLocationManager)
    }

    @Singleton
    @Provides
    fun provideChatRoomCardsProvider(
            tumOnlineClient: TUMOnlineClient,
            tumCabeClient: TUMCabeClient,
            chatRoomController: ChatRoomController,
            database: TcaDb
    ): ChatRoomCardsProvider {
        return ChatRoomCardsProvider(context, tumOnlineClient, tumCabeClient, chatRoomController, database)
    }

    @Singleton
    @Provides
    fun provideChatRoomController(): ChatRoomController {
        return ChatRoomController(context)
    }

    @Singleton
    @Provides
    fun provideFetchBestMatchMensaInteractor(
            cafeteriaManager: CafeteriaManager
    ): FetchBestMatchMensaInteractor {
        return FetchBestMatchMensaInteractor(cafeteriaManager)
    }

    @Singleton
    @Provides
    fun provideCafeteriaNotificationProvider(
            tumLocationManager: TumLocationManager,
            cafeteriaManager: CafeteriaManager,
            cafeteriaMenuManager: CafeteriaMenuManager,
            localRepository: CafeteriaLocalRepository
    ): CafeteriaNotificationProvider {
        return CafeteriaNotificationProvider(
                context, tumLocationManager, cafeteriaManager, cafeteriaMenuManager, localRepository
        )
    }

    @Singleton
    @Provides
    fun provideTransportNotificationProvider(
            remoteRepository: TransportRemoteRepository,
            tumLocationManager: TumLocationManager
    ): TransportNotificationProvider {
        return TransportNotificationProvider(context, remoteRepository, tumLocationManager)
    }

    @Singleton
    @Provides
    fun provideTuitionFeeManager(): TuitionFeeManager {
        return TuitionFeeManager(context)
    }

    @Singleton
    @Provides
    fun provideTuitionFeesCardsProvider(
            tuitionFeeManager: TuitionFeeManager
    ): TuitionFeesCardsProvider {
        return TuitionFeesCardsProvider(context, tuitionFeeManager)
    }

    @Singleton
    @Provides
    fun provideTuitionFeesNotificationProvider(): TuitionFeesNotificationProvider {
        return TuitionFeesNotificationProvider(context)
    }

    @Singleton
    @Provides
    fun provideEduroamController(): EduroamController {
        return EduroamController(context)
    }

}
