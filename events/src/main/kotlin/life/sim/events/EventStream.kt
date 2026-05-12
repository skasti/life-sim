package life.sim.events

fun interface EventListener {
    fun onEvent(event: Event)
}

fun interface Subscription {
    fun unsubscribe()
}

interface EventStream {
    fun publish(event: Event)

    fun subscribe(
        topic: String? = null,
        routingTagPrefix: String? = null,
        listener: EventListener,
    ): Subscription
}
