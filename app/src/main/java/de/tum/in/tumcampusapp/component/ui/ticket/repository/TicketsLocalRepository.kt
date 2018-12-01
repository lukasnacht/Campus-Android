package de.tum.`in`.tumcampusapp.component.ui.ticket.repository

import androidx.lifecycle.LiveData
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.Ticket
import de.tum.`in`.tumcampusapp.component.ui.ticket.model.TicketType
import de.tum.`in`.tumcampusapp.database.TcaDb
import javax.inject.Inject

class TicketsLocalRepository @Inject constructor(
        private val database: TcaDb
) {

    fun getAll(): LiveData<List<Ticket>> = database.ticketDao().all

    fun storeTickets(tickets: List<Ticket>) {
        insert(*tickets.toTypedArray())
    }

    fun insert(vararg tickets: Ticket) {
        database.ticketDao().insert(*tickets)
    }

    fun getTicketByEventId(eventId: Int): Ticket {
        return database.ticketDao().getByEventId(eventId)
    }

    fun getTicketTypeById(id: Int): TicketType {
        return database.ticketTypeDao().getById(id)
    }

    fun addTicketTypes(ticketTypes: List<TicketType>) {
        database.ticketTypeDao().insert(ticketTypes)
    }

}