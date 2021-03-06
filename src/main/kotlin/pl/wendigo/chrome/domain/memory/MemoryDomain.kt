package pl.wendigo.chrome.domain.memory

/**
 * MemoryDomain represents remote debugger protocol domain.
 */
class MemoryDomain internal constructor(private val connectionRemote : pl.wendigo.chrome.DebuggerProtocol) {
    /**
     *
     */
    fun getDOMCounters() : io.reactivex.Single<GetDOMCountersResponse> {
        return connectionRemote.runAndCaptureResponse("Memory.getDOMCounters", null, GetDOMCountersResponse::class.java).map {
            it.value()
        }
    }

    /**
     *
     */
    fun prepareForLeakDetection() : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Memory.prepareForLeakDetection", null, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
    }

    /**
     * Enable/disable suppressing memory pressure notifications in all processes.
     */
    fun setPressureNotificationsSuppressed(input : SetPressureNotificationsSuppressedRequest) : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Memory.setPressureNotificationsSuppressed", input, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
    }

    /**
     * Simulate a memory pressure notification in all processes.
     */
    fun simulatePressureNotification(input : SimulatePressureNotificationRequest) : io.reactivex.Single<pl.wendigo.chrome.ResponseFrame> {
        return connectionRemote.runAndCaptureResponse("Memory.simulatePressureNotification", input, pl.wendigo.chrome.ResponseFrame::class.java).map {
            it.value()
        }
    }

    /**
     * Returns flowable capturing all Memory domains events.
     */
    fun events() : io.reactivex.Flowable<pl.wendigo.chrome.ProtocolEvent> {
        return connectionRemote.captureAllEvents().map { it.value() }.filter {
            it.protocolDomain() == "Memory"
        }
    }
}

/**
 * Represents response frame for Memory.getDOMCounters method call.
 *
 *
 */
data class GetDOMCountersResponse(
  /**
   *
   */
  val documents : Int,

  /**
   *
   */
  val nodes : Int,

  /**
   *
   */
  val jsEventListeners : Int

)

/**
 * Represents request frame that can be used with Memory.setPressureNotificationsSuppressed method call.
 *
 * Enable/disable suppressing memory pressure notifications in all processes.
 */
data class SetPressureNotificationsSuppressedRequest (
    /**
     * If true, memory pressure notifications will be suppressed.
     */
    val suppressed : Boolean

)

/**
 * Represents request frame that can be used with Memory.simulatePressureNotification method call.
 *
 * Simulate a memory pressure notification in all processes.
 */
data class SimulatePressureNotificationRequest (
    /**
     * Memory pressure level of the notification.
     */
    val level : PressureLevel

)

