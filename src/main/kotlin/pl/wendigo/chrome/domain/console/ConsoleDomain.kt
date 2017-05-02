package pl.wendigo.chrome.domain.console

/**
 * This domain is deprecated - use Runtime or Log instead.
 */
@pl.wendigo.chrome.Deprecated class ConsoleDomain internal constructor(private val connectionRemote : pl.wendigo.chrome.DebuggerProtocol) {

	/**
	 * Enables console domain, sends the messages collected so far to the client by means of the <code>messageAdded</code> notification.
	 */
	 fun enable() : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Console.enable", null, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
	}

    /**
     * Enables console domain, sends the messages collected so far to the client by means of the <code>messageAdded</code> notification.
    */
     fun enableTimed() : io.reactivex.Single<io.reactivex.schedulers.Timed<pl.wendigo.chrome.ResponseFrame>> {
        return connectionRemote.runAndCaptureResponse("Console.enable", null, pl.wendigo.chrome.ResponseFrame::class.java)
    }

	/**
	 * Disables console domain, prevents further console messages from being reported to the client.
	 */
	 fun disable() : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Console.disable", null, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
	}

    /**
     * Disables console domain, prevents further console messages from being reported to the client.
    */
     fun disableTimed() : io.reactivex.Single<io.reactivex.schedulers.Timed<pl.wendigo.chrome.ResponseFrame>> {
        return connectionRemote.runAndCaptureResponse("Console.disable", null, pl.wendigo.chrome.ResponseFrame::class.java)
    }

	/**
	 * Does nothing.
	 */
	 fun clearMessages() : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Console.clearMessages", null, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
	}

    /**
     * Does nothing.
    */
     fun clearMessagesTimed() : io.reactivex.Single<io.reactivex.schedulers.Timed<pl.wendigo.chrome.ResponseFrame>> {
        return connectionRemote.runAndCaptureResponse("Console.clearMessages", null, pl.wendigo.chrome.ResponseFrame::class.java)
    }

  
    /**
     * Issued when new console message is added.
     */
    fun messageAdded() : io.reactivex.Flowable<MessageAddedEvent> {
        return messageAddedTimed().map {
            it.value()
        }
    }

    /**
     * Issued when new console message is added.
     */
     fun messageAddedTimed() : io.reactivex.Flowable<io.reactivex.schedulers.Timed<MessageAddedEvent>> {
        return connectionRemote.captureEvents("Console.messageAdded", MessageAddedEvent::class.java)
     }

    /**
     * Returns flowable capturing all Console domains events.
     */
    fun events() : io.reactivex.Flowable<pl.wendigo.chrome.ProtocolEvent> {
        return connectionRemote.captureAllEvents().map { it.value() }.filter {
            it.protocolDomain() == "Console"
        }
    }
}






/**
 * Represents responseFrame from Console. method call.
 *
 * Issued when new console message is added.
 */
data class MessageAddedEvent(
  /**
   * Console message that has been added.
   */
  val message : ConsoleMessage

) : pl.wendigo.chrome.ProtocolEvent(domain = "Console", name = "messageAdded")

