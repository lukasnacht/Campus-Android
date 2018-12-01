package de.tum.`in`.tumcampusapp.component.ui.ticket.di

import dagger.BindsInstance
import dagger.Subcomponent
import de.tum.`in`.tumcampusapp.component.ui.ticket.activity.BuyTicketActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.activity.ShowTicketActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.activity.StripePaymentActivity
import de.tum.`in`.tumcampusapp.component.ui.ticket.fragment.EventDetailsFragment
import de.tum.`in`.tumcampusapp.component.ui.ticket.fragment.EventsFragment
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.EventType

@Subcomponent(modules = [TicketsModule::class])
interface TicketsComponent {

    fun inject(eventsFragment: EventsFragment)

    fun inject(eventDetailsFragment: EventDetailsFragment)

    fun inject(buyTicketActivity: BuyTicketActivity)

    fun inject(stripePaymentActivity: StripePaymentActivity)

    fun inject(showTicketActivity: ShowTicketActivity)

    @Subcomponent.Builder
    interface Builder {

        fun ticketsModule(ticketsModule: TicketsModule): TicketsComponent.Builder

        @BindsInstance
        fun eventType(eventType: EventType): TicketsComponent.Builder

        fun build(): TicketsComponent

    }

}